/**
 * this class keeps necessary information for a class
 * 
 * @author onurm
 *
 */
public class ClassObj {
	int classId; // classId
	int numberOfDoc; // number of document in this class
	int numberOfWords; // number of words in this class
	String name; // class name like abbasGuclu

	FeatureSet featureSet; // featureSet I implemented

	/**
	 * default constructor
	 */
	public ClassObj() {
		classId = 0;
		numberOfDoc = 0;
		numberOfWords = 0;
		name = "";
		featureSet = new FeatureSet();
	}

	public FeatureSet getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getNumberOfDoc() {
		return numberOfDoc;
	}

	public void setNumberOfDoc(int numberOfDoc) {
		this.numberOfDoc = numberOfDoc;
	}

	public int getNumberOfWords() {
		return numberOfWords;
	}

	public void setNumberOfWords(int numberOfWords) {
		this.numberOfWords = numberOfWords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
