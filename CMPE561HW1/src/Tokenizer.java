import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * this class is designed for taking files, creating corpus, creating vector of
 * documents, and doing tokenization operation
 * 
 * @author OnurM
 *
 */
public class Tokenizer {

	// to take the invertedIntex of training set.
	// it keeps words in the format=> word : { <class1, termFrequency1>,
	// <class2,termFrequency2>}
	public Map<String, TreeSet<Posting>> invertedIndex;

	// keep each class' number of documents
	public Map<Integer, ClassObj> classes;

	// feature set of given document to be read
	public FeatureSet docFeatureSet;

	// total number of documents for all classes
	public int totalDocsinAllClass = 0;

	// number of words in a document
	private int wordsInDoc = 0;

	// to calculate total word length of document
	private int totalwordLenInDoc = 0;

	// these are for keeping the number of characters that are mostly used in
	// turkish ie.: , ? ! .
	private int totalComasInDoc = 0;
	private int totalQuotMarkInDoc = 0;
	private int totalExcMacInDoc = 0;
	private int totalSentenceInDoc = 0;

	/**
	 * constructor. Initialize invertedIndex and fileToDocId list
	 */
	public Tokenizer() {
		invertedIndex = new TreeMap<String, TreeSet<Posting>>();
		classes = new HashMap<Integer, ClassObj>();
	}

	/**
	 * this method is for calculating number of words for a class
	 */
	public void getTotalNumberOfWordsinClass() {
		// for each vocabulary entry
		for (Map.Entry<String, TreeSet<Posting>> entry : invertedIndex.entrySet()) {
			for (Posting posting : entry.getValue()) { // for each posting
				// calculate frequency, increase it, then add to class
				int numOfWords = classes.get(posting.getClassId()).getNumberOfWords();
				numOfWords += posting.getTermFrequency();
				classes.get(posting.getClassId()).setNumberOfWords(numOfWords);
			}
		}
	}

	/**
	 * this method process a given document and take regarding info
	 * 
	 * @param path
	 *            path of file of document
	 * @return document terms and frequencies
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public Map<String, Integer> getDocVector(String path) throws UnsupportedEncodingException, FileNotFoundException {
		Map<String, Integer> docVector = new HashMap<String, Integer>();

		@SuppressWarnings("resource")
		Scanner scn = new Scanner(new InputStreamReader(new FileInputStream(path), "ISO-8859-9"));

		scn.next();

		wordsInDoc = 0;
		totalwordLenInDoc = 0;
		totalComasInDoc = 0;
		totalQuotMarkInDoc = 0;
		totalExcMacInDoc = 0;
		totalSentenceInDoc = 0;

		while (scn.hasNext()) { // for each new term
			String newTerm = scn.next();

			if (newTerm.contains(","))
				totalComasInDoc++;
			if (newTerm.contains("\""))
				totalQuotMarkInDoc++;
			if (newTerm.contains("!"))
				totalExcMacInDoc++;
			if (newTerm.contains("."))
				totalSentenceInDoc++;

			String candidateWord = newTerm;
			candidateWord = tokenize(candidateWord); // tokenize it
			if (!candidateWord.trim().equals("")) { // if not empty add it to
													// list
				candidateWord = candidateWord.toLowerCase(); // case fold
				if (!candidateWord.equals("")) {

					wordsInDoc++;
					totalwordLenInDoc += candidateWord.length();

					if (docVector.containsKey(candidateWord)) {// if word
																// already in
																// map
						int freq = docVector.get(candidateWord);
						freq++;
						docVector.put(candidateWord, freq);
					} else {// if word is new
						docVector.put(candidateWord, 1);
					}
				}
			}
		}

		// assign feature set properties for document
		docFeatureSet = new FeatureSet();
		docFeatureSet.setAvgDocLength((double) totalwordLenInDoc);
		docFeatureSet.setAvgDocWordLength((double) totalwordLenInDoc / wordsInDoc);
		docFeatureSet.setAvgWordCount((double) wordsInDoc);
		docFeatureSet.setAvgNumOfComma((double) totalComasInDoc);
		docFeatureSet.setAvgNumOfExcMark((double) totalExcMacInDoc);
		docFeatureSet.setAvgNumOfQuotMark((double) totalQuotMarkInDoc);
		docFeatureSet.setAvgNumOfSentence((double) totalSentenceInDoc);

		return docVector;
	}

	/**
	 * this will call readGivenFile method for all files in training set
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 *             e
	 */
	public void read(String path) throws FileNotFoundException, UnsupportedEncodingException {
		// list of author folders
		List<File> trainingFolderList = new ArrayList<File>(getFolderList(path));

		int classId = 0;// class id of classes.
		for (File myFolder : trainingFolderList) {// for each folder
			ClassObj clas = new ClassObj();
			clas.setName(myFolder.getName());
			clas.setClassId(classId);
			clas.setNumberOfDoc(myFolder.listFiles().length);

			int docsInClass = myFolder.listFiles().length;

			totalDocsinAllClass += docsInClass;

			int totalWordsInClass = 0;	//to keep words in this class
			int totalWordsLength = 0;	//to keep total Class word length

			for (File classFiles : myFolder.listFiles()) {

				wordsInDoc = 0;
				totalwordLenInDoc = 0;
				totalComasInDoc = 0;
				totalQuotMarkInDoc = 0;
				totalExcMacInDoc = 0;
				totalSentenceInDoc = 0;

				readGivenFile(classFiles, classId);

				totalWordsInClass += wordsInDoc;
				totalWordsLength += totalwordLenInDoc;
			}

			//assign class' feature set
			clas.getFeatureSet().setAvgDocLength((double) totalWordsLength / docsInClass);
			clas.getFeatureSet().setAvgDocWordLength((double) totalWordsLength / totalWordsInClass);
			clas.getFeatureSet().setAvgWordCount((double) totalWordsInClass / docsInClass);
			clas.getFeatureSet().setAvgNumOfComma((double) totalComasInDoc / docsInClass);
			clas.getFeatureSet().setAvgNumOfExcMark((double) totalExcMacInDoc / docsInClass);
			clas.getFeatureSet().setAvgNumOfQuotMark((double) totalQuotMarkInDoc / docsInClass);
			clas.getFeatureSet().setAvgNumOfSentence((double) totalSentenceInDoc / docsInClass);
			classes.put(classId, clas);

			classId++;
		}

		printMap(invertedIndex);//print vocabulary to file
		getTotalNumberOfWordsinClass();	//get class term frequencies
	}

