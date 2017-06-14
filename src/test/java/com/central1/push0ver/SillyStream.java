package com.central1.push0ver;

import java.io.ByteArrayInputStream;

public class SillyStream extends ByteArrayInputStream
{
	private int[] sillyReads;
	private int readCount;

	public SillyStream( byte buf[], int... sillyReads )
	{
		super( buf );
		this.sillyReads = sillyReads;
	}

	public int read( byte b[], int off, int len )
	{
		int c;
		if ( readCount < sillyReads.length && sillyReads[ readCount ] >= 0 )
		{
			int toRead = Math.min( len, sillyReads[ readCount ] );
			c = super.read( b, off, toRead );
		}
		else
		{
			c = super.read( b, off, len );
		}
		readCount++;
		return c;
	}

}
