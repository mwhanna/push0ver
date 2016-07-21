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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A total ordering on version numbers that tends to match what humans expect
 * (e.g., "1.2" &lt; "1.2.3alpha99" &lt; "1.2.3beta3" &lt; "1.2.3rc1" &lt; "1.2.3" &lt; "1.2.11" ).
 *
 * Developed with an empirical sample of ~20,000 distinct version numbers extracted from Maven-Central and Debian.
 */
public class VersionComparators
{
	public static Comparator<String> COMPARE_VERSION_STRINGS = new Comparator<String>()
	{
		public int compare( String s1, String s2 )
		{
			if ( s1 == null && s2 == null )
			{
				return 0;
			}
			else if ( s1 == null )
			{
				return -1;
			}
			else if ( s2 == null )
			{
				return 1;
			}
			else
			{
				return COMPARE_VERSIONS.compare( new Version( s1 ), new Version( s2 ) );
			}
		}
	};

	public static Comparator<Version> COMPARE_VERSIONS = new Comparator<Version>()
	{
		public int compare( Version v1, Version v2 )
		{
			if ( v1 == null && v2 == null )
			{
				return 0;
			}
			else if ( v1 == null )
			{
				return -1;
			}
			else if ( v2 == null )
			{
				return 1;
			}
			else
			{
				/*
				This comparison logic considers each component of the version number (split by ".").
				 */
				String[] ver1 = v1.getSplit();
				String[] ver2 = v2.getSplit();
				int c;
				for ( int i = 0; i < Math.min( ver1.length, ver2.length ); i++ )
				{
					final String s1 = ver1[ i ];
					final String s2 = ver2[ i ];
					c = alphaNumericCompare( s1, s2 );
					if ( c != 0 )
					{
						return c;
					}
				}

				// last-resort comparison:  lengths
				Integer len1 = ver1.length;
				Integer len2 = ver2.length;
				c = len1.compareTo( len2 );
				if ( c == 0 )
				{
					len1 = v1.toString().length();
					len2 = v2.toString().length();
					c = len1.compareTo( len2 );
				}
				return c;
			}
		}
	};

	/**
	 * This logic sub-splits each component of the version number at any numeric-to-alpha transitions, as well at
	 * any sequences of special characters.
	 *
	 * Each token in the sub-split is then considered.
	 * Special case handling for alpha sequences that indicate "pre" or "post" releases, eg., this logic
	 * knows that "alpha" versions comes before "beta" versions which comes before "rc" versions.
	 *
	 * @param s1 the component between dots from version1 of comparison
	 * @param s2 the component between dots from version2 of comparison
	 * @return comparator contract (e.g., +int if s1 larger, -int if s1 smaller, 0 if they are equal).
	 */
	private static int alphaNumericCompare( String s1, String s2 )
	{
		String[] words1 = splitIntoAlphasAndNums( s1 );
		String[] words2 = splitIntoAlphasAndNums( s2 );
		for ( int j = 0; j < Math.min( words1.length, words2.length ); j++ )
		{
			int c;
			final String sp1 = words1[ j ];
			final String sp2 = words2[ j ];

			// First look for special alpha sequences (e.g., "alpha" or "beta" or "rc"):
			int subScore1 = calculateScore( sp1 );
			int subScore2 = calculateScore( sp2 );
			c = subScore1 - subScore2;
			if ( c != 0 )
			{
				return c;
			}

			// Since splitIntoAlphasAndNums() was called, that means that if 1st char is a digit, then
			// all characters are digits:
			if ( startsWithDigit( sp1 ) || startsWithDigit( sp2 ) )
			{
				Comparable<Long> v1 = toLong( sp1 );
				Long v2 = toLong( sp2 );
				if ( v1 != null && v2 != null )
				{
					c = v1.compareTo( v2 );
				}
				else
				{
					// null == null, and null is smaller than non-null
					c = v1 == v2 ? 0 : v1 == null ? -1 : 1;
				}
			}
			else
			{
				// Both are pure non-numerics, so use regular lexicographic compare:
				c = sp1.compareTo( sp2 );
			}

			if ( c != 0 )
			{
				return c;
			}
		}

		// Last resort comparision:  sub-splitting array length.
		return words1.length - words2.length;
	}

	private final static boolean[] IS_DIGIT = new boolean[ '9' + 1 ];
	private final static boolean[] IS_SPECIAL = new boolean[ 128 ];
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

