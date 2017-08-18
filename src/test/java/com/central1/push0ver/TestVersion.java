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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class TestVersion
{
	@Test
	public void testOrder()
	{
		ArrayList<Version> versions = new ArrayList<Version>();
		versions.add( new Version( "0.2.1.1-3+b2" ) );
		versions.add( new Version( "0.2-1.2" ) );
		versions.add( new Version( "0.2.8-5+b3" ) );
		versions.add( new Version( "alpha" ) );
		versions.add( new Version( "beta" ) );
		versions.add( new Version( "apple" ) );
		versions.add( new Version( "banana" ) );
		versions.add( new Version( "0.0~r99-4" ) );
		versions.add( new Version( "0.0~r131-2" ) );
		versions.add( new Version( "0.0~r131-3" ) );
		versions.add( new Version( "0.0~r131-11" ) );
		versions.add( new Version( "0.0~r131-001" ) );
		versions.add( new Version( "0.0~r131-3alpha" ) );
		versions.add( new Version( "0.0~r131-3beta" ) );
		versions.add( new Version( "0.0~r131-3update" ) );
		versions.add( new Version( "0.0~r131-3rc" ) );
		versions.add( new Version( "0.0~r131-3rc5" ) );
		versions.add( new Version( "0.0~r131-3rc111" ) );
		versions.add( new Version( "0.0~r131-3rc11" ) );
		versions.add( new Version( "0.0~r131-3rc55" ) );
		versions.add( new Version( "0.0~r18-3" ) );
		versions.add( new Version( "1" ) );
		versions.add( new Version( "0" ) );
		versions.add( new Version( "" ) );
		versions.add( new Version( "-1" ) );
		versions.add( new Version( "-1.-2" ) );
		versions.add( new Version( "-1.-2.-3" ) );
		versions.add( new Version( "1.2.3a" ) );
		versions.add( new Version( "1.2.3b" ) );
		versions.add( new Version( "1.2.3c" ) );
		versions.add( new Version( "1.2.3t" ) );
		versions.add( new Version( "1.2.3u" ) );
		versions.add( new Version( "1.2.3v" ) );
		versions.add( new Version( "1.2.3.a" ) );
		versions.add( new Version( "1.2.3.b" ) );
		versions.add( new Version( "1.2.3.c" ) );
		versions.add( new Version( "1.2.3.t" ) );
		versions.add( new Version( "1.2.3.u" ) );
		versions.add( new Version( "1.2.3.v" ) );
		versions.add( new Version( "1.2.3" ) );
		versions.add( new Version( "v1.2.3" ) );
		versions.add( new Version( "v.1.2.3" ) );
		versions.add( new Version( "-1.2.3" ) );
		versions.add( new Version( "1.-2.3" ) );
		versions.add( new Version( "1.2.-3" ) );
		versions.add( new Version( "1.2.9" ) );
		versions.add( new Version( "1.2.10" ) );
		versions.add( new Version( "1.2.11" ) );
		versions.add( new Version( "1.2.12z" ) );
		versions.add( new Version( "1.2.0" ) );
		versions.add( new Version( "1.2.00" ) );
		versions.add( new Version( "1.2.000" ) );
		versions.add( new Version( "1.2.0000" ) );
		versions.add( new Version( "1.2.0001" ) );
		versions.add( new Version( "1.2.0003" ) );
		versions.add( new Version( "1.2.3.4" ) );
		versions.add( new Version( "1.2.0003.4" ) );
		versions.add( new Version( "1.2.0004" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.22" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23a" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23" ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.24 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.u25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.u3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.z3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc2 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc.3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.aa25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.aa3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.a25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.a3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.c25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.c3 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.26 " ) );
		versions.add( new Version( "1.2.3" ) );
		versions.add( new Version( "1.2.3" ) );
		versions.add( new Version( "1.2.5" ) );
		versions.add( new Version( "1.1.1" ) );
		versions.add( new Version( "0.0.0" ) );

		Collections.sort( versions );

		Assert.assertEquals( "alpha", "" + versions.get( 0 ) );
		Assert.assertEquals( "beta", "" + versions.get( 1 ) );
		Assert.assertEquals( "", "" + versions.get( 2 ) );
		Assert.assertEquals( "apple", "" + versions.get( 3 ) );
		Assert.assertEquals( "banana", "" + versions.get( 4 ) );
		Assert.assertEquals( "v.1.2.3", "" + versions.get( 5 ) );
		Assert.assertEquals( "-1", "" + versions.get( 6 ) );
		Assert.assertEquals( "-1.-2", "" + versions.get( 7 ) );
		Assert.assertEquals( "-1.-2.-3", "" + versions.get( 8 ) );
		Assert.assertEquals( "-1.2.3", "" + versions.get( 9 ) );
		Assert.assertEquals( "0", "" + versions.get( 10 ) );
		Assert.assertEquals( "0.0~r18-3", "" + versions.get( 11 ) );
		Assert.assertEquals( "0.0~r99-4", "" + versions.get( 12 ) );
		Assert.assertEquals( "0.0~r131-001", "" + versions.get( 13 ) );
		Assert.assertEquals( "0.0~r131-2", "" + versions.get( 14 ) );
		Assert.assertEquals( "0.0~r131-3alpha", "" + versions.get( 15 ) );
		Assert.assertEquals( "0.0~r131-3beta", "" + versions.get( 16 ) );
		Assert.assertEquals( "0.0~r131-3rc", "" + versions.get( 17 ) );
		Assert.assertEquals( "0.0~r131-3rc5", "" + versions.get( 18 ) );
		Assert.assertEquals( "0.0~r131-3rc11", "" + versions.get( 19 ) );
		Assert.assertEquals( "0.0~r131-3rc55", "" + versions.get( 20 ) );
		Assert.assertEquals( "0.0~r131-3rc111", "" + versions.get( 21 ) );
		Assert.assertEquals( "0.0~r131-3", "" + versions.get( 22 ) );
		Assert.assertEquals( "0.0~r131-3update", "" + versions.get( 23 ) );
		Assert.assertEquals( "0.0~r131-11", "" + versions.get( 24 ) );
		Assert.assertEquals( "0.0.0", "" + versions.get( 25 ) );
		Assert.assertEquals( "0.2.1.1-3+b2", "" + versions.get( 26 ) );
		Assert.assertEquals( "0.2.8-5+b3", "" + versions.get( 27 ) );
		Assert.assertEquals( "0.2-1.2", "" + versions.get( 28 ) );
		Assert.assertEquals( "1", "" + versions.get( 29 ) );
		Assert.assertEquals( "1.-2.3", "" + versions.get( 30 ) );
		Assert.assertEquals( "1.1.1", "" + versions.get( 31 ) );
		Assert.assertEquals( "1.2.-3", "" + versions.get( 32 ) );
		Assert.assertEquals( "1.2.0", "" + versions.get( 33 ) );
		Assert.assertEquals( "1.2.00", "" + versions.get( 34 ) );
		Assert.assertEquals( "1.2.000", "" + versions.get( 35 ) );
		Assert.assertEquals( "1.2.0000", "" + versions.get( 36 ) );
		Assert.assertEquals( "1.2.0001", "" + versions.get( 37 ) );
		Assert.assertEquals( "1.2.3a", "" + versions.get( 38 ) );
		Assert.assertEquals( "1.2.3b", "" + versions.get( 39 ) );
		Assert.assertEquals( "1.2.3.a", "" + versions.get( 40 ) );
		Assert.assertEquals( "1.2.3.b", "" + versions.get( 41 ) );
		Assert.assertEquals( "1.2.0003", "" + versions.get( 42 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 43 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 44 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 45 ) );
		Assert.assertEquals( "v1.2.3", "" + versions.get( 46 ) );
		Assert.assertEquals( "1.2.3.c", "" + versions.get( 47 ) );
		Assert.assertEquals( "1.2.3.t", "" + versions.get( 48 ) );
		Assert.assertEquals( "1.2.3.v", "" + versions.get( 49 ) );
		Assert.assertEquals( "1.2.3.u", "" + versions.get( 50 ) );
		Assert.assertEquals( "1.2.0003.4", "" + versions.get( 51 ) );
		Assert.assertEquals( "1.2.3.4", "" + versions.get( 52 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20", "" + versions.get( 53 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21", "" + versions.get( 54 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.a3", "" + versions.get( 55 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.a25", "" + versions.get( 56 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b3", "" + versions.get( 57 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b25", "" + versions.get( 58 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc.3", "" + versions.get( 59 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc2", "" + versions.get( 60 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc3", "" + versions.get( 61 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.rc25", "" + versions.get( 62 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22", "" + versions.get( 63 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.aa3", "" + versions.get( 64 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.aa25", "" + versions.get( 65 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.c3", "" + versions.get( 66 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.c25", "" + versions.get( 67 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.z3", "" + versions.get( 68 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.u3", "" + versions.get( 69 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.u25", "" + versions.get( 70 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.22", "" + versions.get( 71 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23a", "" + versions.get( 72 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23", "" + versions.get( 73 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.24", "" + versions.get( 74 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.25", "" + versions.get( 75 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.26", "" + versions.get( 76 ) );
		Assert.assertEquals( "1.2.3c", "" + versions.get( 77 ) );
		Assert.assertEquals( "1.2.3t", "" + versions.get( 78 ) );
		Assert.assertEquals( "1.2.3v", "" + versions.get( 79 ) );
		Assert.assertEquals( "1.2.3u", "" + versions.get( 80 ) );
		Assert.assertEquals( "1.2.0004", "" + versions.get( 81 ) );
		Assert.assertEquals( "1.2.5", "" + versions.get( 82 ) );
		Assert.assertEquals( "1.2.9", "" + versions.get( 83 ) );
		Assert.assertEquals( "1.2.10", "" + versions.get( 84 ) );
		Assert.assertEquals( "1.2.11", "" + versions.get( 85 ) );
		Assert.assertEquals( "1.2.12z", "" + versions.get( 86 ) );

		/*
				int i = 0;
				for ( Version v : versions )
				{
					System.out.println( "Assert.assertEquals( \"" + v + "\", \"\" + versions.get( " + i++ + " ) );" );
				}
		*/
	}

	@Test
	public void testLeadingZeroes1()
	{
		Version v1 = new Version( "1.2.4" );
		Version v2 = new Version( "1.2.0003" );
		Assert.assertTrue( v1.compareTo( v2 ) > 0 );
		Assert.assertTrue( v2.compareTo( v1 ) < 0 );
	}

	@Test
	public void testLeadingZeroes2()
	{
		Version v1 = new Version( "1.2.3" );
		Version v2 = new Version( "1.2.0003" );
		Assert.assertTrue( v2.compareTo( v1 ) < 0 );
		Assert.assertTrue( v1.compareTo( v2 ) > 0 );
	}

	@Test
	public void testSplitOnSpecial()
	{
		String s1 = "0~r131-2";
		String s2 = "abc123xyz";
		String s3 = "abc--12-3__;__xyz";
		Assert.assertEquals( "[0, r, 131, 2, ]", Arrays.toString( VersionComparators.splitIntoAlphasAndNums( s1 ) ) );
		Assert.assertEquals( "[abc, 123, xyz, ]", Arrays.toString( VersionComparators.splitIntoAlphasAndNums( s2 ) ) );
		Assert.assertEquals( "[abc, 12, 3, xyz, ]", Arrays.toString( VersionComparators.splitIntoAlphasAndNums( s3 ) ) );
	}

	@Test
	public void testEqualsHashcode() {
		String s1 = "1.2.3";
		String s2 = "v1.2.3";
		String s3 = "v.1.2.3";

		Version v1 = new Version(s1);
		Version v2 = new Version(s2);
		Version v3 = new Version(s3);

		System.out.println(v1.hashCode());
		System.out.println(v2.hashCode());
		System.out.println(v3.hashCode());
		System.out.println(v1.equals(v2));
		System.out.println(v2.equals(v1));
		System.out.println(v1.equals(v3));
		System.out.println(v3.equals(v1));

	}

	/**
	 * Sorts the provided file (arg0) and outputs the sorted version to stdout.
	 */
	public static void main( String[] args ) throws Exception
	{
		FileInputStream fin = new FileInputStream( args[ 0 ] );
		InputStreamReader isr = new InputStreamReader( fin, "UTF-8" );
		BufferedReader br = new BufferedReader( isr );
		String line;
		ArrayList<Version> list = new ArrayList<Version>( 32 * 1024 );
		while ( ( line = br.readLine() ) != null )
		{
			Version v = new Version( line );
			list.add( v );
		}
		Collections.sort( list );
		for ( Version v : list )
		{
			System.out.println( v.toString() );
		}
	}
}
