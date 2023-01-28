package projects.service;

import projects.dao.ProjectsDao;
import java.util.List;
import java.util.NoSuchElementException;

import projects.entity.Project;



public class ProjectService {

	//this application is what mostly acts as a pass-through, between the 
	//main application file that runs and the DAO file data layer
	
	
	private ProjectsDao projectDao = new ProjectsDao();
	

	public Project addProject(Project project) {
		
		return projectDao.insertProject(project);
	}
		
	/* Here this next method will call the project DAO to retrieve all project rows without the other stuff. 
	 * but will return a list of project records 
	 */
	
	public List<Project>fetchAllProjects(){
		return projectDao.fetchAllProjects();	
	}
	
	/*This next method calls the project DAO to now get all the details that we didn't call in the above method. If the project ID is 
	 * invalid, an exception will be thrown. 
	 */
	
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
				"Project with project Id = " + projectId + "does not exist."));
	}

	public void createAndPopulateTables() {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
