import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class TrainTaggerMain {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		//TrainHmmTagger class will do necessary operations 
		TrainHmmTagger tht = new TrainHmmTagger(args);

		//read train file and create dictionary
		tht.readTrainConll();
		//print dictionary to file
		tht.printDictionary();
		//print tag transition matrix to file 
		tht.printTagTransitionMap();

	}
}
