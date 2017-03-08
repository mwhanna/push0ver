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
import java.util.*;

/**
 * Given a git checkout, this utility extracts the most salient tag for a given HEAD.
 *
 * The logic is compatible with traditional Maven artifact versioning and numbering schemes.
 *
 * Logic:
 *
 *   1.) If the current HEAD commit contains one or more tags, return the "largest" one.
 *       Note:  1.11 is considered larger than 1.9, and 0.0.1 is considered larger than 999.999.999-SNAPSHOT.
 *
 *   2.) If the current HEAD does not contain any tags, go back in history to find the most
 *       recent tag.  If the most recent tag is a "*-SNAPSHOT" tag, return that.  If
 *       the most recent tag is a regular release tag (e.g., no occurrence of the word "SNAPSHOT"),
 *       then return null.
 */
public class TagExtractor
{
	public static void main( String[] args ) throws IOException
	{
		final StringBuilder logBuf = new StringBuilder(5000);
	    MyLogger logger = new MyLogger() {
			@Override
			public String addBuildLogEntry(String logLine) {
				logBuf.append(logLine).append('\n');
				return logLine;
			}
		};

	    // Travel up until we find the ".git" dir.
	    File projectDir = new File(".").getCanonicalFile();
	    File gitDir = new File(projectDir + "/.git");
	    while (!gitDir.exists()) {
	        projectDir = projectDir.getParentFile();
	        gitDir = new File(projectDir + "/.git");
        }

		String tag = getTag(projectDir.getAbsolutePath(), false, logger, null );

		if ( tag != null )
		{
			System.out.println( tag );
		}
		else
		{
			System.out.println( "NO TAG" );
			System.out.println(logBuf.toString());
		}
	}

	public static String getTag( String projectDir, boolean releaseExists, MyLogger log, String[] badTag )
	{
		// Use " git symbolic-ref --short HEAD " to get current branch.
		// Including "current branch" in log messages (especially error messages) makes them more useful.
		String cmd = "git --no-pager --git-dir=" + projectDir + "/.git symbolic-ref --short HEAD ";
		String[] command = cmd.split( " " );

		String currentBranch;
		Process process = null;
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try
		{
			process = Runtime.getRuntime().exec( command );
			in = process.getInputStream();
			isr = new InputStreamReader( in, "UTF-8" );
			br = new BufferedReader( isr );
			currentBranch = br.readLine();
		}
		catch ( IOException ioe )
		{
			// No luck!
			final String msg = "push0ver - TagExtractor Failed to run 'git symbolic-ref --short HEAD' command: " + ioe.toString();
			if ( log != null )
			{
				log.addBuildLogEntry( msg );
			}
			else
			{
				System.err.println( msg );
			}
			return null;
		}
		finally
		{
			Finally.close( br, isr, in, process );
		}

		// Get last 1000 commits to scan for possible release and snapshot tags:
		cmd = "git --no-pager --git-dir=" + projectDir + "/.git log --pretty=%d --first-parent --max-count=1000";
		command = cmd.split( " " );
		try
		{
			process = Runtime.getRuntime().exec( command );
			in = process.getInputStream();
			isr = new InputStreamReader( in, "UTF-8" );
			return extractTag( isr, releaseExists, log, badTag, currentBranch );
		}
		catch ( IOException ioe )
		{
			// No luck!
			final String msg = "push0ver - TagExtractor Failed to run 'git log' command: " + ioe.toString();
			if ( log != null )
			{
				log.addBuildLogEntry( msg );
			}
			else
			{
				System.err.println( msg );
			}
			return null;
		}
		finally
		{
			Finally.close( isr, in, process );
		}
	}

	static String extractTagTestLogic( Reader r )
	{
		return extractTag( r, false, null, null, "master" );
	}

