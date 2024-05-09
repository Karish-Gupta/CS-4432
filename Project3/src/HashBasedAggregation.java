import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class HashBasedAggregation {
    Hashtable<String, Double> hashtable = new Hashtable<>(100);
    static final String DatasetA = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-A/"; // path for my PC
    static final String DatasetB = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project3\\src\\Project3Dataset-B/"; // path for my PC

    /***
     * Conducts hash-based aggregation
     * @param dataset, String takes in A or B
     * @param operation, String, takes in operation command
     * @throws IOException, throws exception if path does not work
     */
    public void handleHashBasedAggregation(String dataset, String operation) throws IOException {
        long currentTime = System.currentTimeMillis(); // Start time
        // Set path for dataset
        String selectedDataset = "";
        if (dataset.equals("A")) {
            selectedDataset = DatasetA;
        }
        else if (dataset.equals("B")) {
            selectedDataset = DatasetB;
        }
        int[] avgTotalTracker = new int[100];
        int[] avgCountTracker = new int[100];
        // Iterate through all files and populate hashmap
        String[] fileNames = new String[99];
        for (int i = 0; i < 99; i++) {
            fileNames[i] = dataset + (i + 1) + ".txt";
        }
        for (int i = 0; i < 99; i++) {
            String filePath = selectedDataset + fileNames[i];
            Path path = Paths.get(filePath);
            byte[] fileData = Files.readAllBytes(path);

            // Iterate through each record
            int startPoint = 0;
            for (int record = 0; record < 100; record++) {
                byte[] randomVByte = new byte[4];
                byte[] col2Byte = new byte[7];
                System.arraycopy(fileData, startPoint + 33, randomVByte, 0, 4); // copy randomV
                System.arraycopy(fileData, startPoint + 12, col2Byte, 0, 7); // copy col 2

                String col2 = new String(col2Byte, StandardCharsets.UTF_8); // record content
                int randomV = Integer.parseInt(new String(randomVByte, StandardCharsets.UTF_8)); // random V
                int avgTrackerIndex = Integer.parseInt(col2.substring(4, 7));

                // Check if already in hashtable
                if (hashtable.containsKey(col2)) {
                    double currVal = hashtable.get(col2);
                    if (operation.equals("SUM(RandomV)")) {
                        hashtable.put(col2, currVal + randomV);
                    }
                    else if(operation.equals("AVG(RandomV)")) {
                        double newAvg = ((double) avgTotalTracker[avgTrackerIndex - 1] + randomV) / ( (double) avgCountTracker[avgTrackerIndex -1] + 1);
                        hashtable.put(col2, newAvg);
                        avgCountTracker[avgTrackerIndex - 1] += 1; // update avg tracker vals
                        avgTotalTracker[avgTrackerIndex - 1] += randomV;
                    }
                } else {
                    // Add to table
                    if (operation.equals("SUM(RandomV)")) {
                        hashtable.put(col2, (double) randomV);
                    }
                    else if(operation.equals("AVG(RandomV)")) {
                        hashtable.put(col2, (double) randomV); //average of one value is just the value
                        avgCountTracker[avgTrackerIndex - 1] += 1; // update avg tracker vals
                        avgTotalTracker[avgTrackerIndex - 1] += randomV;
                    }
                }
                startPoint += 40;
            }
        }
        // Aggregate and print
        for (int i = 1; i <= hashtable.size(); i++) {
            if (i <= 9) {
                String key = "Name00" + i;
                double val = Math.round(hashtable.get(key)*100) / (double) 100;
                System.out.println("Group: Name00" + i + "  " + "Aggregation Value: " + val);
            }
            else if (i > 9 && i != 100) {
                String key = "Name0" + i;
                double val = Math.round(hashtable.get(key)*100) / (double) 100;
                System.out.println("Group: Name0" + i + "  " + "Aggregation Value: " + val);
            }
            else if (i == 100) {
                double val = Math.round(hashtable.get("Name100")*100) / (double) 100;
                System.out.println("Group: Name100" + "  " + "Aggregation Value: " + val);
            }
        }
        long endTime = System.currentTimeMillis(); // End time
        long totalTime = endTime - currentTime;
        System.out.println("Total Time: " + totalTime + " milliseconds");
    }

}
