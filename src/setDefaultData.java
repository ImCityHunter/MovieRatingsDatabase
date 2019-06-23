

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class setDefaultData {
	static String [] dependentTables = {"Endorsement","review","Attendance",};
	static String [] independentTables= {"Customer", "Movie"};
	static ResultSet rs = null;
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = Connect.newConnection();
		Statement stmt = conn.createStatement();
		CreateTables.create(conn, stmt);
		defaultData(conn, stmt);
		UIFunctions.printAllTable(conn);
		conn.close();
	}
	public static void defaultData(Connection conn,Statement stmt) {
		
		try {
			stmt = conn.createStatement();
			
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
			readCustomer(conn,stmt,rs);
			readMovie(conn,stmt,rs);
			readAttendance (conn,stmt,rs);
			readReview(conn,stmt,rs);
			readEndorsement (conn,stmt,rs);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * convert date from string to java.util.date to sql.date
	 * @param input
	 * @return
	 */
	private static Date convertToDate(String input) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		try {
			date = sdf1.parse(input);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		//System.out.println(sqlDate);
		return sqlDate;
	}
	/**
	 * convert string to int (id)
	 * @param id
	 * @return
	 */
	private static int convertToId(String id) {
		int parseInt = Integer.parseInt(id);
		return parseInt;
	}
	private static void readCustomer(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "customerTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				if (data.length != 4) continue;
				PreparedStatement insertRow_Customer;
				try {
					insertRow_Customer = conn.prepareStatement(
							"insert into Customer (CustomerID, Name, Email, joinedDate)values(?, ?, ?, ?)");
					// split input line into fields at tab delimiter

					int id = convertToId(data[0]);
		            Date date = convertToDate(data[3]);
		            insertRow_Customer.setInt(1, id);
		            insertRow_Customer.setString(2, data[1]);
		            insertRow_Customer.setString(3, data[2]);
		            insertRow_Customer.setDate(4, date);
		            insertRow_Customer.execute();
					// print number of rows in tables
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("one insertion fail in customer");
				}

				}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	private static void readMovie(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "movieTable.txt";
		try (
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			// insert prepared statements
			
				
			) {
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				if (data.length != 2) continue;
				PreparedStatement insertRow_Customer;
				try {
					insertRow_Customer = conn.prepareStatement(
							"insert into Movie (Title, MovieID)values(?, ?)");
					// split input line into fields at tab delimiter
					int id = convertToId(data[1]);
		            insertRow_Customer.setString(1, data[0]);
		            insertRow_Customer.setInt(2, id);
		            insertRow_Customer.execute();
					// print number of rows in tables
				} catch (SQLException e) {
					System.out.println("One insertion fail in movie");
				}

				}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	private static void readAttendance(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "attendenceTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
						
			) {
	            
			String line;

			while ((line = br.readLine()) != null) {
				String[] data = line.split("\t");
				if (data.length != 3) continue;
				PreparedStatement insertRow_Customer;
				try {
					insertRow_Customer = conn.prepareStatement(
							"insert into Attendance(MovieID, CustomerId, AttendanceDATE)values(?, ?, ?)");
					// split input line into fields at tab delimiter
		            insertRow_Customer.setInt(1, convertToId(data[0]));
		            insertRow_Customer.setInt(2, convertToId(data[1]));
		            insertRow_Customer.setString(3, data[2]);
		            insertRow_Customer.execute();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("one insertion fail in Attendance");
				}

				}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	private static void readEndorsement(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "endorsementTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
						
			) {
			String line;
			while ((line = br.readLine()) != null) {
				PreparedStatement insertRow_Endorsement;
				String[] data = line.split("\t");
				if (data.length != 3) continue;
				try {
					insertRow_Endorsement = conn.prepareStatement(
							"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");
					insertRow_Endorsement.setInt(1, convertToId(data[0]));
					insertRow_Endorsement.setInt(2, convertToId(data[1]));
					insertRow_Endorsement.setString(3, data[2]);
					insertRow_Endorsement.execute();
				} catch (SQLException e) {
					System.out.println("one insertion fail in endorsement");
				}
				
			}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	private static void readReview(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "reviewTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				if (data.length != 6) continue;
				try {
					PreparedStatement insertRow_Review = conn.prepareStatement(
							"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");		
					insertRow_Review.setInt(1, convertToId(data[0]));
					insertRow_Review.setInt(2, convertToId(data[1]));
					insertRow_Review.setInt(3, convertToId(data[2]));
					insertRow_Review.setDate(4, convertToDate(data[3]));
					insertRow_Review.setString(5, data[4]);
					insertRow_Review.setInt(6, convertToId(data[5]));
					insertRow_Review.execute();
				} catch (SQLException e) {
					System.out.println("One insertion fail in Review Table");
				}
				// print number of rows in tables
			}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	
}
