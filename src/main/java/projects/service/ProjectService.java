package projects.service;

import projects.Unit;
import projects.dao.ProjectsDao;
import java.util.List;

import jdk.jfr.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
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

	public void createAndPopulateTables() {
		// TODO Auto-generated method stub
		
	}

	public List<Project> fetchProjects() {
		// TODO Auto-generated method stub
		return projectDao.fetchAllProjects(); 
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

public List<Unit>fetchUnits(){
	return projectDao.fetchAllUnits();
}

public void addMaterial(Material material) {
	projectDao.addMaterialToProject(material);
}

public void addStep(Step step) {
	projectDao.addStepToProject(step);
}

public List<Category> fetchCategories() {
	return projectDao.fetchAllCategories();
}

public void addCategoryToProject (Integer projectId, String category) {
	projectDao.addCategoryToProject(projectId, category);
}
public List<Step> fetchSteps(Integer projectId){
	return projectDao.fetchProjectSteps(projectId);
}

public void modifyStep(Step step) {
	if(!projectDao.modifyProejctStep(step)) {
		throw new DbException(
				"Step with ID=" + step.getStepId() + "does not exist.");
	}
	
}
}
	
	

