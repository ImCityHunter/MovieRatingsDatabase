import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

public class CustomerView {
	static Date now = new Date();
	static java.sql.Date today = new java.sql.Date(now.getTime());
	public static void Options (Connection conn, Statement stmt,Scanner readUser) {
		int option = 0;
		while(option != 8 ) {
			printOption();
			String read= readUser.nextLine();
			option = UIFunctions.validId(read);
			if(option == 1) {
				printTable(stmt);	
			}
			else if(option ==2) {
				insertCustomer(conn, readUser);	
			}
			else if(option ==3) {
				insertAttendance(conn,readUser, stmt);
			}
			else if(option ==4) {
				insertReview(conn,readUser);
			}
			else if(option ==5) {
				readUser.nextLine();
				insertEndorse(conn, readUser, stmt);
			}
			else if(option ==6) {
				getFreeConcessionLst(conn,stmt,readUser);
			}
			else if(option ==7){
				getFreeTicketList(conn,stmt,readUser);
			}
			else {
				option=8; //force out
			}
		}
		
	}
	private static boolean getFreeTicketList(Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n\n\n\n");
		System.out.print("\nGive me a Deadline Date: (format: YYYY-MM-DD)\t");
		String getDate = readUser.nextLine();
		java.sql.Date date = UIFunctions.convertToDate(getDate);
		if(date == null) return false;
		
		System.out.println();//need this to get to next line
		UIFunctions.getFreeTicketCustomer(conn, date);
		
		return true;
		
	}
	private static boolean getFreeConcessionLst(Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n\n\n\n");
		System.out.print("\nGive me a Deadline Date: (format: YYYY-MM-DD)\t");
		String getDate = readUser.nextLine();
		java.sql.Date date = UIFunctions.convertToDate(getDate);
		if(date == null) return false;
		
		ResultSet rs = UIFunctions.getFreeConcessionLst(conn, date);
		if(rs == null) return false;
		
		System.out.println("\nEmails of who get Free Gifts");
		UIFunctions.printResultSet(rs, stmt);
		
		
		return true;
	}
	private static boolean insertEndorse(Connection conn, Scanner readUser, Statement stmt) {
		//"insert into Endorsement (ReviewId, CustomerId, EndorsementDate) values (?, ?, ?)"
		UIFunctions.printOneTable(stmt, "review");
		System.out.println("\n\n Endorse a review!");
		System.out.print("\nenter a review id:\t");
		String getRid = readUser.nextLine();
		int rid = UIFunctions.validId(getRid);
		if(rid==-1) return false;
		System.out.print("\nWhat is your customer id:\t");
		String getCid = readUser.nextLine();
		int cid = UIFunctions.validId(getCid);
		if(cid==-1) return false;
		
		return UIFunctions.insertEndorsement(conn, rid, cid, today);
	}
	private static boolean insertAttendance(Connection conn, Scanner readUser, Statement stmt) {
		//"insert into Attendence (MovieId, CustomerId, AttendenceDate) values (?, ?, ?)"
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
		
		return UIFunctions.insertAttendence(conn, mid, cid, date);
	}
	private static boolean insertReview (Connection conn, Scanner readUser) {
		//insertReview(Connection conn, String mid, String cid, java.sql.Date date, int rating, String review, int id)
		
		System.out.println("\n\n\nInserting a review:");
		System.out.print("\ninsert a movie id that you watched recently: ");
		String getMid = readUser.nextLine();
		int mid = UIFunctions.validId(getMid);
		if(mid==-1) return false;
		
		System.out.print("\ninsert your cid (all int): ");
		String getCid = readUser.nextLine();
		int cid = UIFunctions.validId(getCid);
		if(cid==-1) return false;
		
		System.out.print("\nwhat is your rating for this movie? 1-5 only");
		String getRating = readUser.nextLine();
		int rating = UIFunctions.validId(getRating);
		if(rating==-1||rating>5) return false;
		
		System.out.print("\nwhat is your comment on this review?");
		String review = readUser.nextLine();
		if(!UIFunctions.validString(review)) return false;
		
		System.out.print("\ngive your review an id (all int): ");
		String getRid = readUser.nextLine();
		int rid = UIFunctions.validId(getRid);
		if(rid==-1) return false;
		
		return UIFunctions.insertReview(conn, mid, cid, today, rating, review, rid);
	}
	
	private static boolean insertCustomer (Connection conn, Scanner readUser) {

		System.out.println("\n\n\nRegistering a Customer");
		System.out.print("\ninsert your name: ");
		String name = readUser.nextLine();
		if(!UIFunctions.validString(name)) return false;
		System.out.print("\ninsert id (all int): ");
		String getId = readUser.nextLine();
		int id = UIFunctions.validId(getId);
		if(id==-1) return false;
		System.out.print("\nEnter your email (with @): ");
		String email = readUser.nextLine();
		if(!UIFunctions.validEmail(email)) return false;
		else return UIFunctions.registerCustomer(conn, id, name, email, today);
	}
	private static void printTable(Statement stmt) {
		UIFunctions.printOneTable(stmt, "Customer");
	}
	private static void printOption() {
		System.out.print("\n"
				+ "1. Print Customer Table \n"
				+ "2. New Customer \n"
				+ "3. Enter a movie you watched \n"
				+ "4. Insert Review \n"
				+ "5. Endorse a Review \n"
				+ "6. Get Free Concession List\n"
				+ "7. Get Free Ticket List \n"
				+ "8. Main Page\n"
				+ "Option: ");
	}

}