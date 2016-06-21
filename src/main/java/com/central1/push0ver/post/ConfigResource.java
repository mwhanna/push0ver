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

package com.central1.push0ver.post;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

@ExportAsService( {ConfigResource.class} )
@Named( "ConfigResource" )
@Path( "/" )
@Consumes( {MediaType.APPLICATION_JSON} )
@Produces( {MediaType.APPLICATION_JSON} )
public class ConfigResource
{
	private static final String PLUGIN_STORAGE_KEY = "push0ver.adminui";

	@ComponentImport
	private final UserManager userManager;

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@ComponentImport
	private final TransactionTemplate transactionTemplate;

	@Inject
	public ConfigResource( UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate )
	{
		this.userManager = userManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
		this.transactionTemplate = transactionTemplate;
	}

	@XmlRootElement
	@XmlAccessorType( XmlAccessType.FIELD )
	public static final class Config
	{
		@XmlElement
		private String name;
		@XmlElement
		private int time;

		public String getName()
		{
			return name;
		}

		public void setName( String name )
		{
			this.name = name;
		}

		public int getTime()
		{
			return time;
		}

		public void setTime( int time )
		{
			this.time = time;
		}
	}

	@GET
	@Path( "/" )
	@Produces( MediaType.APPLICATION_JSON )
	public Response get( @Context HttpServletRequest request )
	{
		String username = userManager.getRemoteUsername( request );
		if ( username == null || !userManager.isSystemAdmin( username ) )
		{
			return Response.status( Status.UNAUTHORIZED ).build();
		}

		return Response.ok( transactionTemplate.execute( new TransactionCallback()
		{
			public Object doInTransaction()
			{
				PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
				Config config = new Config();
				config.setName( (String) settings.get( PLUGIN_STORAGE_KEY + ".name" ) );
				System.out.println( config.getName() );

				String time = (String) settings.get( PLUGIN_STORAGE_KEY + ".time" );
				if ( time != null )
				{
					config.setTime( Integer.parseInt( time ) );
				}
				return config;
			}
		} ) ).build();
	}

	@PUT
	@Path( "/" )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response put( final Config config, @Context HttpServletRequest request )
	{
		String username = userManager.getRemoteUsername( request );
		if ( username == null || !userManager.isSystemAdmin( username ) )
		{
			return Response.status( Status.UNAUTHORIZED ).build();
		}

		transactionTemplate.execute( new TransactionCallback()
		{
			public Object doInTransaction()
			{
				PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
				pluginSettings.put( PLUGIN_STORAGE_KEY + ".name", config.getName() );
				System.out.println( config.getName() );
				pluginSettings.put( PLUGIN_STORAGE_KEY + ".time", Integer.toString( config.getTime() ) );
				System.out.println( config.getTime() );
				return null;
			}
		} );
		return Response.noContent().build();
	}
}
