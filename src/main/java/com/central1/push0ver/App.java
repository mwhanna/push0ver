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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

public class App
{
	private static final Pattern WS_PATTERN = Pattern.compile( "\\s" );

	public static void main( String[] args ) throws Exception
	{
		invoke( args, System.getProperties(), logLine -> {
			System.out.println( logLine );
			return logLine;
		} );
	}

	public static void invoke( final String[] args, final Properties p, final MyLogger log ) throws Exception
	{
		String pathToPom = args.length > 0 ? args[ 0 ] : null;
		pathToPom = pathToPom != null ? pathToPom.trim() : "";
		if ( "".equals( pathToPom ) )
		{
			pathToPom = ".";
		}

		// Need to check "push0ver.windup.txt" early because we MUST delete this file if it exists.
		// Otherwise we risk polluting a subsequent run if this file is accidentally left behind.
		String windupStatus = null;
		File pretest = new File( pathToPom + "/push0ver.windup.txt" );
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
			windupStatus = buf.toString().trim();
		}

		String userName = null;
		String userPassword = null;
		boolean doSomething = true;

		final boolean sslTrustAll = "true".equalsIgnoreCase( p.getProperty( "ssl.trustAll" ) );
		final boolean doPush = args.length > 1 && "push".equalsIgnoreCase( args[ 1 ] );
		String repoName = null;
		String snapRepo = null;
		String url = null;
		String userHome = System.getProperty( "user.home" );
		if ( p.getProperty( "repo.name" ) != null )
		{
			repoName = p.getProperty( "repo.name" );
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

		fetchTags( log, gitTarget );
		String tag = TagExtractor.getTag( gitTarget, false, log, null );
		if ( tag == null )
		{
			log.addBuildLogEntry( "push0ver - ABORT: unable to extract valid tag." );
			return;
		}

		log.addBuildLogEntry( "push0ver - EXTRACTED TAG:       " + tag );
		final String basicAuthHeader = basicAuthHeader( userName, userPassword );

		if ( tag.contains( "SNAPSHOT" ) )
		{
			if ( snapRepo == null || "".equals( snapRepo ) )
			{
				log.addBuildLogEntry( "push0ver - No Global or Local Repo set for SNAPSHOT, using RELEASE Repo." );
			}
			else
			{
				repoName = snapRepo;
			}
		}

		Struct struct = checkIfAlreadyReleased(
				tag, log, mvnCommand, pathToPom, basicAuthHeader, url, gitTarget, repoName );
		if ( struct == null )
		{
			return;
		}
		tag = struct.tag;

		String search = "0.0.0.0.0-SNAPSHOT";
		// If windupStatus ==  null, then push0ver-windup did not run, so no need to check its status.
		if ( windupStatus != null )
		{
			if ( !"pre=valid".equals( windupStatus ) )
			{
				log.addBuildLogEntry( "push0ver - Windup was run! ABORTING: no valid tag found: [" + windupStatus + "]" );
				return;
			}
			log.addBuildLogEntry( "push0ver - Windup was run!" );
			search = tag;
		}

		log.addBuildLogEntry( "push0ver - Will Execute:  " + String.valueOf( doSomething ) );

		for ( int z = 0; z < struct.moduleNames.size(); z++ )
		{
			if ( doSomething )
			{
				String m = struct.moduleNames.get( z );
				String g = struct.groupNames.get( z );
				g = g.replace( '.', '/' );
				String target = userHome + "/.m2/repository/" + g + "/" + m + "/" + search;
				Rename r = new Rename( pathToPom, m, g, tag, repoName, basicAuthHeader, url, log, sslTrustAll );
				try
				{
					r.renameJars( search, tag, target, doPush );
				}
				finally
				{
					if ( doPush )
					{
						if ( !containsWhiteSpace( pathToPom ) )
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

	public static class Struct
	{
		public String tag;
		public final List<String> groupNames = new ArrayList<>();
		public final List<String> moduleNames = new ArrayList<>();
	}

	public static Struct checkIfAlreadyReleased(
			String tag, MyLogger log, String mvnCommand, String pathToPom, String basicAuthHeader, String artUrl,
			String gitTarget, String repoName ) throws IOException
	{
		Struct s = new Struct();
		parseMavenPoms( log, mvnCommand, pathToPom, s.groupNames, s.moduleNames );

		String group = s.groupNames.get( 0 ).replace( '.', '/' );
		String checkTarget = artUrl + repoName + "/" + group + "/" + s.moduleNames.get( 0 ) + "/" + tag;
		if ( !tag.contains( "-SNAPSHOT" ) )
		{
			final String releaseTag = tag;
			if ( exists( log, checkTarget, basicAuthHeader ) )
			{
				tag = TagExtractor.getTag( gitTarget, true, log, null );
				if ( tag == null || releaseTag.equals( tag ) )
				{
					log.addBuildLogEntry( "push0ver - RELEASE " + releaseTag + " ALREADY PUBLISHED TO ARTIFACTORY! (" + checkTarget + ")" );
					return null;
				}
				log.addBuildLogEntry( "push0ver - RELEASE " + releaseTag + " ALREADY PUBLISHED TO ARTIFACTORY! SWITCHING TAG TO: " + tag );
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
		process.waitFor( 2, TimeUnit.SECONDS );
	}

	private static void parseMavenPoms(
			MyLogger log, String mvn, String pom, List<String> groupNames, List<String> moduleNames ) throws IOException
	{
		String[] command = new String[]{mvn, "dependency:tree"};
		log.addBuildLogEntry( "push0ver - RUNNING:   " + command[ 0 ] + " " + command[ 1 ] + " IN " + pom );
		Process process = Runtime.getRuntime().exec( command, null, new File( pom ) );
		BufferedReader br = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		String line;
		boolean lookingForInfo = false;
		List<String> complete = new ArrayList<>();
		while ( ( line = br.readLine() ) != null )
		{
			if ( line.startsWith( "[WARNING]" ) )
			{
				continue;
			}
			if ( lookingForInfo )
			{
				if ( line.startsWith( "[INFO]" ) )
				{
					if ( !line.contains( "--" ) )
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
								log.addBuildLogEntry( infoLine );
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
		for ( String b : complete )
		{
			String[] temp = b.split( ":" );
			groupNames.add( temp[ 0 ] );
			moduleNames.add( temp[ 1 ] );
			log.addBuildLogEntry( "push0ver - EXTRACTED: " + temp[ 0 ] + "." + temp[ 1 ] );
		}
	}

	private static boolean containsWhiteSpace( String s )
	{
		Matcher matcher = WS_PATTERN.matcher( s );
		return matcher.find();
	}

	public static boolean exists( MyLogger log, String target, String basicAuthHeader )
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
			ioe.printStackTrace( System.out );
			log.addBuildLogEntry( "push0ver - URL Connection Failed:" );
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
