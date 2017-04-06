# Nearest-Neighbour-Classifier

### This project implements k-nearest neighbor classifiers in JAVA.

### Compilation command: 
        - javac -classpath . knnclassify.java


### Execution command:
        - java knnclassify pendigits_training.txt pendigits_test.txt 5
        - java knnclassify satellite_training_adj.txt satellite_test_adj.txt 5
        - java knnclassify yeast_training_adj.txt yeast_test_adj.txt 5


### Note:
        - In execution commands change last arguments to try different K values.
        - Input files with named '*_adj' are files updated to modify class labels from 0 to (no_of_class - 1) insted of 
          1 to no_of_class.
