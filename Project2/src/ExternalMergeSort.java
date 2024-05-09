import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ExternalMergeSort {
    static final String myPath = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project2\\src\\Project2Dataset\\"; // Project2Dataset path for my PC
    static final String mergeSortFilesPath = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project2\\src\\MergeSortFiles\\"; // MergeSort file for my pc path for my PC
    int numBuffers;
    ArrayList<ArrayList<RecordSortLocation>> buffers;

    /***
     * Constructor for External Merge Sort, initializes buffers
     * @param numBuffers
     */
    public ExternalMergeSort(int numBuffers) {
        this.numBuffers = numBuffers;
        this.buffers = new ArrayList<>(numBuffers);
        for (int i = 0; i < numBuffers; i++) {
            this.buffers.add(i, new ArrayList<>(100 * numBuffers)); // Initialize buffers
        }
    }

    /***
     * Runs the first phase of external merge sort
     */
    public void prepPhase() {
        int numPhaseOneRuns = (int) Math.ceil(99.0 / this.numBuffers);

        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "F" + (i + 1) + ".txt";
        }

        int currFile = 0;
        int currBuffer = 0;
        for (int run = 0; run < numPhaseOneRuns; run++) {
            int capacity = 0;

            // populate buffers from current file not accessed
            for (int file = currFile; file < Math.min(currFile + numBuffers, 99); file++) {
                try {
                    // Get file path and contents
                    String filePath = myPath + fileNames[file];
                    Path path = Paths.get(filePath);
                    byte[] fileData = Files.readAllBytes(path);

                    // Iterate through each record
                    int startPoint = 0;
                    for (int recordNum = 1; recordNum <= 100; recordNum++) {
                        byte[] randomVByte = new byte[4];
                        System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                        String str = new String(randomVByte, StandardCharsets.UTF_8);
                        int randomV = Integer.parseInt(str); // Random V

                        this.buffers.get(currBuffer).add(new RecordSortLocation(randomV, file + 1, startPoint)); // Add record info to buffer
                        capacity++;

                        startPoint += 40; // Move pointer to next record
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            quickSort(buffers.get(currBuffer), 0, capacity - 1);

            String phaseOneText = "";
            for (int record = 0; record < capacity; record++) {
                RecordSortLocation bufferRecord = buffers.get(currBuffer).get(record);
                int bufferRecordFileNum = bufferRecord.getFileNum();
                int bufferRecordOffset = bufferRecord.getOffset();
                phaseOneText += getData(bufferRecordFileNum, bufferRecordOffset);
            }

            try {
                // Specify the file path
                String filePath = "C:/Users/karis/WorcesterPolytechnicInstitute/CS_4432/Project2/src/MergeSortFiles/" + "PhaseOne" + run + ".txt";

                // Create a FileWriter object with the file path
                FileWriter writer = new FileWriter(filePath);

                // Write the string to the file
                writer.write(phaseOneText);

                // Close the writer
                writer.close();

                System.out.println("String has been written to the file successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }

            // Increment currFile and check if currBuffer needs to be reset
            if(currBuffer + 1 == numBuffers) {
                this.clearBuffers();
            }
            currFile += numBuffers;
            currBuffer = (currBuffer + 1) % numBuffers;
        }
    }


    /***
     * Merge Phase, produce one complete merged file
     */
    public void mergePhase1() {
        int phaseOneFiles = (int) Math.ceil(99.0 / this.numBuffers);
        this.clearBuffers();

        String[] fileNames = new String[phaseOneFiles];
        for (int i = 0; i < phaseOneFiles; i++) {
            fileNames[i] = "PhaseOne" + (i) + ".txt";
        }

        int currBuffer = 0;
        int capacity = 0;

            for (int file = 0; file < (phaseOneFiles); file++) {
                try {
                    // Get file path and contents
                    String filePath = mergeSortFilesPath + fileNames[file];
                    Path path = Paths.get(filePath);
                    byte[] fileData = Files.readAllBytes(path);

                    // Iterate through each record
                    int startPoint = 0;
                    int totalRecords = 100 * numBuffers;
                    if (file == phaseOneFiles - 1) {
                        totalRecords = numRecordsInLastPhaseOneFile(99);
                    }
                    for (int recordNum = 1; recordNum <= totalRecords; recordNum++) {
                        byte[] randomVByte = new byte[4];
                        System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                        String str = new String(randomVByte, StandardCharsets.UTF_8);
                        int randomV = Integer.parseInt(str); // Random V

                        this.buffers.get(currBuffer).add(new RecordSortLocation(randomV, file + 1, startPoint)); // Add record info to buffer
                        capacity++;
                        startPoint += 40; // Move pointer to next record
                    }
                    currBuffer++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (currBuffer == phaseOneFiles) {

                buffers.set(currBuffer, mergeSort(buffers)); // set next buffer in buffers as the fully merged file

                String phaseOneText = "";
                for (int record = 0; record < capacity; record++) {
                    RecordSortLocation bufferRecord = buffers.get(currBuffer).get(record);
                    int bufferRecordFileNum = bufferRecord.getFileNum();
                    int bufferRecordOffset = bufferRecord.getOffset();
                    phaseOneText += getDataPhaseOne(bufferRecordFileNum, bufferRecordOffset);
                }

                try {
                    // Specify the file path
                    String filePath = "C:/Users/karis/WorcesterPolytechnicInstitute/CS_4432/Project2/src/MergeSortFiles/" + "Merge" + ".txt";

                    // Create a FileWriter object with the file path
                    FileWriter writer = new FileWriter(filePath);

                    // Write the string to the file
                    writer.write(phaseOneText);

                    // Close the writer
                    writer.close();

                    System.out.println("Final merged file complete");
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }

                // Increment currFile and check if currBuffer needs to be reset
                if(currBuffer + 1 == numBuffers) {
                    this.clearBuffers();
                }
            }

        }

    /***
     * Calculates the number of recors in last phase one file
     * @param N
     * @return int, number of files
     */
    public int numRecordsInLastPhaseOneFile(int N) {
        int lowerBound = (int) Math.floor((double) N / numBuffers);
        int numLastFiles = N - lowerBound * numBuffers;

        if (numLastFiles == 0) {
            return 100 * numBuffers;
        }else {
            return 100 * numLastFiles;
        }
    }

    /***
     * Clears buffers
     */
    public void clearBuffers() {
        this.buffers = new ArrayList<>(numBuffers);
        for (int i = 0; i < numBuffers; i++) {
            this.buffers.add(i, new ArrayList<>(100 * numBuffers)); // empty buffers
        }
    }

    /***
     * Quicksort algorithm
     * @param array, array of RecordSortLocations
     * @param low, low index
     * @param high, high index
     */
    public static void quickSort(ArrayList<RecordSortLocation> array, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(array, low, high);
            quickSort(array, low, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, high);
        }
    }

    /***
     * Helper function for quicksort
     * @param array, array of RecordSortLocations
     * @param low, low index
     * @param high, high index
     * @return
     */
    public static int partition(ArrayList<RecordSortLocation> array, int low, int high) {
        int pivot = array.get(high).getRandomV();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array.get(j).getRandomV() < pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    /***
     * Helper function for quicksort
     * @param array, array of RecordSortLocations
     * @param i, randomV
     * @param j, randomV
     */
    public static void swap(ArrayList<RecordSortLocation> array, int i, int j) {
        RecordSortLocation temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
    }

    /***
     * Merge sort algorithm
     * @param lists, Takes buffers array
     * @return ArrayList, a buffer
     */
    public static ArrayList<RecordSortLocation> mergeSort(ArrayList<ArrayList<RecordSortLocation>> lists) {
        ArrayList<RecordSortLocation> result = new ArrayList<>();
        for (ArrayList<RecordSortLocation> list : lists) {
            result.addAll(list);
        }
        return mergeSortHelper(result);
    }

    /***
     * Helper for merge sort algorithm
     * @param arr, buffer
     * @return buffer
     */
    private static ArrayList<RecordSortLocation> mergeSortHelper(ArrayList<RecordSortLocation> arr) {
        if (arr.size() <= 1) {
            return arr;
        }

        int middle = arr.size() / 2;
        ArrayList<RecordSortLocation> left = new ArrayList<>(arr.subList(0, middle));
        ArrayList<RecordSortLocation> right = new ArrayList<>(arr.subList(middle, arr.size()));

        left = mergeSortHelper(left);
        right = mergeSortHelper(right);

        return merge(left, right);
    }

    /***
     * Merge helper method
     * @param left, buffer
     * @param right, buffer
     * @return ArrayList, a buffer
     */
    private static ArrayList<RecordSortLocation> merge(ArrayList<RecordSortLocation> left, ArrayList<RecordSortLocation> right) {
        ArrayList<RecordSortLocation> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (left.get(leftIndex).getRandomV() < right.get(rightIndex).getRandomV()) {
                result.add(left.get(leftIndex));
                leftIndex++;
            } else {
                result.add(right.get(rightIndex));
                rightIndex++;
            }
        }

        while (leftIndex < left.size()) {
            result.add(left.get(leftIndex));
            leftIndex++;
        }

        while (rightIndex < right.size()) {
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }

    /***
     * Retrieves records as strings from Phase One files
     * @param fileNum, int file number
     * @param offset, int record offset
     * @return record string
     */
    public static String getDataPhaseOne(int fileNum, int offset) {
        String filePath = mergeSortFilesPath + "PhaseOne" + (fileNum - 1) + ".txt";
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

}


