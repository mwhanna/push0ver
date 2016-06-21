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

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;

public class PreConfig extends AbstractTaskConfigurator
{

	public Map<String, String> generateTaskConfigMap( final ActionParametersMap params, final TaskDefinition previousTaskDefinition )
	{
		final Map<String, String> config = super.generateTaskConfigMap( params, previousTaskDefinition );
		config.put( "tasklocaldir", params.getString( "tasklocaldir" ) );
		config.put( "allowAllConnect", params.getString( "allowAllConnect" ) );
		config.put( "defaultcheckbox", params.getString( "defaultcheckbox" ) );
		config.put( "mavenhome", params.getString( "mavenhome" ) );
		config.put( "taskusername", params.getString( "taskusername" ) );
		config.put( "taskpassword", params.getString( "taskpassword" ) );
		config.put( "taskurl", params.getString( "taskurl" ) );
		return config;
	}

	public void validate( final ActionParametersMap params, final ErrorCollection errorCollection )
	{
		super.validate( params, errorCollection );
	}

	public void populateContextForCreate( final Map<String, Object> context )
	{
		super.populateContextForCreate( context );
		context.put( "defaultcheckbox", "true" );
	}

	public void populateContextForEdit( final Map<String, Object> context, final TaskDefinition taskDefinition )
	{
		super.populateContextForEdit( context, taskDefinition );
		context.put( "tasklocaldir", taskDefinition.getConfiguration().get( "tasklocaldir" ) );
		context.put( "allowAllConnect", taskDefinition.getConfiguration().get( "allowAllConnect" ) );
		context.put( "defaultcheckbox", taskDefinition.getConfiguration().get( "defaultcheckbox" ) );
		context.put( "taskusername", taskDefinition.getConfiguration().get( "taskusername" ) );
		context.put( "mavenhome", taskDefinition.getConfiguration().get( "mavenhome" ) );
		context.put( "taskpassword", taskDefinition.getConfiguration().get( "taskpassword" ) );
		context.put( "taskurl", taskDefinition.getConfiguration().get( "taskurl" ) );
	}

	public void populateContextForView( final Map<String, Object> context, final TaskDefinition taskDefinition )
	{
		super.populateContextForView( context, taskDefinition );
		context.put( "tasklocaldir", taskDefinition.getConfiguration().get( "tasklocaldir" ) );
		context.put( "allowAllConnect", taskDefinition.getConfiguration().get( "allowAllConnect" ) );
		context.put( "defaultcheckbox", taskDefinition.getConfiguration().get( "defaultcheckbox" ) );
		context.put( "taskusername", taskDefinition.getConfiguration().get( "taskusername" ) );
		context.put( "mavenhome", taskDefinition.getConfiguration().get( "mavenhome" ) );
		context.put( "taskpassword", taskDefinition.getConfiguration().get( "taskpassword" ) );
		context.put( "taskurl", taskDefinition.getConfiguration().get( "taskurl" ) );
	}

}
