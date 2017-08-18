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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

public class App
{
	private static final Pattern WS_PATTERN = Pattern.compile( "\\s" );

	public static void main( String[] args ) throws Exception
	{
		invoke( args, System.getProperties(), logLine -> {
			System.out.println( logLine );
			return logLine;
		});
	}

	public static void invoke( final String[] args, final Properties p, final MyLogger log ) throws Exception
	{
		String pathToPom = args.length > 0 ? args[ 0 ] : null;
		pathToPom = pathToPom != null ? pathToPom.trim() : "";
		if ( "".equals( pathToPom ) )
		{
			pathToPom = ".";
		}

		String userName = null;
		String userPassword = null;
		boolean doSomething = true;

		final boolean sslTrustAll = "true".equalsIgnoreCase( p.getProperty( "ssl.trustAll" ) );
		final boolean doPush = args.length > 1 && "push".equalsIgnoreCase( args[ 1 ] );
		String mvnRepoName = null;
		String snapRepo = null;
		String nodeRepo = null;
		String url = null;
		String userHome = System.getProperty( "user.home" );
		if ( p.getProperty( "repo.name" ) != null )
		{
			mvnRepoName = p.getProperty( "repo.name" );
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter a Repository name! (-Drepo.name=x)" );
			doSomething = false;
		}

		if ( p.getProperty( "snap.repo" ) != null )
		{
			snapRepo = p.getProperty( "snap.repo" );
		}

		if ( p.getProperty( "art.url" ) != null )
		{
			url = p.getProperty( "art.url" );
			if ( !url.endsWith( "/" ) )
			{
				url = url + "/";
			}
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter a Repository URL! (-Dart.url=x)" );
			doSomething = false;
		}

		if ( p.getProperty("noderepo.name") != null )
		{
			nodeRepo = p.getProperty( "noderepo.name" );
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter an Node Repository! (-Dnoderepo.name=x)" );
		}

		if ( p.getProperty( "art.username" ) != null )
		{
			userName = p.getProperty( "art.username" );
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter an Artifactory Username! (-Dart.username=x)" );
			doSomething = false;
		}

		if ( p.getProperty( "art.password" ) != null )
		{
			userPassword = p.getProperty( "art.password" );
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter an Artifactory Password! (-Dart.password=x)" );
			doSomething = false;
		}

		String pathToMaven = null;
		String mvnCommand = "mvn";
		if ( p.getProperty( "mvn.home" ) != null )
		{
			pathToMaven = p.getProperty( "mvn.home" );
		}
		if ( pathToMaven != null && !"".equals( pathToMaven.trim() ) )
		{
			mvnCommand = pathToMaven.trim() + "/bin/mvn";
		}

		String gitRepo = null;
		String gitTarget = pathToPom;
		if ( p.getProperty( "git.repo" ) != null )
		{
			gitRepo = p.getProperty( "git.repo" );
		}
		if ( gitRepo != null )
		{
			gitTarget = gitRepo;
		}

		String[] badTag = new String[1];
		fetchTags( log, gitTarget );
		Map<String, Tag> tags = TagExtractor.getTag( gitTarget, pathToPom,false, log, badTag);

		if ( tags != null && tags.size() > 0 )
		{
			for (Tag t : tags.values())
			{
                if ( t == null )
                {
                    log.addBuildLogEntry( "push0ver - ABORTING - unable to extract valid release or snapshot tag." );
                    return;
                }

                String windupStatus = extractAndDeletePreStatus(pathToPom, t, log);

                log.addBuildLogEntry( "push0ver - EXTRACTED TAG:       " + t );
                final String basicAuthHeader = basicAuthHeader( userName, userPassword );

                if ( t.getVersion().toString().contains( "SNAPSHOT" ) )
                {
                    if ( snapRepo == null || "".equals( snapRepo ) )
                    {
                        log.addBuildLogEntry( "push0ver - No Global or Local Repo set for SNAPSHOT, using RELEASE Repo." );
                    }
                    else
                    {
                        mvnRepoName = snapRepo;
                    }
                }

                String search = "0".substring( 0, 1 ) + ".0.0.0.0-SNAPSHOT";

				Set<File> matches = new HashSet<>();
                if ( windupStatus != null )
                {
					String[] lines = windupStatus.split("[\\r\\n]+");
					for (int i = 1; i < lines.length; i++) {
						matches.add(new File(lines[i]).getCanonicalFile());
					}

                    if ( !windupStatus.startsWith("pre=valid\n"))
                    {
                        log.addBuildLogEntry( "push0ver - Windup was run! ABORTING: no valid tag found: [" + windupStatus + "]" );
                        return;
                    }
                    log.addBuildLogEntry( "push0ver - Windup was run!" );
                    search = t.getVersion().toString();
                }

                log.addBuildLogEntry( "push0ver - Will Execute:  " + String.valueOf( doSomething ) + " Based on: " + pathToPom + t.getDirectory() );
                final Rename r = new Rename(pathToPom, t, mvnRepoName, nodeRepo, basicAuthHeader, url, log, sslTrustAll );

				try
				{
					if ( t.isMaven() )
					{
						MavenStruct struct = mavenCheckIfAlreadyReleased(
								t, log, mvnCommand, pathToPom, basicAuthHeader, url, gitTarget, mvnRepoName
						);
						if (struct != null) {
							for (int z = 0; z < struct.moduleNames.size(); z++) {
								if (doSomething) {
									String m = struct.moduleNames.get(z);
									String g = struct.groupNames.get(z);
									g = g.replace('.', '/');
									String target = userHome + "/.m2/repository/" + g + "/" + m + "/" + search;
									r.renameJars(search, t.getVersion().toString(), target, g, m, doPush);
								}
							}
						}
					}
					if ( t.isNode() )
					{
						NodeStruct struct = nodeCheckIfAlreadyReleased(
								t, log, pathToPom, basicAuthHeader, url, nodeRepo );

						if ( struct != null && doSomething )
						{
							File root = new File( pathToPom + t.getDirectory() );
							Files.walk( root.toPath() )
									.map( Path::toFile )
									.forEach( f -> {

										String name = f.getName();
										if ( "package.json".equalsIgnoreCase( name ) )
										{
											f = canonical( f );
											if ( matches.contains( f ) )
											{
												r.npmPublish( f, doPush );
											}
											else
											{
												log.addBuildLogEntry( "Ignoring [" + f.getPath()
														+ "] since it did not contain the sentinel (0.0.0-PUSH0VE" + "R)" );
											}
										}

									} );
						}
					}
				}
				finally
				{
					if ( doPush )
					{
						if ( false && !containsWhiteSpace( pathToPom ) )
						{
							String[] param = new String[]{"rm", "-rf", pathToPom + "/target/updates"};
							r.exec( param, null );
							param = new String[]{"rm", "-rf", pathToPom + "/target/newfiles"};
							r.exec( param, null );
							param = new String[]{"rm", "-f", pathToPom + "/push0ver.windup.txt"};
							r.exec( param, null );
						}
						else
						{
							log.addBuildLogEntry( "push0ver - Your project dir [" + pathToPom
									+ "] contains whitespace, so you'll have to clean it yourself." );
						}
					}
				}
            }
		}
	}

