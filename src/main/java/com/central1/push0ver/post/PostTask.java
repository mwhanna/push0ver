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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.central1.push0ver.App;

@ExportAsService( {PostTask.class} )
@Named( "PostTask" )
public class PostTask implements TaskType
{
	private static final String PLUGIN_STORAGE_KEY = "push0ver.adminui";

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public PostTask( PluginSettingsFactory pluginSettingsFactory )
	{
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	public TaskResult execute( TaskContext taskContext ) throws TaskException
	{
		BuildLogger log = taskContext.getBuildLogger();
		TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder( taskContext );
		PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
		Map<String, Object> context = new HashMap<>();

		context.put( "username", settings.get( PLUGIN_STORAGE_KEY + ".username" ) );
		context.put( "password", settings.get( PLUGIN_STORAGE_KEY + ".password" ) );
		context.put( "url", settings.get( PLUGIN_STORAGE_KEY + ".url" ) );
		context.put( "reponame", settings.get( PLUGIN_STORAGE_KEY + ".reponame" ) );
		context.put( "globalclient", settings.get( PLUGIN_STORAGE_KEY + ".globalclient" ) );

		final String globalConfig = taskContext.getConfigurationMap().get( "checkbox" );
		final String pushCheckBox = taskContext.getConfigurationMap().get( "pushcheckbox" );
		String taskClient = taskContext.getConfigurationMap().get( "taskclient" );
		if ( "true".equals( globalConfig ) )
		{
			taskClient = context.get( "globalclient" ).toString();
			if ( "".equals( taskClient ) )
			{
				taskClient = "false";
			}
		}
		final boolean sslTrustAll = "true".equalsIgnoreCase( taskClient.trim() );
		String localdir = taskContext.getConfigurationMap().get( "tasklocaldir" ).trim();
		String mavenHome = taskContext.getConfigurationMap().get( "mavenhome" ).trim();

		String taskUsername = taskContext.getConfigurationMap().get( "taskusername" ).trim();
		String taskPassword = taskContext.getConfigurationMap().get( "taskpassword" ).trim();
		String taskUrl = taskContext.getConfigurationMap().get( "taskurl" ).trim();
		String taskReponame = taskContext.getConfigurationMap().get( "taskreponame" ).trim();

		String push = "";
		if ( pushCheckBox.equals( "true" ) )
		{
			push = "push";
		}

		if ( globalConfig.equals( "true" ) )
		{
			if ( taskUsername.trim().length() > 2 )
			{
				taskUsername = taskContext.getConfigurationMap().get( "taskusername" ).trim();
			}
			else if ( context.get( "username" ) == null )
			{
				taskUsername = taskContext.getConfigurationMap().get( "taskusername" ).trim();
			}
			else if ( context.get( "username" ).toString().length() > 2 )
			{
				taskUsername = context.get( "username" ).toString();
			}
			else
			{
				throw new RuntimeException( "You forgot to enter a Username." );
			}

			if ( taskPassword.trim().length() > 2 )
			{
				taskPassword = taskContext.getConfigurationMap().get( "taskpassword" ).trim();
			}
			else if ( context.get( "password" ) == null )
			{
				taskPassword = taskContext.getConfigurationMap().get( "taskpassword" ).trim();
			}
			else if ( context.get( "password" ).toString().length() > 2 )
			{
				taskPassword = context.get( "password" ).toString();
			}
			else
			{
				throw new RuntimeException( "You forgot to enter a Password." );
			}

			if ( taskUrl.trim().length() > 2 )
			{
				taskUrl = taskContext.getConfigurationMap().get( "taskurl" ).trim();
			}
			else if ( context.get( "url" ) == null )
			{
				taskUrl = taskContext.getConfigurationMap().get( "taskurl" ).trim();
			}
			else if ( context.get( "url" ).toString().length() > 2 )
			{
				taskUrl = context.get( "url" ).toString();
			}
			else
			{
				throw new RuntimeException( "You forgot to enter a URL." );
			}

			if ( taskReponame.trim().length() > 2 )
			{
				taskReponame = taskContext.getConfigurationMap().get( "taskreponame" ).trim();
			}
			else if ( context.get( "reponame" ) == null )
			{
				taskReponame = taskContext.getConfigurationMap().get( "taskreponame" ).trim();
			}
			else if ( context.get( "reponame" ).toString().length() > 2 )
			{
				taskReponame = context.get( "reponame" ).toString();
			}
			else
			{
				throw new RuntimeException( "You forgot to enter a Reponame." );
			}
		}
		log.addBuildLogEntry( "URL:  " + taskUrl );
		log.addBuildLogEntry( "REPO:  " + taskReponame );

		if ( taskUsername.equals( "Empty" ) || taskPassword.equals( "Empty" ) )
		{
			log.addBuildLogEntry( "Username/Password NOT SET" );
		}

		String bambooWorkDir = taskContext.getWorkingDirectory().getPath();
		Properties p = new Properties();

		if ( "".equals( localdir ) )
		{
			localdir = bambooWorkDir;
		}
		else if ( !localdir.startsWith( "/" ) )
		{
			p.setProperty( "git.repo", bambooWorkDir );
			if ( !bambooWorkDir.substring( bambooWorkDir.length() - 1 ).equals( "/" ) )
			{
				localdir = bambooWorkDir + "/" + localdir;
			}
		}
		log.addBuildLogEntry( "WORKING DIR:  " + localdir );

		try
		{
			String[] arg = new String[]{localdir, push};
			p.setProperty( "repo.name", taskReponame );
			p.setProperty( "art.username", taskUsername );
			p.setProperty( "art.password", taskPassword );
			p.setProperty( "art.url", taskUrl );
			p.setProperty( "mvn.home", mavenHome );
			p.setProperty( "ssl.trustAll", Boolean.toString( sslTrustAll ) );
			App.invoke( arg, p, log::addBuildLogEntry );
		}
		catch ( Exception e )
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			e.printStackTrace( pw );
			String stacktrace = sw.toString();
			log.addBuildLogEntry( "STACKTRACE:     " + stacktrace );
			taskResultBuilder.failed();
		}

		return taskResultBuilder.build();
	}
}
