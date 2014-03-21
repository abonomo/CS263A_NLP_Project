package latentSpace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tfkld.MSRTrainingData;
import tfkld.TFKLD;

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
	
	public static void printClusterAssignments(String arffFile, String tweetFile) throws IOException
	{
		Map<String, Set<String>> clusters = getClusterAssignments(arffFile, tweetFile);
		
		//now we want to print the tweets to separate files (1 file per cluster)
		int lastFileSep = arffFile.lastIndexOf(File.separator) + 1;
		String basePath = arffFile.substring(0, lastFileSep);//"/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/clusters/";
		for(String curCluster : clusters.keySet())
		{
			PrintWriter out = new PrintWriter(basePath + curCluster + "_tweets.txt");
			for(String curTweet : clusters.get(curCluster))
				out.println(curTweet);
			
			out.close();
		}
	}
	
	public static void printTopTermsPerCluster(String arffFile,String tweetFile) throws IOException
	{
		TFKLD tfkld = new TFKLD(new MSRTrainingData());
		Map<String, Set<String>> clusters = getClusterAssignments(arffFile, tweetFile);
		
		//now we want to print the tweets to separate files (1 file per cluster)
		int lastFileSep = arffFile.lastIndexOf(File.separator) + 1;
		String basePath = arffFile.substring(0, lastFileSep);//"/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/clusters/";
		for(String curCluster : clusters.keySet())
		{
			System.out.println(curCluster);
//			PrintWriter out = new PrintWriter(basePath + curCluster + "_topTerms.txt");
			
			Map<String, Double> totalTermWeights = new LinkedHashMap<String, Double>();
			for(String tweet : clusters.get(curCluster))
			{
				String[] tweetWords = tweet.split("[\\s]");
				for(int i = 0; i < tweetWords.length; i++)
				{
					String curTerm = tweetWords[i];
					double curWeight = tfkld.getKLDWeight(curTerm);
					if(totalTermWeights.containsKey(curTerm))
					{
						double newWeight = totalTermWeights.get(curTerm) + curWeight;
						totalTermWeights.put(curTerm, newWeight);
					}
					else
						totalTermWeights.put(curTerm, curWeight);
				}
			}
			
			//now sort the total term weights by the values
			ArrayList<Entry<String, Double>> termWeightArray = new ArrayList<Entry<String, Double>>();
			termWeightArray.addAll(totalTermWeights.entrySet());
			Collections.sort(termWeightArray, new Comparator<Entry<String, Double>>()
			{
				@Override
				public int compare(Entry<String, Double> arg0, Entry<String, Double> arg1) 
				{
					return arg0.getValue().compareTo(arg1.getValue());
				}
				
			});
			for(int i = 0; i < termWeightArray.size(); i++)
				System.out.println(termWeightArray.get(i));
		}
	}
	
	public static Map<String, Set<String>> getClusterAssignments(String arffFile, String tweetFile) throws IOException
	{
		//obtain actual tweets in order
		ArrayList<String> tweets = Tweet.getTweetsFromFile(tweetFile);
//		ArrayList<String> tweets = Tweet.getTweetsFromFile("/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/tweets.txt");
		
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
		
		return clusters;
	}
	
	public static void main(String[] args) throws IOException
	{
//		String filepath = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/nmfW.tsv";
//		matlabTSVtoWekaCSV(filepath);
		
		String arffFile = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/clusters_mar_18/em_4.arff";
		String tweetFile = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/matlab/mar_18/tweets.txt";
//		printClusterAssignments(arffFile, tweetFile);
		
		printTopTermsPerCluster(arffFile, tweetFile);
	}
}
