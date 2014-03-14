package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MySQLTweetStore 
{

	private static Connection getConnection() throws SQLException
	{
		return MySQLConnection.getConnection();
	}
	
	public boolean storeTweet(Tweet t)  
	{
		try 
		{
			Timestamp timestamp = new Timestamp(t.getTimestamp().getTime());
			
			String query = "INSERT INTO `SM`.`Tweets` (`id_str`, `timestamp`, `text`) VALUES ('" +
					t.getId() + "', ?" + ", '" + t.getText().replace("'", "\\'") + "');";
			
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setTimestamp(1, timestamp);
			stmt.execute();
		} 
		catch (SQLException e) 
		{
			String tweetText = t.getText();
			System.out.println(tweetText);
//			throw new DataStoreException(e);
		}

		return false;
	}

	public static List<String> getTweetsByTime(Date from, Date to) throws SQLException 
	{
		Timestamp tsFrom	= new Timestamp(from.getTime());
		Timestamp tsTo		= new Timestamp(to.getTime());
		
		//TODO finish writing this query based on schema
		String query = "SELECT `text` FROM `SM`.`Tweets` WHERE timestamp > ? AND timestamp < ?;";
		PreparedStatement stmt = getConnection().prepareStatement(query);
		stmt.setTimestamp(1, tsFrom);
		stmt.setTimestamp(2, tsTo);
		
		return getTweetsFromStmt(stmt);
	}
	
	public static List<String> getTweets() throws SQLException
	{
		
		String query = "SELECT `text` FROM `SM`.`Tweets`;";
		PreparedStatement stmt = getConnection().prepareStatement(query);
		return getTweetsFromStmt(stmt);
	}
	
	private static List<String> getTweetsFromStmt(PreparedStatement stmt) throws SQLException
	{
		LinkedList<String> tweets = new LinkedList<String>();
		
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			String twt = rs.getString("text");
			tweets.add(twt);
		}
		
		return tweets;
	}

}
