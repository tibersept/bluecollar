/**
 * 
 */
package com.isd.bluecollar.controller;

import java.util.List;

import com.isd.bluecollar.data.store.Project;

/**
 * Project controller.
 * @author doan
 */
public class ProjectController {

	/**
	 * Adds a project to the list of projects.
	 * @param aUser the user
	 * @param aName the project name
	 * @param aDescription the project description
	 * @return the list of all user projects
	 */
	public List<String> addProject( String aUser, String aName, String aDescription ) {
		Project project = new Project();
		project.addProject(aUser, aName, aDescription);
		return project.getProjects(aUser, true);
	}
	
	/**
	 * Returns a list of all user projects.
	 * @param aUser the user
	 * @return the list of all projects
	 */
	public List<String> getProjects( String aUser ) {
		Project project = new Project();
		return project.getProjects(aUser,  true);
	}
}