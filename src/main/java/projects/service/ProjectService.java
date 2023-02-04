package projects.service;

import projects.dao.ProjectsDao;
import java.util.List;
import projects.entity.Project;
import projects.exception.DbException;



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
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new DbException 
				("Project with project ID=" + projectId + " does not exist."));
				//here this is if no project exists it will throw this DbException
	}

	


public void modifyProjectDetails(Project project) {
	if(!projectDao.modifyProjectDetails(project)) {
		throw new DbException("Project with ID = " + project.getProjectId() + " does not exist.");
	}
	
}
public void deleteProject(Integer projectId) {
	if(!projectDao.deleteProject(projectId)) {
		throw new DbException("Recipe with ID = " + projectId + " does not exist.");
	}
	
}




}
	
	

