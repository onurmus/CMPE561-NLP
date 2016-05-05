import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
	
	static int totalDocsinAllClass=0;
	static String testPath = "Sets/Test";
	static String trainingPath = "Sets/Training";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String pathToDataSet = args[0];
		trainingPath = args[1];
		testPath = args[2];
		
		createTestAndTrainingSets(pathToDataSet);
		
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.read(trainingPath);
	}
	
	
	public static void createTestAndTrainingSets(String pathToDataSet) throws IOException{
	
		File train = new File(trainingPath);
		if(train.exists()){
			deleteDirectory(train);
		}
		
		File test = new File(testPath);
		if(test.exists()){
			deleteDirectory(test);
		}
		if(!train.mkdirs()){
			System.out.println("Training Directory oluşturulamadı");
		}
		
		if(!test.mkdirs()){
			System.out.println("Test Directory oluşturulamadı");
		}
		
		List<File> allFolders = getFileList(pathToDataSet);
		
		for(File folder : allFolders ){
			
			File trainingFolder = new File(trainingPath+"/"+folder.getName());
			File testFolder = new File(testPath+"/"+folder.getName());
			
			if(!trainingFolder.mkdir()) System.out.println(trainingFolder.getName()+" oluşturulamadı±");
			if(!testFolder.mkdir()) System.out.println(testFolder.getName()+" oluşturulamadı±");
			
			int numOfDocs = folder.listFiles().length;
			
			totalDocsinAllClass += numOfDocs;
			
			int numOfTraining = (int) Math.ceil(numOfDocs * 0.6);
			
			ArrayList<Integer> allDocIds = new ArrayList<Integer>();
			
			for(int i=0; i<numOfDocs; i++){
				allDocIds.add(i);
			}
			
			Collections.shuffle(allDocIds);
			
			ArrayList<Integer> trainingDocIds = new ArrayList<Integer>(allDocIds.subList(0, numOfTraining));
			
			for(int i=0; i<numOfDocs; i++){
				
				CopyOption[] options = new CopyOption[]{
										  StandardCopyOption.REPLACE_EXISTING,
										  StandardCopyOption.COPY_ATTRIBUTES
										}; 
				Path FROM = Paths.get(folder.listFiles()[i].getAbsolutePath());
				
				if(trainingDocIds.contains(i)){			
					Path TO = Paths.get(new File(trainingFolder.getAbsolutePath()+"/"+folder.listFiles()[i].getName()).getAbsolutePath());
					Files.copy(FROM, TO, options);
				}else{
					Path TO = Paths.get(new File(testFolder.getAbsolutePath()+"/"+folder.listFiles()[i].getName()).getAbsolutePath());
					Files.copy(FROM, TO, options);
				}
			}
		}
		
	}
	
	public static List<File> getFileList(String path) throws FileNotFoundException{
		File folder = new File(path);
		List<File> fileList = new ArrayList<File>();	//list of files that is ended with txt
		for (File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isFile()) {
	        	fileList.add(fileEntry);
	        
	        }
	    }
		
		return fileList;
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
}
