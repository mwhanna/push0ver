package com.central1.push0ver.pre;

import java.io.*;

import com.central1.push0ver.KmpStringMatcher;

public class BinarySed
{
	public static void sed( String find, String replace, InputStream in, OutputStream out ) throws IOException
	{
		KmpStringMatcher kmp = new KmpStringMatcher( find );
		byte[] buf = new byte[ 10000 ];
		int pos = 0;
		int len = buf.length;
		byte[] findBytes = find.getBytes( "UTF-8" );
		byte[] replaceBytes = replace.getBytes( "UTF-8" );
		int c;
		int savedFromLastTime = 0;
		while ( ( c = in.read( buf, pos, len ) ) >= 0 )
		{
			int x = kmp.search( buf );
			if ( x >= 0 )
			{
				out.write( buf, 0, x );
				out.write( replaceBytes );
				System.arraycopy( buf, x + findBytes.length, buf, 0, c - x - findBytes.length + savedFromLastTime );
				pos = c - x - findBytes.length + savedFromLastTime;
				savedFromLastTime = pos;
			}
			else
			{
				out.write( buf, 0, c - findBytes.length + savedFromLastTime );
				System.arraycopy( buf, c - findBytes.length + savedFromLastTime, buf, 0, findBytes.length );
				pos = findBytes.length;
				savedFromLastTime = pos;
			}
			len = buf.length - pos;
		}

		int x;
		while ( ( x = kmp.search( buf ) ) >= 0 && savedFromLastTime >= findBytes.length )
		{
			c = 0;
			out.write( buf, 0, x );
			out.write( replaceBytes );
			if ( savedFromLastTime > x + findBytes.length && x + findBytes.length < buf.length )
			{
				System.arraycopy( buf, x + findBytes.length, buf, 0, c - x - findBytes.length + savedFromLastTime );
				pos = c - x - findBytes.length + savedFromLastTime;
				savedFromLastTime = pos;
			}
			else
			{
				if ( savedFromLastTime > x + findBytes.length )
				{
					out.write( buf, x + findBytes.length, savedFromLastTime - x - findBytes.length );
				}
				savedFromLastTime = 0;
			}
		}
		if ( savedFromLastTime > 0 )
		{
			out.write( buf, 0, savedFromLastTime );
		}
	}

	public static void main( String[] args ) throws Exception
	{
		FileInputStream in = new FileInputStream( args[ 0 ] );
		FileOutputStream out = new FileOutputStream( args[ 0 ] + ".out" );
		String find = "0.0.0.0.0-SNAPSHOT";
		String replace = "1.2.3";
		sed( find, replace, in, out );
	}

}