		IS_SPECIAL[ '`' ] = true;
		IS_SPECIAL[ '^' ] = true;
		IS_SPECIAL[ '~' ] = true;
		IS_SPECIAL[ '=' ] = true;
		IS_SPECIAL[ '|' ] = true;
		IS_SPECIAL[ '-' ] = true;
		IS_SPECIAL[ ',' ] = true;
		IS_SPECIAL[ ';' ] = true;
		IS_SPECIAL[ ':' ] = true;
		IS_SPECIAL[ '!' ] = true;
		IS_SPECIAL[ '?' ] = true;
		IS_SPECIAL[ '/' ] = true;
		IS_SPECIAL[ '\'' ] = true;
		IS_SPECIAL[ '"' ] = true;
		IS_SPECIAL[ '(' ] = true;
		IS_SPECIAL[ ')' ] = true;
		IS_SPECIAL[ '[' ] = true;
		IS_SPECIAL[ ']' ] = true;
		IS_SPECIAL[ '{' ] = true;
		IS_SPECIAL[ '}' ] = true;
		IS_SPECIAL[ '@' ] = true;
		IS_SPECIAL[ '$' ] = true;
		IS_SPECIAL[ '*' ] = true;
		IS_SPECIAL[ '\\' ] = true;
		IS_SPECIAL[ '&' ] = true;
		IS_SPECIAL[ '#' ] = true;
		IS_SPECIAL[ '%' ] = true;
		IS_SPECIAL[ '+' ] = true;
		IS_SPECIAL[ '_' ] = true;
		IS_SPECIAL[ 0 ] = true;
		IS_SPECIAL[ '\n' ] = true;
		IS_SPECIAL[ '\r' ] = true;
		IS_SPECIAL[ '\t' ] = true;
		IS_SPECIAL[ ' ' ] = true;
	}

	/**
	 * This method transforms the given String into an array of splits.  The splitting function splits on
	 * transitions from alpha to numeric.  It also splits on any sequence of special characters, but
	 * only includes alpha and numeric components of the string in its output.
	 *
	 * It also always appends an empty-string to the returned split.
	 *
	 * e.g.,  "abc123xyz" would return ["abc", "123", "xyz", ""]
	 *        "abc--12-3__;__xyz" would return ["abc", "12", "3", "xyz", ""]
	 */
	static String[] splitIntoAlphasAndNums( String s )
	{
		if ( "".equals( s ) )
		{
			return new String[]{""};
		}
		s = s.toLowerCase( Locale.ENGLISH );

		List<String> splits = new ArrayList<>();
		String tok = "";

		char c = s.charAt( 0 );
		boolean isDigit = isDigit( c );
		boolean isSpecial = c < IS_SPECIAL.length && IS_SPECIAL[ c ];
		boolean isAlpha = !isDigit && !isSpecial;
		int prevMode = isAlpha ? 0 : isDigit ? 1 : -1;

		for ( int i = 0; i < s.length(); i++ )
		{
			c = s.charAt( i );
			isDigit = isDigit( c );
			isSpecial = c < IS_SPECIAL.length && IS_SPECIAL[ c ];
			isAlpha = !isDigit && !isSpecial;
			int mode = isAlpha ? 0 : isDigit ? 1 : -1;
			if ( mode != prevMode )
			{
				if ( !"".equals( tok ) )
				{
					splits.add( tok );
					tok = "";
				}
			}

			// alpha=0, digit=1.  Don't append for specials.
			if ( mode >= 0 )
			{
				// Special case for minus sign.
				if ( i == 1 && isDigit && '-' == s.charAt( 0 ) )
				{
					tok = "-";
				}
				tok += c;
			}
			prevMode = mode;
		}
		if ( !"".equals( tok ) )
		{
			splits.add( tok );
		}
		splits.add( "" );  // very important: append empty-string to all returned splits.
		return splits.toArray( new String[ splits.size() ] );
	}

	private static int calculateScore( String word )
	{
		// special case for "RC" or "alpha" or "beta" or "a" or "b" or "u" or "update" or
		// "patch" or "p" or "rev" or "r" or "svn" or "bzr" or "rel" or "release".
		switch ( word )
		{
			case "rc":
				return -1;
			case "b":
				return -2;
			case "beta":
				return -3;
			case "a":
				return -4;
			case "alpha":
				return -5;
			case "snapshot":
				return -10;
			case "cvs":
				return -11;
			case "svn":
				return -11;
			case "bzr":
				return -11;
			case "hg":
				return -11;
			case "git":
				return -11;
			case "rev":
				return -100;
			case "r":
				return -101;
			case "release":
				return 12;
			case "update":
				return 11;
			case "u":
				return 10;
			case "patch":
				return 9;
			case "p":
				return 8;
			case "hotfix":
				return 7;
			case "fix":
				return 6;
		}
		Long l = toLong( word );
		return l != null ? 100 : 0; // a pure number (with no alpha) wins against all of those.
	}

	private static boolean isDigit( char c )
	{
		return c < IS_DIGIT.length && IS_DIGIT[ c ];
	}

	private static boolean startsWithDigit( String s )
	{
		switch ( s.length() )
		{
			case 0:
				return false;
			case 1:
				return isDigit( s.charAt( 0 ) );
			default:
				char c = s.charAt( 0 );
				return isDigit( c ) || c == '-' && isDigit( s.charAt( 1 ) );
		}
	}

	private static Long toLong( String s )
	{
		if ( "".equals( s ) )
		{
			return null; // no digits.
		}
		try
		{
			return Long.parseLong( s );
		}
		catch ( NumberFormatException nfe )
		{
			return null;// contained alpha, or Number larger than Long.MAX_VALUE ?
		}
	}
}
