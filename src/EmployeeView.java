import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class EmployeeView {
	public static void Options (Connection conn, Statement stmt,Scanner readUser) {
		int option = 0;
		while(option != 4) {
			printOption();
			String read = readUser.nextLine();
			option = UIFunctions.validId(read);
			if(option == 1) {
				insertMovie(conn, stmt, readUser);
			}
			else if(option ==2) {
				printMovie(stmt);
			}
			else if(option ==3) {
				printMovieRating(conn,stmt);
			}
			else {
				option = 4;
			}
		}
		
	}
	private static boolean printMovieRating(Connection conn,Statement stmt) {
		
		System.out.println("\nAVG Rating Per Movie");
		ResultSet rs = UIFunctions.checkMovieRate(conn);
		if(rs==null) return false;
		UIFunctions.printResultSet(rs, stmt);
		return true;
	}
	private static boolean insertMovie (Connection conn, Statement stmt, Scanner readUser) {
		System.out.println("\n\n\nInputting Movie");
		System.out.print("\ninsert title of the movie: ");
		String title = readUser.nextLine();
		if(!UIFunctions.validString(title)) return false;
		System.out.print("\ninsert id of the movie (all int): ");
		String getId = readUser.nextLine();
		int id = UIFunctions.validId(getId);
		if(id==-1) return false;
		else return UIFunctions.insertMovie(conn, title, id);
	}
	private static void printMovie( Statement stmt) {
		UIFunctions.printOneTable(stmt, "movie");
		
	}
	private static void printOption() {
		System.out.print("\n"
				+ "Employee View: \n"
				+ "1. Insert movie \n"
				+ "2. Print movie table \n"
				+ "3. Print moving ratings \n"
				+ "4. Back to main \n"
				+ "Option: ");
	}
}
