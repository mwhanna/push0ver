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
	private String[] toFill = { "tasklocaldir", "allowAllConnect", "defaultcheckbox", "taskusername", "mavenhome",
			"taskpassword", "taskurl", "taskreponame"
	};

	public Map<String, String> generateTaskConfigMap( final ActionParametersMap params, final TaskDefinition previousTaskDefinition )
	{
		final Map<String, String> config = super.generateTaskConfigMap( params, previousTaskDefinition );
		fillConfig(config, params, toFill);
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

		fillContext(context, taskDefinition, toFill);
	}

	public void populateContextForView( final Map<String, Object> context, final TaskDefinition taskDefinition )
	{
		super.populateContextForView( context, taskDefinition );
		fillContext( context, taskDefinition, toFill);
	}

	private void fillContext(Map<String, Object> context, TaskDefinition taskDefinition, String[] toFill) {
		for (String s : toFill) {
			context.put(s, taskDefinition.getConfiguration().get(s));
		}
	}

	private void fillConfig(Map<String, String> config, ActionParametersMap params, String[] toFill) {
		for (String s : toFill) {
			config.put( s, params.getString( s ) );
		}
	}

}
