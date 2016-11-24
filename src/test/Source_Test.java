package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import utils.AppProperties;

public class Source_Test {
	private final String _hostname = "192.168.83.76";
	private final String _port = "16544";
	private final Boolean _secure = false;
	
	private final String _dburl = "jdbc:mysql://192.168.83.76/mythconverg_test";
	private final String _dbuser = "MythFrontendJava";
	private final String _dbpass = "avaJdnetnorFhtyM";
	
	private Connection _conn = null;
	private static Source_Test _instance = null;
	
	private Source_Test() {
		// Open a Connection
		try {
			_conn = DriverManager.getConnection(_dburl, _dbuser, _dbpass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Setup the App Properties for <fake> API
		AppProperties.setSourceAddress(_hostname);
		AppProperties.setSourcePort(_port);
		AppProperties.setSourceSecure(_secure);
	}
	
	public static Source_Test getInstance() {
		// Singleton Pattern
		if (_instance == null)
			_instance = new Source_Test();
		return _instance;
	}
	
	public ResultSet execute(String sql) {
		ResultSet results = null;
		Statement stmt = null;
		
		try {
			// Execute the Query
			if (_conn != null) {
				stmt = _conn.createStatement();
				stmt.execute(sql);
				results = stmt.getResultSet();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Cleanup
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return results;
	}
	
	@Override
	public void finalize() {
		try {
			if (_conn != null)
				_conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
