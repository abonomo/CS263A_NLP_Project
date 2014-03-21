package tfkld;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import db.MySQLTweetStore;

public class TweetsToMatlab 
{
	public static void main(String[] args) throws IOException, SQLException
	{
		TrainingData trainingData = new MSRTrainingData();
		TFKLD tfkld = new TFKLD(trainingData);
		
		PrintWriter vecFile = new PrintWriter("/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/tweets.m");
		PrintWriter txtFile = new PrintWriter("/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/tweets.txt");
		
		vecFile.println("function tweetRowVecs = tweets;");
		vecFile.println();
		vecFile.print("tweetRowVecs = [");
		
		//From and to dates
		Calendar from = Calendar.getInstance();
//		from.set(2014, Calendar.MARCH, 3, 6, 30);
//		from.set(2014, Calendar.MARCH, 14, 6, 30);
		from.set(2014, Calendar.MARCH, 18, 6, 30);
		
		Calendar to = Calendar.getInstance();
//		to.set(2014, Calendar.MARCH, 3, 13, 30);
//		to.set(2014, Calendar.MARCH, 14, 13, 30);
		to.set(2014, Calendar.MARCH, 18, 13, 30);
		
//		List<String> tweets = MySQLTweetStore.getTweets();
		List<String> tweets = MySQLTweetStore.getTweetsByTime(from.getTime(), to.getTime());
		System.out.println("Num tweets:" + tweets.size());
		int curTweet = 0;
		for(String s : tweets)
		{			
//			System.out.println(s);
			s.replaceAll("[\\n\\r]", "");
			txtFile.println(curTweet + ":" + s);
			ArrayList<Double> tweetFeatures = tfkld.stringToTFKLDVector(s);
			for(int i = 0; i < tweetFeatures.size(); i++)
				vecFile.print(tweetFeatures.get(i).toString() + " ");
			vecFile.print(";");
			vecFile.println();
			
			curTweet++;
		}
		
		vecFile.print("];");
		
		vecFile.close();
		txtFile.close();
	}
}
