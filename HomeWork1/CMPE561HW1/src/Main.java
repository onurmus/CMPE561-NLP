import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Main {

	// total number of documents in all classes
	static int totalDocsinAllClass = 0;

	// path to test data set
	static String testPath = "Sets/Test";

	// path to training dataset
	static String trainingPath = "Sets/Training";

	// to take the invertedIntex of training set.
	// it keeps words in the format=> word : { <class1, termFrequency1>,
	// <class2,termFrequency2>}
	static Map<String, TreeSet<Posting>> invertedIndex;

	// keep each class' number of documents
	static Map<Integer, ClassObj> classes;

	/**
	 * main method of project
	 * 
	 * @param args
	 *            paths to data set, training set and test sets respectively
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		trainingPath = args[0];
		testPath = args[1];

		// train the train set and get result.
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.read(trainingPath); // read and training training data
		invertedIndex = tokenizer.invertedIndex; // get vocabulary
		classes = tokenizer.classes; // get created classes
		// get total number of documents in all classes
		totalDocsinAllClass = tokenizer.totalDocsinAllClass;

		// test the text data set with Bag of words (BoW) Model
		analyze(false);

		// test the text data set with Bag of words (BoW) + created Feature set
		analyze(true);
	}

	/**
	 * this method makes test operations and result calculations
	 * 
	 * @param useFeatureSet
	 *            use feature set or just BoW
	 * @throws FileNotFoundException
	 *             e
	 * @throws UnsupportedEncodingException
	 *             e
	 */
	public static void analyze(boolean useFeatureSet) throws FileNotFoundException, UnsupportedEncodingException {
		Tokenizer fileOp = new Tokenizer(); // to read files

		// get all folders of test class
		ArrayList<File> testFolderList = new ArrayList<File>(fileOp.getFolderList(testPath));

		// confusion matrix for keeping and evaluating results
		int[][] confusionMatrix = new int[classes.size()][classes.size()];

		for (File testFolder : testFolderList) { // for each test class
			int currentClassId = getClassIdFromName(testFolder.getName());

			// for each file in this folder OR
			// for each document in test class
			for (File classFile : testFolder.listFiles()) {

				// get document terms and their frequencies
				Map<String, Integer> docVector = new HashMap<String, Integer>(fileOp.getDocVector(classFile.getPath()));
				// get feature set for given document
				FeatureSet givenDocFeatureSet = fileOp.docFeatureSet;

				// create Naive Bayes Class
				NaiveBayes bayes = new NaiveBayes(invertedIndex, totalDocsinAllClass, classes, docVector,
						givenDocFeatureSet, useFeatureSet);

				// get most probable class that this document belongs to
				int suggestedClass = bayes.getDocsSuggestedClass();

				// fill the confusion matrix
				if (suggestedClass == currentClassId) { // if true class
					confusionMatrix[currentClassId][currentClassId]++;
				} else {
					confusionMatrix[currentClassId][suggestedClass]++;
				}

			}
		}

		double macroSumRecall = 0; // to calculate macro-averaging recall
		double macroSumPresicion = 0; // to calculate macro-averaging precision
		double macroSumFscore = 0; // to calculate macro-averaging f-score

		int totalTpFpTnFn = 0; // to sum up confusion matrix elements
		int totalDiagonal = 0; // to calculate tp's

		for (int i = 0; i < classes.size(); i++) { // for each class

			double recallOfClass = 0; // recall of class
			double precisionOfClass = 0; // precision of class
			double fScore = 0; // f-score of class

			totalDiagonal += confusionMatrix[i][i]; // add tp to total tp

			int someCij = 0;
			int someCji = 0;
			for (int j = 0; j < classes.size(); j++) {
				someCij += confusionMatrix[i][j];
				someCji += confusionMatrix[j][i];

				totalTpFpTnFn += confusionMatrix[i][j];
			}

			recallOfClass = (double) confusionMatrix[i][i] / someCij; // calculate
																		// recall
			precisionOfClass = (double) confusionMatrix[i][i] / someCji; // calculate
																			// precision
			// prevent them from being NaN
			if (Double.isNaN(recallOfClass)) {
				recallOfClass = 0;
			}
			if (Double.isNaN(precisionOfClass)) {
				precisionOfClass = 0;
			}
			fScore = 2 * recallOfClass * precisionOfClass / (precisionOfClass + recallOfClass);
			if (Double.isNaN(fScore)) {
				fScore = 0;
			}

			// for calculating macro averaing sum them
			macroSumRecall += recallOfClass;
			macroSumPresicion += precisionOfClass;
			macroSumFscore += fScore;

		}

		if (useFeatureSet == true) {
			System.out.println("BoW + My FeatureSet Results:");
		} else {
			System.out.println("BoW Results:");
		}
		System.out.println(
				"Macro:   precision: " + new DecimalFormat("#0.00000").format(macroSumPresicion / classes.size())
						+ "  | recall: " + new DecimalFormat("#0.00000").format(macroSumRecall / classes.size())
						+ "  | fScore: " + new DecimalFormat("#0.00000").format(macroSumFscore / classes.size()));

		// micro averaging is same for recall and precision because they both
		// equals to tp/total, so that f-score
		double micro = (double) totalDiagonal / totalTpFpTnFn;
		System.out.println("Micro:    precision: " + new DecimalFormat("#0.00000").format(micro) + "  | recall: "
				+ new DecimalFormat("#0.00000").format(micro) + "  | fScore: "
				+ new DecimalFormat("#0.00000").format(micro));

	}

	/**
	 * this method gets id of class using its name note that class names are
	 * kept in classObj class
	 * 
	 * @param className
	 *            class' name
	 * @return
	 */
	public static int getClassIdFromName(String className) {
		for (Map.Entry<Integer, ClassObj> entry : classes.entrySet()) {
			if (entry.getValue().getName().equals(className)) {
				return entry.getKey();
			}
		}
		return -1;
	}

}
