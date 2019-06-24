import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

/**
 * This class is the page for employee operation
 *
 */
public class EmployeeView {
	static Date now = new Date();
	static java.sql.Date today = new java.sql.Date(now.getTime());
	
	/**
	 * List five choices for employee 
	 * @param conn
	 * @param stmt
	 * @param readUser
	 */
	public static void Options (Connection conn, Statement stmt,Scanner readUser) {
		int option = 0;
		while(option != 5) {
			printOption();
			String read = readUser.nextLine();
			option = UIFunctions.validId(read);
			if(option == 1) {
				if(insertMovie(conn, stmt, readUser)) System.out.println("Insert Movie Success");
			}
			else if(option ==2) {
				printMovie(stmt);
			}else if(option ==3) {
				getFreeConcessionLst(conn,stmt,readUser);
			}
			else if(option ==4){
				getFreeTicketList(conn,stmt,readUser);
			}
			else {
				option = 5;
			}
		}
		
	}
	
	/**
	 * the main page for employee
	 */
	private static void printOption() {
		System.out.print("\n"
				+ "Employee View: \n"
				+ "1. Insert movie \n"
				+ "2. Print movie table \n"
				+ "3. Print the list of concession winners  \n"
				+ "4. Print the list of free ticket winners \n"
				+ "5. Main Page \n"
				+ "Option: ");
	}
	
	/**
	 * insert a movie
	 * @param conn
	 * @param stmt
	 * @param readUser
	 * @return
	 */
	private static boolean insertMovie (Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n");
		System.out.println("Inputting Movie");
		System.out.print("\ninsert title of the movie: ");
		String title = readUser.nextLine();
		if(!UIFunctions.validString(title)) return false;
		int id = UIFunctions.generateId(conn, "movie");
		if(id==-1) return false;
		else return UIFunctions.insertMovie(conn, title, id);
	}
	
	/**
	 * print movie table
	 * @param stmt
	 */
	private static void printMovie( Statement stmt) {
		System.out.print("\n\n\n\n");
		UIFunctions.printOneTable(stmt, "movie");	
	}
	
	/**
	 * get free ticket list today
	 * @param conn
	 * @param stmt
	 * @param readUser
	 * @return
	 */
	private static boolean getFreeTicketList(Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n\n");
//		System.out.print("\nGive me a Deadline Date: (format: YYYY-MM-DD)\t");
//		String getDate = readUser.nextLine();
//		java.sql.Date date = UIFunctions.convertToDate(getDate);
//		if(date == null) return false;
		ResultSet rs = UIFunctions.getFreeTicketCustomer(conn, today);
		if(rs==null) return false;
		
		System.out.println("\n\nResult of the endorsement. #1 will get a free ticket");
		UIFunctions.printResultSet(rs, stmt);
		return true;
		
	}
	
	/**
	 * get free concession list today
	 * @param conn
	 * @param stmt
	 * @param readUser
	 * @return
	 */
	private static boolean getFreeConcessionLst(Connection conn, Statement stmt, Scanner readUser) {
		System.out.print("\n\n\n\n\n\n\n");
		
		ResultSet rs = UIFunctions.getFreeConcessionLst(conn, today);
		if(rs == null) return false;
		
		System.out.println("\nEmails of who get Free Gifts");
		UIFunctions.printResultSet(rs, stmt);		
		return true;
	}
}
