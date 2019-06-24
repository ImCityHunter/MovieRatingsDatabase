

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

/**
 * This class is to insert some valid data on the database as stored information.
 *
 */
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
		
		stmt.close();
		conn.close();
	}
	/**
	 * Insert data from default file
	 * @param conn
	 * @param stmt
	 */
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
	            } catch (SQLException ex) {
	            	System.out.println("Did not truncate table " + tbl);
	            }
	        }
	        
	        //insert from the file
			readCustomer(conn,stmt,rs);
			readMovie(conn,stmt,rs);
			readAttendance (conn,stmt,rs);
			readReview(conn,stmt,rs);
			readEndorsement (conn,stmt,rs);
						
		} catch (SQLException e) {
		}

	}
	
	/**
	 * convert date from string to java.util.date to sql.date
	 * @param input the date string
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
	
	/**
	 * Read from the default customer file
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private static void readCustomer(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "customerTable.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					if (data.length != 4) continue;
					PreparedStatement insertRow_Customer;
					try {
						insertRow_Customer = conn.prepareStatement(
								"insert into Customer (CustomerID, Name, Email, joinedDate)values(?, ?, ?, ?)");
					
				        insertRow_Customer.setInt(1, convertToId(data[0]));
				        insertRow_Customer.setString(2, data[1]);
				        insertRow_Customer.setString(3, data[2]);
				        insertRow_Customer.setDate(4, convertToDate(data[3]));
				        insertRow_Customer.execute();
						
					} catch (SQLException e) {
						System.out.println("one insertion fail in Customer");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * Read from the default movie file
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private static void readMovie(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "movieTable.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					if (data.length != 2) continue;
					PreparedStatement insertRow_Movie;
					try {
						insertRow_Movie = conn.prepareStatement(
								"insert into Movie (Title, MovieID)values(?, ?)");
						
			            insertRow_Movie.setString(1, data[0]);
			            insertRow_Movie.setInt(2, convertToId(data[1]));
			            insertRow_Movie.execute();
						
					} catch (SQLException e) {
						System.out.println("one insertion fail in Movie");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	/**
	 * Read form the default attendance file
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private static void readAttendance(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "attendenceTable.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					if (data.length != 3) continue;
					PreparedStatement insertRow_Attendance;
					try {
						insertRow_Attendance = conn.prepareStatement(
								"insert into Attendance(MovieID, CustomerId, AttendanceDATE)values(?, ?, ?)");
						
			            insertRow_Attendance.setInt(1, convertToId(data[0]));
			            insertRow_Attendance.setInt(2, convertToId(data[1]));
			            insertRow_Attendance.setString(3, data[2]);
			            insertRow_Attendance.execute();
						
					} catch (SQLException e) {
						System.out.println("one insertion fail in Attendance");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * Read form the default review file
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private static void readReview(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "reviewTable.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					if (data.length != 6) continue;
					PreparedStatement insertRow_Review;
					try {
						insertRow_Review = conn.prepareStatement(
								"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");		
						insertRow_Review.setInt(1, convertToId(data[0]));
						insertRow_Review.setInt(2, convertToId(data[1]));
						insertRow_Review.setInt(3, convertToId(data[2]));
						insertRow_Review.setDate(4, convertToDate(data[3]));
						insertRow_Review.setString(5, data[4]);
						insertRow_Review.setInt(6, convertToId(data[5]));
						insertRow_Review.execute();
						
					} catch (SQLException e) {
						System.out.println("one insertion fail in Review");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	
	/**
	 * Read from the default endorsement file
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private static void readEndorsement(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "endorsementTable.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split("\t");
					if (data.length != 3) continue;
					PreparedStatement insertRow_Endorsement;
					try {
						insertRow_Endorsement = conn.prepareStatement(
								"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");
						insertRow_Endorsement.setInt(1, convertToId(data[0]));
						insertRow_Endorsement.setInt(2, convertToId(data[1]));
						insertRow_Endorsement.setString(3, data[2]);
						insertRow_Endorsement.execute();
						
					} catch (SQLException e) {
						System.out.println("one insertion fail in Endorsement");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	
}
