<html>
  <head>
    <title>Push0ver - 2. Push to Artifactory Configuration</title>
    <meta name="decorator" content="atl.admin">
  </head>
  <body>
  [@ww.textfield labelKey="push0ver.task.localdir" name="tasklocaldir" required='false'/]
  [@ww.textfield labelKey="push0ver.task.mvnhome" name="mavenhome" required='false'/]
  [@ww.checkbox labelKey='push0ver.push.label' name='pushcheckbox' toggle='true'/]
  [@ww.checkbox labelKey='push0ver.checkbox.label' name='checkbox' toggle='true'/]
  [@ww.checkbox labelKey='push0ver.task.client' name='taskclient' toggle='true'/]
  <h2>Below only required if not using Defaults</h2>
  [@ww.textfield labelKey="push0ver.task.username" name="taskusername" required='false'/]
  [@ww.textfield labelKey="push0ver.task.password" name="taskpassword" required='false'/]
  [@ww.textfield labelKey="push0ver.task.url" name="taskurl" required='false'/]
  [@ww.textfield labelKey="push0ver.task.releaserepo" name="taskreleaserepo" required='false'/]
  [@ww.textfield labelKey="push0ver.task.noderepo" name="tasknoderepo" required='false'/]
  [@ww.textfield labelKey="push0ver.task.snaprepo" name="tasksnaprepo" required='false'/]
  </body>
</html>
