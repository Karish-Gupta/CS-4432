import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    static final String myPath = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project2\\src\\Project2Dataset\\"; // Project2Dataset path for my PC

    /***
     * Gets the record as a string
     * @param fileNum, int file number
     * @param offset, int record offset
     * @return string, the 40 byte record
     */
    public static String getData(int fileNum, int offset) {
        String filePath = myPath + "F" + fileNum + ".txt";
        Path path = Paths.get(filePath);
        try {
            byte[] fileData = Files.readAllBytes(path);
            byte[] record = new byte[40];

            System.arraycopy(fileData, offset, record, 0, 40);
            return new String(record, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Uses hash index to find the record of given randomV and prints output
     * @param parts, array of string, user input command in array form
     * @param index, index object
     */
    public static void equalitySearchWithIndex(String[] parts, Index index) {
        HashSet<Integer> filesAccessed = new HashSet<Integer>();
        int v = Integer.parseInt(parts[7]);
        // Time to answer query
        long startTime = System.currentTimeMillis();
        ArrayList<RecordLocation> valueList = index.hashIndex.get(v); // Query

        // Using hash index for query lookup
        for (int i = 0; i < valueList.size(); i++) {
            RecordLocation value = valueList.get(i);
            int fileNumber = value.getFileNumber();
            int offset = value.getOffset();
            String record = getData(fileNumber, offset);
            filesAccessed.add(fileNumber);
            System.out.println(record);
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Used hash-based index");
        System.out.println("Time taken to answer the query: " + elapsedTime + " milliseconds");
        System.out.println("Number of data files (disk blocks) read: " + filesAccessed.size() + "\n");
    }

    /***
     * Uses hash index to find range of records between two randomV's and prints output
     * @param parts, array of string, user input command in array form
     * @param index, index object
     */
    public static void rangeSearchWithIndex(String[] parts, Index index) {
        int v1 = Integer.parseInt(parts[7]);
        int v2 = Integer.parseInt(parts[11]);
        HashSet<Integer> filesAccessed = new HashSet<Integer>();

        @SuppressWarnings("unchecked")
        ArrayList<RecordLocation>[] records = new ArrayList[v2 - v1 - 1];

        // Using array index for query lookup
        int iterator = 0;
        long startTime = System.currentTimeMillis();
        for (int i = v1 + 1; i < v2; i++) {
            records[iterator] = index.arrayIndex[i];
            iterator++;
        }

        // Getting values and printing them
        for (int i = 0; i < records.length; i++) {
            for (int j = 0; j < records[i].size(); j++) {
                RecordLocation value = records[i].get(j);
                int fileNumber = value.getFileNumber();
                int offset = value.getOffset();
                String record = getData(fileNumber, offset);
                filesAccessed.add(fileNumber);
                System.out.println(record);
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Used array-based index");
        System.out.println("Time taken to answer the query: " + elapsedTime + " milliseconds");
        System.out.println("Number of data files (disk blocks) read: " + filesAccessed.size() + "\n");
    }

    /***
     * Conducts a full table scan to find records for given randomV and prints output
     * @param parts, array of string, user input command in array form
     */
    public static void tableScanEquality(String[] parts) {
        int v = Integer.parseInt(parts[7]);
        long startTime = System.currentTimeMillis();
        HashSet<Integer> filesAccessed = new HashSet<Integer>();

        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "F" + (i + 1) + ".txt";
        }

        // For each file, scan for record
        for (int file = 0; file < 99; file++) {
            try {
                // Get file path and contents
                String filePath = myPath + fileNames[file];
                Path path = Paths.get(filePath);
                byte[] fileData = Files.readAllBytes(path);
                filesAccessed.add(file);

                // Iterate through each record
                int startPoint = 0;
                int fileNum = file + 1;
                for (int record = 0; record < 100; record++) {
                    byte[] randomVByte = new byte[4];
                    System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                    String str = new String(randomVByte, StandardCharsets.UTF_8);
                    int randomV = Integer.parseInt(str); // Random V
                    if (randomV == v) {
                        String recordText = getData(fileNum, startPoint);
                        System.out.println(recordText);
                    }

                    startPoint += 40; // Move pointer to next record
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Used table scan for equality search");
        System.out.println("Time taken to answer the query: " + elapsedTime + " milliseconds");
        System.out.println("Number of data files (disk blocks) read: " + filesAccessed.size() + "\n");
    }

    /***
     * Conducts a full table scan to find records for a range of randomV's and prints output
     * @param parts, array of string, user input command in array form
     */
    public static void tableScanRange(String[] parts) {
        int v1 = Integer.parseInt(parts[7]);
        int v2 = Integer.parseInt(parts[11]);
        HashSet<Integer> filesAccessed = new HashSet<Integer>();

        long startTime = System.currentTimeMillis();

        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "F" + (i + 1) + ".txt";
        }

        // For each file, scan for record
        for (int file = 0; file < 99; file++) {
            try {
                // Get file path and contents
                String filePath = myPath + fileNames[file];
                Path path = Paths.get(filePath);
                byte[] fileData = Files.readAllBytes(path);
                filesAccessed.add(file);

                // Iterate through each record
                int startPoint = 0;
                int fileNum = file + 1;
                for (int record = 0; record < 100; record++) {
                    byte[] randomVByte = new byte[4];
                    System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                    String str = new String(randomVByte, StandardCharsets.UTF_8);
                    int randomV = Integer.parseInt(str); // Random V
                    if (randomV >  v1 && randomV < v2) {
                        String recordText = getData(fileNum, startPoint);
                        System.out.println(recordText);
                    }

                    startPoint += 40; // Move pointer to next record
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Used table scan for range search");
        System.out.println("Time taken to answer the query: " + elapsedTime + " milliseconds");
        System.out.println("Number of data files (disk blocks) read: " + filesAccessed.size() + "\n");
    }

    /***
     * Conducts full table scan to find all records that are NOT equal to given randomV and prints output
     * @param parts, array of string, user input command in array form
     */
    public static void inequalitySearch(String[] parts) {
        int v = Integer.parseInt(parts[7]);
        long startTime = System.currentTimeMillis();
        HashSet<Integer> filesAccessed = new HashSet<Integer>();

        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "F" + (i + 1) + ".txt";
        }

        // For each file, scan for record
        for (int file = 0; file < 99; file++) {
            try {
                // Get file path and contents
                String filePath = myPath + fileNames[file];
                Path path = Paths.get(filePath);
                byte[] fileData = Files.readAllBytes(path);
                filesAccessed.add(file);

                // Iterate through each record
                int startPoint = 0;
                int fileNum = file + 1;
                for (int record = 0; record < 100; record++) {
                    byte[] randomVByte = new byte[4];
                    System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                    String str = new String(randomVByte, StandardCharsets.UTF_8);
                    int randomV = Integer.parseInt(str); // Random V
                    if (randomV != v) {
                        String recordText = getData(fileNum, startPoint);
                        System.out.println(recordText);
                    }
                    startPoint += 40; // Move pointer to next record
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Used table scan for equality search");
        System.out.println("Time taken to answer the query: " + elapsedTime + " milliseconds");
        System.out.println("Number of data files (disk blocks) read: " + filesAccessed.size() + "\n");

    }


    /***
     * Takes user input and runs program
     * @param args, array of strings, specified command
     */
    public static void main(String[] args) {

        // ISU External Merge Sort
        if (args.length == 2) {
            if (args[0].equals("-mergesort")) {
                int numBuffers = Integer.parseInt(args[1]);
                ExternalMergeSort mergesort = new ExternalMergeSort(numBuffers);
                mergesort.prepPhase();
                mergesort.mergePhase1();
                System.exit(1);
            }
        }
        boolean exitRequested = false;
        boolean isIndexed = false;
        Index index = null;


        while (!exitRequested) {
            System.out.println("Program is ready and waiting for user command");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] parts = input.split(" ");

            // CREATE INDEX ON Project2Dataset (RandomV)
            if (input.equals("CREATE INDEX ON Project2Dataset (RandomV)")) {
                index = new Index();
                index.readData();
                System.out.println("The hash-based and array-based indexes are built successfully." + "\n");
                isIndexed = true;
            }
            if (isIndexed) {
                // SELECT * FROM Project2Dataset WHERE RandomV = v
                if (input.startsWith("SELECT * FROM Project2Dataset WHERE RandomV = ")) {
                    equalitySearchWithIndex(parts, index);
                }

                // SELECT * FROM Project2Dataset WHERE RandomV > v1 AND RandomV < v2
                if (input.startsWith("SELECT * FROM Project2Dataset WHERE RandomV >")) {
                  rangeSearchWithIndex(parts, index);
                }
            }
            else if (!isIndexed){
                // SELECT * FROM Project2Dataset WHERE RandomV = v (TABLE SCAN)
                if (input.startsWith("SELECT * FROM Project2Dataset WHERE RandomV = ")) {
                    tableScanEquality(parts);
                }
                // SELECT * FROM Project2Dataset WHERE RandomV > v1 AND RandomV < v2 (TABLE SCAN)
                if (input.startsWith("SELECT * FROM Project2Dataset WHERE RandomV >")) {
                    tableScanRange(parts);
                }
            }
            // SELECT * FROM Project2Dataset WHERE RandomV != v (TABLE SCAN)
            if (input.startsWith("SELECT * FROM Project2Dataset WHERE RandomV != ")) {
                inequalitySearch(parts);
            }

            if (input.equalsIgnoreCase("exit")) {
                exitRequested = true;
                scanner.close();
            }

        }

    }
}