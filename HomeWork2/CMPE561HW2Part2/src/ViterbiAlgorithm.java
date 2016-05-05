import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * this class is for operations for Viterbi Algorithm
 * @author onurm
 *
 */
public class ViterbiAlgorithm {
	
	//tag transition matrix.
	//this will be used to keep consecutive tag occurrences
	// tagTransitions[i][j]: number of times tag j comes after tag i.
	// also keep in mind that start tag is the last row of the array
	public int[][] tagTransitions; 
	
	// to keep the words and their frequencies in tags
	// it keeps words in the format=> word : { <tag1, termFrequency1>,
	// <tag2,termFrequency2>}
	//I will call tag and frequency pairs as Posting
	public Map<String, TreeSet<TagFreqType>> wordTagDictionary;
	
	double [][] tagTransitionNorm; // normalized tag transition table
	
	// to keep count(Ti) information of tags seperately 
	Map<Integer,Integer> tagCounts = new HashMap<Integer,Integer>();
	
	//current list of tags depending on whether we want postag or cpostag
	ArrayList<String> currentTagList;
	
	//current sentence to process
	private List<String> sentence;
	
	/**
	 * constructor
	 * @param wordTagDictionary
	 * @param tagTransitions
	 * @param currentTagList
	 */
	public ViterbiAlgorithm(Map<String, TreeSet<TagFreqType>> wordTagDictionary,int[][] tagTransitions,ArrayList<String> currentTagList){
		this.wordTagDictionary = wordTagDictionary;
		this.tagTransitions = tagTransitions;
		this.tagTransitionNorm = new double[tagTransitions.length][tagTransitions[0].length];
		this.currentTagList = currentTagList;
	}
	
	/**
	 * this method will do the Viterbi algorithm operations
	 * @param out print stream to write tag and word to output file
	 */
	public void viterbi(PrintStream out){
		//normalize tag transitions 
		normalizeTagTransitions();
		//calculate total nuvmer of usages of tags
		calculateTagCounts();
	
		//number of rows in algorithm matrix
		int rowNum = tagTransitions.length;
		
		//number of columns in algorithm matrix
		//sentence size plus 1 because of start tag
		int colNum = sentence.size()+1;
		//tags corresponding to rows and words corresponding to columns
		double[][] viterbi = new double[rowNum][colNum];
		
		viterbi[rowNum-1][0]= 1.0; //mark start probability as 1
		
		for(int j=1; j<colNum; j++){ // for each new word
			
			//calculate maximum likelihood up to this word
			double[] rtnArr = getPreviousMax(viterbi,j-1);
			double previousMax = rtnArr[1];
			int prevMaxTagId = (int)rtnArr[0];
			
			//print word and its found tag to file
			if( j > 1){
				out.println(sentence.get(j-2) + "|" + currentTagList.get(prevMaxTagId));
			}
			
			//for each tag
			for(int i=0; i<rowNum-1; i++){
				int currentTagID = i;
				String word = sentence.get(j-1);
				
				//calculate tag transition probabiliy P(Ti|(Ti-1))
				double PTiTim1 = tagTransitionNorm[prevMaxTagId][currentTagID];
								
				//calculate word likelihood probability P(Wi|Ti) = cnt(ti,wi)/count(ti)
				double PWiTi = (double)getWordCountForTag(word,currentTagID)/tagCounts.get(currentTagID);
					
				//calculate viterbi value for this tag and word
				viterbi[i][j] = previousMax * PTiTim1 * PWiTi;
			}
			
		}
		
		double[] rtnArr = getPreviousMax(viterbi,colNum-1);
		int prevMaxTagId = (int)rtnArr[0];

		//to prevent empty line duplication situations
		if(prevMaxTagId < currentTagList.size() && prevMaxTagId > -1 ){
			//print word and its found tag to file
			out.println(sentence.get(sentence.size()-1) + "|" + currentTagList.get(prevMaxTagId));
		}
	}
	
	/**
	 * this method will give  cnt(ti,wi)
	 * this means it will give the total number of usages of word in given tag
	 * @param word
	 * @param tagId
	 * @return
	 */
	private int getWordCountForTag(String word, int tagId){
		int frequency = 0;
		
		if(wordTagDictionary.containsKey(word)){
			TreeSet<TagFreqType> tagFreqList = wordTagDictionary.get(word);
			
			if(tagFreqList.contains(new TagFreqType(tagId,0))){
				
				Iterator<TagFreqType> it = tagFreqList.iterator();
				TagFreqType current;
				while (it.hasNext()) { // iterate over posting list
					current = it.next();
					if(current.getTagId() == tagId){
						frequency = current.getFrequency();
					}
				}
			}
		}else{
			//if word not found then control whether given tag is most frequent tag
			//if it is then return 1.
			// so that mark word as this tag
			if(tagId == getMostFrequentTagId()){
				return 1;
			}
		}
		
		return frequency;
	}
	
	/**
	 * this method will return the most frequently used tag Id
	 * @return
	 */
	private int getMostFrequentTagId(){
		
		int maxCount = -9999999;
		int maxID = -10000;
		
		for(Map.Entry<Integer, Integer> entry : tagCounts.entrySet()){
			if(entry.getValue() > maxCount){
				maxCount = entry.getValue();
				maxID = entry.getKey();
			}
		}
		
		return maxID;
	}
	
	/**
	 * this method will return the maximum value element
	 * in the given column in the given viterbi matrix
	 * @param viterbi
	 * @param col
	 * @return
	 */
	private double[] getPreviousMax(double[][] viterbi,int col){
		
		double maxTagId = -1000;
		double maxValue = -9999999;
		for(int i=0; i<viterbi.length; i++){
			if(viterbi[i][col] > maxValue){
				maxValue = viterbi[i][col];
				maxTagId = i;
			}
		}
		
		double[] rtnArr = {maxTagId,maxValue} ;
		
		return rtnArr;
	}
	
	/**
	 * this method will normalize the tag transition matrix.
	 * it will calculate transition probabilities from transition counts.
	 */
	private void normalizeTagTransitions(){
		for(int i =0; i< tagTransitions.length; i++){
			int sumOfRow = 0;
			for(int j = 0; j<tagTransitions[0].length; j++){
				sumOfRow += tagTransitions[i][j];
			}
			for(int j = 0; j<tagTransitions[0].length; j++){
				tagTransitionNorm[i][j] = (double)tagTransitions[i][j]/sumOfRow;
			}
		}
	}
	
	/**
	 * this method calculates the tag counts from dictionary
	 * and saves them to tagCounts map
	 */
	private void calculateTagCounts(){

		for(Map.Entry<String,TreeSet<TagFreqType>> entry : wordTagDictionary.entrySet()){
			Set<TagFreqType> postingList = entry.getValue();
						
			Iterator<TagFreqType> it = postingList.iterator();
			TagFreqType current;
			while (it.hasNext()) { // iterate over posting list
				current = it.next();
				int tagID = current.getTagId();
				int freq = current.getFrequency();
				
				if(tagCounts.containsKey(tagID)){
					Integer tempFreq = tagCounts.get(tagID);
					tempFreq += freq;
					tagCounts.put(tagID, tempFreq);
				}else{
					tagCounts.put(tagID, freq);
				}

			}
		}
		
		//if there is a tag not used in training set, 
		//then set its count as 0
		for(int i=0; i<currentTagList.size(); i++){
			if(!tagCounts.containsKey(i))
				tagCounts.put(i, 0);
		}
	}

	public List<String> getSentence() {
		return sentence;
	}

	public void setSentence(List<String> sentence) {
		this.sentence = sentence;
	}	

	
	
}
