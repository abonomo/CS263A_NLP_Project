package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection 
{
	private static final String URL		= "";
	private static final String USER	= "root";
	private static final String PW		= "";
	
	private static Connection conn;
	
//	MySQLConnection() throws SQLException
	{
		try 
		{
			initialize();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static Connection getConnection() throws SQLException
	{
		if(conn == null)
			initialize();
		
		return conn;
	}
	
	static void closeConnection() throws SQLException
	{
		if(conn != null)
			conn.close();
		
		conn = null;
	}
	
	private static final void initialize() throws SQLException
	{
		if(conn == null)
		{
			conn = DriverManager.getConnection(URL, USER, PW);
			
			//close the connection when the program shuts down
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run()
				{
					try {
						MySQLConnection.closeConnection();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
}
