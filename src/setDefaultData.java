import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class setDefaultData {
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie","Endorsement"};
	static String [] functions= {"isISSN","isDoi","isORCID","parseISSN","issnToString","orcidToString","parseOrcid"};
	public static void defaultData(Connection  conn, Statement stmt, ResultSet rs) {
		try (
				// insert prepared statements
				PreparedStatement insertRow_Customer = conn.prepareStatement(
						"insert into Customer (Name, Email, joinedDate)values(?, ?, ?)");
				
			) {
	            
				
	            // clear data from tables
	            for (String tbl : dependentTables) {
		            try {
		            	stmt.executeUpdate("delete from " + tbl);
		            	
		            } catch (SQLException ex) {
		            	System.out.println("Did not truncate table " + tbl);
		            }
	            }
	            for (String tbl : independentTables) {
		            try {
		            	stmt.executeUpdate("delete from " + tbl);
		            
		            	//System.out.println("Truncated table " + tbl);
		            } catch (SQLException ex) {
		            	ex.printStackTrace();
		            	System.out.println("Did not truncate table " + tbl);
		            }
	            }
	            
	            
	            
	            System.out.println("\nAll Data are Dropped");
	            
	            Date CreateDate = new Date(System.currentTimeMillis());
	            insertRow_Customer.setString(1, "Luke");
	            insertRow_Customer.setString(2, "Luke@gmail.com");
	            insertRow_Customer.setTimestamp(3, new java.sql.Timestamp(CreateDate.getTime()));
	            insertRow_Customer.execute();
				System.out.println("Refresh Data: ");
				// print number of rows in tables
				for (String tbl : independentTables) {
					rs = stmt.executeQuery("select count(*) from " + tbl);
					if (rs.next()) {
						int count = rs.getInt(1);
						System.out.printf("Table %s : count: %d\n", tbl, count);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
