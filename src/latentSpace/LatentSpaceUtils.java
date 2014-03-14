package latentSpace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import db.Tweet;

public class LatentSpaceUtils 
{
	public static void matlabTSVtoWekaCSV(String filePath) throws IOException
	{
		BufferedReader	in	= new BufferedReader(new FileReader(filePath));
		PrintWriter		out	= new PrintWriter(filePath + ".csv");
		
		String firstLine = null;
		String inLine;
		while((inLine = in.readLine()) != null)
		{
			String[] features = inLine.split("\\t");
			
			if(firstLine == null)
			{
				firstLine = "dim1";
				for(int i = 2; i <= features.length; i++)
					firstLine += ",dim" + i;
				out.println(firstLine);
			}
			
			String curLine = features[0].trim();
			for(int i = 1; i < features.length; i++)
				curLine += "," + features[i].trim();
			out.println(curLine);
		}
		
		in.close();
		out.close();
	}
	
	public static void printClusterAssignments(String arffFile) throws IOException
	{
		//obtain actual tweets in order
		ArrayList<String> tweets = Tweet.getTweetsFromFile("/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/tweets.txt");
		
		//Map<clusterID, set of tweets>
		Map<String, Set<String>> clusters = new HashMap<String, Set<String>>();
		
		BufferedReader in = new BufferedReader(new FileReader(arffFile));
		
		String line;
		
		//run through until we've gotten to the data
		while((line = in.readLine()) != null)
			if(line.contains("@data"))
				break;
		
		//add the tweets to the clusters
		while((line = in.readLine()) != null)
		{
			String[] vector = line.split(",");
			int tweetIndex = (new Integer(vector[0])).intValue();
			
			String cluster = vector[vector.length - 1];
			if(!clusters.containsKey(cluster))
				clusters.put(cluster, new LinkedHashSet<String>());
			
			clusters.get(cluster).add(tweets.get(tweetIndex));
		}
		in.close();
		
		//now we want to print the tweets to separate files (1 file per cluster)
		String basePath = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/clusters/";
		for(String curCluster : clusters.keySet())
		{
			PrintWriter out = new PrintWriter(basePath + curCluster + "_tweets.txt");
			for(String curTweet : clusters.get(curCluster))
				out.println(curTweet);
			
			out.close();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
//		String filepath = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/nmfW.tsv";
//		matlabTSVtoWekaCSV(filepath);
		
		String arffFile = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/clusters/em_3.arff";
		printClusterAssignments(arffFile);
	}
}
