NAME: Karish Gupta
Student ID: 690580795

Section I:

Compile code by running: javac Frame.java BufferPool.java Main.java
Run code with: java Main <Number of frames in buffer>

*File absolute paths are used for data files. However, these paths are specific to my PC, so the data file paths would have to be different for your PC when testing.
This can be changed in the findFilePath method in bufferpool class.

Program will first prompt you with what frame eviction algorithm you would like to use.
You may choose between Circular (The algorithm described in the base homework assignment) and Least Recently Used (LRU).
Enter "Circular" or "LRU".

The program will then say: The program is ready for the next command (GET, SET, PIN, UNPIN).
Enter one of the commands in all caps. The arguments that go with the command will be prompted after you select the command.
"GET" will prompt you with "GET: Enter record number". So, simply enter the integer. PIN and UNPIN will also have similar prompts
SET will prompt you with the record number and then will prompt you for the new content you want to set.

Section II:

After going through all the test case commands, they should all work properly and have output with very similar content.
The only differences between my output and the test file may be the order of the output. For example, the line printed when a frame's content is evicted prints before a record.
However, everything printed is correctly and accurately.

The output for the algorithm described in the homework (circular) has just about identical output as the example.
The tests that I ran for my LRU algorithm also had correct output with all commands, as expected for this replacement policy.

Section III:

I implemented some extra getter and setter functions in the frame class such as isPinned() and isDirty(),
which just return these booleans. I also have setters for these variables as well.

One thing to note is that I use my constructors as initialize methods. So, the constructor initializes the buffer pool,
and the respective constructor initializes the frame. I do have an initialize method in the frame class, which I use to set a frame to initial conditions after eviction.

I implemented a findFilePath() method, which takes a blockID and retrieves the file path object that matches it in the folder. This was useful for writing to disk.

I also have methods and a class implemented for LRU. The LRU class has a Deque<Integer> queue, which is used to maintain the order of frame usage.
The use of a Deque allows me to move values from either end of the queue. The access() method is called whenever a frame is accessed (GET and SET) and moves the frame to the front of the queue.
The further from the back, the more recent the frame, and thus the further from eviction its content is.
The getQueue() method simply gets the queue, so we can access it.

In the buffer pool I implemented an LRU object variable, so I could use this data structure. I also implemented an LRUPolicy boolean,
which would tell me if LRU is in use or not (use circular algorithm).