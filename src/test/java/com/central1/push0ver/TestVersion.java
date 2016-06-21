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
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class TestVersion
{
	@Test
	public void testOrder()
	{
		ArrayList<Version> versions = new ArrayList<Version>();
		versions.add( new Version( "apple" ) );
		versions.add( new Version( "banana" ) );
		versions.add( new Version( "1" ) );
		versions.add( new Version( "0" ) );
		versions.add( new Version( "" ) );
		versions.add( new Version( "-1" ) );
		versions.add( new Version( "-1.-2" ) );
		versions.add( new Version( "-1.-2.-3" ) );
		versions.add( new Version( "1.2.3" ) );
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
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.25 " ) );
		versions.add( new Version( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.26 " ) );
		versions.add( new Version( "1.2.3" ) );
		versions.add( new Version( "1.2.3" ) );
		versions.add( new Version( "1.2.5" ) );
		versions.add( new Version( "1.1.1" ) );
		versions.add( new Version( "0.0.0" ) );

		Collections.sort( versions );

		Assert.assertEquals( "", "" + versions.get( 0 ) );
		Assert.assertEquals( "apple", "" + versions.get( 1 ) );
		Assert.assertEquals( "banana", "" + versions.get( 2 ) );
		Assert.assertEquals( "-1", "" + versions.get( 3 ) );
		Assert.assertEquals( "-1.-2", "" + versions.get( 4 ) );
		Assert.assertEquals( "-1.-2.-3", "" + versions.get( 5 ) );
		Assert.assertEquals( "-1.2.3", "" + versions.get( 6 ) );
		Assert.assertEquals( "0", "" + versions.get( 7 ) );
		Assert.assertEquals( "0.0.0", "" + versions.get( 8 ) );
		Assert.assertEquals( "1", "" + versions.get( 9 ) );
		Assert.assertEquals( "1.-2.3", "" + versions.get( 10 ) );
		Assert.assertEquals( "1.1.1", "" + versions.get( 11 ) );
		Assert.assertEquals( "1.2.-3", "" + versions.get( 12 ) );
		Assert.assertEquals( "1.2.0", "" + versions.get( 13 ) );
		Assert.assertEquals( "1.2.00", "" + versions.get( 14 ) );
		Assert.assertEquals( "1.2.000", "" + versions.get( 15 ) );
		Assert.assertEquals( "1.2.0000", "" + versions.get( 16 ) );
		Assert.assertEquals( "1.2.0001", "" + versions.get( 17 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 18 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 19 ) );
		Assert.assertEquals( "1.2.3", "" + versions.get( 20 ) );
		Assert.assertEquals( "1.2.0003", "" + versions.get( 21 ) );
		Assert.assertEquals( "1.2.3.4", "" + versions.get( 22 ) );
		Assert.assertEquals( "1.2.0003.4", "" + versions.get( 23 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20", "" + versions.get( 24 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21", "" + versions.get( 25 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22", "" + versions.get( 26 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.b25", "" + versions.get( 27 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.22", "" + versions.get( 28 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23", "" + versions.get( 29 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.23a", "" + versions.get( 30 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.24", "" + versions.get( 31 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.25", "" + versions.get( 32 ) );
		Assert.assertEquals( "1.2.3.4.5.6.7.8.9.10.11.12.13.14.15.16.17.18.19.20.21.22.26", "" + versions.get( 33 ) );
		Assert.assertEquals( "1.2.0004", "" + versions.get( 34 ) );
		Assert.assertEquals( "1.2.5", "" + versions.get( 35 ) );
		Assert.assertEquals( "1.2.9", "" + versions.get( 36 ) );
		Assert.assertEquals( "1.2.10", "" + versions.get( 37 ) );
		Assert.assertEquals( "1.2.11", "" + versions.get( 38 ) );
		Assert.assertEquals( "1.2.12z", "" + versions.get( 39 ) );

		/*
		for ( Version v : versions )
		{
			System.out.println( v );
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
		Assert.assertTrue( v1.compareTo( v2 ) < 0 );
		Assert.assertTrue( v2.compareTo( v1 ) > 0 );
	}
}
