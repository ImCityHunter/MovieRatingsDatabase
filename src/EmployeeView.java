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
				if(insertMovie(conn, stmt, readUser)) System.out.println("Insert Movie Success");
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
		System.out.print("\n\n\n\n");
		ResultSet rs = UIFunctions.checkMovieRate(conn);
		if(rs==null) return false;
		System.out.println("\nAVG Rating Per Movie");
		UIFunctions.printResultSet(rs, stmt);
		return true;
	}
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
	private static void printMovie( Statement stmt) {
		System.out.print("\n\n\n\n");
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
