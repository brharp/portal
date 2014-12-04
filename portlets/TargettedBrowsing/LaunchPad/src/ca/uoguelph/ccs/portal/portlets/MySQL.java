package ca.uoguelph.ccs.portal.portlets;
/* import needed for JDBC access */

import java.sql.*;

/**
 * MySQL Demo Program this program is just a little demonstration of the usage
 * of MySQL in combination with Java JDBC
 * 
 * http://www.javacoding.net
 */
public class MySQL {

	public void test(String host, String database, String user, String pass)
			throws Exception {

		/* first, we'll test whether the MySQL driver is installed */
		testDriver();

		/* then, we'll get a connection to the database */
		Connection con = getConnection(host, database, user, pass);

		/* we create a table */
		executeUpdate(con,
				"create table test (id int not null,text varchar(20))");

		/* we insert some data */
		executeUpdate(con,
				"insert into test (id,text) values (1,'first entry')");
		executeUpdate(con,
				"insert into test (id,text) values (2,'second entry')");
		executeUpdate(con,
				"insert into test (id,text) values (3,'third entry')");

		/* then we'll fetch this data */
		executeQuery(con, "select * from test");

		/* and we'll destroy the table ... */
		executeUpdate(con, "drop table test");

		/* finally, we close the database */
		con.close();
	}

	/**
	 * Checks whether the MySQL JDBC Driver is installed
	 */
	protected void testDriver() throws Exception {

		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("MySQL Driver Found");
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println("MySQL JDBC Driver not found ... ");
			throw (e);
		}
	}

	/**
	 * Returns a connection to the MySQL database
	 *  
	 */
	protected Connection getConnection(String host, String database,
			String user, String pass) throws Exception {

		String url = "";
		try {
			url = "jdbc:mysql://" + host + "/" + database + "?user=" + user
					+ "&password=" + pass;
			Connection con = DriverManager.getConnection(url);
			System.out.println("Connection established to " + url + "...");

			return con;
		} catch (java.sql.SQLException e) {
			System.out.println("Connection couldn't be established to " + url);
			throw (e);
		}
	}

	/**
	 * This method executes an update statement
	 * 
	 * @param con
	 *            database connection
	 * @param sqlStatement
	 *            SQL DDL or DML statement to execute
	 */
	protected void executeUpdate(Connection con, String sqlStatement)
			throws Exception {

		try {
			Statement s = con.createStatement();
			s.execute(sqlStatement);
			s.close();
		} catch (SQLException e) {
			System.out.println("Error executing sql statement");
			throw (e);
		}
	}

	/**
	 * This method executes a select statement and displays the result
	 * 
	 * @param con
	 *            database connection
	 * @param sqlStatement
	 *            SQL SELECT statement to execute
	 */
	protected void executeQuery(Connection con, String sqlStatement)
			throws Exception {

		try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sqlStatement);

			int col_number = 1;
			while (rs.next()) {

				String id = (rs.getObject("id").toString());
				String text = (rs.getObject("text").toString());

				System.out.println("found record : " + id + " " + text);
			}

			rs.close();

		} catch (SQLException e) {
			System.out.println("Error executing sql statement");
			throw (e);
		}
	}

	protected ResultSet getResultSet(Connection con, String sqlStatement)
			throws Exception {

		try {
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sqlStatement);

			return rs;

		} catch (SQLException e) {
			System.out.println("Error executing sql statement");
			throw (e);
		}
	}

	/**
	 * This one is used to start the program.
	 */
	//	public static void main(String args[]) throws Exception {
	//		new MySQL().test("jtey.com", "scbvviau_testing");
	//	}
}