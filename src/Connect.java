import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class is used to link all classes to the same database path;
 * @author hky
 *
 */
public class Connect {
	static String create = "create=true";
	private static final String localhost_driver = "org.apache.derby.jdbc.ClientDriver";
	private static String localhostUrl = "jdbc:derby://localhost:8080/iRateDatabase;"+create;
	
	
	private static final String embedded_driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String embedded = "jdbc:derby:iRateDatabase;"+create;
	
	
	public static Connection newConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = null;
		Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
		try {
			Class.forName( embedded_driver ); 
			conn = DriverManager.getConnection(embedded,props);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Driver or Database not connected");
		} 
		
		return conn; 
	}

}
