<html>
  <head>
    <title>Push0ver - 1. Extract Git Tag Configuration</title>
    <meta name="decorator" content="atl.admin">
  </head>
  <body>
  [@ww.textfield labelKey="push0ver.task.localdir" name="tasklocaldir" required='false'/]
  [@ww.textfield labelKey="push0ver.task.mvnhome" name="mavenhome" required='false'/]
  [@ww.checkbox labelKey='push0ver.checkbox.label' name='defaultcheckbox' toggle='true'/]
  [@ww.checkbox labelKey='push0ver.task.client' name='allowAllConnect' toggle='true'/]
  <h2>Below only required if not using Defaults</h2>
  [@ww.textfield labelKey="push0ver.task.username" name="taskusername" required='false'/]
  [@ww.textfield labelKey="push0ver.task.password" name="taskpassword" required='false'/]
  [@ww.textfield labelKey="push0ver.task.url" name="taskurl" required='false'/]
  [@ww.textfield labelKey="push0ver.task.releaserepo" name="taskreleaserepo" required='false'/]
  </body>
</html>
