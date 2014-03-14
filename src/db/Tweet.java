package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet 
{
	private String		id;
	private String		text;
	private Date 		timestamp;
	
	public Tweet(JSONObject json) throws JSONException, ParseException
	{
		if(json.has("id_str"))
			id = json.getString("id_str");
		if(json.has("text"))
			text = json.getString("text");
		if(json.has("created_at"))
		{
			SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
			String createdAt = json.getString("created_at");
			timestamp = parser.parse(createdAt);
		}
		
	}

	public String getId() 
	{
		return id;
	}

	public String getText() 
	{
		return text;
	}

	public Date getTimestamp() 
	{
		return timestamp;
	}
	
	@Override
	public String toString()
	{
		return "id:" + this.id + ", created_at:" + this.timestamp + ", text:" + this.text;
	}
	
	public static ArrayList<String> getTweetsFromFile(String filepath) throws IOException
	{
		ArrayList<String> tweets = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(filepath));
		
		int curTweetID = 0;
		String curTweet = "";
		String line;
		while((line = in.readLine()) != null)
		{
			if(line.startsWith(curTweetID + ":"))
			{
				curTweet = line;
			}
			else if(line.startsWith((curTweetID + 1) + ":"))
			{
				tweets.add(curTweet);
				
				curTweetID++;
				curTweet = line;
			}
			else
				curTweet += " " + line;
		}
		tweets.add(curTweet);
		
		in.close();
		return tweets;
	}
}
