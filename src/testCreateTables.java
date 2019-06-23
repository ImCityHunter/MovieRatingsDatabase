




import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;






/**
 * This program tests the version of the publication database tables for Assignment 5 
 * that uses attributes for the PublishedBy and PublishedIn relations. The sample
 * data is stored in a tab-separated data file The columns of the data file are:
 * pubName, pubCity, jnlName, jnlISSN, artTitle, artDOI, auFamiily, auGiven, auORCID
 * 
 * @author philip gust
 */
public class testCreateTables {
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	public static void main(String[] args) {

        ResultSet rs = null;

		try {
			Connection  conn = Connect.newConnection();
			Statement stmt = conn.createStatement();
			CreateTables.create(conn, stmt);
			//call to store data into database
			setDefaultData.defaultData(conn, stmt);
			
			UIFunctions.printAllTable(conn);
			
			
		
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

}
