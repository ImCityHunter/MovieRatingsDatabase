import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is used to link all classes to the same database path;
 * @author hky
 *
 */
public class Connect {
	static String databaseURL = "jdbc:derby://localhost:1527/publication;create=true";
	private static final String embedded = "jdbc:derby:publication;create=true";
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	

	public static Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = null;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(embedded);
		} catch (SQLException e) {
			System.err.println("Database not connected");
		} 
		return conn; 
	}

}
