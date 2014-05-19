package com.tkruse.gradle.groovysh

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec

abstract class ShellTask extends JavaExec {

    abstract ShellTaskExtension getTaskExtension()

    protected ShellTask(final String taskConfigurationName) {
        this.group = 'help'
        this.outputs.upToDateWhen { false }
        this.dependsOn(PatchedMainCompileTask.NAME)
        // use an independent configuration for the task dependencies, so that application compile is not influenced
        project.configurations.create(taskConfigurationName)
        TaskHelper.addGroovyDependencies(project, taskConfigurationName, project.groovysh.groovyVersion)

        List<String> jvmArgs = taskExtension.jvmArgs
        if (jvmArgs != null && jvmArgs.size() > 0) {
            this.jvmArgs = jvmArgs
        }
        List<String> args = taskExtension.args
        if (args != null && args.size() > 0) {
            this.args = args
        }
        File workingDir = taskExtension.workingDir
        if (workingDir != null) {
            this.workingDir = workingDir
        }

        FileCollection bootstrapClasspath = taskExtension.bootstrapClasspath
        if (bootstrapClasspath != null) {
            this.bootstrapClasspath = bootstrapClasspath
        }

        this.enableAssertions = taskExtension.enableAssertions

        Map<String, Object> environment = taskExtension.environment
        if (environment != null) {
            this.environment = environment
        }
        OutputStream errorOutput = taskExtension.errorOutput
        if (errorOutput != null) {
            this.errorOutput = errorOutput
        }
        String maxHeapSize = taskExtension.maxHeapSize
        if (maxHeapSize != null) {
            this.maxHeapSize = maxHeapSize
        }
        InputStream standardInput = taskExtension.standardInput
        if (standardInput != null) {
            this.standardInput = standardInput
        }
        OutputStream standardOutput = taskExtension.standardOutput
        if (standardOutput != null) {
            this.standardOutput = standardOutput
        }
        Map<String, Object> systemProperties = taskExtension.systemProperties
        if (systemProperties != null) {
            this.systemProperties = systemProperties
        }
    }

    @Override
    void exec() {
        TaskHelper.checkDaemon(project)
        TaskHelper.checkQuiet(project)
        TaskHelper.checkParallel(project)

        String className = 'PatchedMain'
        this.classpath = this.classpath.add(project.files(new File(project.buildDir, 'groovyshClasses')))
        //this.main = 'org.codehaus.groovy.tools.shell.Main'
        this.main = className
        super.exec()
    }
}
