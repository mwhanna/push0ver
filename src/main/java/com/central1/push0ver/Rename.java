/*
Copyright 2016 - Central 1 Credit Union - https://www.central1.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.central1.push0ver;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.io.Files;

public class Rename
{
	private String interimTarget;
	private String repoName;
	private String nodeRepo;
	private Tag tag;
	private String url;
	private String basicAuth;
	private MyLogger buildLogger;
	private boolean sslTrustAll;




	public Rename(
			String pathToPom, Tag tag, String repoName, String nodeRepo, String basicAuth,
			String url, MyLogger buildLogger, boolean sslTrustAll)
	{
		this.tag = tag;
		this.repoName = repoName;
		this.basicAuth = basicAuth;
		this.nodeRepo = nodeRepo;
		this.url = url;
		if ( !url.endsWith( "/" ) )
		{
			this.url = url + "/";
		}

		this.interimTarget = new File( pathToPom + tag.getDirectory() + "/target" ).getAbsolutePath();
		this.buildLogger = buildLogger;
		this.sslTrustAll = sslTrustAll;
		new File( interimTarget + "/newfiles" ).mkdirs();
		new File( interimTarget + "/updates" ).mkdirs();
	}


	public void renameJars( String search, String replace, String localRepo, String group, String module, boolean doPush )
	{
		File folderr = new File( localRepo );
		buildLogger.addBuildLogEntry( "push0ver - SEARCHING: " + folderr.getPath() );
		File[] listOfFiles = folderr.listFiles();
		if ( listOfFiles != null )
		{
			if ( listOfFiles.length < 1 )
			{
				buildLogger.addBuildLogEntry( "push0ver - No files in " + folderr.getPath()
						+ "  Maven Build may not have been ran" );
			}
			for ( File g : listOfFiles )
			{
				if ( g.isFile() )
				{
					File f = new File( localRepo + "/" + g.getName() );
					String filename = g.getName();
					int startIdx = 0;
					int idxOld;
					final StringBuilder result = new StringBuilder();
					while ( ( idxOld = filename.indexOf( search, startIdx ) ) >= 0 )
					{
						result.append( filename.substring( startIdx, idxOld ) );
						result.append( replace );
						startIdx = idxOld + search.length();
					}
					//the final chunk will go to the end of aInput
					result.append( filename.substring( startIdx ) );
					filename = result.toString();
					
					File target = new File( interimTarget + "/newfiles/" + filename );
					try
					{
						Files.move( f, target );
					}
					catch ( IOException ioe )
					{
						throw new RuntimeException( "push0ver failed to move files from .m2 repo: " + ioe );
					}

					buildLogger.addBuildLogEntry( "push0ver - renaming "
							+ f.getAbsolutePath() + " to " + target.getAbsolutePath() );
				}
			}
		}

		updateJars( search, replace, group, module, doPush );

	}

	public void publishNode ( File packageDir )
	{
		//runs after version replacement
		String publish = "npm publish --registry " + url + "api/npm/" + nodeRepo + "/";
		Command nodePublish = new Command(publish);
		boolean doPublish = false;
		File[] nodeFiles = packageDir.listFiles();

		for (File f : nodeFiles)
		{
			if ( f.isFile() && f.getAbsolutePath().toLowerCase().contains("package.json"))
			{
				doPublish = true;
				break;
			}
		}

		if (doPublish)
		{
			try
			{
				buildLogger.addBuildLogEntry("Executing: " + publish );
				nodePublish.execute(packageDir);
				buildLogger.addBuildLogEntry( nodePublish.getStdout());
			}
			catch (IOException ioe)
			{
				buildLogger.addBuildLogEntry("Caught IOexception: " + ioe.getMessage());
			}
		}
		else
		{
			buildLogger.addBuildLogEntry("NO PACKAGE.JSON - skipping npm publish");
		}

	}

	public void npmPublish( File packageFile, boolean doPush )
	{
		String dryRun = doPush ? "" : "(DRY-RUN)";
		File f = packageFile.getParentFile();
		if ( f.exists() )
		{
			buildLogger.addBuildLogEntry("RUNNING NPM PUBLISH IN: [" + f + "] " + dryRun);
			if (doPush) {
				publishNode(f);
			}
		} else {
			buildLogger.addBuildLogEntry("CANNOT NPM PUBLISH [" + f + "] FILE-NOT-FOUND " + dryRun);
		}
	}

	private static final Comparator POMS_LAST = new Comparator<File>()
	{
		public int compare( File f1, File f2 )
		{
			if ( f1 == null && f2 == null )
			{
				return 0;
			}
			else if ( f1 == null )
			{
				return -1;
			}
			else if ( f2 == null )
			{
				return 1;
			}
			String name1 = f1.getName();
			String name2 = f2.getName();
			boolean b1 = name1.endsWith( ".pom" );
			boolean b2 = name2.endsWith( ".pom" );
			if ( b1 == b2 )
			{
				return f1.compareTo( f2 );
			}
			else
			{
				return b1 ? 1 : -1;
			}
		}
	};

	public void updateJars( String search, String replace, String group, String module, boolean doPush )
	{
		File[] files = new File( interimTarget + "/newfiles" ).listFiles();
		if ( files == null )
		{
			return;
		}

		Arrays.sort( files, POMS_LAST );

		long currentTime = System.currentTimeMillis();
		for ( File f : files )
		{

			String n = f.getName().toUpperCase();
			if ( n.toLowerCase().endsWith(".uploaded"))
			{
				continue;
			}

			if ( n.endsWith( ".POM" ) )
			{
				try
				{
					String replaced = readAndReplace( f, search, replace );
					if ( replaced != null )
					{
						FileOutputStream fOut = new FileOutputStream( f );
						fOut.write( replaced.getBytes( "UTF-8" ) );
						fOut.close();
					}
				}
				catch ( IOException p )
				{
					buildLogger.addBuildLogEntry( "push0ver - failed to replace: " + f.getPath() );
				}
			}
			else if ( n.endsWith( ".JAR" ) || n.endsWith( ".WAR" ) || n.endsWith( ".ZIP" ) || n.endsWith( ".EAR" ) )
			{
				try
				{
					LinkedHashSet<String> thingsToRezip = new LinkedHashSet<String>();
					JarFile jarFile = new JarFile( f );
					Enumeration<JarEntry> entries = jarFile.entries();
					while ( entries.hasMoreElements() )
					{
						JarEntry current = entries.nextElement();
						String name = current.getName();
						if ( replaceable( name ) )
						{
							InputStream is = jarFile.getInputStream( current );
							String replaced = readAndReplace( is, search, replace );
							if ( replaced != null )
							{
								String holdingLocation = interimTarget + "/updates/" + f.getName();
								File temp = new File( holdingLocation + "/" + current.getName() );
								temp.getParentFile().mkdirs();

								FileWriter writer = new FileWriter( temp );
								writer.write( replaced );
								writer.close();
								thingsToRezip.add( holdingLocation );
							}
							is.close();
						}
					}
					jarFile.close();

					// rezip!
					for ( String s : thingsToRezip )
					{
						int x = s.lastIndexOf( '/' );
						String zipName = s.substring( x );
						String[] rezipCmd = new String[]{"jar", "-uf", interimTarget + "/newfiles" + zipName, "."};
						exec( rezipCmd, s );
					}

				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}

			/*
			curl -i -X PUT -T /opt/central1/version-experiment/target/newfiles/my-app-0.0.3-SNAPSHOT.jar
				-u deployer:deployer
			https://artifactory.oss.central1.com/artifactory/simple/Central1-local/com/matt2/app/my-app/0.0.3-SNAPSHOT/my-app-0.0.3-SNAPSHOT.jar
			*/
			String version = tag.getVersion().toString();
			String name = f.getName();
			if ( name.contains( version ) && !name.endsWith( ".md5" ) && !name.endsWith( ".sha1" ) )
			{
				if ( !version.endsWith( "-SNAPSHOT" ) )
				{
					String existsSha1Target = existsTarget( name, group, module ) + ".sha1";
					if ( App.exists( buildLogger, existsSha1Target, basicAuth ) )
					{
						buildLogger.addBuildLogEntry( "push0ver - File " + existsTarget( name, group, module ) + " Already Exists! Aborting." );
						return;
					}
				}

				mvnMakeFingerprints( f );
				String fileToUpload = f.getAbsolutePath();
				CloseableHttpClient httpClient = allConnect( sslTrustAll );
				HttpPut[] puts = new HttpPut[ 3 ];
				puts[ 0 ] = new HttpPut( uploadTarget( name, version, group, module, currentTime ) );
				puts[ 1 ] = new HttpPut( uploadTarget( name + ".md5", version, group, module, currentTime ) );
				puts[ 2 ] = new HttpPut( uploadTarget( name + ".sha1", version, group, module, currentTime ) );

				File md5 = new File( fileToUpload + ".md5" );
				File sha1 = new File( fileToUpload + ".sha1" );

				HttpEntity entity = new FileEntity( f );
				puts[ 0 ].setEntity( entity );

				HttpEntity entitymd5 = new FileEntity( md5 );
				puts[ 1 ].setEntity( entitymd5 );

				HttpEntity entitysha1 = new FileEntity( sha1 );
				puts[ 2 ].setEntity( entitysha1 );

				for ( int t = 0; t < 3; t++ )
				{
					if ( doPush )
					{
						CloseableHttpResponse response = null;
						int statusCode = 0;
						try
						{
							puts[ t ].setHeader( "Authorization", basicAuth );
							if ( httpClient == null )
							{
								buildLogger.addBuildLogEntry( "push0ver - HttpClient is NULL" );
							}
							response = httpClient.execute( puts[ t ] );
							StatusLine statusLine = response.getStatusLine();
							statusCode = statusLine.getStatusCode();
							String tempCode = Integer.toString( statusCode ).substring( 0, 1 );

							if ( Integer.parseInt( tempCode ) == 4 || Integer.parseInt( tempCode ) == 5 )
							{
								buildLogger.addBuildLogEntry( "push0ver - ERROR! DID NOT UPLOAD: " + fileToUpload + "--    error code: " + response.getStatusLine() );

								if ( statusCode == 502 )
								{
									throw new RuntimeException( "Possibly " + repoName + " is not configured to receive Snapshots." );
								}
								else
								{
									throw new RuntimeException( "FAILED to push to Artifactory - See Logs." );
								}
							}

						}
						catch ( IOException a )
						{
							a.printStackTrace();
						}
						finally
						{
							try
							{
								if ( response == null )
								{
									buildLogger.addBuildLogEntry( "push0ver - HTTP Response is NULL" );
								}
								else
								{
									response.close();
								}
							}
							catch ( IOException b )
							{
								b.printStackTrace();
							}
						}
						buildLogger.addBuildLogEntry( "push0ver - DONE:     " + statusCode + " - " + puts[ t ] );
					}
					else
					{
						buildLogger.addBuildLogEntry( "push0ver - WOULD-DO:    " + puts[ t ] );
					}
				}


				md5.renameTo(new File(md5.getAbsolutePath() + ".uploaded"));
				sha1.renameTo(new File(sha1.getAbsolutePath() + ".uploaded"));
				f.renameTo(new File(f.getAbsolutePath() + ".uploaded"));

				try
				{
					httpClient.close();
				}
				catch ( IOException b )
				{
					b.printStackTrace();
				}
			}
		}
	}

	private void mvnMakeFingerprints(File f )
	{
		try
		{
			FileInputStream fin = new FileInputStream( f );
			MessageDigest md5 = MessageDigest.getInstance( "MD5" );
			MessageDigest sha1 = MessageDigest.getInstance( "SHA1" );

			if ( f.getName().endsWith( ".md5" ) || f.getName().endsWith( ".sha1" ) )
			{
				return;
			}
			byte[] buf = new byte[ 4096 ];
			int n;
			while ( ( n = fin.read( buf, 0, buf.length ) ) != -1 )
			{
				md5.update( buf, 0, n );
			}

			fin = new FileInputStream( f );
			while ( ( n = fin.read( buf, 0, buf.length ) ) != -1 )
			{
				sha1.update( buf, 0, n );
			}

			String md5sum = DatatypeConverter.printHexBinary( md5.digest() ).toLowerCase( Locale.ENGLISH );
			String sha1sum = DatatypeConverter.printHexBinary( sha1.digest() ).toLowerCase( Locale.ENGLISH );

			FileOutputStream fos = new FileOutputStream( f + ".md5" );
			fos.write( md5sum.getBytes( "UTF-8" ) );
			fos.close();

			fos = new FileOutputStream( f + ".sha1" );
			fos.write( sha1sum.getBytes( "UTF-8" ) );
			fos.close();
		}
		catch ( Exception e )
		{
			throw new RuntimeException( "stuff happened", e );
		}
	}

	private boolean replaceable( String name )
	{
		name = name.toUpperCase( Locale.ENGLISH );
		return name.endsWith( ".XML" ) || name.endsWith( ".PROPERTIES" ) || name.endsWith( ".POM" )
				|| name.endsWith( ".HTML" ) || name.endsWith( ".HTM" ) || name.endsWith( ".XHTML" )
				|| name.endsWith( ".CSS" ) || name.endsWith( ".JSON" )
				|| name.endsWith( ".JS" ) || name.endsWith( ".YAML" ) || name.endsWith( ".YML" )
				|| name.endsWith( ".TXT" ) || name.endsWith( ".MD" ) || name.endsWith( ".MARKDOWN" )
				|| name.endsWith( ".PY" ) || name.endsWith( ".RB" ) || name.endsWith( ".PL" )
				|| name.endsWith( ".SH" ) || name.endsWith( ".RUBY" ) || name.endsWith( ".PERL" )
				|| name.endsWith( ".PM" ) || name.endsWith( ".CONF" ) || name.endsWith( ".CFG" )
				|| name.endsWith( ".INC" ) || name.endsWith( ".SHTML" ) || name.endsWith( ".JSP" )
				|| name.endsWith( ".ASP" ) || name.endsWith( ".JAVA" ) || name.endsWith( ".CS" )
				|| name.endsWith( ".INI" );
	}

	private String uploadTarget( String fileName, String tag, String group, String module, long timestamp )
	{
		String target = url + repoName + "/" + group + "/" + module + "/" + tag + "/" + fileName;
		return target + "?build.timestamp=" + timestamp;
	}

	private String existsTarget( String fileName, String group, String module )
	{
		return url + repoName + "/" + group + "/" + module + "/" + tag.getVersion() + "/" + fileName;
	}

	private static String readAndReplace( File f, String search, String replace ) throws IOException
	{
		FileInputStream fin = new FileInputStream( f );
		try
		{
			return readAndReplace( fin, search, replace );
		}
		finally
		{
			fin.close();
		}
	}

	private static String readAndReplace( InputStream in, String search, String replace ) throws IOException
	{
		Scanner scanner = new Scanner( in, "UTF-8" ).useDelimiter( "\\A" );
		String s = scanner.hasNext() ? scanner.next() : "";
		if ( s.contains( search ) )
		{
			return s.replaceAll( search, replace );
		}
		else
		{
			return null;
		}
	}

	public void exec( String[] command, String dir )
	{
		try
		{
			Process process;
			if ( dir == null )
			{
				process = Runtime.getRuntime().exec( command );
			}
			else
			{
				process = Runtime.getRuntime().exec( command, null, new File( dir ) );
			}

			InputStream in = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try
			{
				boolean hasError = false;
				in = process.getInputStream();
				isr = new InputStreamReader( in, "UTF-8" );
				br = new BufferedReader( isr );
				String line;
				StringBuilder buf = new StringBuilder();
				while ( ( line = br.readLine() ) != null )
				{
					if ( line.startsWith( "HTTP/" ) )
					{
						String[] toks = line.split( " " );
						hasError = toks.length > 1 && toks[ 1 ].charAt( 0 ) == '4';
					}
					buf.append( line ).append( '\n' );
				}

				in = process.getErrorStream();
				isr = new InputStreamReader( in, "UTF-8" );
				br = new BufferedReader( isr );
				while ( ( line = br.readLine() ) != null )
				{
					System.out.println( "ERR-STREAM: " + line );
				}

				process.waitFor();
				// process.waitFor( 60, TimeUnit.SECONDS );
				if ( hasError )
				{
					throw new IOException( "Failed HTTP response: [" + buf + "]" );
				}
			}
			finally
			{
				Finally.close( br, isr, in );
			}
		}
		catch ( Exception e )
		{
			buildLogger.addBuildLogEntry( "push0ver - CMD FAILED: " + command[ 0 ] );
			e.printStackTrace();
		}
	}

	public static CloseableHttpClient allConnect( boolean sslTrustAll )
	{
		CloseableHttpClient httpClient = null;
		if ( sslTrustAll )
		{
			try
			{
				SSLContextBuilder builder = new SSLContextBuilder();
				builder.loadTrustMaterial( null, new TrustStrategy()
				{
					@Override
					public boolean isTrusted( X509Certificate[] chain, String authType ) throws CertificateException
					{
						return true;
					}
				} );
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( builder.build() );
				httpClient = HttpClients.custom().setSSLSocketFactory( sslsf ).build();
			}
			catch ( KeyStoreException u )
			{
				u.printStackTrace();
			}
			catch ( NoSuchAlgorithmException u )
			{
				u.printStackTrace();
			}
			catch ( KeyManagementException u )
			{
				u.printStackTrace();
			}
		}
		else
		{
			httpClient = HttpClients.createDefault();
		}
		return httpClient;
	}
}
