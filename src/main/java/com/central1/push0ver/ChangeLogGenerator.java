package com.central1.push0ver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ChangeLogGenerator
{

	public static void main( String[] args ) throws Exception
	{
		File f = new File( args[ 0 ] );
		String changeLog = generate( f );
		System.out.println(changeLog);
	}

	public static String generate( File dir ) throws IOException
	{
		dir = dir.getCanonicalFile();
		if ( dir.isFile() )
		{
			dir = dir.getParentFile();
		}

		Map<String, String> tags = log( dir, false );
		Map<String, String> msgs = log( dir, true );

		StringBuilder buf = new StringBuilder();
		boolean prevWasTag = true;
		for ( Map.Entry<String, String> entry : tags.entrySet() )
		{
			String hash = entry.getKey();
			String tagString = entry.getValue();
			if ( !"".equals( tagString ) )
			{
				buf.append( "    " ).append( tagString ).append( '\n' );
				prevWasTag = true;
			}
			String msg = msgs.get( hash );
			if ( msg != null )
			{
				if (prevWasTag) {
					buf.append('\n');
				}
				buf.append( hash.substring( 0, 10 ) ).append( " " ).append( msg ).append( "\n\n" );
				prevWasTag = false;
			}
		}

		return buf.toString();
	}

	private static File gitDir( File d )
	{
		String dir = d.getAbsolutePath() + "/.git";
		File gitDir = new File( dir );

		if ( gitDir.exists() )
		{
			return gitDir;
		}
		else
		{
			File parent = d.getParentFile();

			if ( parent == null )
			{
				return null;
			}
			else
			{
				return gitDir( parent );
			}
		}
	}

	private static LinkedHashMap<String, String> log( File d, boolean applyDirFilter ) throws IOException
	{
		LinkedHashMap<String, String> m = new LinkedHashMap<>();

		File gitDir = gitDir( d );
		if ( gitDir == null )
		{
			throw new IllegalArgumentException( "Hit / without finding a .git dir: " + d.getAbsolutePath() );
		}

		String gitDirPath = gitDir.getAbsolutePath();

		List<String> cmdArgs = new ArrayList<>();
		cmdArgs.add( "git" );
		cmdArgs.add( "--no-pager" );
		cmdArgs.add( "--git-dir=" + gitDirPath );
		cmdArgs.add( "log" );
		if ( applyDirFilter )
		{
			cmdArgs.add( "--pretty=%H %s" );
		}
		else
		{
			cmdArgs.add( "--pretty=%H %d" );
		}
		cmdArgs.add( "--date-order" );
		cmdArgs.add( "-2000" );

		if ( applyDirFilter )
		{
			String rootPath = gitDir.getParentFile().getAbsolutePath();
			String subDirPath = d.getAbsolutePath();
			if ( subDirPath.startsWith( rootPath ) )
			{
				subDirPath = subDirPath.substring( rootPath.length() );
				if ( subDirPath.startsWith( "/" ) || subDirPath.startsWith( "\\" ) )
				{
					if ( subDirPath.length() > 0 )
					{
						subDirPath = subDirPath.substring( 1 );
					}
				}
			}

			cmdArgs.add( "--no-merges" );
			cmdArgs.add( "--" );
			cmdArgs.add( subDirPath );
		}

		String[] cmd = cmdArgs.toArray( new String[ cmdArgs.size() ] );

		Process process = Runtime.getRuntime().exec( cmd );
		BufferedReader br = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		String line;
		while ( ( line = br.readLine() ) != null )
		{
			int x = line.indexOf( ' ' );
			String hash = null;
			if ( x >= 0 )
			{
				hash = line.substring( 0, x ).trim();
				line = line.substring( x + 1 ).trim();
			}
			if ( applyDirFilter )
			{
				m.put( hash, line );
			}
			else
			{
				m.put( hash, "" );

				x = line.indexOf( '(' );
				int y = line.lastIndexOf( ')' );
				if ( x >= 0 && y >= x )
				{
					String decorations = line.substring( x + 1, y );

					TreeSet<String> tags = new TreeSet<>();
					String[] toks = decorations.split( ", " );
					for ( String t : toks )
					{
						if ( t.startsWith( "tag: " ) )
						{
							tags.add( t.substring( 5 ) );
						}
					}

					if ( !tags.isEmpty() )
					{
						StringBuilder buf = new StringBuilder();
						for ( String tag : tags )
						{
							buf.append( tag ).append( ", " );
						}

						buf.deleteCharAt( buf.length() - 1 );
						buf.deleteCharAt( buf.length() - 1 );
						m.put( hash, buf.toString().trim() );
					}
				}
			}

		}
		try
		{
			process.waitFor();
		}
		catch ( InterruptedException ie )
		{
			Thread.currentThread().interrupt();
		}
		return m;
	}

}
