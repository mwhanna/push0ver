package com.central1.push0ver;

import java.io.*;

public class BinarySed
{
	public static boolean replaceAll(
			File f, String find, String replace, InputStream in, OutputStream out ) throws IOException
	{
		final byte[] buf = new byte[ 5000 ];
		final byte[] findBytes = find.getBytes( "UTF-8" );
		final byte[] replaceBytes = replace.getBytes( "UTF-8" );
		final KmpStringMatcher kmp = new KmpStringMatcher( find );
		boolean foundMatch = false;
		try
		{
			// Match not possible if file is smaller than sentinel.
			if ( f != null && f.length() < findBytes.length )
			{
				return false;
			}

			int pos = 0;
			int c;
			int savedFromLastTime = 0;
			while ( ( c = in.read( buf, pos, buf.length - pos ) ) >= 0 )
			{
				if ( c == 0 )
				{
					continue;
				}

				int x = kmp.search( buf );
				if ( x >= 0 && x <= c + savedFromLastTime - findBytes.length )
				{
					foundMatch = true;
					out.write( buf, 0, x );
					out.write( replaceBytes );
					pos = c - x - findBytes.length + savedFromLastTime;
					System.arraycopy( buf, x + findBytes.length, buf, 0, pos );
					savedFromLastTime = pos;
				}
				else
				{
					if ( c - findBytes.length + savedFromLastTime > 0 )
					{
						out.write( buf, 0, c - findBytes.length + savedFromLastTime );
						pos = findBytes.length;
						System.arraycopy( buf, c - findBytes.length + savedFromLastTime, buf, 0, pos );
						savedFromLastTime = pos;
					}
					else
					{
						pos = c + savedFromLastTime;
						savedFromLastTime = pos;
					}
				}
			}

			while ( savedFromLastTime >= findBytes.length )
			{
				int x = kmp.search( buf );
				if ( x < 0 || x > savedFromLastTime - findBytes.length )
				{
					break;
				}

				// Finished reading from InputStream...
				foundMatch = true;
				out.write( buf, 0, x );
				out.write( replaceBytes );
				savedFromLastTime -= ( x + findBytes.length );
				System.arraycopy( buf, x + findBytes.length, buf, 0, savedFromLastTime );
			}
			if ( savedFromLastTime > 0 )
			{
				out.write( buf, 0, savedFromLastTime );
			}
		}
		finally
		{
			if ( in != null )
			{
				in.close();
			}
			if ( out != null )
			{
				out.flush();
				out.close();
			}
		}
		return foundMatch;
	}

	public static void main( String[] args ) throws Exception
	{
		FileInputStream in = new FileInputStream( args[ 0 ] );
		FileOutputStream out = new FileOutputStream( args[ 0 ] + ".out" );
		String find = "0".substring( 0, 1 ) + ".0.0.0.0-SNAPSHOT";
		String replace = "1.2.3";
		replaceAll( new File( args[ 0 ] ), find, replace, in, out );
	}

}