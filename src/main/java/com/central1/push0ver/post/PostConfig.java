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

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;

public class PostConfig extends AbstractTaskConfigurator
{
	private String checkBox = "false";
	private String pushCheckBox = "true";
	private String taskClient = "false";

	public Map<String, String> generateTaskConfigMap( final ActionParametersMap params, final TaskDefinition previousTaskDefinition )
	{
		final Map<String, String> config = super.generateTaskConfigMap( params, previousTaskDefinition );
		config.put( "taskusername", params.getString( "taskusername" ) );
		config.put( "taskpassword", params.getString( "taskpassword" ) );
		config.put( "taskreponame", params.getString( "taskreponame" ) );
		config.put( "tasklocaldir", params.getString( "tasklocaldir" ) );
		config.put( "taskurl", params.getString( "taskurl" ) );
		config.put( "mavenhome", params.getString( "mavenhome" ) );
		config.put( "checkbox", params.getString( "checkbox" ) );
		config.put( "pushcheckbox", params.getString( "pushcheckbox" ) );
		config.put( "taskclient", params.getString( "taskclient" ) );
		checkBox = params.getString( "checkbox" );
		pushCheckBox = params.getString( "pushcheckbox" );
		taskClient = params.getString( "taskclient" );
		return config;
	}

	public void validate( final ActionParametersMap params, final ErrorCollection errorCollection )
	{
		super.validate( params, errorCollection );
	}

	public void populateContextForCreate( final Map<String, Object> context )
	{
		super.populateContextForCreate( context );
		context.put( "checkbox", checkBox );
		context.put( "pushcheckbox", pushCheckBox );
		context.put( "taskclient", taskClient );
	}

	public void populateContextForEdit( final Map<String, Object> context, final TaskDefinition taskDefinition )
	{
		super.populateContextForEdit( context, taskDefinition );
		context.put( "tasklocaldir", taskDefinition.getConfiguration().get( "tasklocaldir" ) );
		context.put( "taskreponame", taskDefinition.getConfiguration().get( "taskreponame" ) );
		context.put( "taskurl", taskDefinition.getConfiguration().get( "taskurl" ) );
		context.put( "taskusername", taskDefinition.getConfiguration().get( "taskusername" ) );
		context.put( "taskpassword", taskDefinition.getConfiguration().get( "taskpassword" ) );
		context.put( "mavenhome", taskDefinition.getConfiguration().get( "mavenhome" ) );
		context.put( "checkbox", taskDefinition.getConfiguration().get( "checkbox" ) );
		context.put( "pushcheckbox", taskDefinition.getConfiguration().get( "pushcheckbox" ) );
		context.put( "taskclient", taskDefinition.getConfiguration().get( "taskclient" ) );
	}

	public void populateContextForView( final Map<String, Object> context, final TaskDefinition taskDefinition )
	{
		super.populateContextForView( context, taskDefinition );
		context.put( "tasklocaldir", taskDefinition.getConfiguration().get( "tasklocaldir" ) );
		context.put( "taskreponame", taskDefinition.getConfiguration().get( "taskreponame" ) );
		context.put( "taskurl", taskDefinition.getConfiguration().get( "taskurl" ) );
		context.put( "taskusername", taskDefinition.getConfiguration().get( "taskusername" ) );
		context.put( "taskpassword", taskDefinition.getConfiguration().get( "taskpassword" ) );
		context.put( "mavenhome", taskDefinition.getConfiguration().get( "mavenhome" ) );
		context.put( "checkbox", taskDefinition.getConfiguration().get( "checkbox" ) );
		context.put( "pushcheckbox", taskDefinition.getConfiguration().get( "pushcheckbox" ) );
		context.put( "taskclient", taskDefinition.getConfiguration().get( "taskclient" ) );
	}

}
