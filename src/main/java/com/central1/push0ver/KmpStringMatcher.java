/*
 * Knuth-Morris-Pratt string matcher (Java)
 *
 * Copyright (c) 2016 Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/knuth-morris-pratt-string-matching
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package com.central1.push0ver;

import java.io.IOException;

public final class KmpStringMatcher
{

	private final byte[] pattern;
	private final int[] lsp;  // Longest suffix-prefix table

	public KmpStringMatcher( String patt ) throws IOException
	{
		if ( patt == null )
		{
			throw new NullPointerException();
		}
		pattern = patt.getBytes( "UTF-8" );

		// Compute longest suffix-prefix table
		lsp = new int[ pattern.length ];
		if ( lsp.length > 0 )
		{
			lsp[ 0 ] = 0;  // Base case
		}
		for ( int i = 1; i < pattern.length; i++ )
		{
			int j = lsp[ i - 1 ];  // Start by assuming we're extending the previous LSP
			while ( j > 0 && pattern[ i ] != pattern[ j ] )
			{
				j = lsp[ j - 1 ];
			}
			if ( pattern[ i ] == pattern[ j ] )
			{
				j++;
			}
			lsp[ i ] = j;
		}
	}

	public int search( byte[] text )
	{
		if ( text == null )
		{
			throw new NullPointerException();
		}
		if ( pattern.length == 0 )
		{
			return 0;
		}

		// Walk through text string
		int j = 0;  // Number of chars matched in pattern
		for ( int i = 0; i < text.length; i++ )
		{
			while ( j > 0 && text[ i ] != pattern[ j ] )
			{
				j = lsp[ j - 1 ];  // Fall back in the pattern
			}
			if ( text[ i ] == pattern[ j ] )
			{
				j++;  // Next char matched, increment position
				if ( j == pattern.length )
				{
					return i - ( j - 1 );
				}
			}
		}
		return -1;  // Not found
	}

}