import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

public class HashBasedJoin {
    Hashtable<Integer, ArrayList<ArrayList<String>>> hashIndex = new Hashtable<>(50);
    static final String DatasetA = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-A/"; // path for my PC
    static final String DatasetB = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-B/"; // path for my PC

    // key = RandomV mod 50
    // value = array with array of duplicate records (records withs same randomV)

    /***
     * Constructor initializes array list with 50 buckets
     */
    public HashBasedJoin() {
        for (int i = 0; i < 50; i++) {
            ArrayList<ArrayList<String>> bucket = new ArrayList<>(50);
            this.hashIndex.put(i, bucket);
        }
    }

    /***
     * Populates hashtable with record all records from dataset A
     * @throws IOException, throws exception if path not found
     */
    private void populateHashIndex() throws IOException {
        // Iterate through all files and populate hashmap
        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "A" + (i + 1) + ".txt";
        }

        for (int i = 0; i < 99; i++) {
            String filePath = DatasetA + fileNames[i];
            Path path = Paths.get(filePath);
            byte[] fileData = Files.readAllBytes(path);

            // Iterate through each record
            int startPoint = 0;
            for (int record = 0; record < 100; record++) {
                byte[] randomVByte = new byte[4];
                byte[] recordByte = new byte[40];
                System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4); // copy randomV
                System.arraycopy(fileData, startPoint, recordByte, 0, 40); // copy record content

                String recordContent = new String(recordByte, StandardCharsets.UTF_8); // record content
                int randomV = Integer.parseInt(new String(randomVByte, StandardCharsets.UTF_8)); // random V

                int key = randomV % 50; // bucket
                boolean alreadyAdded = false;
                for (int randomVList = 0; randomVList < this.hashIndex.get(key).size(); randomVList++) {
                    String hashRecord = this.hashIndex.get(key).get(randomVList).get(0);
                    // If randomV list already in hash, add record
                    if (randomV == getRandomV(hashRecord)) {
                        this.hashIndex.get(key).get(randomVList).add(recordContent);
                        alreadyAdded = true;
                    }
                }
                // If randomV not in hash,create new array for it and add it to the bucket
                if (!alreadyAdded) {
                    ArrayList<String> recordList = new ArrayList<>();
                    recordList.add(recordContent);
                    this.hashIndex.get(key).add(recordList);
                }
                startPoint += 40;
            }
        }
    }

    /***
     * Compares dataset A and dataset B records and prints out the join column values
     * @throws IOException, throws exception if path not found
     */
    public void handleHashBaseJoin() throws IOException {
        this.populateHashIndex();
        long currentTime = System.currentTimeMillis(); // Start time
        int total = 0;

        // Iterate through all files and populate hashmap
        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = "B" + (i + 1) + ".txt";
        }
        System.out.println("ColA1         " + "ColA2    " + "ColB1        " + "ColB2");
        for (int i = 0; i < 99; i++) {
            String filePath = DatasetB + fileNames[i];
            Path path = Paths.get(filePath);
            byte[] fileData = Files.readAllBytes(path);

            // Iterate through each record
            int startPoint = 0;
            for (int record = 0; record < 100; record++) {
                byte[] randomVByte = new byte[4];
                byte[] recordByte = new byte[40];
                System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4); // copy randomV
                System.arraycopy(fileData, startPoint, recordByte, 0, 40); // copy record content

                String recordContent = new String(recordByte, StandardCharsets.UTF_8); // record content
                int randomV = Integer.parseInt(new String(randomVByte, StandardCharsets.UTF_8)); // random V
                int key = randomV % 50; // bucket

                for (int randomVList = 0; randomVList < this.hashIndex.get(key).size(); randomVList++) { // Iterate through each array list of RandomV

                    for (int randomVVal = 0; randomVVal < hashIndex.get(key).get(randomVList).size(); randomVVal++) { // Iterate through each RandomV record in each array list
                        String hashRecord = this.hashIndex.get(key).get(randomVList).get(randomVVal);
                        // If randomV list already in hash, add record
                        if (randomV == getRandomV(hashRecord)) {
                            // dataset A
                            String colA1 = hashRecord.substring(0, 10);
                            String colA2 = hashRecord.substring(12, 19);
                            // dataset B
                            String colB1 = recordContent.substring(0, 10);
                            String colB2 = recordContent.substring(12, 19);
                            System.out.println(colA1 + "    " + colA2 + "   " + colB1 + "   " + colB2);
                            total++;
                        }
                    }

                }
                startPoint += 40;
            }
        }
        long endTime = System.currentTimeMillis(); // End time
        long totalTime = endTime - currentTime;
        System.out.println("Total Time: " + totalTime + " milliseconds");
        System.out.println("Total Qualifying records: " + total);
    }

    /***
     * Takes in record string and parses substring for randomV value
     * @param record, string full record
     * @return int, randomV
     */
    public int getRandomV(String record) {
        return Integer.parseInt(record.substring(33, 37));
    }
}