	public static class MavenStruct
	{
		public Tag tag;
		public final List<String> groupNames = new ArrayList<String>();
		public final List<String> moduleNames = new ArrayList<String>();
	}

	public static class NodeStruct {
		public Tag tag;
		public String packageName;
	}

	private static File canonical(File f) {
		try {
			return f.getCanonicalFile();
		} catch (IOException ioe) {
			throw new RuntimeException("f.getCanonicalFile() failed: " + ioe);
		}
	}

	public static NodeStruct nodeCheckIfAlreadyReleased(
			Tag tag, MyLogger log, String pathToPackage, String basicAuthHeader, String url,
			 String repoName
	) throws IOException
	{
		File path = new File ( pathToPackage + tag.getDirectory() + "/package.json" );
		NodeStruct n = new NodeStruct();

		if ( path.exists() ) {
			InputStream in = new FileInputStream(path.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader( in, StandardCharsets.UTF_8 );
			BufferedReader br = new BufferedReader( isr );
			JsonStreamParser p = new JsonStreamParser( br );
			String name = "";
			String version = "";
			if ( p.hasNext() ) {
                JsonObject json = p.next().getAsJsonObject();
                JsonPrimitive namePrimitive = json.getAsJsonPrimitive("name");
                JsonPrimitive versionPrimitive = json.getAsJsonPrimitive("version");
                name = namePrimitive.getAsString();
                version = versionPrimitive.getAsString();
            }

			String targetName = name.replace("/", "%2F");
			String target = url + "api/npm/" + repoName + "/" + targetName + "/-/" + targetName + "-" + tag.getVersion().toString() + ".tgz";
			if ( !tag.getVersion().toString().contains( "-SNAPSHOT" ) )
			{
				if ( exists( log, target, basicAuthHeader ) )
				{
					log.addBuildLogEntry("push0ver: " + tag.toString() + " exists in artifactory, skipping push0ver");
					return null;
				}

			}
			n.tag = tag;
			n.packageName = name;
			return n;
		}
		return null;
	}



	public static MavenStruct mavenCheckIfAlreadyReleased(
			Tag tag, MyLogger log, String mvnCommand, String pathToPom, String basicAuthHeader, String artUrl,
			String gitTarget, String repoName ) throws IOException
	{
		MavenStruct s = new MavenStruct();
		parseMavenPoms( log, mvnCommand, pathToPom + tag.getDirectory(), s.groupNames, s.moduleNames );

		if ( s.groupNames.isEmpty() || s.moduleNames.isEmpty() )
		{
			log.addBuildLogEntry( "push0ver - ERROR parsing output from mvn dependency:tree (is there a BUILD FAILURE?)" );
			throw new RuntimeException( "push0ver - ERROR parsing output from mvn dependency:tree (is there a BUILD FAILURE?)" );
		}

		String group = s.groupNames.get( 0 ).replace( '.', '/' );
		String checkTarget = artUrl + repoName + "/" + group + "/" + s.moduleNames.get( 0 ) + "/" + tag.getVersion();
		if ( !tag.getVersion().toString().contains( "-SNAPSHOT" ) )
		{

			if ( exists( log, checkTarget, basicAuthHeader ) )
			{
				log.addBuildLogEntry("push0ver: " + tag.toString() + " exists in artifactory, skipping push0ver");

				return null;
			}

		}
		s.tag = tag;
		return s;
	}

	public static void fetchTags( MyLogger log, String projectDir ) throws Exception
	{
		String cmd = "git --no-pager --git-dir=" + projectDir + "/.git fetch --tags";
		String[] command = cmd.split( " " );
		Process process = Runtime.getRuntime().exec( command );
		BufferedReader br = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		String k;
		while ( ( k = br.readLine() ) != null )
		{
			log.addBuildLogEntry( "push0ver - Fetch Tags:   " + k );
		}
		process.waitFor();
		// process.waitFor( 2, TimeUnit.SECONDS );
	}


	private static void parseMavenPoms(
			MyLogger log, String mvn, String pom, List<String> groupNames, List<String> moduleNames ) throws IOException
	{
		String[] command = new String[]{mvn, "dependency:tree"};
		log.addBuildLogEntry( "push0ver - RUNNING:   " + command[ 0 ] + " " + command[ 1 ] + " IN " + pom );
		Process process = Runtime.getRuntime().exec( command, null, new File( pom ) );
		BufferedReader br = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		parse( br, log, groupNames, moduleNames );
	}

	private static void parse(
			BufferedReader br, MyLogger log, List<String> groupNames, List<String> moduleNames ) throws IOException
	{
		String line;
		boolean lookingForInfo = false;
		List<String> last500 = new LinkedList<>();
		List<String> complete = new ArrayList<String>();
		int linesSinceBuildFailure = -1;
		while ( ( line = br.readLine() ) != null )
		{
			// Keep an eye out for "BUILD FAILURE" in mvn dependency:tree output,
			// and dump the output to the log if it happens.
			last500.add( line );
			while ( last500.size() > 500 )
			{
				last500.remove( 0 );
			}
			if ( linesSinceBuildFailure < 0 && line.contains( "BUILD FAILURE" ) )
			{
				linesSinceBuildFailure = 0;
			}
			else if ( linesSinceBuildFailure >= 0 )
			{
				linesSinceBuildFailure++;

				if ( linesSinceBuildFailure == 100 )
				{
					for ( String traceLine : last500 )
					{
						log.addBuildLogEntry( "push0ver - mvn:dependency output: " + traceLine );
					}
					last500.clear();
					linesSinceBuildFailure = -1;
				}
			}

			if ( line.startsWith( "[WARNING]" ) )
			{
				continue;
			}
			// log.addBuildLogEntry( "mvn dependency:tree OUTPUT: [" + line + "] lookingForInfo=" + lookingForInfo);
			if ( lookingForInfo )
			{
				if ( line.startsWith( "[INFO]" ) )
				{
					if ( line.length() > 7 && !line.contains( "--" ) )
					{
						// Need to ignore these:
						// "[INFO] artifact org.codehaus.woodstox:stax2-api: checking for updates from maven"
						// Logic:  first colon in line must come before first space.
						String infoLine = line.substring( 7 ).trim();
						int firstColon = infoLine.indexOf( ':' );
						int firstSpace = infoLine.indexOf( ' ' );
						if ( firstSpace == -1 )
						{
							firstSpace = Integer.MAX_VALUE;
						}
						if ( firstColon != -1 )
						{
							if ( firstColon < firstSpace )
							{
								// log.addBuildLogEntry( infoLine );
								complete.add( infoLine );
								lookingForInfo = false;
							}
						}
					}
				}
			}
			if ( line.contains( "maven-dependency-plugin" ) && line.contains( ":tree" ) )
			{
				lookingForInfo = true;
			}
		}

		// Did we notice "BUILD FAILURE" in mvn dependency:tree output?
		if ( linesSinceBuildFailure >= 0 )
		{
			for ( String traceLine : last500 )
			{
				log.addBuildLogEntry( "push0ver - mvn dependency:tree output: " + traceLine );
			}
		}

		for ( String b : complete )
		{
			String[] temp = b.split( ":" );
			groupNames.add( temp[ 0 ] );
			moduleNames.add( temp[ 1 ] );
			log.addBuildLogEntry( "push0ver - EXTRACTED: " + temp[ 0 ] + "." + temp[ 1 ] );
		}
	}

	private static String extractAndDeletePreStatus( String pathToPom, Tag t, MyLogger log ) throws IOException
	{
		File pretest = new File( pathToPom + t.getDirectory() + File.separator + "push0ver.windup.txt" );
		if ( pretest.exists() )
		{
			FileInputStream fin = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			StringBuilder buf = new StringBuilder();
			try
			{
				fin = new FileInputStream( pretest );
				isr = new InputStreamReader( fin, "UTF-8" );
				br = new BufferedReader( isr );
				String line;
				while ( ( line = br.readLine() ) != null )
				{
					buf.append( line ).append( '\n' );
				}
			}
			finally
			{
				Finally.close( br, isr, fin );
				boolean deleteSucceeded = pretest.delete();
				if ( !deleteSucceeded )
				{
					log.addBuildLogEntry( "push0ver - WARNING: failed to delete [" + pretest.getAbsolutePath() + "]" );
				}
			}
			return buf.toString().trim();
		}
		else
		{
			return null;
		}
	}

	private static class C
	{
		public static void main( String[] args ) throws Exception
		{
			String s = "[INFO] Scanning for projects...\n" +
					"[INFO]                                                                         \n" +
					"[INFO] ------------------------------------------------------------------------\n" +
					"[INFO] Building OpenID Connect Server Webapp 1.2.7\n" +
					"[INFO] ------------------------------------------------------------------------\n" +
					"[INFO] \n" +
					"[INFO] --- maven-dependency-plugin:2.1:tree (default-cli) @ oidc-webapp ---\n" +
					"[INFO] com.central1:oidc-webapp:war:1.2.7\n" +
					"[INFO] +- org.mitre:openid-connect-server:jar:1.2.6:compile\n" +
					"[INFO] |  +- org.mitre:openid-connect-common:jar:1.2.6:compile\n" +
					"[INFO] |  |  +- org.springframework:spring-webmvc:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  |  +- com.google.guava:guava:jar:18.0:compile\n" +
					"[INFO] |  |  +- org.apache.httpcomponents:httpclient:jar:4.3.6:compile\n" +
					"[INFO] |  |  |  +- org.apache.httpcomponents:httpcore:jar:4.3.3:compile\n" +
					"[INFO] |  |  |  \\- commons-codec:commons-codec:jar:1.6:compile\n" +
					"[INFO] |  |  +- org.springframework.security.oauth:spring-security-oauth2:jar:2.0.3.RELEASE:compile\n" +
					"[INFO] |  |  |  +- org.springframework.security:spring-security-config:jar:3.2.9.RELEASE:compile (version managed from 3.2.3.RELEASE)\n"
					+
					"[INFO] |  |  |  \\- org.codehaus.jackson:jackson-mapper-asl:jar:1.9.13:compile\n" +
					"[INFO] |  |  |     \\- org.codehaus.jackson:jackson-core-asl:jar:1.9.13:compile\n" +
					"[INFO] |  |  +- com.nimbusds:nimbus-jose-jwt:jar:4.3:compile\n" +
					"[INFO] |  |  |  +- net.jcip:jcip-annotations:jar:1.0:compile\n" +
					"[INFO] |  |  |  +- net.minidev:json-smart:jar:1.3.1:compile\n" +
					"[INFO] |  |  |  \\- commons-io:commons-io:jar:2.4:compile\n" +
					"[INFO] |  |  +- com.google.code.gson:gson:jar:2.3.1:compile\n" +
					"[INFO] |  |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.3.4:compile\n" +
					"[INFO] |  |  |  \\- com.fasterxml.jackson.core:jackson-core:jar:2.3.4:compile\n" +
					"[INFO] |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.3.4:compile\n" +
					"[INFO] |  |  \\- org.bouncycastle:bcprov-jdk15on:jar:1.56:compile\n" +
					"[INFO] |  \\- org.springframework:spring-tx:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] +- org.springframework:spring-orm:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework:spring-beans:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework:spring-core:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  \\- org.springframework:spring-jdbc:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] +- org.slf4j:jcl-over-slf4j:jar:1.7.12:compile\n" +
					"[INFO] |  \\- org.slf4j:slf4j-api:jar:1.7.12:compile\n" +
					"[INFO] +- org.slf4j:slf4j-log4j12:jar:1.7.12:runtime\n" +
					"[INFO] +- log4j:log4j:jar:1.2.15:runtime\n" +
					"[INFO] +- org.hsqldb:hsqldb:jar:2.2.9:compile\n" +
					"[INFO] +- org.eclipse.persistence:org.eclipse.persistence.jpa:jar:2.5.1:compile\n" +
					"[INFO] |  +- org.eclipse.persistence:javax.persistence:jar:2.1.0:compile\n" +
					"[INFO] |  +- org.eclipse.persistence:org.eclipse.persistence.asm:jar:2.5.1:compile\n" +
					"[INFO] |  +- org.eclipse.persistence:org.eclipse.persistence.antlr:jar:2.5.1:compile\n" +
					"[INFO] |  +- org.eclipse.persistence:org.eclipse.persistence.jpa.jpql:jar:2.5.1:compile\n" +
					"[INFO] |  \\- org.eclipse.persistence:org.eclipse.persistence.core:jar:2.5.1:compile\n" +
					"[INFO] +- org.springframework.security:spring-security-taglibs:jar:3.2.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework.security:spring-security-acl:jar:3.2.9.RELEASE:compile\n" +
					"[INFO] |  |  \\- aopalliance:aopalliance:jar:1.0:compile\n" +
					"[INFO] |  +- org.springframework.security:spring-security-core:jar:3.2.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework.security:spring-security-web:jar:3.2.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework:spring-aop:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework:spring-context:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  +- org.springframework:spring-expression:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] |  \\- org.springframework:spring-web:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] +- javax.servlet:jstl:jar:1.2:compile\n" +
					"[INFO] +- com.zaxxer:HikariCP:jar:2.4.1:compile\n" +
					"[INFO] +- junit:junit:jar:4.7:test\n" +
					"[INFO] +- org.easymock:easymock:jar:2.0:test\n" +
					"[INFO] +- org.springframework:spring-test:jar:4.1.9.RELEASE:compile\n" +
					"[INFO] +- org.mockito:mockito-all:jar:1.9.5:test\n" +
					"[INFO] +- org.slf4j:slf4j-jdk14:jar:1.7.12:test\n" +
					"[INFO] +- javax.servlet:servlet-api:jar:2.5:provided\n" +
					"[INFO] \\- javax.servlet.jsp:jsp-api:jar:2.1:provided\n" +
					"[INFO] ------------------------------------------------------------------------\n" +
					"[INFO] BUILD SUCCESS\n" +
					"[INFO] ------------------------------------------------------------------------\n" +
					"[INFO] Total time: 0.888 s\n" +
					"[INFO] Finished at: 2017-03-22T15:30:17-07:00\n" +
					"[INFO] Final Memory: 20M/1237M\n" +
					"[INFO] ------------------------------------------------------------------------\n";

			StringReader sr = new StringReader( s );
			BufferedReader br = new BufferedReader( sr );
			List<String> groups = new ArrayList<String>();
			List<String> arts = new ArrayList<String>();
			MyLogger ml = new MyLogger()
			{

				@Override
				public String addBuildLogEntry( String logLine )
				{
					System.out.println( logLine );
					return logLine;
				}
			};

			parse( br, ml, groups, arts );
			System.out.println( "GROUPS: " + groups );
			System.out.println( "ARTS: " + arts );

		}
	}

	private static boolean containsWhiteSpace( String s )
	{
		Matcher matcher = WS_PATTERN.matcher( s );
		return matcher.find();
	}

	public static boolean exists(MyLogger log, String target, String basicAuthHeader )
	{
		try
		{
			HttpURLConnection.setFollowRedirects( false );
			HttpURLConnection con = (HttpURLConnection) new URL( target ).openConnection();
			con.setRequestProperty( "Authorization", basicAuthHeader );
			con.setRequestMethod( "GET" );
			int resp = con.getResponseCode();
			int firstDigit = Integer.parseInt( Integer.toString( resp ).substring( 0, 1 ) );
			con.disconnect();
			return firstDigit != 4 && firstDigit != 5;
		}
		catch ( IOException ioe )
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			ioe.printStackTrace( pw );
			pw.flush();
			log.addBuildLogEntry( "push0ver - URL Connection Failed" );
			log.addBuildLogEntry( sw.toString() );
			try
			{
				pw.close();
				sw.close();
			}
			catch ( IOException e )
			{
				log.addBuildLogEntry( "Failed to close StringWriter: " + e );
			}
			return false;
		}
	}

	public static String basicAuthHeader( String user, String pass )
	{
		if ( user == null || pass == null )
		{
			return null;
		}
		try
		{
			byte[] bytes = ( user + ":" + pass ).getBytes( "UTF-8" );
			String base64 = DatatypeConverter.printBase64Binary( bytes );
			return "Basic " + base64;
		}
		catch ( IOException ioe )
		{
			throw new RuntimeException( "Stop the world, Java broken: " + ioe, ioe );
		}
	}
}
