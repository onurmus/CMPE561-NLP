/**
 * this class for keeping the feature set information that I designed
 * 
 * @author onurm
 *
 */
public class FeatureSet {
	double avgDocWordLength; // average word length in a document
	double avgWordCount; // average number of words in a document
	double avgDocLength; // average document length for a document
	double avgNumOfComma; // average number of commas in a document
	double avgNumOfQuotMark; // average number of Quotation mark
	double avgNumOfExcMark; // average number of Exclamation mark
	double avgNumOfSentence; // average number of sentences

	/**
	 * default constructor
	 */
	public FeatureSet() {

	}

	public double getAvgDocWordLength() {
		return avgDocWordLength;
	}

	public void setAvgDocWordLength(double avgDocWordLength) {
		this.avgDocWordLength = avgDocWordLength;
	}

	public double getAvgWordCount() {
		return avgWordCount;
	}

	public void setAvgWordCount(double avgWordCount) {
		this.avgWordCount = avgWordCount;
	}

	public double getAvgDocLength() {
		return avgDocLength;
	}

	public void setAvgDocLength(double avgDocLength) {
		this.avgDocLength = avgDocLength;
	}

	public double getAvgNumOfComma() {
		return avgNumOfComma;
	}

	public void setAvgNumOfComma(double avgNumOfComma) {
		this.avgNumOfComma = avgNumOfComma;
	}

	public double getAvgNumOfQuotMark() {
		return avgNumOfQuotMark;
	}

	public void setAvgNumOfQuotMark(double avgNumOfQuotMark) {
		this.avgNumOfQuotMark = avgNumOfQuotMark;
	}

	public double getAvgNumOfExcMark() {
		return avgNumOfExcMark;
	}

	public void setAvgNumOfExcMark(double avgNumOfExcMark) {
		this.avgNumOfExcMark = avgNumOfExcMark;
	}

	public double getAvgNumOfSentence() {
		return avgNumOfSentence;
	}

	public void setAvgNumOfSentence(double avgNumOfSentence) {
		this.avgNumOfSentence = avgNumOfSentence;
	}

}
