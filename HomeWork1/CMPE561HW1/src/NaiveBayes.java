import java.util.Map;
import java.util.TreeSet;

/**
 * this class is probabilistic calculations of naiveBayes
 * 
 * @author onurm
 *
 */
public class NaiveBayes {

	// corpus of training set
	private Map<String, TreeSet<Posting>> vocabulary;

	// total number of document in all classes
	private int numberOfDocinAllClasses;

	// for each class it keeps class information
	private Map<Integer, ClassObj> classInfo;

	// given document's all words and their frequencies
	private Map<String, Integer> givenDoc;

	// for smoothing coefficient alpha
	private final double laplaceCoeff = 0.01;

	// whether to use feature set or not
	private boolean useFeatureSet;

	// feature set of given document
	FeatureSet givenDocFeatureSet;

	/**
	 * Default Constructor
	 */
	public NaiveBayes(Map<String, TreeSet<Posting>> vocabulary, int numberOfDocinAllClasses,
			Map<Integer, ClassObj> classInfo, Map<String, Integer> givenDoc, FeatureSet givenDocFeatureSet,
			boolean useFeatureSet) {
		this.vocabulary = vocabulary;
		this.numberOfDocinAllClasses = numberOfDocinAllClasses;
		this.classInfo = classInfo;
		this.givenDoc = givenDoc;
		this.givenDocFeatureSet = givenDocFeatureSet;
		this.useFeatureSet = useFeatureSet;
	}

	/**
	 * this method returns the suggested class for a given document
	 * 
	 * @return class id
	 */
	public int getDocsSuggestedClass() {

		int vocabularySize = vocabulary.size(); // size of vocabulary

		double maxProb = -9999999999.9; // maximum probability
		int maxClassId = 0; // the class that has maximum probability

		// for each class
		for (Map.Entry<Integer, ClassObj> entry : classInfo.entrySet()) {
			ClassObj currentClass = entry.getValue(); // current class info

			double totalProb = 0;// Probability of class

			// probability of a document to be in this class namely P(c)
			double probBeingClass = (double) currentClass.getNumberOfDoc() / numberOfDocinAllClasses;
			// i will sum log of elements rather than multiplication
			probBeingClass = Math.log10(probBeingClass);

			totalProb += probBeingClass;

			// total number of words in current class
			int totalNumOfWordInClass = currentClass.getNumberOfWords();

			// for each word in the given document
			for (Map.Entry<String, Integer> wordEntry : givenDoc.entrySet()) {

				int wordFreq = wordEntry.getValue(); // get word frequency

				// frequency of word in this class
				int wordCntInClass = getNumOfOccurencesInClassOfWord(currentClass.getClassId(), wordEntry.getKey());

				// probabilty of word being in this class namely P(w|c)
				double probOfWordInClass = (double) (wordCntInClass + laplaceCoeff)
						/ (totalNumOfWordInClass + laplaceCoeff * vocabularySize);
				probOfWordInClass = Math.log10(probOfWordInClass);

				// multiply it with frequency because of logarithmic operation
				totalProb += wordFreq * probOfWordInClass;

			}

			if (useFeatureSet == true) {// use feature set when calculating
										// probability
				double a = getFeatureScore(currentClass);
				totalProb += a;
			}

			// if current class' probability is higher than mark it as max
			if (totalProb > maxProb) {
				maxProb = totalProb;
				maxClassId = currentClass.getClassId();
			}

		}

		return maxClassId;
	}

	/**
	 * this method returns the number of occurrences of a given word in a given
	 * class
	 * 
	 * @param classId
	 *            class id
	 * @param word
	 *            word
	 * @return number of occurrences
	 */
	public int getNumOfOccurencesInClassOfWord(int classId, String word) {
		int numberOfOccurences = 0;

		// if vocabulary does not have word return 0
		if (!vocabulary.containsKey(word)) {
			return 0;
		}
		// get posting list of given word in vocabulary
		TreeSet<Posting> postings = vocabulary.get(word);

		if (postings.size() > 0) {
			for (Posting posting : postings) {// for each posting
				if (posting.getClassId() == classId) {// if this is wanted class
					// then get number of occurrences
					numberOfOccurences = posting.getTermFrequency();
					break;
				}
			}
		}

		return numberOfOccurences;
	}

	/**
	 * this class calculated feature set scores for a given class and document
	 * 
	 * @param clas
	 *            class
	 * @return
	 */
	public double getFeatureScore(ClassObj clas) {
		double TOLERANCE = 0.00001;
		// dissimilarity in number of words
		double simWordCount = Math.log(
				TOLERANCE + Math.abs(clas.getFeatureSet().getAvgWordCount() - givenDocFeatureSet.getAvgWordCount()));
		// dissimilarity in total length of words
		double simDocLength = Math.log(
				TOLERANCE + Math.abs(clas.getFeatureSet().getAvgDocLength() - givenDocFeatureSet.getAvgDocLength()));

		// dissimilarity in average of word legth
		double simDocWordLen = Math.log(TOLERANCE
				+ Math.abs(clas.getFeatureSet().getAvgDocWordLength() - givenDocFeatureSet.getAvgDocWordLength()));

		// dissimilarity in comma usage
		double simComma = Math.log(
				TOLERANCE + Math.abs(clas.getFeatureSet().getAvgNumOfComma() - givenDocFeatureSet.getAvgNumOfComma()));

		// dissimilarity in quotation mark usage
		double simQuotMark = Math.log(TOLERANCE
				+ Math.abs(clas.getFeatureSet().getAvgNumOfQuotMark() - givenDocFeatureSet.getAvgNumOfQuotMark()));

		// dissimilarity in Exclamation mark usage
		double simExcMark = Math.log(TOLERANCE
				+ Math.abs(clas.getFeatureSet().getAvgNumOfExcMark() - givenDocFeatureSet.getAvgNumOfExcMark()));

		// dissimilarity in number of sentences
		double simSentenceNum = Math.log(TOLERANCE
				+ Math.abs(clas.getFeatureSet().getAvgNumOfSentence() - givenDocFeatureSet.getAvgNumOfSentence()));

		return -5 * (simWordCount + simDocLength + simDocWordLen)
				- 1 * (simComma + simQuotMark + simExcMark + simSentenceNum);
	}
}
