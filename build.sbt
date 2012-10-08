import com.typesafe.startscript.StartScriptPlugin


scalariformSettings

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

seq(StartScriptPlugin.startScriptForClassesSettings: _*)