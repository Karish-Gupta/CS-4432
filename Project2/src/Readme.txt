Name: Karish Gupta
Student ID: 690580795

ISU External Merge Sort:
To run the external merge sort functionality, first compile all files:
- javac RecordLocation.java Index.java Main.java ExternalMergeSort.java RecordSortLocation.java

Then to run the program run:
- java Main -mergesort [buffer size]
Buffer size must be 11 or greater.

Then it should print "String has been written to the file successfully." for each phase 1 file created
and "Final merged file complete", when the final merged file is complete.

The files will appear in the MergeSortFiles folder.

PhaseOne files are labeled as "PhaseOne[run].txt" and the final merged file is "Merge.txt".
The final merge takes a bit more time than the phase one merges.

It is best to delete all PhaseOne.txt files and the final merged file before running this again.

Additionally, when testing ensure that the path constant is set to your PC's respective path.
This can be changed in the "myPath" and "mergeSortFilesPath" constants in ExternalMergeSort.java.


Base Assignment:
Section I:

To compile: javac RecordLocation.java Index.java Main.java
To run: java Main

To ensure that files are accessible make sure to change the myPath constant in Main and in Index files.
This constant is set to the path of the Project2Dataset folder of my personal computer. So please ensure it is set to the proper path.

Once the program is run, you may enter the following commands as in the instructions:
-   CREATE INDEX ON Project2Dataset (RandomV)
-   SELECT * FROM Project2Dataset WHERE RandomV = v
-   SELECT * FROM Project2Dataset WHERE RandomV > v1 AND RandomV < v2
-   SELECT * FROM Project2Dataset WHERE RandomV != v


Section II:

All parts of the code work as described in the assignment instructions.
Note: There are some randomV's that do not have any records associated with them such as 0082.
Many students brought this up on the Discord that 82 did not exist and I noticed that as well.

Section III:

I created a Record Location class that allows me to instantiate objects that hold a record's file number and offset.
I also created getters for both the offset and file number.

In Main I have multiple static methods that I call within the main program. Each of these runs a different search
function: equalitySearchWithIndex, rangeSearchWithIndex, tableScanEquality, tableScanRange, and inequalitySearch.
I also have a method called getData that takes in a file number and offset and returns the record as a string.

Having these methods as static methods allows me to easily call them within the main program and keep my code clean and readable.

