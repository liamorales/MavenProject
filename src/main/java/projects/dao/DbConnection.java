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

//This file is used to connect our project to the database driver. The driver is used to manage connections
//send SQL to a database and manage results from teh database in a consistent manner. 
//The POM file will control our build 
//The MySQL driver will be a dependency in the POM
//In the end our application will be a database client
public class DbConnection {

	private static  String SCHEMA = "projects";
	private static  String USER = "projects";
	private static  String PASSWORD = "projects";
	private static  String HOST = "localhost";
	private static final int PORT = 3306;

	public static Connection getConnection() {
		String url = String.format("jdbc: mysql://%s:%d/%s?user=%s&password=%s", HOST, PORT, SCHEMA, USER, PASSWORD);
		
	
		
		try {
			Connection conn = DriverManager.getConnection(url);
			System.out.println("Connection to schem ' " + SCHEMA + " ' Successfully obtained connection!");
			return conn;
		} catch (SQLException e) {
			System.out.println("Error getting connection " + url);
			throw new DbException("Unable to get connection at \" + url");
		}
	}
}
