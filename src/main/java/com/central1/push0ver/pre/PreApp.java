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

package com.central1.push0ver.pre;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import com.central1.push0ver.App;
import com.central1.push0ver.FileUtil;
import com.central1.push0ver.MyLogger;
import com.central1.push0ver.TagExtractor;

public class PreApp
{
	public static void main( String[] args ) throws Exception
	{
		invoke( args, System.getProperties(), new MyLogger()
		{
			@Override
			public String addBuildLogEntry( String logLine )
			{
				System.out.println( logLine );
				return logLine;
			}
		} );
	}

	public static void invoke( String[] args, Properties p, MyLogger log ) throws Exception
	{
		String userName = null;
		String userPassword = null;
		String repoName = null;
		String pathToPom = args.length > 0 ? args[ 0 ] : null;
		pathToPom = pathToPom != null ? pathToPom.trim() : "";
		if ( "".equals( pathToPom ) )
		{
			pathToPom = ".";
		}
		File parentPomDir = new File( pathToPom );

		if ( p.getProperty( "repo.name" ) != null )
		{
			repoName = p.getProperty( "repo.name" );
		}
		else
		{
			log.addBuildLogEntry( "push0ver - You forgot to enter a Repository name! (-Drepo.name=x)" );
		}

		if ( p.getProperty( "art.username" ) != null )
		{
			userName = p.getProperty( "art.username" );
		}
		if ( p.getProperty( "art.password" ) != null )
		{
			userPassword = p.getProperty( "art.password" );
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

		final String basicAuthHeader = App.basicAuthHeader( userName, userPassword );
		String url = p.getProperty( "art.url" );
		url = url != null ? url.trim() : "";
		if ( !url.endsWith( "/" ) )
		{
			url = url + "/";
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

		File n = new File( pathToPom + "/push0ver.windup.txt" );
		FileWriter pref = new FileWriter( n );
		String[] badTag = new String[]{null};
		App.fetchTags( log, gitTarget );
		String tag = TagExtractor.getTag( gitTarget, false, log, badTag );
		try
		{
			if ( tag != null )
			{
				// Switch to SNAPSHOT if appropriate:
				App.Struct struct = App.checkIfAlreadyReleased(
						tag, log, mvnCommand, pathToPom, basicAuthHeader, url, gitTarget, repoName );
				if ( struct != null )
				{
					tag = struct.tag;
					log.addBuildLogEntry( "push0ver - WINDUP EXTRACTED VALID TAG: " + tag );
					pref.write( "pre=valid\n" );
				}
				else
				{
					log.addBuildLogEntry( "push0ver - WINDUP EXTRACTED ALREADY RELEASED TAG: " + tag );
					pref.write( "pre=released\n" );
				}
			}
			else if ( badTag[ 0 ] != null )
			{
				tag = badTag[ 0 ];
				log.addBuildLogEntry( "push0ver - WINDUP EXTRACTED STALE TAG: " + tag );
				pref.write( "pre=stale\n" );
			}
			else
			{
				return;
			}
		}
		finally
		{
			pref.close();
		}

		// Replace the SENTINEL with the TAG !
		FileUtil.injectTagRecursive( parentPomDir, tag, log );
	}
}
