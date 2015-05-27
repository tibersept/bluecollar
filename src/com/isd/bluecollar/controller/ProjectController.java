/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller;

import java.util.ArrayList;
import java.util.List;

import com.isd.bluecollar.data.internal.Project;
import com.isd.bluecollar.data.store.ProjectDP;

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
		ProjectDP project = new ProjectDP();
		project.addProject(aUser, aName, aDescription);
		return mapProjectsToNames(project.getProjects(aUser, true));
	}
	
	/**
	 * Returns a list of all user projects.
	 * @param aUser the user
	 * @return the list of all projects
	 */
	public List<String> getProjects( String aUser ) {
		ProjectDP project = new ProjectDP();
		return mapProjectsToNames(project.getProjects(aUser,  true));
	}
	
	/**
	 * Extracts the project names from the project list.
	 * @param aProjects the project list
	 * @return a project name list
	 */
	private List<String> mapProjectsToNames( List<Project> aProjects ) {
		List<String> list = new ArrayList<String>(aProjects.size());
		for( Project p : aProjects ) {
			list.add(p.getName());
		}
		return list;
	}
}