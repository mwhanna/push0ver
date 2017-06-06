package com.central1.push0ver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Locale;

public class FileUtil
{

	// Can't have the SENTINEL appear in source or bytecode here!
	// (else push0ver might replace it!)
	private final static String SENTINEL = "0".substring( 0, 1 ) + ".0.0.0.0-SNAPSHOT";

	public static void injectTagRecursive( File root, String tag, MyLogger log )
	{
		if ( root != null )
		{
			String name = root.getName();
			if ( "..".equals( name ) )
			{
				return;
			}

			Path rootPath = Paths.get( root.getAbsolutePath() );
			try
			{

				Files.walk( rootPath )
						.sorted( Comparator.reverseOrder() )
						.map( Path::toFile )
						.forEach( f -> {
							if ( f.isFile() && f.canRead() )
							{
								String p = f.getPath();
								String n = f.getName().toLowerCase( Locale.ENGLISH );
								if ( p.contains( "/node_modules/" ) || p.contains( "/.git/" ) || n.endsWith( ".class" ) )
								{
									// Don't edit *.class files, it just corrupts them.
									// Don't edit anything under ".git/"
									// Don't edit anything under "node_modules/"
									return;
								}

								try
								{
									injectTag( f, tag, log );
								}
								catch ( IOException ioe )
								{
									throw new RuntimeException( "Failed to inject tag [" + tag + "] in file [" + f.getAbsolutePath() + "]" );
								}
							}
						} );
			}
			catch ( IOException ioe )
			{
				throw new RuntimeException( "Files.walk() failed for [" + root.getAbsolutePath() + "] because " + ioe );
			}
		}
	}

	public static void injectTag( File f, String tag, MyLogger log ) throws IOException
	{
		long millis = System.currentTimeMillis() % 1000;
		File outputFile = new File( f + ".push0ver" + millis + ".tmp" );
		boolean deleteSuccess = true;
		long size = f.length();
		FileInputStream in = new FileInputStream( f );
		if ( size < 1000000 )
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( (int) size );
			boolean foundMatch = BinarySed.replaceAll( f, SENTINEL, tag, in, out );
			if ( foundMatch )
			{
				FileOutputStream fout = null;
				try
				{
					fout = new FileOutputStream( f );
					fout.write( out.toByteArray() );
					fout.flush();
				}
				finally
				{
					if ( fout != null )
					{
						fout.close();
						log.addBuildLogEntry( "Injected tag [" + tag + "] into file [" + f + "] (in-memory)" );
					}
				}
			}
		}
		else
		{
			try
			{
				FileOutputStream out = new FileOutputStream( outputFile );
				boolean foundMatch = BinarySed.replaceAll( f, SENTINEL, tag, in, out );
				if ( foundMatch )
				{
					if ( !outputFile.renameTo( f ) )
					{
						throw new RuntimeException( "Failed to move [" + outputFile.getName() + "] to [" + f.getName() + "]." );
					}
					log.addBuildLogEntry( "Injected tag [" + tag + "] into file [" + f + "] (via tmp-file)" );
				}
			}
			finally
			{
				if ( outputFile.exists() )
				{
					deleteSuccess = outputFile.delete();
				}
			}
		}
		if ( !deleteSuccess )
		{
			throw new RuntimeException( "Failed to delete [" + outputFile.getName() + "]." );
		}
	}

	public static void main( String[] args ) throws Exception
	{
		final StringBuilder logBuf = new StringBuilder( 5000 );
		MyLogger logger = new MyLogger()
		{
			@Override
			public String addBuildLogEntry( String logLine )
			{
				logBuf.append( logLine ).append( '\n' );
				return logLine;
			}
		};

		File f = new File( args[ 0 ] );
		injectTagRecursive( f, "1.2.3.4.5-TIGERCAT", logger );
		System.out.println( logBuf.toString() );

	}

}
