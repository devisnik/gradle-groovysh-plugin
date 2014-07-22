package com.tkruse.gradle.groovysh

import org.gradle.api.GradleScriptException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

/**
 * Plugin main class
 */
class GroovyshPlugin implements Plugin<Project> {

    public static final String PLUGIN_ID = 'com.github.tkruse.groovysh'
    public static final String NAME = 'groovysh'

    @Override
    void apply( final Project project ) {
        project.repositories.jcenter()
        project.extensions.create(NAME, GroovyshPluginExtension)
        // not sure how to create nested extentions else...
        project.groovysh.extensions.create(BuildDevShellTask.NAME, BuildDevShellTaskExtension)
        project.groovysh.extensions.create(BuildShellTask.NAME, BuildShellTaskExtension)
        project.groovysh.extensions.create(ApplicationShellTask.NAME, ApplicationShellTaskExtension)

        // need to have extensions read
        project.afterEvaluate {
            setupTasks(project)
        }
    }

    static void setupTasks(final Project project) {
        if (project.groovysh.enableBuildDevShell) {
            project.configurations.create(BuildDevShellTask.CONFIGURATION_NAME)

            try {
                project.tasks.create(BuildDevShellTask.NAME, BuildDevShellTask)
            } catch (InvalidUserDataException e) {
                // task already exists, not 100 lines of stacktrace needed to understand
                throw new GradleScriptException("$NAME: Cannot create task ${BuildDevShellTask.NAME}", e)
            }
            URLClassLoader loader = GroovyObject.classLoader

            if (Gradle.gradleVersion.startsWith('1')) {
                // gradle < 2.0 runs with groovy 1.8.6, groovysh needs jline 1.0
                project.dependencies.add(BuildDevShellTask.CONFIGURATION_NAME, 'jline:jline:1.0')
            } else {
                // gradle 2.0 runs with groovy 2.3.3, groovysh needs jline 2.11
                project.dependencies.add(BuildDevShellTask.CONFIGURATION_NAME, 'jline:jline:2.11')
            }

            project.configurations.getByName(BuildDevShellTask.CONFIGURATION_NAME).each { File file ->
                loader.addURL(file.toURL())
            }
        }
        if (project.groovysh.enableBuildShell) {
            try {
                project.tasks.create(BuildShellTask.NAME, BuildShellTask)
            } catch (InvalidUserDataException e) {
                // task already exists, not 100 lines of stacktrace needed to understand
                throw new GradleScriptException("$NAME: Cannot create task ${BuildShellTask.NAME}", e)
            }
        }
        if (project.groovysh.enableAppShell) {
            if (project.configurations.names.contains('runtime')) {
                try {
                    project.tasks.create(ApplicationShellTask.NAME, ApplicationShellTask)
                } catch (InvalidUserDataException e) {
                    // task already exists, not 100 lines of stacktrace needed to understand
                    throw new GradleScriptException("$NAME: Cannot create task ${ApplicationShellTask.NAME}", e)
                }
            }
        }
        if (project.groovysh.enableAppShell || project.groovysh.enableBuildShell) {
            try {
                project.tasks.create(PatchedMainCompileTask.NAME, PatchedMainCompileTask)
            } catch (InvalidUserDataException e) {
                // task already exists, not 100 lines of stacktrace needed to understand
                throw new GradleScriptException("$NAME: Cannot create task ${PatchedMainCompileTask.NAME}", e)
            }
        }
    }
}
