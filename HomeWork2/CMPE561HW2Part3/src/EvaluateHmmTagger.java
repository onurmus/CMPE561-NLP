import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * this method will evaluate results of tagger
 * @author onurm
 *
 */
public class EvaluateHmmTagger {

	
	//current list of tags depending on whether we want postag or cpostag
	static ArrayList<String> currentTagList;
	
	// to keep the words and their frequencies in tags
	// it keeps words in the format=> word : { <tag1, termFrequency1>,
	// <tag2,termFrequency2>}
	//I will call tag and frequency pairs as Posting
	static Map<String, TreeSet<TagFreqType>> wordTagDictionary;
	
	//this will keep the results of our tagger
	//it will keep word and its corresponding tag pair.
	static List<WordTagTuple<String, String>> outputResults;
	
	//this will keep word and its corresponding tag pair from given gold file.
	static List<WordTagTuple<String, String>> goldResults;	

	//this is tag type coming from word tag dictionary file
	static String tagType;
	
	public static void main(String[] args) throws FileNotFoundException {

		String outputFilePath = args[0];
		String goldFilePath = args[1];
		
		//to read word tag dictionary files
		//use FileRead class
		FileRead fr = new FileRead();
		fr.readWordTagDictionary();
		//read given hmm tagger output file
		fr.readOutputFile(outputFilePath);
		//read given gold file
		fr.readGoldFile(goldFilePath);
		
		wordTagDictionary = fr.wordTagDictionary;
		currentTagList = fr.currentTagList;
		outputResults = fr.outputResults;
		goldResults = fr.goldResults;
		
		//compute evaluation results
		compareResultWithGold();
		
	}
	
	/**
	 * this method will calculate all evaluation results
	 */
	private static void compareResultWithGold(){
		
		//if given gold file and parser output sizes are not same then give error
		if(goldResults.size() != outputResults.size()){
			System.out.println("Comparision lists does not have same size! gold: "+ goldResults.size()+ " output: "+outputResults.size());
			return;
		}
		
		//number of true tagged words
		int trueResult=0;
		//total number of words
		int totalResult=0;
		
		//number of true tagged unknown words
		int trueResultOverUnKnown = 0;
		//number of true tagged known words
		int trueResultOverKnown =0;
		//total number of unknown words
		int totalResultOverUnKnown = 0;
		//total number of known words
		int totalResultOverKnown =0;
		
		// confusion matrix for keeping and evaluating results
		int[][] confusionMatrix = new int[currentTagList.size()][currentTagList.size()];
		
		//for each word-tag pair in the tagger result
		for(int i=0 ; i< outputResults.size(); i++){
			//gets corresponding word-tag tuples from lists.
			WordTagTuple<String, String> outputTuple = outputResults.get(i);
			WordTagTuple<String, String> goldTuple = goldResults.get(i);
			
			//if words in result and gold files are not same then just alert them
			// and continue with new word
			if(!outputTuple.word.equals(goldTuple.word)){
				System.out.println("Same index words are not same output: "+outputTuple.word+" gold: "+goldTuple.word);
				continue;
			}
			
			totalResult++;
			
			//whether word was in the training file or in other words in word tag dictionary
			boolean isWordKnown = wordTagDictionary.containsKey(outputTuple.word);
			
			//increment word known counters accoringly
			if(!isWordKnown)
				totalResultOverUnKnown++;
			else
				totalResultOverKnown++;
			
			//if tags are same then increment true results
			if(outputTuple.tag.equals(goldTuple.tag)){ 

				trueResult++;
				
				if(!isWordKnown)
					trueResultOverUnKnown++;
				else
					trueResultOverKnown++;
				
				//add true result to confiuion matrix
				int tagIndex = getTagIndex(goldTuple.tag);
				confusionMatrix[tagIndex][tagIndex]++;
			}else{
				//add false result with true one, to confisuion matrix
				int currentTagIndex = getTagIndex(outputTuple.tag);
				int trueTagIndex = getTagIndex(goldTuple.tag);
				confusionMatrix[currentTagIndex][trueTagIndex]++;
			}
		}
		
		System.out.println("overal accuracy : " + (double)trueResult/totalResult);
		System.out.println("overal accuracy known : " + (double)trueResultOverKnown/totalResultOverKnown);
		System.out.println("overal accuracy unknown : " + (double)trueResultOverUnKnown/totalResultOverUnKnown);
		System.out.println();
		
		double macroSumRecall = 0; // to calculate macro-averaging recall
		double macroSumPresicion = 0; // to calculate macro-averaging precision
		double macroSumFscore = 0; // to calculate macro-averaging f-score

		System.out.println("Tag results:");
		for (int i = 0; i < currentTagList.size(); i++) { // for each Tag

			double recallOfTag = 0; // recall of Tag
			double precisionOfTag = 0; // precision of Tag
			double fScore = 0; // f-score of Tag

			int someCij = 0;
			int someCji = 0;
			for (int j = 0; j < currentTagList.size(); j++) {
				someCij += confusionMatrix[i][j];
				someCji += confusionMatrix[j][i];

			}

			// calculate recall
			recallOfTag = (double) confusionMatrix[i][i] / someCij; 
			// calculate precision
			precisionOfTag = (double) confusionMatrix[i][i] / someCji; 

			// prevent them from being NaN
			if (Double.isNaN(recallOfTag)) {
				recallOfTag = 0;
			}
			if (Double.isNaN(precisionOfTag)) {
				precisionOfTag = 0;
			}
			fScore = 2 * recallOfTag * precisionOfTag / (precisionOfTag + recallOfTag);
			if (Double.isNaN(fScore)) {
				fScore = 0;
			}

			// for calculating macro averaing sum them
			macroSumRecall += recallOfTag;
			macroSumPresicion += precisionOfTag;
			macroSumFscore += fScore;
			
			
			System.out.println(currentTagList.get(i) +
					" : precision: " + new DecimalFormat("#0.00000").format(recallOfTag)
							+ "  | recall: " + new DecimalFormat("#0.00000").format(precisionOfTag)
							+ "  | fScore: " + new DecimalFormat("#0.00000").format(fScore));

		}
		
		System.out.println();
		System.out.println(
				"Macro:   precision: " + new DecimalFormat("#0.00000").format(macroSumPresicion / currentTagList.size())
						+ "  | recall: " + new DecimalFormat("#0.00000").format(macroSumRecall / currentTagList.size())
						+ "  | fScore: " + new DecimalFormat("#0.00000").format(macroSumFscore / currentTagList.size()));		
	}
	
	/**
	 * this method will return tag index from current tag list
	 * this index corresponds to tag id of tag
	 * @param tag
	 * @return
	 */
	public static int getTagIndex(String tag){
		for(int i=0; i< currentTagList.size(); i++){
			if(currentTagList.get(i).equals(tag))
				return i;
		}
		return -1;
	}
}
