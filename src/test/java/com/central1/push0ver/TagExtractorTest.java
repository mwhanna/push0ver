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

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

/*
 * Logic to test:
 *
 *   1.) If the current HEAD commit contains one or more tags, return the "largest" one.
 *       Note:  1.11 is considered larger than 1.9, and 0.0.1 is considered larger than 999.999.999-SNAPSHOT.
 *
 *   2.) If the current HEAD does not contain any tags, go back in history to find the most
 *       recent tag.  If the most recent tag is a "*-SNAPSHOT" tag, return that.  If
 *       the most recent tag is a regular release tag (e.g., no occurrence of the word "SNAPSHOT"),
 *       then return null.
 */
public class TagExtractorTest
{
	final static String CASE_2A =
			"\n" +
					" (tag: test)\n" +
					"\n" +
					" (tag: 0.0.1-SNAPSHOT)\n" +
					" (tag: 0.0.2-SNAPSHOT)\n" +
					" (tag: abc, tag: 0.0.1, tag: 0.0.0.1, tag: 0.0.0.0.1)\n";

	final static String CASE_2B =
			"\n" +
					" (tag: test)\n" +
					"\n" +
					" (tag: 0.0.1b)\n" +
					" (tag: 0.0.2-SNAPSHOT)\n" +
					" (tag: abc, tag: 0.0.1, tag: 0.0.0.1, tag: 0.0.0.0.1)\n";

	final static String CASE_2C =
			"\n" +
					" (tag: test)\n" +
					"\n" +
					"\n" +
					"\n" +
					" (tag: abc, tag: 0.0.1, tag: 0.0.0.1, tag: 0.0.0.0.1, tag: 99-SNAPSHOT)\n";

	final static String CASE_2D =
			"\n" +
					" (tag: test)\n" +
					"\n" +
					"\n" +
					"\n" +
					" (tag: abc, tag: 99, tag: 0.0.1, tag: 0.0.0.1, tag: 0.0.0.0.1, tag: 99-SNAPSHOT)\n";

	final static String CASE_2E =
			"\n" +
					" (tag: test)\n" +
					"\n" +
					"\n" +
					"\n" +
					" (tag: abc, tag: 100.0.0, tag: 0.0.1, tag: 0.0.0.1, tag: 0.0.0.0.1, tag: 99-SNAPSHOT)\n";

	final static String CASE_1A =
			" (HEAD -> master, origin/master, origin/HEAD, tag: 1.1.1.2, tag: 1.1.1, tag: 1.1.1.2a, tag: 99.99-SNAPSHOT, tag: 1.0, tag: zzz)\n" +
					CASE_2A;

	final static String CASE_1B =
			" (HEAD -> master, origin/master, origin/HEAD, tag: 99.99-SNAPSHOT, tag: 1.0-SNAPSHOT, tag: zzz)\n" + CASE_2A;

	@Test
	public void testCases()
	{
		// Case 1a.) Largest non-snapshot tag in HEAD is "1.1.1.2a":
		StringReader r = new StringReader( CASE_1A );
		Assert.assertEquals( "1.1.1.2a", TagExtractor.extractTagTestLogic( r ) );

		// Case 1b.) Largest tag in HEAD is "99.99-SNAPSHOT".  All tags in HEAD contain "-SNAPSHOT".
		// (Except for tag "zzz" which is ignored because it's invalid).
		r = new StringReader( CASE_1B );
		Assert.assertEquals( "99.99-SNAPSHOT", TagExtractor.extractTagTestLogic( r ) );

		// Case 2a.) No tags in HEAD.  Go back in history and nearest tag is "0.0.1-SNAPSHOT".
		r = new StringReader( CASE_2A );
		Assert.assertEquals( "0.0.1-SNAPSHOT", TagExtractor.extractTagTestLogic( r ) );

		// Case 2b.) No tags in HEAD.  Go back in history and nearest tag is "0.0.1b", which is
		// not a SNAPSHOT and so null is returned.
		r = new StringReader( CASE_2B );
		Assert.assertNull( TagExtractor.extractTagTestLogic( r ) );

		// Case 2c.) No tags in HEAD.  Go back in history and nearest tag is "0.0.1b", which is
		// not a SNAPSHOT and so null is returned EXCEPT that same commit also has 99-SNAPSHOT,
		// and so 99-SNAPSHOT is returned.
		r = new StringReader( CASE_2C );
		Assert.assertEquals( "99-SNAPSHOT", TagExtractor.extractTagTestLogic( r ) );

		// Case 2d.) and 2e.) No tags in HEAD.  Go back in history and nearest tag is "99", which is
		// not a SNAPSHOT and so null is returned EXCEPT that same commit also has 99-SNAPSHOT,
		// and so 99-SNAPSHOT would be returned, except that SNAPSHOT must be larger than release
		// tag, and so null is returned.
		r = new StringReader( CASE_2D );

		// 99 vs. 99-SNAPSHOT
		Assert.assertNull( TagExtractor.extractTagTestLogic( r ) );

		// 100.0.0 vs. 99-SNAPSHOT
		r = new StringReader( CASE_2E );
		Assert.assertNull( TagExtractor.extractTagTestLogic( r ) );

		// The nothing-interesting case.  No tags at all!
		r = new StringReader( "\n\n\n\n\n\n\n\n" );
		Assert.assertNull( TagExtractor.extractTagTestLogic( r ) );
	}

}
