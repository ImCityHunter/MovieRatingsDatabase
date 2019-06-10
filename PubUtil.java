

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains functions for printing publishers,
 * journals, articles, and authors in the database.
 * 
 * @author philip Gust
 *
 */
public class PubUtil {

	/**
	 * Print authors table.
	 * @param conn the connection
	 * @return number of authors
	 * @throws SQLException if a database operation fails
	 */
	static int printAuthors(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select orcid, familyname, givenname from author order by familyname, givenname");
		) {
			System.out.println("Authors:");
			int count = 0;
			while (rs.next()) {
				long orcid = rs.getLong(1);
				String orcIdStr = Biblio.orcidToString(orcid);
				String familyName = rs.getString(2);
				String givenName = rs.getString(3);
				System.out.printf("  %s, %s (%s)\n", familyName, givenName, orcIdStr);
				count++;
			}
			return count;
		}
	}
	
	/**
	 * Print articles table.
	 * @param conn the connection
	 * @return number of articles
	 * @throws SQLException if a database operation fails
	 */
	static int printArticles(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
				"select title, doi from Article order by title");
		) {
			System.out.println("Articles:");
			int count = 0;
			while (rs.next()) {
				String title = rs.getString(1);
				String doi = rs.getString(2);
				System.out.printf("  %s (%s)\n", title, doi);
				count++;
			}
			return count;
		}		
	}
	
	/**
	 * Print journals table.
	 * @param conn the connection
	 * @return number of journals
	 * @throws SQLException if a database operation fails
	 */
	static int printJournals(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
				"select issn, title from Journal order by title");
		) {
			System.out.println("Journals:");
			int count = 0;
			while (rs.next()) {
				int issn = rs.getInt(1);
				String issnStr = Biblio.issnToString(issn);
				String title = rs.getString(2);
				System.out.printf("  %s (%s)\n", title, issnStr);
				count++;
			}
			return count;
		}		
	}
	
	/**
	 * Print publishers table.
	 * @param conn the connection
	 * @return number of publishers
	 * @throws SQLException if a database operation fails
	 */
	static int printPublishers(Connection conn) throws SQLException {
		try (
			Statement stmt = conn.createStatement();
			// list authors and their ORCIDs
			ResultSet rs = stmt.executeQuery(
					"select name, city from Publisher order by name, city");
		) {
			System.out.println("Publishers:");
			int count = 0;
			while (rs.next()) {
				String name = rs.getString(1);
				String city = rs.getString(2);
				System.out.printf("  %s, %s\n", name, city);
				count++;
			}
			return count;
		}
	}

}
