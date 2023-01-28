/**
 * 
 */
package projects;

import java.math.BigDecimal;
import java.sql.Connection;
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
public class ProjectsApp {

	// going to place the scanner that represents the console input
	private Scanner scanner = new Scanner(System.in); // this is the inverse of System.print.out
	private ProjectService projectService = new ProjectService();

	// here we will have formatter off

	private List<String> operations = List.of(
			"1) Create and populate all tables",
			"2) Create project",
			"3) List projects");

	// here formatter will be on
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
					createTables();
					break;
					//To catch if they enter a number that isn't 1 or 0 we write below
					default:
						System.out.println("\n + "  + operations + "is not valid, please try again.");
					break;
					
				case 2: 
					createProject();
					break;
					
				case 3:
					listProjects();
					break;
				}// here we will catch any type of exception
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + "Try again");
			}
		}

	}
	
	private void listProjects() {
		List<Project> projects = projectService.fetchProjects();
		System.out.println("\nprojects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() + " : " + project.getProjectName()));
	}
	
	private boolean exitMenu() {
		System.out.println("Exiting selection, bye!");
		return true;
		//Now when the user wants to leave the application it will give them a message and the app will terminate.
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
	
	

	private void createTables() {
		projectService.createAndPopulateTables();
		System.out.println("\nTables have been created");

	}  
	
	

//Below will be our get INT input method
	private int getUserSelection() {
		printGetUserSelections();
		Integer input = getIntInput("Enter an operation number here(select Enter to quit");

		return Objects.isNull(input) ? -1 : input; // This can return null if it isn't a valid number
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

	private void printGetUserSelections() {
		System.out.println();
		System.out.println("Here are your options:");
		;

		operations.forEach(line -> System.out.println(" " + line)); // This will print all of our selections

	}
	
	@SuppressWarnings({ "unused" })
	public class ProjectApp {
		private Scanner scanner = new Scanner (System.in);
		private ProjectService projectService = new ProjectService();
		private Project curProject;
		
		//here the formatter if off
		
		private List<String> operations = List.of(
				"1) Add a project",
				"2) List projects",
				"3) Select a project"
				);
		//formatter is on;
		
		@SuppressWarnings("unused")
		private void processUserSelections() {
			boolean done = false;
			
			while(!done) {
				try {
					int selection = getUserSelection();
					
					switch(selection) {
					case -1: 
						done = exitMenu();
						break;
						
					case 1: 
						createProject();
						break;
						
					case 2:
						listProjects();
						break;
						
					case 3: 
						selectProject();
						break;
					
					default:
						System.out.println("\n" + selection + "is not a valid selection. Try again.");
						break;
					}
				}
				
				catch(Exception e ) {
					System.out.println("\nError: " + e + "Try again");
				}
			}
		}
		
		private void selectProject() {
			listProjects();
			Integer projectId = getIntInput ("Enter a project ID to select a project");
			
			/*de-select the selected project */
			
		curProject = null;
		
		// This will throw an exception if an invalid project Id is entered 
		curProject = projectService.fetchProjectById(projectId);
		}
		private void listProjects() {
			List<Project> projects = projectService.fetchAllProjects();
			
			System.out.println("\nProjects:");
			
			projects.forEach(project -> System.out.println(" " + project.getProjectId()
			+ ": " + project.getProjectName()));
		}
		//now we will do the method to print menu selections, per line
		
		@SuppressWarnings("unused")
		private void printOperations() {
			System.out.println("\nThese are the selections availbale. Press enter to quit:");
			
			operations.forEach(line ->    System.out.println(" " + line));
			
			//With enhanced for loop, for(String line : operations 
			
			if (Objects.isNull(curProject)) {
				System.out.println("\nNo working on project in status");
			}
			else {
				System.out.println("\nstatus alive for working project" + curProject);
			}
		}
		public Scanner getScanner() {
			return scanner;
		}
		public void setScanner(Scanner scanner) {
			this.scanner = scanner;
		}
	}

}
