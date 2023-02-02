/**
 * 
 */
package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Collection;
import jdk.jfr.Category;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import projects.service.Unit;
import provided.util.DaoBase;
/**
 * @author liamorales
 *
 */
// This layer will read write data to tables 
// will not create exceptions, and data is generally not transformed, 
// here data is put into DAO classes that model the tables, then it will return multiple rows, (fetchAll)
// or can return single rows (fetchByld)
public class ProjectsDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	@SuppressWarnings("unused")
	private static final String MATERIAL_TALBE = "material";
	private static final String PROJECT_TABLE = "project";
	@SuppressWarnings("unused")
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	@SuppressWarnings("unused")
	private static final String STEP_TABLE = "step";

	
	public Project insertProject(Project project) {
		// here we will have the formatter off
		String sql = "" + "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes)" + "VALUES" + "(?, ?, ?, ?, ?)";
		// HERE WILL WILL TURN FORMATTER BACK ON

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn); // inDAOBase

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();
				// method from DAOBase, and if we haven't had any exceptions, here we commit.

				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM" + PROJECT_TABLE + " ORDER BY project_name ";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();//will return projects

					while (rs.next()) { //will return an empty list
						Project project = new Project();
						
						project.setActualHours(rs.getBigDecimal("actual hours"));
						project.setDifficulty(rs.getObject("difficulty" , Integer.class));
						project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
						project.setNotes(rs.getString("notes"));
						project.setProjectId(rs.getObject("project_id", Integer.class));
						project.setProjectName(rs.getString("project_name"));
						
						projects.add(project);
						
					}

					return projects;
				}
			}

			catch (SQLException e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}

		catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project>fetchProjectById(Integer projectId){
		String sql = "SELECT * FROM " + PROJECT_TABLE + "WHERE project_id = ? ";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try {
				Project project = null;
			
			//here we are getting our details of our projects, pass info from sql
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				
				try (ResultSet rs = stmt.executeQuery()){
					if (rs.next()) {
					
						project = (extract(rs, Project.class));
					}
				}
			}
			
			if (Objects.nonNull(project)) {
				project.getMaterials()
				.addAll(fetchMaterialsForProject(conn, projectId));
				
				project.getSteps()
				.addAll(fetchStepsForProject(conn, projectId));
				
				project.getCategories()
				.addAll(fetchCategoriesForProject(conn, projectId));
			}
			
			commitTransaction(conn);
			return Optional.ofNullable(project);
			}
			
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		
		catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException{
		//here formatter is off formatter: off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
				+ "JOIN " + PROJECT_CATEGORY_TABLE + "pc USING (category_id)"
				+ "WHERE project_id = ? ";
		//formatter:on is on 
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()){
				List<Category> categories = new LinkedList<Category>();
				
				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}
	
	private List<Step> fetchStepsForProject (Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM" + STEP_TABLE + "WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()){
				List<Step> steps = new LinkedList<>();
				
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	}
	
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException{
		String sql = "SELECT * FROM " + MATERIAL_TALBE + "WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter (stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material>materials = new LinkedList<>();
				
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
		}
	}
	
	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on
		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updated;
				
			} catch (SQLException e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				boolean deleted = stmt.executeUpdate() == 1;
				
				commitTransaction(conn);
				return deleted;
			} catch (SQLException e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Unit> fetchAllUnits() {
		String sql = "SELECT * FROM " + UNIT_TABLE + "ORDER BY unit_name_singular";
		
		try ( Connection conn = DbConnection.getConnection()){
		startTransaction (conn);
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			try(ResultSet rs = stmt.executeQuery()){
				List<Unit> units = new LinkedList<>();
				
				while(rs.next()) {
					units.add(extract(rs, Unit.class));
				}
				return units;
						
			}
		}
		}catch (SQLException e) {
			throw new DbException(e);
		}
		return null;
	}

	public void addMaterialToProject(Material material) {
		String sql = "INSERT INTO" + MATERIAL_TABLE + "(project_id, unit_id, material_name, instruction, ingredient_order, amount)"
		+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		try(Connection conn = DbConnection.getConnection()){
		startTransaction(conn);
		
		try {
			Integer order = getNextSequenceNumber(conn, material.getProjectId(), 
					MATERIAL_TABLE, "project_id");
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, material.getProjectId(), Integer.class);
				setParameter (stmt, 2, material.getUnit(), Integer.class);
				setParameter(stmt, 3, material.getMaterialName(), String.class);
				setParameter(stmt, 4, material.getInstruction(), String.class);
				setParameter(stmt, 5, order, Integer.class);
				setParameter(stmt, 6, material.getAmount(), BigDecimal.class);
				
				stmt.executeUpdate();
				commitTransaction(conn);
			}
		}
		catch (Exception e) {
			rollbackTransaction(conn);
			throw new DbException(e);
		}
			
		}catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public void addStepToProject(Step step) {
		String sql =
				"INSERT INTO " + STEP_TABLE + "(project_id, step_order, step_text)"
				+ "VALES (?, ?, ?)";
		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			Integer order = getNextSequenceNumber(conn, step.getProjectId(),
					STEP_TABLE, "project_id");
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, step.getProjectId(), Integer.class);
				setParameter(stmt, 2, order, Integer.class);
				setParameter(stmt, 3, step.getStepText(), String.class);
				
				stmt.executeUpdate();
				commitTransaction(conn);
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException(e);
		
	} 
	}

 	public List<jdk.jfr.Category> fetchAllCategories() {
		String sql = "SELECT * FROM " + CATEGORY_TABLE + "ORDER BY category_name";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				try(ResultSet rs = stmt.executeQuery()){
					List<Category> categories = new LinkedList<>();
					
					while(rs.next()) {
						categories.add(extract(rs, Category.class));
					}
					return categories;
				}
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException (e);
		}
	}

	public void addCategoryToProject(Integer projectId, String category) {
		String subQuery = "(SELECT category_id FROM " + CATEGORY_TABLE + "WHERE category_name = ?)";
		
		String sql = "INSERT INTO " + PROJECT_CATEGORY_TABLE + 
				"(project_id, category_id) VALUES (?, " + subQuery + ")";

		try (Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			setParameter (stmt, 2, category, String.class);
			
			stmt.executeUpdate();
			commitTransaction(conn);
			}
				catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
			}catch (SQLException e) {
				throw new DbException(e);
			}
			
	}

	public List<Step> fetchProjectSteps(Integer projectId) {
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try {
				List<Step> steps = fetchProjectSteps(conn, projectId);
				commitTransaction(conn);
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Step> fetchProjectSteps(Connection conn, Integer projectId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean modifyProejctStep(Step step) {
		String sql = "UPDATE " + STEP_TABLE + " SET step_text = ? WHERE step_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, step.getStepText(), String.class);
				setParameter(stmt, 2, step.getStepId(), Integer.class);
				
				//here is our update statement
				boolean updated = stmt.executeUpdate()==1;
				commitTransaction(conn);
				
				return updated;
			}
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch (SQLException e) {
			throw new DbException(e);
		}
	}
}
