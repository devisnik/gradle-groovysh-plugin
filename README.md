# Gradle plugin providing a Groovy Shell

[![Build Status](https://travis-ci.org/tkruse/gradle-groovysh-plugin.svg)](https://travis-ci.org/tkruse/gradle-groovysh-plugin)

This plugin provides gradle tasks that start an interactive groovy shells, based on
the '[groovysh](http://groovy.codehaus.org/Groovy+Shell)' command that ships with any [Groovy](http://groovy.codehaus.org/) version.

The main feature is an Application shell, meaning a shell where your application
classes (Java or other JVM language) can be imported, instantiated and run. This allows you to interact
directly with your database-layer, service layer, running application, etc. without having to
change a line of your code.

There is also build shell, which allows to connect to a gradle project using the gradle tooling API to run builds.

Another particular feature is a build development shell that has a variable ```project``` which represents
your gradle project, allowing you to introspect your project after it has been instantiated. This can be useful
for developing custom gradle plugins, or debugging large setups.


This plugin is **Work In Progress**, expect some rough edges, but please do report troubles you face.


## Prerequisites

* [Java](http://www.java.com/)
* [Gradle](http://www.gradle.org) (From gradle wrapper is fine)

- **NO GROOVY INSTALLATION REQUIRED** (gradle will fetch it like any other dependency)
- **NO CHANGE TO YOUR JAVA CODE REQUIRED**


## Installing the plugin

Include the plugin in your build.gradle file like this:

```Groovy
apply plugin: 'groovysh'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.tkruse.gradle:gradle-groovysh-plugin:0.1.0'
    }
}

```

Currently your project needs to also have the java plugin applied for the ```shell``` task.

## Configuring the Plugin

No configuration is required. To change the defaults, see [Configuration](doc/Configuration.md)

If you encounter groovy version mismatches, explicitly set your system version (until I find a clean way to fix this).
```Groovy
groovysh {
    // groovyVersion determines the features of the shell and buildShell tasks.
    groovyVersion = '2.3.1'
}
```


## Using the Plugin

Invoke either shell task with option ```-q```.
If you have the gradle daemon configured, also add ```--no-daemon```

```bash
gradle -q shell
gradle -q buildShell
gradle -q buildDevShell
```
When using the gradle wrapper, that would be ```./gradlew``` instead.

You can find samples in the [samples subfolder](samples/README.md).



### Some things you can do with the ```shell```

- Instantiate a Spring Container (provided you add Spring dependencies to your project)
- Instantiate a Database connection, write business entities etc.
- Run your algorithms interactively

### Some things you can do with the ```buildShell```

```Groovy
groovy:000> import org.gradle.tooling.*
===> org.gradle.tooling.*
groovy:000> import org.gradle.tooling.model.*
===> org.gradle.tooling.model.*
groovy:000> connector = GradleConnector.newConnector()
===> org.gradle.tooling.internal.consumer.DefaultGradleConnector@6d3666fb
groovy:000> connector.forProjectDirectory(new File("."))
groovy:000> connection = connector.connect()
===> org.gradle.tooling.internal.consumer.DefaultProjectConnection@598b4d64
groovy:000> project = connection.getModel(GradleProject.class)
===> GradleProject{path=':'}
groovy:000> launcher = connection.newBuild()
===> org.gradle.tooling.internal.consumer.DefaultBuildLauncher@3a370a0
groovy:000> launcher.run()
```

### Some things you can do with the ```buildDevShell```:

```Groovy
groovy:000> project.tasks
===> [task ':assemble', task ':build', ...
groovy:000> project.configurations
===> [configuration ':appShellConf', configuration ':archives', ...
groovy:000> project.ext.properties
===> {org.gradle.parallel=false, org.gradle.daemon=false, SLF4J_VERSION=1.7.7}
groovy:000> project.repositories.each {println it.name}
BintrayJCenter
groovy:000> project.apply(plugin: 'java')
groovy:000> project.sourceSets.main.getCompileTaskName()
===> compileMain
groovy:000> project.tasks.compileJava.execute() // only executes once
groovy:000> project.tasks.clean.execute() // only executes once
groovy:000> project.gradle.startParameter
===> StartParameter{taskNames=[buildDevShell], ...}
```

### Caveats


Notice that the Groovy version for the build shell is the same as for gradle (1.8.6), whereas for the application
shell a much more recent version of Groovy (2.3.x) can be used. As a consequence the application shell is much prettier,
and the allowed syntax is different.

Also if you are new to groovysh, the number one quirk to know is that you **must not** declare variables, e.g.:

```Groovy
groovy:000> def x = 3
===> 3
groovy:000> x
Unknown property: x
groovy:000> x = 3
===> 3
groovy:000> x
===> 3
```

## TODOs

see [Configuration](doc/TODO.md)

## Installing the plugin to modify it and contribute

See - [Dev setup](doc/Contributing.md)

### Getting a shell without installing this plugin:

See the docs at:
 - [Manual Build Shell README](doc/InstallBuildDevShellManually.md)
 - [Manual App Shell README](doc/InstallAppShellManually.md)
Or just install Groovy and run ```groovysh``` with a suitable classpath.

## Compatibility

* Java Versions (6?, 7, 8?)

* Operating Systems (Ubuntu Precise)

* Gradle versions (1.8?, 1.9, 1.10, 1.11, 1.12)

* Groovy Versions (>= 2.2.1)
