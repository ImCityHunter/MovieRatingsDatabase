import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

/**
 * This class is page for customer operation
 *
 */
public class CustomerView {
	static Date now = new Date();
	static java.sql.Date today = new java.sql.Date(now.getTime());
	
	/**
	 * List seven options for customer
	 * @param conn
	 * @param stmt
	 * @param readUser
	 */
	public static void Options (Connection conn, Statement stmt,Scanner readUser) {
		int option = 0;
		while(option != 7) {
			printOption();
			String read= readUser.nextLine();
			option = UIFunctions.validId(read);
			if(option == 1) {
				printTable(stmt);	
			}
			else if(option == 2) {
				if(insertCustomer(conn, readUser)) {
					System.out.println("\n\nRegister Success\n\n");
					UIFunctions.printOneTable(stmt, "Customer");
					}
			}
			else if(option == 3) {
				if(insertAttendance(conn,readUser, stmt)) {
					System.out.println("\n\nAttendendance Success\n\n");
					UIFunctions.printOneTable(stmt, "Attendance");
				}
			}
			else if(option == 4) {
				if(insertReview(conn, stmt, readUser)) {
					UIFunctions.printOneTable(stmt, "Review");
					System.out.println("\n\nReview Inserted");				
					}
			}
			else if(option == 5) {
				if(insertEndorse(conn, readUser, stmt)) {
					System.out.println("\n\nEndorse Success\n\n");
					UIFunctions.printOneTable(stmt, "Endorsement");			
					}
			}else if(option == 6) {
				printMovieRating(conn,stmt);
			}else {
				option = 7; 
			}
		}		
	}
		
	/**
	 * the main page for customer
	 */
	private static void printOption() {
		System.out.print("\n"
				+ "1. Print Customer Table \n"
				+ "2. New Customer \n"
				+ "3. Enter a movie you watched \n"
				+ "4. Insert Review \n"
				+ "5. Endorse a Review \n"
				+ "6. Print AVG Movie Ratings\n"
				+ "7. Main Page\n"
				+ "Option: ");
	}
	
	/**
	 * insert a customer
	 * @param conn
	 * @param readUser
	 * @return
	 */
	private static boolean insertCustomer (Connection conn, Scanner readUser) {
		
		System.out.println("\n\n\nRegistering a Customer");
		System.out.print("\ninsert your name: ");
		String name = readUser.nextLine();
		System.out.print("\nEnter your email (with @): ");
		String email = readUser.nextLine();
		int id = UIFunctions.generateId(conn, "customer");
		if(!UIFunctions.validEmail(email)) return false;
		else return UIFunctions.registerCustomer(conn, id, name, email, today);
	}
	
	/**
	 * insert an attendance
	 * @param conn
	 * @param readUser
	 * @param stmt
	 * @return
	 */
	private static boolean insertAttendance(Connection conn, Scanner readUser, Statement stmt) {
		System.out.print("\n\n\n\n\n\n");
		UIFunctions.printOneTable(stmt, "movie");
		System.out.print("\n\nPick a movie you watched (enter movieid):\t");
		String getMid = readUser.nextLine();
		int mid = UIFunctions.validId(getMid);
		if(mid==-1) return false;
		
		System.out.print("\nenter your id (all int):\t");
		String getCid = readUser.nextLine();
		int cid = UIFunctions.validId(getCid);
		if(cid==-1) return false;
		
		System.out.print("\nwhen did you watched it? format(YYYY-MM-DD)\t");
		String getDate = readUser.nextLine();
		java.sql.Date date = UIFunctions.convertToDate(getDate);
		if(date == null) return false;
		
		boolean valid = UIFunctions.insertAttendance(conn, mid, cid, date);
		
		if(valid) UIFunctions.printOneTable(stmt, "Attendance");
		
		return valid;
	}
	
	/**
	 * insert a review
	 * @param conn
	 * @param stmt
	 * @param readUser
	 * @return
	 */
	private static boolean insertReview (Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n");
		UIFunctions.printOneTable(stmt,"movie");
		System.out.println("\nInserting a review:");
		System.out.print("\ninsert a movie id that you watched recently: ");
		String getMid = readUser.nextLine();
		int mid = UIFunctions.validId(getMid);
		if(mid==-1) return false;
		
		System.out.print("\ninsert your cid (all int): ");
		String getCid = readUser.nextLine();
		int cid = UIFunctions.validId(getCid);
		if(cid==-1) return false;
		
		System.out.print("\nwhat is your rating for this movie (1-5 only)? \t\t\t");
		String getRating = readUser.nextLine();
		int rating = UIFunctions.validId(getRating);
		if(rating==-1||rating>5) return false;
		
		System.out.print("\nwhat is your comment on this review? \t\t");
		String review = readUser.nextLine();
		if(!UIFunctions.validString(review)) return false;
		
		int rid = UIFunctions.generateId(conn, "review");
		if(rid==-1) return false;
		
		return UIFunctions.insertReview(conn, mid, cid, today, rating, review, rid);
	}
	
	/**
	 * insert a endorsement
	 * @param conn
	 * @param readUser
	 * @param stmt
	 * @return
	 */
	private static boolean insertEndorse(Connection conn, Scanner readUser, Statement stmt) {
		System.out.print("\n\n\n");
		UIFunctions.printOneTable(stmt, "review");
		System.out.println("\n\nEndorse a review!");
		System.out.print("\nEnter a review id:\t");
		String getRid = readUser.nextLine();
		int rid = UIFunctions.validId(getRid);
		if(rid==-1) return false;
		System.out.print("\nWhat is your customer id:\t");
		String getCid = readUser.nextLine();
		int cid = UIFunctions.validId(getCid);
		if(cid==-1) return false;		
		return UIFunctions.insertEndorsement(conn, rid, cid, today);
	}
	
	/**
	 * print a table
	 * @param stmt
	 */
	private static void printTable(Statement stmt) {
		System.out.print("\n\n\n\n");
		UIFunctions.printOneTable(stmt, "Customer");
	}
	
	/**
	 * print the rating for per movie
	 * @param conn
	 * @param stmt
	 * @return
	 */
	private static boolean printMovieRating(Connection conn,Statement stmt) {
		System.out.print("\n\n\n\n");
		ResultSet rs = UIFunctions.checkMovieRate(conn);
		if(rs==null) return false;
		System.out.println("\nAVG Rating Per Movie");
		UIFunctions.printResultSet(rs, stmt);
		return true;
	}
}
