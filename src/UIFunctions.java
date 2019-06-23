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
import java.util.Random;

public class UIFunctions{
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	static ResultSet rs = null;
	
	private static void printError() {
		String errorInput = "\n\n\n\nwow....you are trolling us :(  bye! \n\n\n";
		System.err.println(errorInput);
	}
	
	
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
			printError();
			return null;
		}
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		//System.out.println(sqlDate);
		return sqlDate;
	}
	public static boolean validString(String s) {
		if(s.isEmpty()||s.isBlank()) {
			printError();
		}
		return true;
	}
	static boolean validEmail(String email) {
		   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		   boolean valid = email.matches(regex);
		   if(email.isBlank()||email.isEmpty()||!valid) {
			   printError();
		   }
		   return valid;
		}
	public static int validId(String s) {
		
		if(s.isEmpty()||s.isBlank()) {
			printError();
			return -1;
		}
		try {
			int result = Integer.parseInt(s);
			return result;
		}
		catch (NumberFormatException e) {
			printError();
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
		return true;
		
	}
	
	public static boolean insertAttendance(Connection conn, int mid, int cid, java.sql.Date date) {	
		PreparedStatement insertAttendance;
		boolean valid = false;
		try {
			insertAttendance = conn.prepareStatement("insert into ATTENDANCE (MovieId, CustomerId, ATTENDANCEDate) values (?, ?, ?)");
			insertAttendance.setInt(1, mid);
			insertAttendance.setInt(2, cid);
			insertAttendance.setDate(3, date);		
			valid = insertAttendance.execute();
		} catch (SQLException e) {
			System.out.println("Sorry, we cannot confirm you watched this movie!");
			return false;
		}
		
		if(valid) System.out.println("You watched a movie! Was it good? Give it a review");
		return true;
	}
	
	public static boolean insertReview(Connection conn, int mid, int cid, java.sql.Date date, int rating, String review, int id) {
		PreparedStatement insertReview;
		
		try {
			insertReview = conn.prepareStatement("insert into Review (MovieId, CustomerId, Rating, ReviewDate, Review, ReviewId) values (?, ?, ?, ?, ?,?)");
			insertReview.setInt(1, mid);
			insertReview.setInt(2, cid);
			insertReview.setInt(3, rating);
			insertReview.setDate(4, date);
			insertReview.setString(5, review);	
			insertReview.setInt(6, id);
			insertReview.execute();
		} catch (SQLException e) {
			System.out.println("\n\nha ha....oh...oh seems like review is not added");
			return false;
		}
	
		return true;
		
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
		
		return true;
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
	
	/**
	 * Use Query to find all the people that make a review. And they will each receive gift
	 * @param conn
	 * @param date
	 * @return
	 */
	public static ResultSet getFreeConcessionLst(Connection conn, java.sql.Date date) {
		ResultSet rs = null;
		try {
			String query2 = "(select count(*) from endorsement where customer.customerid = endorsement.customerid) as num_gifts";
			String query3 = "select name, email, "+query2+" from customer";
			System.out.println("query: "+query3);
			PreparedStatement getLst = conn.prepareStatement(query3);
			//getLst.setDate(1, date);
			rs = getLst.executeQuery();
			if(rs==null) {System.out.println("getFreeConcessionLst is null");}
		} catch (SQLException e) {
			System.err.println("\noooops. I cannot provide you a Free ConcessionList. Check Back Later \n");
		}
		return rs;
	}
	public static ResultSet getFreeTicketCustomer(Connection conn, java.sql.Date date) {
		ResultSet rs = null;
		int currentMax = 0;
		String freeCustomerRid = "";
		LocalDate local = LocalDate.parse(date.toString()).minusDays(3);
		java.sql.Date checkDate = java.sql.Date.valueOf(local);
		try {
		
			String query = "("
					+ " Select customer.name, customer.customerid, count(endorsement.reviewid) as count \n"
					+ " From customer \n"
					+ " left join review on customer.customerid = review.customerid \n"
					+ " left join endorsement on review.reviewid = endorsement.reviewid \n"
					+ " group by customer.name, customer.customerid \n"
					+ " order by count desc )";
			
			String query2 = "("
					+ " Select movie.title, customer.name, count(endorsement.reviewid) as count2 \n"
					+ " From movie \n"
					+ " left join review on movie.movieid = review.movieid \n"
					+ " left join customer on customer.customerid = review.customerid \n"
					+ " left join endorsement on review.reviewid = endorsement.reviewid \n"
					+ " group by movie.title, customer.name \n"
					+ " order by movie.title desc )";
			
			System.out.println("query: "+query);
			PreparedStatement getLst = conn.prepareStatement(query);
			rs = getLst.executeQuery();
		} catch (SQLException e) {
			System.err.println("ooooops. This query has issue. Check Back Later");
		}
		
		return rs;
		
	}
	public static int generateId(Connection conn, String table){
		
		ResultSet rs = null;
		int id = 0;
		boolean exit = false;
		while(!exit) {
			
			try {
			id = 1 + (int)(Math.random() * 10000);
			
			String query = "select * from "+table+" where "+table+"id = "+id;
			PreparedStatement findId = conn.prepareStatement(query);
			//System.out.println(query);
			rs = findId.executeQuery();
			if(!rs.next())  exit = true;
			else {
				System.out.println(id+" is taking. auto generating id again");
				}
			} catch (SQLException e) {
				exit = true;
			}
		}

		
		
		return id;
	}
	
	/**
	 * Join Moive, Review to get name of the movie and Avg Rating
	 * @param conn
	 * @return
	 */
	public static ResultSet checkMovieRate(Connection conn){
		ResultSet rs = null;
		try {
			String query1 = "(select cast(avg(Rating) as DOUBLE) From Review where review.movieid = movie.movieid) as avgRating";
			String query2 = "select title,"+query1+" from movie";
			System.out.println("query: "+query2);
			PreparedStatement findmovieId = conn.prepareStatement(query2);
			rs = findmovieId.executeQuery();
			} catch (SQLException e) {
			System.out.println("sorry. cannot provide you a list of all movies' ratings");
		}
		return rs;
	}
}