	/**
	 * This reads given file and takes all terms one by one and gives term to
	 * tokenization operations
	 * 
	 * @param myFile
	 *            given file
	 * @param classId
	 *            given file determined class id
	 * @throws FileNotFoundException
	 *             exception
	 * @throws UnsupportedEncodingException
	 *             e
	 */
	public void readGivenFile(File myFile, int classId) throws FileNotFoundException, UnsupportedEncodingException {
		@SuppressWarnings("resource")
		Scanner scn = new Scanner(new InputStreamReader(new FileInputStream(myFile), "ISO-8859-9"));
		scn.next(); // subject:

		int lastClassId = classId; // keep the info of what class id is read

		while (scn.hasNext()) { // for each new term
			String newTerm = scn.next();

			if (newTerm.contains(","))
				totalComasInDoc++;
			if (newTerm.contains("\""))
				totalQuotMarkInDoc++;
			if (newTerm.contains("!"))
				totalExcMacInDoc++;
			if (newTerm.contains("."))
				totalSentenceInDoc++;

			tokenizeTheTerm(newTerm, lastClassId); // add taken list of words to
													// token list

		}

	}

	/**
	 * to tokenize and add the token to tokenList
	 * 
	 * @param term
	 *            a new term
	 * @param classId
	 *            the current document id
	 */
	private void tokenizeTheTerm(String term, int classId) {
		String candidateWord = term;
		candidateWord = tokenize(candidateWord); // tokenize it
		if (!candidateWord.trim().equals("")) { // if not empty add it to list
			candidateWord = candidateWord.toLowerCase(); // case fold
			if (!candidateWord.equals("")) {
				wordsInDoc++;
				totalwordLenInDoc += candidateWord.length();
				addToCorpus(candidateWord, classId);
			}

		}
	}

