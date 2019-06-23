import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MainPage {
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = Connect.getConnection();
		Statement stmt = null;
		System.out.println();
		
		try {
			stmt = conn.createStatement();
			CreateTables.create(conn, stmt);
			setDefaultData.defaultData(conn, stmt);
			Scanner readUser = new Scanner(System.in);
			int option = -1;
			while(option!=4) {
				printMainOption();
				String read = readUser.nextLine();
				option = UIFunctions.validId(read);
				if( option == 1) {
					System.out.println();
					UIFunctions.printAllTable(conn);
				}
				else if( option == 2) {
					CustomerView.Options(conn, stmt, readUser);
				}
				else if( option == 3) {
					EmployeeView.Options(conn, stmt, readUser);					
				}
				else if( option == 4) {
					System.out.print("\nBye Bye\n");
				}
				else {
					System.out.print("\n!!!!!!INVALID INPUT!!!!!\n");
					option = 4;
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			System.out.println("ERROR before main function");
		}

	
	}
	
	public static void printMainOption() {
		System.out.print("\n\n"
				+ "Main Page\n"
				+ "1. Print All Tables \n"
				+ "2. Access as Customer \n"
				+ "3. Access as IT \n"
				+ "4. Exit with All program \n"
				+ "Option: ");
		
	}
	

	
	
}