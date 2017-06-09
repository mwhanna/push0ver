package com.central1.push0ver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class Npm
{

	public static void main( String[] args ) throws Exception
	{

		// https://artifactory.oss.central1.com/artifactory/api/npm/npm-virtual/@c1%2Fc1-devkit

		CloseableHttpClient httpClient = Rename.allConnect( true );
		HttpGet get = new HttpGet( "https://artifactory.oss.central1.com/artifactory/api/npm/npm-virtual/@c1%2Fc1-devkit" );
		CloseableHttpResponse response = null;
		response = httpClient.execute( get );
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		InputStream in = response.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader( in, StandardCharsets.UTF_8 );
		BufferedReader br = new BufferedReader( isr );

		/*
		https://artifactory.oss.central1.com/artifactory/api/npm/npm-virtual/@c1%2Fc1-devkit

		JSON from NPM registry (and Artifactory pretending to be an NPM registry)
		looks something like this:

		{
			"_id": "@c1/c1-devkit",
			"_rev": "1-0",
			"name": "@c1/c1-devkit",
			"description": "Central 1 Development Kit",
			"dist-tags": {
				"latest": "0.5.4"
			},
			"versions": {
				"0.5.1": {
		
		*/
		JsonStreamParser p = new JsonStreamParser( br );
		ArrayList<Version> versions = new ArrayList<>();
		if ( p.hasNext() )
		{
			JsonObject json = p.next().getAsJsonObject();
			JsonObject versionsObj = json.getAsJsonObject( "versions" );
			for ( Map.Entry<String, JsonElement> entry : versionsObj.entrySet() )
			{
				String v = entry.getKey();
				versions.add( new Version( v ) );
			}
		}

		Collections.sort( versions );
		for ( Version v : versions )
		{
			System.out.println( v );
		}

	}

}
