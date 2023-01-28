/**
 * 
 */
package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

/**
 * @author liamorales
 *
 */
public class DbConnection {

	private static final String SCHEMA = "projects";
	private static final String USER = "projects";
	private static final String PASSWORD = "projects";
	private static final String HOST = "localhost";
	private static final int PORT = 3306;

	public static Connection getConnection() {
		String uri = String.format("jdbc: mysql://%s: %d/%s?user=%s&password=%s", HOST, PORT, SCHEMA, USER, PASSWORD);
		
	
		
		try {
			Connection conn = DriverManager.getConnection(uri);
			System.out.println("Successfully obtained connection!");
			return conn;
		} catch (SQLException e) {
			System.out.println("Error getting connection " + uri);
			throw new DbException("Unable to get connection at \" + uri");
		}
	}
}