	/**
	 * this adds given word to corpus
	 * 
	 * @param term
	 *            given term
	 * @param classId
	 *            given document id
	 */
	private void addToCorpus(String term, int classId) {
		if (invertedIndex.containsKey(term)) {// if term is in corpus
			
			Set<Posting> postingList = invertedIndex.get(term); // get posting
																// list

			Posting tempPost = new Posting(); // create a temp post to control
												// whether posting list contains
												// current class or not
			tempPost.setClassId(classId);
			tempPost.setTermFrequency(0);

			if (postingList.contains(tempPost)) { // if posting list contains
													// current classID
				Iterator<Posting> it = postingList.iterator();
				Posting current;
				while (it.hasNext()) { // iterate over posting list
					current = it.next();
					if (current.getClassId() == classId) { // find document and
															// then increase
															// term frequency
															// for document by
															// one
						int occurences = current.getTermFrequency();
						occurences++;
						current.setTermFrequency(occurences);
					}

				}
			} else { // if document is new then add it to posting set
				Posting newPosting = new Posting();
				newPosting.setClassId(classId);
				newPosting.setTermFrequency(1);
				postingList.add(newPosting);
			}

		} else {// if term is not in vocabulary add it to vocabulary
			TreeSet<Posting> postList = new TreeSet<Posting>();// post list for
																// new term
			Posting posting = new Posting(); // posting for new document
			posting.setClassId(classId);
			posting.setTermFrequency(1);
			postList.add(posting); // add posting to posting list
			invertedIndex.put(term, postList); // insert to map
		}
	}

	/**
	 * for printing corpus to the corpus.txt
	 * 
	 * @param corpus
	 *            the inverted index print each term with the form: term
	 *            {postings list: <docId: term frequency>}
	 * @throws FileNotFoundException
	 */
	public void printMap(Map<String, TreeSet<Posting>> corpus) throws FileNotFoundException {
		PrintStream out = new PrintStream(new File("corpus.txt"));
		for (Map.Entry<String, TreeSet<Posting>> entry : corpus.entrySet()) {
			String term = entry.getKey();
			out.print(term + " " + entry.getValue().size() + " ");
			for (Posting posting : entry.getValue()) {
				out.print("< " + posting.getClassId() + " , " + posting.getTermFrequency() + " > ");
			}
			out.print("\n");
		}
		out.close();
	}

	/**
	 * this method is for tokenize the token
	 * 
	 * @param word
	 *            token
	 * @return tokenized token
	 */
	public String tokenize(String word) {
		// remove all hypens from string and concatanate string
		while (word.contains("-")) {
			word = word.substring(0, word.indexOf("-")) + word.substring(word.indexOf("-") + 1, word.length());
		}

		// if the last char of string not a letter or number then remove it
		while (word.length() > 0 && !Character.isLetter(word.charAt(word.length() - 1))
				&& !Character.isDigit(word.charAt(word.length() - 1))) {
			word = word.substring(0, word.length() - 1);
		}

		// if the first char of string not a letter or number then remove it
		while (word.length() > 1 && !Character.isLetter(word.charAt(0)) && !Character.isDigit(word.charAt(0))) {
			word = word.substring(1, word.length());
		}

		// this part is for Clitics
		if (word.contains("'")) {
			if ((word.indexOf("'") - word.length()) < 3) { // if it is like I'm,
															// or dog's
				word = word.substring(0, word.indexOf("'"));
			}
		}

		// if word length is greater than 8 then cut the word because probably
		// it has a lot suffix
		if (word.length() > 8) {
			word = word.substring(0, 8);
		}

		return word;
	}

	/**
	 * gets files of type txt in a given folder
	 * 
	 * @param path
	 *            path of given folder
	 * @return files to be read
	 * @throws FileNotFoundException
	 *             file not found exception
	 */
	public List<File> getFileList(String path) throws FileNotFoundException {
		File folder = new File(path);
		List<File> fileList = new ArrayList<File>(); // list of files that is
														// ended with txt
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {

				if (fileEntry.getName().endsWith(".txt")) { // for all files
															// that is ended
															// with txt
					fileList.add(fileEntry);
				}
			}
		}

		return fileList;
	}

	/**
	 * gets folders of given folder
	 * 
	 * @param path
	 *            path of given folder
	 * @return files to be read
	 * @throws FileNotFoundException
	 *             file not found exception
	 */
	public List<File> getFolderList(String path) throws FileNotFoundException {
		File folder = new File(path);
		List<File> fileList = new ArrayList<File>(); // list of folders

		for (File fileEntry : folder.listFiles()) {
			if (!fileEntry.isFile()) {
				fileList.add(fileEntry);
			}
		}

		return fileList;
	}

}
