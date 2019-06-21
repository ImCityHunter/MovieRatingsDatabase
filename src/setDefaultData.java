

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
	public static void defaultData(Connection  conn, Statement stmt, ResultSet rs) {
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
	}
	/**
	 * convert date from string to java.util.date to sql.date
	 * @param input
	 * @return
	 */
	public static Date convertToDate(String input) {
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
	public static int convertToId(String id) {
		int parseInt = Integer.parseInt(id);
		return parseInt;
	}
	public static void readCustomer(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "customerTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				PreparedStatement insertRow_Customer = conn.prepareStatement(
						"insert into Customer (CustomerID, Name, Email, joinedDate)values(?, ?, ?, ?)");
				
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				
	            
				int id = convertToId(data[0]);
	            Date date = convertToDate(data[3]);
	         
	            insertRow_Customer.setInt(1, id);
	            insertRow_Customer.setString(2, data[1]);
	            insertRow_Customer.setString(3, data[2]);
	            insertRow_Customer.setDate(4, date);
	            insertRow_Customer.execute();
				// print number of rows in tables
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	public static void readMovie(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "movieTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				PreparedStatement insertRow_Customer = conn.prepareStatement(
						"insert into Movie (Title, MovieID)values(?, ?)");
				
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				
	            
				int id = convertToId(data[1]);
	   
	         
	            insertRow_Customer.setString(1, data[0]);
	            insertRow_Customer.setInt(2, id);
	            insertRow_Customer.execute();
				// print number of rows in tables
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	public static void readAttendance(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "attendenceTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				PreparedStatement insertRow_Customer = conn.prepareStatement(
						"insert into Attendance(MovieID, CustomerId, AttendanceDATE)values(?, ?, ?)");		
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
	            insertRow_Customer.setInt(1, convertToId(data[0]));
	            insertRow_Customer.setInt(2, convertToId(data[1]));
	            insertRow_Customer.setString(3, data[2]);
	            insertRow_Customer.execute();
				// print number of rows in tables
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	public static void readEndorsement(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "endorsementTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				PreparedStatement insertRow_Endorsement = conn.prepareStatement(
						"insert into Endorsement (reviewID, CustomerId, endorsementDATE)values(?, ?, ?)");		
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				insertRow_Endorsement.setInt(1, convertToId(data[0]));
				insertRow_Endorsement.setInt(2, convertToId(data[1]));
				insertRow_Endorsement.setString(3, data[2]);
				insertRow_Endorsement.execute();
				// print number of rows in tables
				}
				
			} catch (SQLException e) {
				System.out.println("input not success in endorsement");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	public static void readReview(Connection conn, Statement stmt, ResultSet rs) {
		String fileName = "reviewTable.txt";
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				// insert prepared statements
				PreparedStatement insertRow_Review = conn.prepareStatement(
						"insert into Review(MovieId, CustomerId, rating, reviewDate, Review, reviewId)values(?, ?, ?, ?, ?, ?)");		
			) {
	            
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				insertRow_Review.setInt(1, convertToId(data[0]));
				insertRow_Review.setInt(2, convertToId(data[1]));
				insertRow_Review.setInt(3, convertToId(data[2]));
				insertRow_Review.setDate(4, convertToDate(data[3]));
				insertRow_Review.setString(5, data[4]);
				insertRow_Review.setInt(6, convertToId(data[5]));
				insertRow_Review.execute();
				// print number of rows in tables
				}
				
			} catch (SQLException e) {
				System.out.println("input not success in review table");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
	}
	
}
