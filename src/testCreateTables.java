import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is to create the database and insert all the default valid information from default files 
 * as information stored in the database already
 *
 */
public class testCreateTables {
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	
	public static void main(String[] args) {
		try {
			Connection conn = Connect.newConnection();
			Statement stmt = conn.createStatement();
			CreateTables.create(conn, stmt);
			setDefaultData.defaultData(conn, stmt);		
			UIFunctions.printAllTable(conn);
			conn.close();
					
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

}


