package com.division.freeforall.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.division.freeforall.config.FFAConfig;
import com.division.freeforall.core.FreeForAll;

public class MySQLc extends MainSQL {

	private FreeForAll FFA;

	public MySQLc(FreeForAll instance) throws ClassNotFoundException, SQLException {
		super(instance);
		this.FFA = instance;
		getConnection();
	}

	final protected synchronized void getConnection()
			throws ClassNotFoundException, SQLException {
		FFAConfig TC = FFA.getFFAConfig();
		String host = TC.getMySQLHost();
		String username = TC.getMySQLUsername();
		String password = TC.getMySQLPassword();
		String databaseName = TC.getMySQLDatabase();
		String port = TC.getMySQLPort();
		Class.forName("com.mysql.jdbc.Driver");
		FFA.getLogger().info("MySQL driver loaded");
		conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port
				+ "/" + databaseName, username, password);
		FFA.getLogger().info("Connected to Database");
		setup();
	}

	final protected synchronized void setup() throws SQLException {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS `ffa_leaderboards` ("
					+ "`player_id` int(11) NOT NULL AUTO_INCREMENT,"
					+ "`player_name` varchar(255) NOT NULL,"
					+ "`killcount` int(11) DEFAULT '0',"
					+ "`killstreak` int(11) DEFAULT '0',"
					+ "`times_played` int(11) DEFAULT '0',"
					+ "`deaths` int(11) DEFAULT '0',"
					+ "PRIMARY KEY (`player_id`),"
					+ "UNIQUE KEY `Name` (`player_name`) USING BTREE"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");

		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException ex) {
				}
			}
		}
	}
}
