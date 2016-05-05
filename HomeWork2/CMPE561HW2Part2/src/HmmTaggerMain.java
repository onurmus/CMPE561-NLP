import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * main class for hmm tagger
 * @author onurm
 *
 */
public class HmmTaggerMain {
	
	//current list of tags depending on whether we want postag or cpostag
	static ArrayList<String> currentTagList;

	//tag transition matrix.
	//this will be used to keep consecutive tag occurrences
	// tagTransitions[i][j]: number of times tag j comes after tag i.
	// also keep in mind that start tag is the last row of the array
	static int[][] tagTransitions; 
	
	// to keep the words and their frequencies in tags
	// it keeps words in the format=> word : { <tag1, termFrequency1>,
	// <tag2,termFrequency2>}
	//I will call tag and frequency pairs as Posting
	static Map<String, TreeSet<TagFreqType>> wordTagDictionary;
	
	/**
	 * main method
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		String testFilePath = args[0];
		String outputFilePath = args[1];
		
		//to read tag transitions and word tag dictionary files
		//use FileRead class
		FileRead fr = new FileRead();
		fr.readTagTransitions();
		fr.readWordTagDictionary();
		wordTagDictionary = fr.wordTagDictionary;
		tagTransitions = fr.tagTransitions;
		currentTagList = fr.currentTagList;
		
		readTestBlindFile(testFilePath,outputFilePath);
		
	}
	
	/**
	 * this method will read the test file
	 * outputs output.txt file
	 * It calls Viterbi algorithm for each sentence 
	 * @param testFilePath
	 * @param outputFilePath
	 * @throws FileNotFoundException
	 */
	public static void readTestBlindFile(String testFilePath,String outputFilePath) throws FileNotFoundException{
		Scanner scn = new Scanner(new File(testFilePath));
		
		PrintStream out = new PrintStream(new File(outputFilePath));
		
		//by using ViterbiAlgorithm class we will tag the given file
		ViterbiAlgorithm va = new ViterbiAlgorithm(wordTagDictionary,tagTransitions,currentTagList);
		
		//this list will contains words in a sentence in given test file
		List<String> currentSentence = new ArrayList<String>();
		
		while(scn.hasNextLine()){
			String line = scn.nextLine();

			// when an empty line appear then this is end of sentence, do not
			// process it;
			//this is end of the sentence so send it to the ViterbiAlgorithm
			if (line.trim().equals("")){
				va.setSentence(currentSentence);
				va.viterbi(out);
				out.println();
				//also re initialize sentence for coming new sentence
				currentSentence = new ArrayList<String>();
				continue;
			}
				
			//split by white spaces
			String params[] = line.split("\\s");

			String form = params[1];
			
			// if form starts with an underscore then ignore it
			if (form.equals("_"))
				continue;
			
			//add word to sentence
			currentSentence.add(form);

		}

		//since last sentence will not processed in the while loop
		//owing to the empty line at the end of the file
		//make tagging operation also for it
		if(currentSentence.size() > 0){
			va.setSentence(currentSentence);
			va.viterbi(out);
		}
		out.close();
	}
}
