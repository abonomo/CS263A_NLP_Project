package tfkld;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class MSRTrainingData implements TrainingData
{
	private LinkedList<String> similarStrings		= new LinkedList<String>();
	private LinkedList<String> dissimilarStrings	= new LinkedList<String>();
	
	public MSRTrainingData() throws IOException
	{
		loadTrainingData();
	}
	
	private void loadTrainingData() throws IOException
	{
		String filename = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/MSRParaphraseCorpus/msr_paraphrase_train.txt";
		//smaller training set for debugging
//		String filename = "/Users/alexanderbonomo/Documents/School/CS263A_NLP/project/MSRParaphraseCorpus/msr_paraphrase_train_small.txt";
		
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		//discard the first line
		String line = in.readLine();
		while((line = in.readLine()) != null)
		{
			String[] parts = line.split("\\t");
			int iIsSame = (new Integer(parts[0])).intValue();			
			String sentence1 = parts[3];
			String sentence2 = parts[4];

			if(iIsSame == 0)
			{
				this.dissimilarStrings.add(sentence1);
				this.dissimilarStrings.add(sentence2);
			}
			if(iIsSame == 1)
			{
				this.similarStrings.add(sentence1);
				this.similarStrings.add(sentence2);
			}
		}
		
		in.close();
	}

	@Override
	public Collection<String> getPositiveStrings() 
	{
		return this.similarStrings;
	}

	@Override
	public Collection<String> getNegativeStrings() 
	{
		return this.dissimilarStrings;
	}
	
}
