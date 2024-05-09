import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

public class Index {
    static final String myPath = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project2\\src\\Project2Dataset\\"; // Project2Dataset path for my PC
    Hashtable<Integer, ArrayList<RecordLocation>> hashIndex = new Hashtable<>(); // Hash-Based Index Structure
    @SuppressWarnings("unchecked")
    ArrayList<RecordLocation>[] arrayIndex = new ArrayList[5001]; //  Array-based index structure increased by 1 for indexing

    /***
     * Index constructor initializes hash index and array index with values up to and including 5000
     */
    public Index() {
        for (int randomV = 0; randomV <= 5000; randomV++) {
            this.hashIndex.put(randomV, new ArrayList<>());
            this.arrayIndex[randomV] = new ArrayList<>();
        }
    }

    /***
     * Populates hash index and array index with all values from Project2Dataset
     */
    public void readData() {
        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
        fileNames[i] = "F" + (i + 1) + ".txt";
        }

        // For each file, for each record populate hashtable
        for (int file = 0; file < 99; file++) {
            try {
                // Get file path and contents
                String filePath = myPath + fileNames[file];
                Path path = Paths.get(filePath);
                byte[] fileData = Files.readAllBytes(path);

                // Iterate through each record
                int startPoint = 0;
                int fileNum = file + 1;
                for (int record = 0; record < 100; record++) {
                    byte[] randomVByte = new byte[4];
                    System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4);
                    String str = new String(randomVByte, StandardCharsets.UTF_8);
                    int randomV = Integer.parseInt(str); // Random V

                    RecordLocation recordLocation = new RecordLocation(fileNum, startPoint);

                    // Populate hash index
                    this.hashIndex.get(randomV).add(0, recordLocation);

                    // Populate array index
                    this.arrayIndex[randomV].add(0, recordLocation);

                    startPoint += 40; // Move pointer to next record
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}

