package com.tkruse.gradle.groovysh

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class TaskHelper {

    private final static String DAEMON_PROP = 'org.gradle.daemon'

    static void checkDaemon(final Project project, final String name) {
        if (project.hasProperty(DAEMON_PROP) && project.property(DAEMON_PROP) == 'true') {
            String msg = "$name: Do not run $name with gradle daemon (use --no-daemon)."
            println(msg)
            throw new IllegalStateException(msg)
        }
    }

    static void checkParallel(final Project project, final String name) {
        if (project.gradle.startParameter.parallelThreadCount != 0) {
            String msg = "$name: Do not run $name with parallel thread. (use --parallel-threads 0)"
            println(msg)
            throw new IllegalStateException(msg)
        }
    }

    static void checkQuiet(final Project project, final String name) {
        if (!project.gradle.startParameter.logLevel == LogLevel.QUIET) {
            String msg = "$name: Do not run $name with logging output. (use -q)"
            println(msg)
            throw new IllegalStateException(msg)
        }
    }

    static void addGroovyDependencies(final Project project, final String groovyVersion) {
        switch (groovyVersion) {
            case ~/1\.8\.[0-9].*/:
                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'org.fusesource.jansi:jansi:1.6')
                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'jline:jline:1.0')
                break
            case ~/0\.[0-9]\.[0-9].*/:
            case ~/1\.[0-9]\.[0-9].*/:
            case ~/2\.0\.[0-9].*/:
            case ~/2\.1\.[0-9].*/:
                // even with dependencies below, some conflict raises groovyCastException
                // for org.apache.commons.cli.HelpFormatter
//                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'org.fusesource.jansi:jansi:1.6')
//                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'jline:jline:1.0')
                //break;
            case ~/2\.2\.0.*/:
//                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'org.fusesource.jansi:jansi:1.10')
//                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'jline:jline:2.10')
                //break
                String msg = "Unsupported Groovy minor version '$groovyVersion'"
                println(msg)
                throw new IllegalStateException(msg)
            case ~/2\.2\.[0-9].*/:
            case ~/2\.3\.[0-9].*/:
                project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'jline:jline:2.11')
                break
            default:
                String msg = "Unknown Groovy minor version '$groovyVersion'"
                println(msg)
                throw new IllegalStateException(msg)
        }
        project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME, 'commons-cli:commons-cli:1.2')
        project.dependencies.add(ApplicationShellTask.CONFIGURATION_NAME,
                "org.codehaus.groovy:groovy-all:${groovyVersion}")
    }

}
