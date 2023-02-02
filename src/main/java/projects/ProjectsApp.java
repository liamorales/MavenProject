/**
 * 
 */
package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import jdk.jfr.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
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
			"3) List projects",
			"4) Set Current Project",
			"5) Find material to current project",
			"6) Add step to Current Project",
			"7) Add category to current Project",
			"8) Modify step in current Project",
			"9) Delete Project");
	

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
				case 4: 
					setCurrentProject();
					break;
					
				case 5:
					addMaterialToCurrentProject();
					break;
					
				case 6: 
					addStepToCurrentProject();
					
				case 7:
					addCategoryToCurrentProject();
					break;
					
				case 8:
					modifyStepInCurrentProject();
					break;
					
				case 9:
					deleteProject();
					break;
					
				}// here we will catch any type of exception
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + "Try again");
			}
		}

	}
	private void deleteProject() {
	listProjects();
	Integer projectId = getIntInput("Enter the ID of the project to delete");
	
	if (Objects.nonNull(projectId)) {
		projectService.deleteProject(projectId);
		
		if(Objects.nonNull(projectId)&& curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}
	
}
	//method modifyStepInCurrentProject is our update step in CRUD
	private void modifyStepInCurrentProject() {
		if (Object.isNull(curProject)) {
			System.out.println("\n Please select a project first");
			return;
		}
	List<Step> steps = projectService.fetchSteps(curProject.getProjectId());
	
	System.out.println("\nSteps for current project");
	steps.forEach(step -> System.out.println(" " + step));
	
	Integer stepId = getIntInput("Enter step ID of step to modify");
	
	if(Objects.nonNull(stepId)) {
		String stepText = getStringInput("Enter new step text");
		
		if(Objects.nonNull(stepText)) {
			Step step = new Step();
			
			step.setStepId(stepId);
			step.setStepText(stepText);
			
			projectService.modifyStep(step);
			cuProject = projectService.fetchProjectById(curProject.getProjectId()); 
		}
	}
}
	private void addCategoryToCurrentProject() {
		if (Object.isNull(curProject)) {
			System.out.println("\n Please select a project first");
			return;
		}
		List<Category> categories = projectService.fetchCategories();
		
		categories.forEach(
				category -> System.out.println(" " + category.getCategoryName()));
	String category = getStringInput ("Enter the category to add "); 
	
	if (Objects.nonNull(category)) {
		projectService.addCategoryToProject(curProject.getProjectId(), category);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}
}
	private void setCurrentProject() {
		// TODO Auto-generated method stub
		
	}
	

	
	private void addStepToCurrentProject() {
		if (Object.isNull(curProject)) {
			System.out.println("\n Please select a project first");
			return;
		}
		
		String stepText = getStringInput ("Enter the step text");
		
		if(Objects.nonNull(stepText)) {
			Step step = new Step();
			
			step.setProjectId(curProject.getProjectId());
			step.setStepText(stepText);
			
			projectService.addStep(step);
			curProject = projectService.fetchProjectById(step.getProjectId());
		}
	
}

	private void addMaterialToCurrentProject() {
	if (Object.isNull(curProject)) {
		System.out.println("\n Please select a project first");
		return;
	}
	String name = getStringInput (" Enter material name");
	String insturction = getStringInput("Enter an instruciton if any ");
	Double  inputAmount = getDoubleInput (" Enter the material amount needed");
	List<Unit> units = projectService.fetchUnits();
	
	BigDecimal amount = Objects.isNull(inputAmount) ? null : new BigDecimal(inputAmount).setScale(2);
	
	System.out.println("units:");
	
	units.forEach(unit -> System.out.println(" " + unit.getUnitId() + " : " + unit.getUnitNameSingular() + "(" + unit.getNamePlural() + ")"));
	
	Integer unitId = getIntInput ("Enter a unit ID (press enter for none)");
	
	Unit unit = new Unit();
	unit.setUnitId(unitId);
	
	Material material = new Material();
	
	material.setProjectId(curProject.getProjectId());
	material.setUnit(unit);
	material.setMaterialName(name);
	material.setInstructin(instruction);
	material.setAmount(amount);
	
	projectService.addMaterial(material);
	curProject = projectService.fetchProjectById(material.getProjectId());
	
	
}
	private Double getDoubleInput(String string) {
		// TODO Auto-generated method stub
		return null;
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
