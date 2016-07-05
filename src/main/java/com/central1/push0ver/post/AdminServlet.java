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

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@ExportAsService( {AdminServlet.class} )
@Named( "Push0verAdminServlet" )
public class AdminServlet extends HttpServlet
{
	private static final String PLUGIN_STORAGE_KEY = "push0ver.adminui";

	@ComponentImport
	private final UserManager userManager;

	@ComponentImport
	private final LoginUriProvider loginUriProvider;

	@ComponentImport
	private final TemplateRenderer renderer;

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public AdminServlet( UserManager userManager, LoginUriProvider loginUriProvider,
			TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory )
	{
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.renderer = renderer;
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@Override
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException
	{
		String name = userManager.getRemoteUsername( request );
		if ( name == null || !userManager.isSystemAdmin( name ) )
		{
			redirectToLogin( request, response );
			return;
		}
		Map<String, Object> context = new HashMap<String, Object>();

		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".username" ) == null ||
				pluginSettings.get( PLUGIN_STORAGE_KEY + ".username" ).equals( "" ) )
		{
			String noName = "Empty";
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".username", noName );
		}

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".password" ) == null ||
				pluginSettings.get( PLUGIN_STORAGE_KEY + ".password" ).equals( "" ) )
		{
			String noPw = "Empty";
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".password", noPw );
		}

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".url" ) == null ||
				pluginSettings.get( PLUGIN_STORAGE_KEY + ".url" ).equals( "" ) )
		{
			String noUrl = "Empty";
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".url", noUrl );
		}

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".releaserepo" ) == null ||
				pluginSettings.get( PLUGIN_STORAGE_KEY + ".releaserepo" ).equals( "" ) )
		{
			String noReponame = "Empty";
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".releaserepo", noReponame );
		}

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".snaprepo" ) == null ||
				pluginSettings.get( PLUGIN_STORAGE_KEY + ".snaprepo" ).equals( "" ) )
		{
			String noSnapname = "Empty";
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".snaprepo", noSnapname );
		}

		if ( pluginSettings.get( PLUGIN_STORAGE_KEY + ".globalclient" ) == null )
		{
			pluginSettings.put( PLUGIN_STORAGE_KEY + ".globalclient", "false" );
		}

		context.put( "username", pluginSettings.get( PLUGIN_STORAGE_KEY + ".username" ) );
		context.put( "password", pluginSettings.get( PLUGIN_STORAGE_KEY + ".password" ) );
		context.put( "url", pluginSettings.get( PLUGIN_STORAGE_KEY + ".url" ) );
		context.put( "releaserepo", pluginSettings.get( PLUGIN_STORAGE_KEY + ".releaserepo" ) );
		context.put( "snaprepo", pluginSettings.get( PLUGIN_STORAGE_KEY + ".snaprepo" ) );
		context.put( "globalclient", pluginSettings.get( PLUGIN_STORAGE_KEY + ".globalclient" ) );

		response.setContentType( "text/html;charset=utf-8" );
		renderer.render( "admin.vm", context, response.getWriter() );
	}

	@Override
	protected void doPost( HttpServletRequest req, HttpServletResponse response )
			throws ServletException, IOException
	{
		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".username", req.getParameter( "username" ) );
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".password", req.getParameter( "password" ) );
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".url", req.getParameter( "url" ) );
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".releaserepo", req.getParameter( "releaserepo" ) );
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".snaprepo", req.getParameter( "snaprepo" ) );
		pluginSettings.put( PLUGIN_STORAGE_KEY + ".globalclient", req.getParameter( "globalclient" ) );
		response.sendRedirect( "admin" );
	}

	private void redirectToLogin( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		response.sendRedirect( loginUriProvider.getLoginUri( getUri( request ) ).toASCIIString() );
	}

	private URI getUri( HttpServletRequest request )
	{
		StringBuffer builder = request.getRequestURL();
		if ( request.getQueryString() != null )
		{
			builder.append( "?" );
			builder.append( request.getQueryString() );
		}
		return URI.create( builder.toString() );
	}
}
