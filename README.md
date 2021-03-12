# TeamCity Kotlin Script Build Step plugin

This plugin introduces a new Kotlin Script build runner that allows users to use platform independent Kotlin scripts. 

The script can be either provided within the corresponding build step settings or as a kts file will be used.

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

## Building the plugin

To build the plugin please use `gradlew clean build pluginZip` 
 
