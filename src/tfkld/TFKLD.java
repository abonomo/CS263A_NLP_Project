package tfkld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TFKLD 
{
	private TreeMap<String, Integer>	positiveCount	= new TreeMap<String, Integer>();
	private TreeMap<String, Integer>	negativeCount	= new TreeMap<String, Integer>();
	private TreeMap<String, Double>		kldWeights		= new TreeMap<String, Double>();
	
	public TFKLD(TrainingData trainingData)
	{
		Collection<String> positive = trainingData.getPositiveStrings();
		for(String s : positive)
			updateCounts(s, true);
		
		Collection<String> negative = trainingData.getNegativeStrings();
		for(String s : negative)
			updateCounts(s, false);
		
		computeKLDWeights();
	}

	public ArrayList<Double> stringToTFKLDVector(String str)
	{
		int numFeatures = kldWeights.size();
		ArrayList<Double> tfkld = new ArrayList<Double>(numFeatures);
		
		//first get string's term frequency 
		Map<String, Integer> termCounts = new HashMap<String, Integer>();
		double maxTermCount = 0.0;
		String[] words = str.split("[\\s]");
		for(int i = 0; i < words.length; i++)
		{
			String w = words[i].replaceAll("[^\\w]", "");
			w = w.toLowerCase();
			if( ! termCounts.containsKey(w))
				termCounts.put(w, new Integer(1));
			else
			{
				int curCount = termCounts.get(w);
				termCounts.put(w, new Integer(curCount + 1));
			}
			
			if(termCounts.get(w) > maxTermCount)
				maxTermCount = termCounts.get(w).doubleValue();
		}
		
		//now iterate through features
		Iterator<String> featureItr = kldWeights.keySet().iterator();
		while(featureItr.hasNext())
		{
			String curFeature = featureItr.next();
			
			if(termCounts.containsKey(curFeature))
			{
				double tf = ((double)termCounts.get(curFeature)) / maxTermCount;
				double kld = kldWeights.get(curFeature);
				double d_tfkld = tf * kld;
				
				tfkld.add(new Double(d_tfkld));
			}
			else
				tfkld.add(new Double(0.0));
		}
		
		return tfkld;
	}
	
	public double getKLDWeight(String term)
	{
		if(this.kldWeights.containsKey(term))
			return this.kldWeights.get(term);
		
		return 0.0;
	}
	
	private void updateCounts(String sentence, boolean isPositive)
	{
		//add counts to the appropriate map
		TreeMap<String, Integer> count = isPositive ? positiveCount : negativeCount;
		String[] words = sentence.split("[\\s]");				
		for(int i = 0; i < words.length; i++)
		{
			//remove non-word characters
			String w = words[i];
			w = w.replaceAll("[^\\w]", "");
			w = w.toLowerCase();
			
			if(!count.containsKey(w))
				count.put(w, new Integer(1));
			else
			{
				int curCount = count.get(w);
				count.put(w, new Integer(curCount + 1));
			}
		}		
	}
	
	private void computeKLDWeights()
	{
		Set<String> features = new LinkedHashSet<String>();
		
		features.addAll(positiveCount.keySet());
		features.addAll(negativeCount.keySet());
		
		System.out.println("Num features:" + features.size());
		
		for(String w : features)
		{
			double posCount = 1.0;
			double negCount = 1.0;
			
			if(positiveCount.containsKey(w))
				posCount += positiveCount.get(w).doubleValue();
			if(negativeCount.containsKey(w))
				negCount += negativeCount.get(w).doubleValue();
			
			double totalCount = posCount + negCount;
			double pk = posCount / totalCount;
			double qk = negCount / totalCount;
			
			double kldWeight = pk * Math.log(pk / qk);
			kldWeights.put(w, new Double(kldWeight));
		}
	}
	
	
}
