Name: Karish Gupta
Student ID: 690580795

Section I:
To compile code: javac Main.java HashBasedJoin.java NestedLoopJoin.java HashBasedAggregation.java
To run code: java Main

You will then be prompted with "Waiting for command"
To run section 2 enter: “SELECT A.Col1, A.Col2, B.Col1, B.Col2 FROM A, B WHERE A.RandomV = B.RandomV”
To run section 3 enter: “SELECT count(*) FROM A, B WHERE A.RandomV > B.RandomV”
To run section 4 enter: “SELECT Col2, <AggregationFunction(ColumnID)> FROM <Dataset> GROUP BY Col2” (where aggregation function and dataset are specified)
To exit the command loop, enter "exit" and end the program


Section II:
All sections of assignment are working. Completed all 3 sections for ISU requirement.


Section III:
One design decision to take note of is in Section 2 (Building Hash-Based Join), I chose not to sort the data, so I did not use binary search.

For this project I decided to create 3 separate classes (one for each command).
Each class has a handle[command] method that is called in Main.
Two of the classes also have a getRandomV method, which takes in a full record as a string,
and returns the RandomV value of the record by parsing it.
One thing to note is that the output for the Hash-Based Aggregation command prints the aggregation
values with decimal points rounded to two decimal points.

