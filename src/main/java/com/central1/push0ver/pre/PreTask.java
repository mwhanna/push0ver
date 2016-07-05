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

package com.central1.push0ver.pre;

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
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.central1.push0ver.MyLogger;

@Named( "PreTask" )
public class PreTask implements TaskType
{
	private static final String PLUGIN_STORAGE_KEY = "push0ver.adminui";

	@ComponentImport
	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public PreTask( PluginSettingsFactory pluginSettingsFactory )
	{
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@Override
	public TaskResult execute( TaskContext taskContext ) throws TaskException
	{
		TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder( taskContext );
		PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
		Map<String, Object> context = new HashMap<>();
		BuildLogger log = taskContext.getBuildLogger();

		context.put( "username", settings.get( PLUGIN_STORAGE_KEY + ".username" ) );
		context.put( "password", settings.get( PLUGIN_STORAGE_KEY + ".password" ) );
		context.put( "url", settings.get( PLUGIN_STORAGE_KEY + ".url" ) );
		context.put( "releaserepo", settings.get( PLUGIN_STORAGE_KEY + ".releaserepo" ) );
		context.put( "globalclient", settings.get( PLUGIN_STORAGE_KEY + ".globalclient" ) );

		final String globalConfig = taskContext.getConfigurationMap().get( "defaultcheckbox" );
		String taskClient = taskContext.getConfigurationMap().get( "allowAllConnect" );
		if ( "true".equals( globalConfig ) )
		{
			taskClient = context.get( "globalclient" ).toString();
			if ( "".equals( taskClient ) )
			{
				taskClient = "false";
			}
		}
		final boolean sslTrustAll = "true".equalsIgnoreCase( taskClient.trim() );
		String localdir = nullTrim(taskContext.getConfigurationMap().get("tasklocaldir"));
		String mavenHome = nullTrim(taskContext.getConfigurationMap().get("mavenhome"));

		String taskUsername = nullTrim(taskContext.getConfigurationMap().get("taskusername"));
		String taskPassword = nullTrim(taskContext.getConfigurationMap().get("taskpassword"));
		String taskUrl = nullTrim(taskContext.getConfigurationMap().get("taskurl"));
		String taskReleaseRepo = nullTrim(taskContext.getConfigurationMap().get("taskreleaserepo"));

		if ( globalConfig.equals( "true" ) )
		{
			taskUsername = checkGlobalConfig( taskUsername, "username", context );
			taskPassword = checkGlobalConfig( taskPassword, "password", context );
			taskUrl = checkGlobalConfig( taskUrl, "url", context );
			taskReleaseRepo = checkGlobalConfig( taskReleaseRepo, "releaserepo", context );
		}

		log.addBuildLogEntry( "URL:  " + taskUrl );
		log.addBuildLogEntry( "RELEASE REPO:  " + taskReleaseRepo );

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
			String[] arg = new String[]{localdir};
			p.setProperty( "art.username", taskUsername );
			p.setProperty( "art.password", taskPassword );
			p.setProperty( "repo.name", taskReleaseRepo );
			p.setProperty( "art.url", taskUrl );
			p.setProperty( "mvn.home", mavenHome );
			p.setProperty( "ssl.trustAll", Boolean.toString( sslTrustAll ) );
			PreApp.invoke( arg, p, log::addBuildLogEntry );
		}
		catch ( Exception e )
		{
			logStackTrace(log, e);
			taskResultBuilder.failed();
		}

		return taskResultBuilder.build();
	}

	private String checkGlobalConfig(String taskTerm, String otherTerm, Map<String, Object> context) {
		if ( nullTrim(taskTerm).length() > 2 || context.get( otherTerm ) == null)
		{
			return taskTerm;
		}
		else if ( context.get( otherTerm ).toString().length() > 2 )
		{
			return context.get( otherTerm ).toString();
		}
		else
		{
			throw new RuntimeException( "You forgot to enter a " + otherTerm );
		}
	}

	private static String nullTrim(String s) {
		return s != null ? s.trim() : "";
	}

	public static void logStackTrace( BuildLogger log, Throwable t )
	{
		StringWriter sw = new StringWriter( 4096 );
		PrintWriter pw = new PrintWriter( sw );
		t.printStackTrace( pw );
		pw.flush();
		pw.close();
		log.addBuildLogEntry( sw.toString() );
	}

}
