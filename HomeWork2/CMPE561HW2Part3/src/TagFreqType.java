import java.util.Comparator;

/**
 * this class is for keeping the tag ,its id and its frequency for any word 
 * default comparator is tag id
 * @author onurm
 *
 */
public class TagFreqType implements Comparator<TagFreqType>, Comparable<TagFreqType> {
	private Tag tag;
	private int frequency;

	/**
	 * default constructor
	 */
	public TagFreqType() {

	}
	
	public TagFreqType(int tagId, int frequency) {
		this.tag = new Tag(); 
		this.tag.tagId = tagId;
		this.frequency = frequency;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public int getTagId() {
		return tag.tagId;
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
