import java.util.Comparator;

/**
 * this class is for keeping the tag and its frequency for any word 
 * @author onurm
 *
 */
public class TagFreqType implements Comparator<TagFreqType>,Comparable<TagFreqType>{
	//id of tag
	private int tagId;
	//frequency value
	private int frequency;
	
	/**
	 * default constructor
	 */
	public TagFreqType(){
		
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public int compareTo(TagFreqType arg0) {
		// TODO Auto-generated method stub
		return this.compare(this, arg0);
	}

	@Override
	public int compare(TagFreqType arg0, TagFreqType arg1) {
		// TODO Auto-generated method stub
		Integer tag1 = arg0.getTagId();
		Integer tag2 = arg1.getTagId();
		
		return -tag1.compareTo(tag2);
	}
	
	
	
}
