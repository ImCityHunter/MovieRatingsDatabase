import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class implement functions for UI query 
 *
 */
public class UIFunctions{
	static String [] dependentTables = {"Endorsement","review","Attendance"};
	static String [] independentTables= {"Customer", "Movie"};
	static ResultSet rs = null;
	
	/**
	 * the request is invalid
	 */
	private static void printError() {
		String errorInput = "\n\n\n\nwow....you are trolling us :(  bye! \n\n\n";
		System.out.println(errorInput);
	}
	
	/**
	 * print the result about the request
	 * @param rs
	 * @param stmt
	 */
	public static void printResultSet(ResultSet rs, Statement stmt) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			String eventFormat = "";
			int numberOfColumns = rsmd.getColumnCount();
			Object [] row = new String [numberOfColumns];
	        
			for(int i = 0; i < numberOfColumns; i++) {
	        	eventFormat += "%-20s";
	        }
			eventFormat += "\n";
	        
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
		     System.out.println("\n\n");
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
		return sqlDate;
	}
	
	/**
	 * check if the string is empty
	 * @param s
	 * @return
	 */
	public static boolean validString(String s) {
		if(s.isEmpty()) {
			printError();
		}
		return true;
	}
	
	/**
	 * check if the string email is valid
	 * @param email
	 * @return
	 */
	static boolean validEmail(String email) {
		   String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		   boolean valid = email.matches(regex);
		   if(email.isEmpty() || !valid) {
			   printError();
		   }
		   return valid;
		}
	
	/**
	 * check if the string id is valid
	 * @param s
	 * @return
	 */
	public static int validId(String s) {
		
		if(s.isEmpty()) {
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
	
	/**
	 * create the database and insert the default file
	 * @param conn
	 * @param stmt
	 */
	public void createDatabase(Connection conn, Statement stmt){
		setDefaultData.defaultData(conn, stmt);
			
	}
	
	/**
	 * print all the tables
	 * @param conn
	 */
	public static void printAllTable(Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			for (String table: dependentTables) printTable(stmt,table,rs);
			for (String table: independentTables) printTable(stmt,table,rs);
			
		} catch (SQLException e) {			
		}	
	}
	
	/**
	 * print one table
	 * @param stmt
	 * @param table
	 */
	public static void printOneTable(Statement stmt, String table) {
		printTable(stmt, table, rs);	
	}
	
	/**
	 * print the result set on the table
	 * @param stmt
	 * @param table
	 * @param rs
	 */
	private static void printTable(Statement stmt, String table, ResultSet rs) {
			System.out.println("["+table.toUpperCase()+"]");
			
			try {
				rs = stmt.executeQuery("SELECT * From "+table);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
			}
			printResultSet(rs, stmt);
	}
	
	/**
	 * register a customer
	 * @param conn
	 * @param id the customer id 
	 * @param name the customer name
	 * @param email the customer email
	 * @param date the register date
	 * @return true if register succeeded, otherwise false
	 */
	public static boolean registerCustomer(Connection conn, int id, String name, String email, java.sql.Date date) {
		try {
			PreparedStatement insertCustomer = conn.prepareStatement("insert into Customer (Customerid, Name, Email, JoinedDate) values (?, ?, ?, ?)");
			insertCustomer.setInt(1, id);
			insertCustomer.setString(2, name);
			insertCustomer.setString(3, email);
			insertCustomer.setDate(4, date);
			insertCustomer.execute();
		} catch (SQLException e) {
			System.out.println("...oh...ohh bad input, you cannot register.");
			return false;
		}
		System.out.println("new customer is added.");
		return true;
	}
	
	/**
	 * add a movie to the table
	 * @param conn
	 * @param title the movie title
	 * @param id the movie id
	 * @return true if add succeeded, otherwise false
	 */
	public static boolean insertMovie(Connection conn, String title, int id) {
		PreparedStatement insertMovie;
		try {
			insertMovie = conn.prepareStatement("insert into Movie (Title, MovieId) values (?,?)");
			insertMovie.setString(1, title);
			insertMovie.setInt(2, id);
			insertMovie.execute();
		} catch (SQLException e) {
			System.out.println("oh oh...... bad input, you cannot add the movie.");
			return false;
		}
		System.out.println("new movie is added.");
		return true;		
	}
	
	/**
	 * add a attendance for a customer to see the movie
	 * @param conn
	 * @param mid the movie id
	 * @param cid the customer id
	 * @param date the date to see
	 * @return
	 */
	public static boolean insertAttendance(Connection conn, int mid, int cid, java.sql.Date date) {	
		PreparedStatement insertAttendance;
		try {
			insertAttendance = conn.prepareStatement("insert into ATTENDANCE (MovieId, CustomerId, ATTENDANCEDate) values (?, ?, ?)");
			insertAttendance.setInt(1, mid);
			insertAttendance.setInt(2, cid);
			insertAttendance.setDate(3, date);		
			insertAttendance.execute();
		} catch (SQLException e) {
			System.out.println("Sorry, we cannot confirm you watched this movie.");
			return false;
		}
		
		System.out.println("You watched a movie! Was it good? Give it a review");
		return true;
	}
	
	/**
	 * add a review for a movie
	 * @param conn
	 * @param mid the movie id
	 * @param cid the customer id
	 * @param date the date to review
	 * @param rating the rating rank
	 * @param review the review
	 * @param id the review id
	 * @return
	 */
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
			System.out.println("\n\nha ha....oh...oh sorry, you cannot add a review for the movie.");
			return false;
		}
		System.out.println("Thank you for reviewing the movie.");
		return true;
		
	}
	
	/**
	 * add a endorsement for a review
	 * @param conn
	 * @param rid the review id
	 * @param cid the customer to endorse
	 * @param date the date to endorse
	 * @return
	 */
	public static boolean insertEndorsement(Connection conn, int rid, int cid, java.sql.Date date){
		PreparedStatement insertEndorsement;
		try {
			insertEndorsement = conn.prepareStatement("insert into Endorsement (ReviewId, CustomerId, EndorsementDate) values (?, ?, ?)");
			insertEndorsement.setInt(1, rid);
			insertEndorsement.setInt(2, cid);
			insertEndorsement.setDate(3, date);	
			insertEndorsement.execute();
		} catch (SQLException e) {
			System.out.println("well... sorry you cannot to endorse for the movie.");
			return false;
		}
		System.out.println("Thank you for endorsing the review.");
		return true;
	}
	
	/**
	 * Find all the people that make one or more endorse will receive a free concession item
	 * @param conn
	 * @param date the date
	 * @return
	 */
	public static ResultSet getFreeConcessionLst(Connection conn, java.sql.Date date) {
		ResultSet rs = null;
		try {
			String query = "\nselect Email \nfrom Customer \nwhere CustomerId in " +
					"(\nselect CustomerId \nfrom Endorsement \nwhere EndorsementDate = ?)";
			PreparedStatement getLst =
							conn.prepareStatement("select Email from Customer where CustomerId in " +
											"(select CustomerId from Endorsement where EndorsementDate = ?)");

			getLst.setDate(1, date);
			System.out.println("Sample Query: "+query);
			rs = getLst.executeQuery();
			if(rs==null) {System.out.println("getFreeConcessionLst is null");}
		} catch (SQLException e) {
			System.err.println("\noooops. I cannot provide you a Free ConcessionList. Check Back Later \n");
		}
		return rs;
	}
	
	
	/**
	 * Find all the writers of the top rated review of a movie written three days earlier
	 * will receive a free movie ticket.
	 * @param conn
	 * @param date
	 * @return
	 */
	public static void getFreeTicketCustomer(Connection conn, Statement stmt, java.sql.Date date) throws SQLException{
		ResultSet rs = null;
		int currentMax = 0;
		String freeCustomerRid = "";
		LocalDate local = LocalDate.parse(date.toString()).minusDays(3);
		java.sql.Date checkdate = java.sql.Date.valueOf(local);
		PreparedStatement findmovieId =
				conn.prepareStatement("select movieId from Review where ReviewDate = ? group by movieID");

		findmovieId.setDate(1, checkdate);
		rs = findmovieId.executeQuery();
		ArrayList<String> reviews = new ArrayList<>();
		while(rs.next()) {
			ResultSet rs2 = null;
			String movieID = rs.getString(1);
			PreparedStatement findReview =
					conn.prepareStatement("select reviewID from review where movieID = ? and ReviewDate = ?");
			findReview.setString(1, movieID);
			findReview.setDate(2, checkdate);
			rs2 = findReview.executeQuery();
			currentMax = 0;
			freeCustomerRid = "";
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
					}
					if(currentMax == rs3.getInt(1)) {
						
					}
				}
			}
			if(currentMax != 0) {
				reviews.add(freeCustomerRid);
			}
		}
		PreparedStatement findcid;
		for (String i : reviews) {
			findcid = conn.prepareStatement("select CustomerId from Review where ReviewId = ?");

			findcid.setString(1, i);
			rs = findcid.executeQuery();
			printResultSet(rs, stmt);
		
		}		
	}
		
	/**
	 * Get the name of the movie and average rating.
	 * @param conn
	 * @return
	 */
	public static ResultSet checkMovieRate(Connection conn){
		ResultSet rs = null;
		try {
			String query1 = "(\nselect cast(avg(Rating) as DOUBLE) \nFrom Review \nwhere review.movieid = movie.movieid) as avgRating";
			String query2 = "\nselect title," + query1 + " \nfrom movie";
			System.out.println("Query: \n"+query2);
			PreparedStatement findmovieId = conn.prepareStatement(query2);
			rs = findmovieId.executeQuery();
			} catch (SQLException e) {
			System.out.println("sorry. cannot provide you a list of all movies' ratings");
		}
		return rs;
	}
	
	/**
	 * It will generate new random id for a table
	 * @param conn
	 * @param table
	 * @return
	 */
	public static int generateId(Connection conn, String table){
		ResultSet rs = null;
		int id = 0;
		boolean exit = false;
		while(!exit) {	
			try {
			id = 1 + (int)(Math.random() * 10000);
			
			String query = "select * from " + table + " where " + table + "id = " + id;
			PreparedStatement findId = conn.prepareStatement(query);
			rs = findId.executeQuery();
			if(!rs.next())  exit = true;
			else {
				System.out.println(id + " is taking. auto generating id again");
				}
			} catch (SQLException e) {
				exit = true;
			}
		}		
		return id;
	}
}