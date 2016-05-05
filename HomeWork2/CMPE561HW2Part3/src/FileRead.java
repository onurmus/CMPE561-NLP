import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class FileRead {

	//postag set from treebank
	String[] arrPostags = new String[]{ "Num", "Card", "Ord", "Percent", "Range", "Real", "Ratio", "Distrib", "Time",
			"Noun", "Inf", "APresPart","NInf","PastPart","APastPart","NPastPart", "FutPart","NFutPart","AFutPart", "Prop", "Zero", "Adj", "PastPart", "FutPart", "PresPart", "Pron",
			"DemonsP", "QuesP", "ReflexP", "PersP", "QuantP", "Adv", "Conj", "Det", "Dup", "Interj", "Ques", "Verb",
			"Postp", "Punc" }; 
	
	//cpostag set from treebank
	//here I also added "Zero" as tag because despite it is not treebank set,
	//it is in train file
	String[] arrcPostags = new String[] { "Noun", "Adj", "Adv", "Conj", "Det", "Dup", "Interj", "Ques", "Verb", "Postp","Zero",
				"Num", "Pron", "Punc" };

	//list of postags
	ArrayList<String> postags = new ArrayList<String>(Arrays.asList(arrPostags));
	
	//list of cpostags
	ArrayList<String> cPostags = new ArrayList<String>(Arrays.asList(arrcPostags));
	
	//current list of tags depending on whether we want postag or cpostag
	public ArrayList<String> currentTagList;

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
	
	//this will keep the results of our tagger
	//it will keep word and its corresponding tag pair.
	public List<WordTagTuple<String, String>> outputResults;
	
	//this will keep word and its corresponding tag pair from given gold file.
	public List<WordTagTuple<String, String>> goldResults;	

	//this is tag type coming from word tag dictionary file
	public String tagType;

	
	/**
	 * this method will read output file and gets word-tag pairs in it
	 * @param outputFilePath
	 * @throws FileNotFoundException
	 */
	public void readOutputFile(String outputFilePath) throws FileNotFoundException{
		outputResults = new ArrayList<WordTagTuple<String,String>>();
		
		Scanner scn = new Scanner(new File(outputFilePath));
		
		while(scn.hasNextLine()){
			String line = scn.nextLine();
			if(line.trim().equals("")){
				continue;
			}
			//get word|tag structured pair and add it to list
			String[] tupleArr = line.split("[|]");
			WordTagTuple<String, String> tuple = new WordTagTuple<String,String>();
			tuple.tag=tupleArr[1];
			tuple.word = tupleArr[0];
			outputResults.add(tuple);
		}
		
	}
	
	/**
	 * this method will read gold file and gets word-tag pairs in it 
	 * @param goldFilePath
	 * @throws FileNotFoundException
	 */
	public void readGoldFile(String goldFilePath) throws FileNotFoundException{
		goldResults = new ArrayList<WordTagTuple<String,String>>();
		
		Scanner scn = new Scanner(new File(goldFilePath));
			
		while(scn.hasNextLine()){
			String line = scn.nextLine();

			// we an empty line appear then this is end of sentence, do not
			// process it;
			if (line.trim().equals("")){
				continue;
			}
				
			String params[] = line.split("\\s");

			String form = params[1];

			// if form starts with an underscore then ignore it
			if (form.equals("_"))
				continue;
			
			WordTagTuple<String, String> tuple = new WordTagTuple<String,String>();
			tuple.word = form;
			
			String cPostag = params[3];
			String postag = params[4];
			
			if(tagType.equals("cpostag")){
				tuple.tag = cPostag;
			}else if(tagType.equals("postag")){
				tuple.tag = postag;
			}else{
				System.out.println("cannot find tag");
			}
			
			//add created word-tag tuple to list
			goldResults.add(tuple);
			
		}
	}

	/**
	 * this method is for reading the word tag dictionary from file
	 * @throws FileNotFoundException
	 */
	public void readWordTagDictionary() throws FileNotFoundException{
		Scanner scn = new Scanner(new File("wordTagDictionary.txt"),"UTF-8");
		
		String nextLine = scn.nextLine();
		//get tag type from first line
		tagType = nextLine.substring(nextLine.indexOf("Tag type: ")+10);
				
		//assign current tag list according to tag type
		if(tagType.equals("cpostag")){
			currentTagList = cPostags;
		}else if(tagType.equals("postag")){
			currentTagList = postags;
		}
		
		scn.nextLine();//emtpy line
		
		wordTagDictionary = new HashMap<String, TreeSet<TagFreqType>>();
		
		//reead dictionary from file
		while(scn.hasNextLine()){
			if(!scn.hasNext()) break;
			
			String word = scn.next();
			
			int numOfDiffClass = scn.nextInt();
			
			for(int i = 0; i< numOfDiffClass; i++){
				scn.next(); // <
				int classId = scn.nextInt();
				scn.next(); // ,
				int freqency = scn.nextInt();
				scn.next(); // >
				
				//add created word to dictionary map.
				addToDictionary(word, classId,freqency);
			}
		}
		
	}

	/**
	 * this method wii add read word and tag and its frequency in this tag to dictionary
	 * @param term
	 * @param tagId
	 * @param frequency
	 */
	private void addToDictionary(String term, int tagId, int frequency) {

		if (wordTagDictionary.containsKey(term)) {// if term is in dictionary
			// get posting list of word
			Set<TagFreqType> postingList = wordTagDictionary.get(term); 
			
			// create a temporary post to
			// control whether posting list contains
			// current tag or not
			TagFreqType tempPost = new TagFreqType(); 
			if(tagId != -1){
				tempPost.setTag(new Tag(tagId,currentTagList.get(tagId)));
			}else{
				tempPost.setTag(new Tag(tagId,"Unknown"));
			}	
			tempPost.setFrequency(frequency);

			postingList.add(tempPost);
		} else {// if term is not in dictionary add it to dictionary
			// posting list for new term
			TreeSet<TagFreqType> postList = new TreeSet<TagFreqType>();
			TagFreqType posting = new TagFreqType(); // posting for new tag
			posting.setTag(new Tag(tagId,currentTagList.get(tagId)));
			posting.setFrequency(frequency);
			postList.add(posting); // add posting to posting list
			wordTagDictionary.put(term, postList); // insert to map
		}
	}


}