	static String extractTag( Reader r, boolean releaseExists, MyLogger log, String[] badTag, String currentBranch )
	{
		Set<String> alreadyLoggedInvalids = new TreeSet<String>();
		BufferedReader br = null;
		String tag = null;
		try
		{
			br = r instanceof BufferedReader ? (BufferedReader) r : new BufferedReader( r );
			String line = br.readLine();

			// Is there a release in current tag (first line of "git log" command")?
			final String releaseTag = extractTag( line, TagMode.RELEASE, log, alreadyLoggedInvalids );
			tag = releaseTag;

			// Special "release exists" logic if snapshot tag on same commit AND release exists in artifactory.
			if ( releaseTag != null && releaseExists )
			{
				String snapshot = extractTag( line, TagMode.SNAPSHOT, null, alreadyLoggedInvalids );
				return snapshotIfLarger( releaseTag, snapshot );
			}

			// Back to regular logic:  is snapshot on same commit?  (unknown if release in artifactory)
			if ( tag == null )
			{
				// okay, what about a SNAPSHOT?
				tag = extractTag( line, TagMode.SNAPSHOT, log, alreadyLoggedInvalids );
			}

			// No release in current commit, so let's go back in history and look for
			// a SNAPSHOT tag.
			int lineCount = 1;
			while ( tag == null && ( line = br.readLine() ) != null )
			{
				lineCount++;
				// First check if a release tag is there:
				tag = extractTag( line, TagMode.RELEASE, log, alreadyLoggedInvalids );
				if ( tag != null )
				{
					// Release tags after the 0th position kills our search for a "SNAPSHOT" tag.
					// But if this tag happens to have SNAPSHOT, we can return that:
					String snapshot = extractTag( line, TagMode.SNAPSHOT, log, alreadyLoggedInvalids );
					String snapshotIfLarger = snapshotIfLarger( tag, snapshot );
					if ( snapshot == null || snapshotIfLarger == null )
					{
						int staleCount = lineCount - 1;
						if ( log != null )
						{
							String commits = staleCount == 1 ? "commit" : "commits";
							log.addBuildLogEntry( "push0ver - EXTRACTED A STALE RELEASE TAG: " + tag + " is " + staleCount
									+ " " + commits + " stale." );
							log.addBuildLogEntry( "To see for yourself:" );
							log.addBuildLogEntry( "  git fetch" );
							log.addBuildLogEntry( "  git log --first-parent --pretty='%h | %ci | %d' -" + lineCount + " origin/" + currentBranch );
							log.addBuildLogEntry(
									"push0ver - NOTE: stale SNAPSHOT tags (e.g., 'x.y.z-SNAPSHOT') are fine as long as they appear AFTER the most recent release tag." );
						}
						if ( badTag != null && badTag.length > 0 )
						{
							// e.g. "1.2.3-PUSH0VER-22" to indicate a 22-commits stale version.
							badTag[ 0 ] = tag + "-PUSH0VER-" + staleCount;
						}
						return null;
					}
					return snapshotIfLarger;
				}

				// If release tag didn't kill our search, look for SNAPSHOT tag:
				tag = extractTag( line, TagMode.SNAPSHOT, log, alreadyLoggedInvalids );
			}

		}
		catch ( IOException ioe )
		{
			// No luck!
			System.err.println( "TagExtractor Failed readline(): " + ioe.toString() );
			return null;
		}
		finally
		{
			Finally.close( br );
		}

		if ( tag == null )
		{
			if ( log != null )
			{
				log.addBuildLogEntry( "push0ver - Unable to set version in pom.xml. No valid tags found in last 1,000 commits." );
				log.addBuildLogEntry( "push0ver - To see for yourself:" );
				log.addBuildLogEntry( "  git fetch" );
				log.addBuildLogEntry( "  git log --first-parent --pretty='%h | %ci | %d' -1000 origin/" + currentBranch );
			}
		}
		return tag;
	}

	private static String snapshotIfLarger( String tag, String snapshot )
	{
		// Non-null is larger:
		if ( snapshot == null )
		{
			return tag;
		}
		if ( tag == null )
		{
			return snapshot;
		}

		// Okay, both not null.  Examine them properly:
		Version v1 = new Version( tag );
		Version v2 = new Version( snapshot );
		if ( v2.compareTo( v1 ) > 0 )
		{
			// SNAPSHOT must be larger.
			// (But not just larger because it ends in "-SNAPSHOT"!)
			if ( snapshot.equals( tag + "-SNAPSHOT" ) )
			{
				return null;
			}
			else
			{
				return snapshot;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * We're only interested in tags where the 1st character of the tag is a digit.
	 *
	 * @param s tag to examine
	 * @return true if the tag is valid (1st character is a digit).
	 */
	private static boolean isValidTag( String s )
	{
		s = s != null ? s.trim() : "";
		char c = s.length() > 0 ? s.charAt( 0 ) : 0;
		return c >= '0' && c <= '9'; // Character.isDigit() is too complicated.
	}

	private enum TagMode
	{
		ALL,
		RELEASE,
		SNAPSHOT
	}

	private static String extractTag(
			String tagString, TagMode mode, MyLogger log, Set<String> alreadyLoggedInvalids )
	{
		tagString = tagString != null ? tagString.trim() : "";
		if ( tagString.length() >= 2 )
		{
			if ( tagString.charAt( 0 ) == '(' )
			{
				tagString = tagString.substring( 1 );
			}
			if ( tagString.charAt( tagString.length() - 1 ) == ')' )
			{
				tagString = tagString.substring( 0, tagString.length() - 1 );
			}
		}
		tagString = tagString.trim();

		String[] toks = tagString.split( ", " );
		List<Version> versions = new ArrayList<Version>();
		for ( String t : toks )
		{
			if ( t.contains( "tag: " ) )
			{
				t = t.substring( 5 );
				boolean isValid = isValidTag( t );
				if ( !isValid && !alreadyLoggedInvalids.contains( t ) )
				{
					if ( log != null )
					{
						log.addBuildLogEntry( "push0ver - IGNORING TAG \"" + t
								+ "\" since the 1st character is not a digit." );
						alreadyLoggedInvalids.add( t );
					}
				}
				if ( isValid )
				{
					Version v = new Version( t );
					boolean containsSnapshot = t.toUpperCase( Locale.ENGLISH ).contains( "-SNAPSHOT" );
					switch ( mode )
					{
						case ALL:
							versions.add( v );
							break;
						case SNAPSHOT:
							if ( containsSnapshot )
							{
								versions.add( v );
							}
							break;
						case RELEASE:
							if ( !containsSnapshot )
							{
								versions.add( v );
							}
					}
				}
			}
		}
		if ( !versions.isEmpty() )
		{
			// Return "biggest" tag !
			Collections.sort( versions, Collections.reverseOrder() );
			return versions.get( 0 ).toString();
		}
		else
		{
			return null;
		}
	}
}
