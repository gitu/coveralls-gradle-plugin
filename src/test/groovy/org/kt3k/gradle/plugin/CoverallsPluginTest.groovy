package org.kt3k.gradle.plugin

import org.junit.Test
import static org.junit.Assert.*

import static org.mockito.Mockito.*

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task

class CoverallsPluginTest {

	@Test
	void testApply() {
		Task task = mock(Task.class)

		Project project = mock(Project.class)
		when(project.task('coveralls')).thenReturn(task)

		Plugin plugin = new CoverallsPlugin()
		plugin.apply(project)

		verify(project).task('coveralls')
		verify(task).leftShift(any(Closure))
	}

}