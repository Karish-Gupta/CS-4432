import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NestedLoopJoin {
    String[] records = new String[100]; // array for 100 records
    static final String DatasetA = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-A/"; // path for my PC
    static final String DatasetB = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-B/"; // path for my PC

    /***
     * Conducts nested loop join
     * @throws IOException, throws exception if path not found
     */
    public void handleNestedLoopJoin() throws IOException {
        long currentTime = System.currentTimeMillis(); // Start time
        int totalCount = 0; // Count of the records matching the join condition
        // Iterate through all files
        String[] fileNamesA = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNamesA[i] = "A" + (i + 1) + ".txt";
        }

        for (int fileA = 0; fileA < 99; fileA++) {
            String filePathA = DatasetA + fileNamesA[fileA];
            Path pathA = Paths.get(filePathA);
            byte[] fileDataA = Files.readAllBytes(pathA);

            // Iterate through each record
            int startPointA = 0;
            for (int recordA = 0; recordA < 100; recordA++) {
                byte[] randomVByte = new byte[4];
                byte[] recordByte = new byte[40];
                System.arraycopy(fileDataA, startPointA + 33, randomVByte, 0, 4); // copy randomV
                System.arraycopy(fileDataA, startPointA, recordByte, 0, 40); // copy record content

                String recordContent = new String(recordByte, StandardCharsets.UTF_8); // record content
                this.records[recordA] = recordContent;
                startPointA += 40;
            }
            // check each record in database B against records in memory
            // Iterate through all files
            String[] fileNamesB = new String[99];
            for (int i = 0; i < 99; i++) {
                fileNamesB[i] = "B" + (i + 1) + ".txt";
            }

            for (int fileB = 0; fileB < 99; fileB++) {
                String filePathB = DatasetB + fileNamesB[fileB];
                Path pathB = Paths.get(filePathB);
                byte[] fileDataB = Files.readAllBytes(pathB);

                // Iterate through each record
                int startPoint = 0;
                for (int recordB = 0; recordB < 100; recordB++) {
                    byte[] randomVByte = new byte[4];
                    System.arraycopy(fileDataB, startPoint + 33, randomVByte, 0, 4); // copy randomV
                    int currRecordB = Integer.parseInt(new String(randomVByte, StandardCharsets.UTF_8));

                    // Compare with each recordA in memory
                    for (int recordA = 0; recordA < records.length; recordA++) {
                        if (getRandomV(records[recordA]) > currRecordB) {
                            totalCount++;
                        }
                    }
                    startPoint += 40;
                }
            }
        }
        long endTime = System.currentTimeMillis(); // End time
        long totalTime = endTime - currentTime;
        System.out.println("Total Time: " + totalTime + " milliseconds");
        System.out.println("Count of records matching join condition: " + totalCount);
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
