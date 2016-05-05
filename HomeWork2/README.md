## CMPE561 Spring 2016 Assignment 2 / Onur Musaoğlu
#About Repository
  This repository contains the second assignment and it consists of three modules. In CMPE561HW2 project you can find the project which trains a tagger according to a input file. I used METU-Sabanci Turkish Dependency Treebank dataset[1,2,3] to take the train data and tags. Secondly, in CMPE561HW2Part2 project you can find a simple taggger based on output of the first project. In the last project CMPE561HW2Part3 you can find the project which evaluates the results of the tagger.

#How to compile and run?
The first project creates two files and second and third files use these files.So to properyl run the second and third project you should keep the these files in the same folder with jar files.

Since these are java projects, to compile and run them you should have JDK in your machine. The first thing you should do is creating
jar files for each project as shown in this link: http://www.wikihow.com/Create-JAR-File. 
Then to run the programs you should run following commands in windows:

For training project you should give 2 parameters <training filename> [cpostagn|postagn]
java -jar trainin_hmm_tagger.jar "C:\Users\onurm\Desktop\Local Disc(D)\COURSES\CMPE561\HW2\metu_sabanci_cmpe_561\metu_sabanci_cmpe_561\train\turkish_metu_sabanci_train.conll" cpostag
  
For tagging an input file and getting output file you should give 2 parameters <input blind filename> <output filename>
java -jar hmm_tagger.jar "C:\Users\onurm\Desktop\Local Disc(D)\COURSES\CMPE561\HW2\metu_sabanci_cmpe_561\metu_sabanci_cmpe_561\validation\turkish_metu_sabanci_val.conll" "output.txt"

For getting the evaluation results you should give 2 parameters <output filename> <gold filename> to third project
java -jar evaluate_hmm_tagger.jar "output.txt" "C:\Users\onurm\Desktop\Local Disc(D)\COURSES\CMPE561\HW2\metu_sabanci_cmpe_561\metu_sabanci_cmpe_561\validation\turkish_metu_sabanci_val.conll"

Aftr running evaluation program, it will output performance measures for both tagsets.

#References
[1] Nart B Atalay, Kemal Oflazer, Bilge Say, et al. The annotation process in the turkish treebank.
In Proc. of the 4th Intern. Workshop on Linguistically Interpreteted Corpora (LINC). Citeseer,
2003.
[2] Gulsen Eryigit, Tugay Ilbay, and Ozan Arkan Can. Multiword expressions in statistical dependency
parsing. In Proceedings of the Second Workshop on Statistical Parsing of Morphologically
Rich Languages ( IWPT - 12th International Conference on Parsing Technologies),
pages 45–55, Dublin, Ireland, October 2011. Association for Computational Linguistics.
[3] Kemal Oflazer, Bilge Say, Dilek Zeynep Hakkani-T¨ur, and G¨okhan T¨ur. Building a turkish
treebank. In Treebanks, pages 261–277. Springer, 2003.