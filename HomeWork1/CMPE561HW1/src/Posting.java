import java.util.Comparator;

/**
 * this class is posting class for a word in corpus.
 * it keeps document id of document and term frequency of word in this document.
 * document id determined by a map, for each document I keep name of document and assign an  id to it
 * @author OnurM
 *
 */
public class Posting implements Comparator<Posting>,Comparable<Posting>{
	
	private int classId; 
	private int termFrequency;

	
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	public int compare(Posting arg0, Posting arg1) {
		// TODO Auto-generated method stub
		Integer docId1 = arg0.getClassId();
		Integer docId2 = arg1.getClassId();
		return -docId1.compareTo(docId2);
	}

	public int compareTo(Posting arg0) {
		// TODO Auto-generated method stub
		return this.compare(this, arg0);
	}
	
}
