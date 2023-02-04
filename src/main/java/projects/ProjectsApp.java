/**
 * 
 */
package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/**
 * @author liamorales
 *
 */
/* We begin by sorting out application by libraries and packages. This will satisfy user input and output
*concerns and business rules and for proper data acquisition and persistence. 
*this file is our input/outpt layer and will interact with the user
*This Layer will gether information from the User, handles exceptions, processes menu operatons and interacts with 
*the service layer*/
public class ProjectsApp {

	// going to place the scanner that represents the console input
	private Scanner scanner = new Scanner(System.in); // this is the inverse of System.print.out
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	// @formatter:off

	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project");
	

	// @formatter: on
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
		
		

	}
//This method will print the operations, get a new user menu selection and perform the requested 
	//operation and will repeat doing so until told to stop
	private void processUserSelections() {
		boolean done = false;
		// here we are going to get input from the user
		while (!done) {
			
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break; // if it turns to be -1, we will exit the selection
					
				case 1: 
					createProject(); //here's the create part of the CRUD
					break;
					
				case 2:
					listProjects();
					break;
				case 3: 
					selectProject();
					break;
					
				case 4:
					updateProjectDetails(); //here we are Updating, the U in CRUD
					break;
					
				case 5:
					deleteProject(); // here is the D in CRUD 
					break;
				
				default:
					System.out.println("\n" + selection + "is not a valid selection. Try again.");
					break;
					
				}// here we will catch any type of exception
			} 
			catch (Exception e) {
				System.out.println("\nError: " + e.toString() + "Try again");
			}
		}

	}
	
	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		
		projectService.deleteProject(projectId);
		System.out.println("Project" + projectId + "was deleted successfully.");
		
		if (Objects.nonNull(projectId)) {
			projectService.deleteProject(projectId);
			
			System.out.println("you have deleted a project");
			
			if(Objects.nonNull(curProject)&& curProject.getProjectId().equals(projectId)) {
				curProject = null;
			}
		}
		/*The deleteProject method is the last step in our CRUD application. It is the delete step*/
	}
	private void updateProjectDetails() {
		
		if (Objects.isNull(curProject)) {
			System.out.println("\n Please select a project first");
			return;
		}
	String projectName = 
			getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
	BigDecimal estimatedHours = 
			getDecimalInput("Enter the estimated hours + [" + curProject.getEstimatedHours() + "]");
	
	BigDecimal actualHours= 
			getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
	
	Integer difficulty = 
			getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
	
	String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
	
	Project project = new Project ();
	
	project.setProjectId(curProject.getProjectId());
	
	project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
	
	project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
	
	project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
	project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
	project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
	
	projectService.modifyProjectDetails(project);
	
	curProject = projectService.fetchProjectById(curProject.getProjectId());
	/*The updateProjectDetails method is our update step in the CRUD application. We can take not of how this is that 
	 * step because we are using SQL keywords such as update table set in these methods.*/
}
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput ("Enter a project ID to select a project");
		
		/*Unselect the selected project */
		
		curProject = null;
	
	 //This will throw an exception if an invalid project Id is entered 
	curProject = projectService.fetchProjectById(projectId);
	/*The selectProject method is the read step in our CRUD application. We can observe this from using
	 * SQL keywords such as SELECT *FRom table in its methods*/
	}
	private void createProject() {
		String projectName= getStringInput("Enter new project name");
		BigDecimal estimatedHours = getDecimalInput("Enter estimated hours");
		BigDecimal actualHours	= getDecimalInput("Enter actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput ("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("you have successfully created a project" + dbProject);
		/*The createProject method is the first step in our CRUD application. We can determine it is the first step because
		 * SQL keywords such as INSERT into table are used in its methods*/
}
	private boolean exitMenu() {
		System.out.println("Exiting selection, bye!");
		return true;
		//Now when the user wants to leave the application it will give them a message and the app will terminate.
}
	//Below will be our get INT input method
	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter an operation number here(select Enter to quit");

		return Objects.isNull(input) ? -1 : input; // This can return null if it isn't a valid number
}
	
	
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nprojects:");
		
		projects.forEach(project -> System.out
				.println("  " + project.getProjectId() + " : " + project.getProjectName()));
		//This method will list our menu options our for the user
}
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			// Here we are creating a BigDecimal object and we will set the decimal to two places
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid decimal number.");
		}
	}
	
	private Integer getIntInput(String prompt) {
			String input = getStringInput(prompt);
			// here we will allow the user to input a string from the scanner
			//we call string method, if we get null we get null, and if we don't get that we 
			//throw an exception 
			if (Objects.isNull(input)) {
				return null;
			}
			try {
				return Integer.parseInt(input);
				// parseInt will throw a number format exception if the string doesn't contain a
				// valid integer
			} catch (NumberFormatException e) {
				throw new DbException(input + "is not a valid number");
			}
		}
	private String getStringInput(String prompt) {
			System.out.print(prompt + ": ");
			String input = scanner.nextLine(); //allows us to get input from a scanner
			// here you will type in a line and press enter and it will return a line (this
			// is to get around that)

			return input.isBlank() ? null : input.trim();
			// if you enter a bunch of spaces it will return it as a null
		
		}
	private void printOperations() {
			System.out.println("\nThese are the selections availbale. Press enter to quit:");
			
			operations.forEach(line ->    System.out.println(" " + line));
			
			if (Objects.isNull(curProject)) {
				System.out.println("\nNo working on project in status");
			}
			else {
				System.out.println("\nstatus alive for working project" + curProject);
			}
		}
		
		
			
		
		
		
		

	
	
	
}
