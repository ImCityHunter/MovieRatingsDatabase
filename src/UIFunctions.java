import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class UIFunctions{
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	static ResultSet rs = null;
	private static final String errorInput = "\n\n\n\nwow....you are trolling us :(  bye! \n\n\n";
	
	public static void printResultSet(ResultSet rs, Statement stmt) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			String eventFormat = "";
			int numberOfColumns = rsmd.getColumnCount();
			String [] row = new String [numberOfColumns];
	        
			for(int i=0;i<numberOfColumns;i++) {
	        	eventFormat+="%-20s";
	        }
			eventFormat+="\n";
	        
		    for (int i = 1; i <= numberOfColumns; i++) {
		         String columnName = rsmd.getColumnName(i);
		         row[i-1] = columnName;
		     }
		     System.out.format(eventFormat,row);
		     while (rs.next()) {
		        for (int i = 1; i <= numberOfColumns; i++) {
		        	
		            String columnValue = rs.getString(i);
		            row [i-1] = columnValue;
		          } 
		        System.out.format(eventFormat, row);
		     }
		     System.out.println();
		      
		} catch (SQLException e) {
			System.out.println("ResultSet Failed to Print");
		}
	}
	/**
	 * convert date from string to java.util.date to java.sql.date
	 * @param input
	 * @return
	 */
	public static java.sql.Date convertToDate(String input) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		try {
			date = sdf1.parse(input);
		} catch (ParseException e) {
			System.out.println(errorInput);
			return null;
		}
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		//System.out.println(sqlDate);
		return sqlDate;
	}
	public static boolean validString(String s) {
		if(s.isEmpty()||s.isBlank()) {
			System.out.println(errorInput);
		}
		return true;
	}
	static boolean validEmail(String email) {
		   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		   boolean valid = email.matches(regex);
		   if(email.isBlank()||email.isEmpty()||!valid) {
			   System.out.println(errorInput);
		   }
		   return valid;
		}
	public static int validId(String s) {
		
		if(s.isEmpty()||s.isBlank()) {
			System.out.println(errorInput);
			return -1;
		}
		try {
			int result = Integer.parseInt(s);
			return result;
		}
		catch (NumberFormatException e) {
			System.out.println(errorInput);
	        return -1;
	    }
		
		
	}
	public void createDatabase(Connection conn, Statement stmt){
		
		setDefaultData.defaultData(conn, stmt);
			
	}
	public static void printAllTable(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			for (String table: dependentTables) printTable(stmt,table,rs);
			for (String table: independentTables) printTable(stmt,table,rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void printOneTable(Statement stmt, String table) {
		printTable(stmt, table, rs);	
	}
	private static void printTable(Statement stmt, String table, ResultSet rs) {
		try {
			//print the name of the table for users
			System.out.println("["+table.toUpperCase()+"]");
			
			rs = stmt.executeQuery("SELECT * From "+table);
			ResultSetMetaData rsmd = rs.getMetaData();
			String eventFormat = "";
			int numberOfColumns = rsmd.getColumnCount();
			String [] row = new String [numberOfColumns];
	        
			//find # of columns in the table
			for(int i=0;i<numberOfColumns;i++) {
	        	eventFormat+="%-20s";
	        }
			eventFormat+="\n";
	        
			//store the name of the column into format
		    for (int i = 1; i <= numberOfColumns; i++) {
		         String columnName = rsmd.getColumnName(i);
		         row[i-1] = columnName;
		    }
		     System.out.format(eventFormat,row);
		     while (rs.next()) {
		        for (int i = 1; i <= numberOfColumns; i++) {
		        	
		            String columnValue = rs.getString(i);
		            row [i-1] = columnValue;
		          } 
		        System.out.format(eventFormat, row);
		     }
		     System.out.println();
		      
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static boolean registerCustomer(Connection conn, int id,String name, String email, java.sql.Date date) {

		boolean valid = false;
		try {
			PreparedStatement insertCustomer = conn.prepareStatement("insert into Customer (Customerid, Name, Email, JoinedDate) values (?, ?, ?, ?)");
			insertCustomer.setInt(1, id);
			insertCustomer.setString(2, name);
			insertCustomer.setString(3, email);
			insertCustomer.setDate(4, date);
			valid= insertCustomer.execute();
		} catch (SQLException e) {
			System.out.println("...oh...ohh bad input. (possible duplicated id)");
			return false;
		}
		if(valid) System.out.println("new customer is added");
		return true;
	}
	
	public static boolean insertMovie(Connection conn, String title, int id) {
		PreparedStatement insertMovie;
		boolean valid = false;
		try {
			insertMovie = conn.prepareStatement("insert into Movie (Title, MovieId) values (?,?)");
			insertMovie.setString(1, title);
			insertMovie.setInt(2, id);
			valid = insertMovie.execute();
		} catch (SQLException e) {
			System.out.println("oh oh...... bad input. (possible: Duplicated id)");
			return false;
		}
		if(valid) System.out.println("new movie is added");
		return valid;
		
	}
	
	public static boolean insertAttendence(Connection conn, int mid, int cid, java.sql.Date date) {	
		PreparedStatement insertAttendence;
		boolean valid = false;
		try {
			insertAttendence = conn.prepareStatement("insert into Attendence (MovieId, CustomerId, AttendenceDate) values (?, ?, ?)");
			insertAttendence.setInt(1, mid);
			insertAttendence.setInt(2, cid);
			insertAttendence.setDate(3, date);		
			valid = insertAttendence.execute();
		} catch (SQLException e) {
			System.out.println("Sorry you cannot watch this movie!");
			return false;
		}
		
		if(valid) System.out.println("You watched a movie! Was it good? Give it a review");
		return valid;
	}
	
	public static boolean insertReview(Connection conn, int mid, int cid, java.sql.Date date, int rating, String review, int id) {
		PreparedStatement insertReview;
		boolean valid = false;
		try {
			insertReview = conn.prepareStatement("insert into Review (MovieId, CustomerId, Rating, ReviewDate, Review, ReviewId) values (?, ?, ?, ?, ?,?)");
			insertReview.setInt(1, mid);
			insertReview.setInt(2, cid);
			insertReview.setInt(3, rating);
			insertReview.setDate(4, date);
			insertReview.setString(5, review);	
			insertReview.setInt(6, id);
			valid = insertReview.execute();
		} catch (SQLException e) {
			System.out.println("ha ha....oh...oh seems like review is not added");
			return false;
		}
		if(valid) System.out.println("nice review. it is added!");
		return valid;
		
	}
	
	public static boolean insertEndorsement(Connection conn, int rid, int cid, java.sql.Date date){
		
		boolean valid = false;
		try {
			PreparedStatement insertEndorsement = conn.prepareStatement("insert into Endorsement (ReviewId, CustomerId, EndorsementDate) values (?, ?, ?)");
			insertEndorsement.setInt(1, rid);
			insertEndorsement.setInt(2, cid);
			insertEndorsement.setDate(3, date);	
			valid = insertEndorsement.execute();
		} catch (SQLException e) {
			System.out.println("well... your endorsement is not taken. sorry");
		}
		
		if(valid) System.out.println("Thank you for your endorsement. You get a gift! ");
		
		return valid;
	}
	
	public void deleteCustomer(Connection conn, String cid) throws SQLException {

	    PreparedStatement delete =
	            conn.prepareStatement("delete from Customer where CustomerId = ?");
	    delete.setString(1, cid);
	    delete.execute();
	  }
	
	public void deleteMovie(Connection conn, String mid) throws SQLException {

	    PreparedStatement delete =
	            conn.prepareStatement("delete from Movie where MovieId = ?");

	    delete.setString(1, mid);
	    delete.execute();
	  }
	
	public static ResultSet getFreeConcessionLst(Connection conn, java.sql.Date date) {
		ResultSet rs = null;
		try {
			PreparedStatement getLst = conn.prepareStatement("select Email from Customer where CustomerId in " +
							"(select CustomerId from Endorsement where EndorsementDate = ?)");
			getLst.setDate(1, date);
			rs = getLst.executeQuery();
		} catch (SQLException e) {
			System.out.println("oooops. I cannot provide you a Free ConcessionList");
		}
		return rs;
	}
	public static void getFreeTicketCustomer(Connection conn, java.sql.Date date) {
		ResultSet rs = null;
		int currentMax = 0;
		String freeCustomerRid = "";
		LocalDate local = LocalDate.parse(date.toString()).minusDays(3);
		java.sql.Date checkDate = java.sql.Date.valueOf(local);
	
		try {
			PreparedStatement findmovieId = conn.prepareStatement("select movieId from Review where ReviewDate = ?");

			findmovieId.setDate(1, checkDate);
			rs = findmovieId.executeQuery();
			while(rs.next()) {
				ResultSet rs2 = null;
				String movieID = rs.getString(1);
				PreparedStatement findReview =
						conn.prepareStatement("select reviewID from review where movieID = ?");
				findReview.setString(1, movieID);
				rs2 = findReview.executeQuery();
				while(rs2.next()) {
					ResultSet rs3 = null;
					String reviewID = rs2.getString(1);
					PreparedStatement findCount =
							conn.prepareStatement("select count(*) from endorsement where reviewID = ?");
					findCount.setString(1, reviewID);
					rs3 = findCount.executeQuery();
					if(rs3.next()) {
						if(currentMax < rs3.getInt(1)) {
							freeCustomerRid = reviewID;
							currentMax = rs3.getInt(1);
							System.out.println("reviewid: "+reviewID+" has "+currentMax+" endorsement");
						}
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ooooops. This query has issue");
		}

		
	}
	public static ResultSet checkMovieRate(Connection conn){
		ResultSet rs = null;
		try {
			PreparedStatement findmovieId = conn.prepareStatement("select movieid, avg(Rating) as avgRating from Review group by movieID order by avgRating desc");
			rs = findmovieId.executeQuery();
		} catch (SQLException e) {
			System.out.println("sorry. cannot provide you a list of all movies' ratings");
		}
		return rs;
	}
}