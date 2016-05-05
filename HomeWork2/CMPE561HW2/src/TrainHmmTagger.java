import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * this class makes main operations to train the tagger
 * @author onurm
 *
 */
public class TrainHmmTagger {

	//train file path
	private String trainFilePath;
	//tag type
	private String tagType;
	
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
	ArrayList<String> currentTagList;

	//tag transition matrix.
	//this will be used to keep consecutive tag occurrences
	// tagTransitions[i][j]: number of times tag j comes after tag i.
	// also keep in mind that start tag is the last row of the array
	int[][] tagTransitions; 
	
	// to keep the words and their frequencies in tags
	// it keeps words in the format=> word : { <tag1, termFrequency1>,
	// <tag2,termFrequency2>}
	//I will call tag and frequency pairs as Posting
	private Map<String, TreeSet<TagFreqType>> wordTagDictionary;

	/**
	 * default constructor
	 * @param args  coming from main method
	 */
	public TrainHmmTagger(String[] args) {

		trainFilePath = args[0];
		tagType = args[1];
		wordTagDictionary = new HashMap<String, TreeSet<TagFreqType>>();
		
		//if tag type is cpostag
		if(tagType.equals("cpostag")){
			currentTagList = cPostags;
			//create with one more row which is for keeping start tag of sentence.
			tagTransitions = new int[cPostags.size()+1][cPostags.size()];
		}else if(tagType.equals("postag")){//if tag type is postag
			currentTagList = postags;
			//create with one more row which is for keeping start tag of sentence
			tagTransitions = new int [postags.size()+1][postags.size()];
		}
	}

	/**
	 * this method will read the train file
	 * feed word tag dictionary
	 * and tag transition matrix
	 * @throws FileNotFoundException
	 */
	public void readTrainConll() throws FileNotFoundException {
		Scanner scn = new Scanner(new File(trainFilePath));

		int prevTagId = -1; //to keep previous tag
		int tagId = -1; //to keep current tag
		
		//number of tags including start tag 
		int rowNum = tagTransitions.length;
		
		while (scn.hasNextLine()) {
			String line = scn.nextLine();

			// we an empty line appear then this is end of sentence, do not
			// process it;
			if (line.trim().equals("")){
				tagId = -1;
				prevTagId = -1;
				continue;
			}
				
			//split by white spaces
			String params[] = line.split("\\s");

			String form = params[1];

			// if form starts with an underscore then ignore it
			if (form.equals("_"))
				continue;

			String cPostag = params[3];
			String postag = params[4];
			
			tagId = -1;
			
			//for tags, get their indexes from current tag list
			// this indexes will be their IDs
			if(tagType.equals("postag")){
				if(currentTagList.contains(postag)){
					tagId = currentTagList.indexOf(postag);
				}else{
					System.out.println("unknown tag : " + postag);
				}
			}else if(tagType.equals("cpostag")){
				if(currentTagList.contains(cPostag)){
					tagId = currentTagList.indexOf(cPostag);
				}else{
					System.out.println("unknown tag : " + cPostag);
				}
			}

			//add this word to dictionary with given tagId
			addToDictionary(form.toLowerCase(),tagId);
			
			//if tag not found continue
			if(tagId == -1) continue;
				
			// if previous tag is start add it to the end row
			if(prevTagId == -1){ 
				tagTransitions[rowNum-1][tagId] += 1;
			}else{
				tagTransitions[prevTagId][tagId] += 1;
			}
			
			//update previous tag
			prevTagId = tagId;
		}
	}

	/**
	 * this adds given word to dictionary
	 * 
	 * @param term
	 *            given term
	 * @param tagId
	 *            given tag id
	 */
	private void addToDictionary(String term, int tagId) {
		
		if (wordTagDictionary.containsKey(term)) {// if term is in dictionary
			
			// get posting list of word
			Set<TagFreqType> postingList = wordTagDictionary.get(term); 

			// create a temporary post to
			// control whether posting list contains
			// current tag or not
			TagFreqType tempPost = new TagFreqType(); 
			tempPost.setTagId(tagId);
			tempPost.setFrequency(0);

			// if posting list contains current tagId
			if (postingList.contains(tempPost)) { 
				Iterator<TagFreqType> it = postingList.iterator();
				TagFreqType current;
				while (it.hasNext()) { // iterate over posting list
					current = it.next();
					// find tag and then increase term frequency for tag by one
					if (current.getTagId() == tagId) { 
						int occurences = current.getFrequency();
						occurences++;
						current.setFrequency(occurences);
					}

				}
			} else { // if tag is new then add it to posting set
				TagFreqType newPosting = new TagFreqType();
				newPosting.setTagId(tagId);
				newPosting.setFrequency(1);
				postingList.add(newPosting);
			}

		} else {// if term is not in dictionary add it to dictionary
			// posting list for new term
			TreeSet<TagFreqType> postList = new TreeSet<TagFreqType>();
			TagFreqType posting = new TagFreqType(); // posting for new tag
			posting.setTagId(tagId);
			posting.setFrequency(1);
			postList.add(posting); // add posting to posting list
			wordTagDictionary.put(term, postList); // insert to map
		}
	}
	
	/**
	 * this will print word tag dictionary to wordTagDictionary.txt
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	public void printDictionary() throws FileNotFoundException, UnsupportedEncodingException{
		Map<String,TreeSet<TagFreqType>> corpus = wordTagDictionary;
		PrintStream out = new PrintStream(new File("wordTagDictionary.txt"),"UTF-8");
		//firstly add tag type to start of file
		out.println("Tag type: "+tagType);
		out.println();
		for(Map.Entry<String, TreeSet<TagFreqType>> entry : corpus.entrySet()){
			String term = entry.getKey();
			out.print(term +" "+entry.getValue().size()+" ");
			for(TagFreqType posting : entry.getValue()){
				out.print("< "+ posting.getTagId() + " , "+ posting.getFrequency() + " > ");
			}
			out.print("\n");
		}
		out.close();
	}
	
	/**
	 * this will print tag transition matrix to tagTransitions.txt file
	 * it will print this array as matrix to file. 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	public void printTagTransitionMap() throws FileNotFoundException, UnsupportedEncodingException{
		PrintStream out = new PrintStream(new File("tagTransitions.txt"),"UTF-8");
		//firstly add tag type to start of file
		out.println("Tag type: "+tagType);
		out.println();
		
		out.print("\t\t");
		for(int i = 0; i <currentTagList.size(); i++){
			out.print(currentTagList.get(i)+ "\t");
		}
		out.println();
		
		for(int i = 0 ; i<tagTransitions.length; i++){
			if(i != tagTransitions.length-1)
				out.print(currentTagList.get(i)+"\t");
			else
				out.print("<st>\t");
			
			for(int j=0; j< tagTransitions[0].length; j++){
				out.print(tagTransitions[i][j]+"\t");
			}
			out.println();
		}
		
		out.close();
	}

}
