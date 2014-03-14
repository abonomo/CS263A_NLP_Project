package tfkld;

import java.util.Collection;

public interface TrainingData 
{
	public Collection<String> getPositiveStrings();
	
	public Collection<String> getNegativeStrings();
}
