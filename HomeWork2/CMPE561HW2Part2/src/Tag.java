import java.util.Comparator;

/**
 * this class is for keeping a tag and its id 
 * default comparator of it is tag id
 * @author onurm
 *
 */
public class Tag implements Comparator<Tag>,Comparable<Tag>{
	public int tagId;
	public String tagName;
	
	/**
	 * default constructor
	 */
	public Tag(){
		
	}
	
	public Tag(int tagId,String tagName){
		this.tagId = tagId;
		this.tagName = tagName;
	}
	
	@Override
	public int compareTo(Tag arg0) {
		// TODO Auto-generated method stub
		return this.compare(this, arg0);
	}

	@Override
	public int compare(Tag arg0, Tag arg1) {
		// TODO Auto-generated method stub
		Integer tag1 = arg0.tagId;
		Integer tag2 = arg1.tagId;
		
		return -tag1.compareTo(tag2);
	}
}
