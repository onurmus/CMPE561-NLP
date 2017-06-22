## CMPE561 Spring 2016 Assignment 1 / Onur Musaoğlu
#### About Repository
  This repository contains the first assignment and it consists of two modules. In CMPE561CreateFolders project you can find the project
which takes path of data set and creates test set and training sets and after makes toknization on training set. You can see the corpus
created from training set in the corpus.txt file after run this program.
  Secondly CMPE561HW1 project is a document classification project, which takes two paths as argument that are training data set path and
test data set path and outputs the performance of classifiers in it.

#### How to compile and run?
Since these are java projects, to compile and run them you should have JDK in your machine. The first thing you should do is creating
jar files for each project as shown in this link: http://www.wikihow.com/Create-JAR-File. 
Then to run the programs you should run following commands in windows:

For creating test and training data sets from dataset you should give 3 parameters <dataset_path> <output_training_set> <output_test_set>
java -jar training_and_test_set_builder.jar "C:\Users\onurm\Desktop\Local Disc(D)\COURSES\CMPE561\69yazar\69yazar\raw_texts"
  "C:\Users\onurm\Desktop\Set\Training" "C:\Users\onurm\Desktop\Set\Test"
  
For running text classifier and see the outputs you should give 2 parameters <training_set_path> <test_set_path>
java -jar my_authorship_recognition_system.jar "C:\Users\onurm\Desktop\Set\Training" "C:\Users\onurm\Desktop\Set\Test"

Aftr running classification program, it will output performance measures for both ,BoW and BoW+My feature set, classifiers.
