# TeamCity Kotlin Script Build Step plugin

This plugin introduces a new Kotlin Script build runner that allows users to use platform independent Kotlin scripts. 

The script can be either provided within the corresponding build step settings as a custom script or as a kts file.

To define external dependencies use the following annotations in the beginning of your script:

```
@file:Repository(<maven repository URL>)
@file:DependsOn(<maven coordinates>)
```

for example:

```
@file:Repository("https://mvnrepository.com")
@file:DependsOn("com.google.code.gson:gson:2.8.6")
```

Please note that these annotations will work with a custom script and will only work with a script file if it's name ends as `.main.kts`

## Building the plugin

To build the plugin please use `gradlew clean build pluginZip` 

Please find more details in [TeamCity Documentation](https://www.jetbrains.com/help/teamcity/kotlin-script.html)
 
