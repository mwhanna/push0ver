package com.central1.push0ver;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class TestBinarySed
{

	@Test
	public void testWeirdness() throws Exception
	{
		String s = "YES THIS IS A GREAT 0.0.0.0.0-SNAPSHOT STRING WHAT A STRING! 0.0.0.0.0-SNAPSHOT";
		byte[] b = s.getBytes( StandardCharsets.UTF_8 );

		SillyStream ss = new SillyStream( b, 8, 1, 0, 13, 1, 2, 3 );
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		BinarySed.replaceAll( null, "0.0.0.0.0-SNAPSHOT", "1.2.3", ss, bout );

		System.out.println( bout.toString( "UTF-8" ) );
	}

}
