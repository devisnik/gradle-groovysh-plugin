package com.tkruse.gradle.groovysh

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class GroovyshPluginTest {

    @Test
    void testApplyNoJava() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'groovysh'
        Plugin plugin = project.plugins.getPlugin('groovysh')
        assert plugin instanceof GroovyshPlugin
        assert project.tasks.findByName('buildShell') == null
        assert project.tasks.findByName('shell') == null

        project.groovysh.enableBuildShell = true
        project.groovysh.enableAppShell = true
        // simulate AfterEvaluate
        ((GroovyshPlugin) plugin).setupTasks(project)

        assert project.tasks.findByName('buildShell') != null
        assert project.tasks.findByName('shell') == null
        assert project.tasks.buildShell instanceof BuildShellTask
        //assert project.tasks.shell instanceof ApplicationShellTask
    }

    @Test
    void testApplyJava() {
        Project project = TestHelper.createProjectWithPlugin()
        TestHelper.setupTasks(project)
        assert project.tasks.findByName('buildShell') != null
        assert project.tasks.findByName('shell') != null
        assert project.tasks.buildShell instanceof BuildShellTask
        assert project.tasks.shell instanceof ApplicationShellTask

        assert project.configurations.appShellConf != null
        assert project.configurations.buildShellConf != null
    }


}
