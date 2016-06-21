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

public class Version implements Comparable<Version>
{
	final static boolean[] IS_DIGIT = new boolean[ '9' + 1 ];
	static
	{
		IS_DIGIT[ '0' ] = true;
		IS_DIGIT[ '1' ] = true;
		IS_DIGIT[ '2' ] = true;
		IS_DIGIT[ '3' ] = true;
		IS_DIGIT[ '4' ] = true;
		IS_DIGIT[ '5' ] = true;
		IS_DIGIT[ '6' ] = true;
		IS_DIGIT[ '7' ] = true;
		IS_DIGIT[ '8' ] = true;
		IS_DIGIT[ '9' ] = true;
	}

	public final String[] version;

	public Version( String version )
	{
		if ( version == null )
		{
			throw new NullPointerException( "Cannot construct Version with null" );
		}
		this.version = version.trim().split( "\\." );
	}

	public String toString()
	{
		// reassemble
		String s = "";
		for ( String v : version )
		{
			s += v + ".";
		}
		return s.substring( 0, s.length() - 1 ); // omit final "."
	}

	@Override
	public boolean equals( Object obj )
	{
		return obj instanceof Version && this.compareTo( (Version) obj ) == 0;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	private static int onePastLastDigit( String s )
	{
		int x = -1;
		if ( s != null )
		{
			x = s.length();
		}
		if ( s != null && !"".equals( s ) && !"-".equals( s ) )
		{
			if ( s.startsWith( "-" ) )
			{
				s = s.substring( 1 );
			}
			for ( int i = 0; i < s.length(); i++ )
			{
				char c = s.charAt( i );
				boolean isDigit = c < IS_DIGIT.length && IS_DIGIT[ c ];
				if ( !isDigit )
				{
					return i;
				}
			}
		}
		return x;
	}

	public int compareTo( Version otherVersion )
	{
		String[] other = otherVersion.version;
		int c;
		for ( int i = 0; i < Math.min( version.length, other.length ); i++ )
		{
			String s1 = version[ i ];
			String s2 = other[ i ];
			Long v1 = toLong( s1 );
			Long v2 = toLong( s2 );
			if ( v1 != null && v2 != null )
			{
				c = v1.compareTo( v2 );
			}
			else
			{
				// null == null, and null is smaller than non-null
				c = v1 == v2 ? 0 : v1 == null ? -1 : 1;
			}

			// fall-back to alpha-numeric comparison (with stripped leading zeroes):
			if ( c == 0 )
			{
				c = stripLeadingZeroes( s1 ).compareTo( stripLeadingZeroes( s2 ) );
			}
			if ( c != 0 )
			{
				return c;
			}
		}

		Integer myLen = version.length;
		Integer otherLen = other.length;
		c = myLen.compareTo( otherLen );
		if ( c == 0 )
		{
			myLen = toString().length();
			otherLen = otherVersion.toString().length();
			c = myLen.compareTo( otherLen );
		}
		return c;
	}

	private static String stripLeadingZeroes( String s )
	{
		if ( s == null )
		{
			return null;
		}
		int x = 0;
		while ( x < s.length() && s.charAt( x ) == '0' )
		{
			x++;
		}
		return s.substring( x );
	}

	/**
	 * Converts provided String to a Long by grabbing any
	 * leading digits in the string (stopping at first non-digit)
	 * and converting that to a Long.
	 *
	 * NOTE:  This is not a normal "String-2-Long" method since "123abc" would
	 * return Long.valueOf(123).
	 *
	 * @param s string to convert to long
	 * @return leading digits of the string converted to long
	 */
	private static Long toLong( String s )
	{
		try
		{
			int x = onePastLastDigit( s );
			return x > 0 ? Long.parseLong( s.substring( 0, x ) ) : null;
		}
		catch ( NumberFormatException nfe )
		{
			// Number larger than Long.MAX_VALUE ?
			return null;
		}
	}
}
