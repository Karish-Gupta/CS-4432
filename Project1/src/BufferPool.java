import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BufferPool {
    Frame[] buffers;
    int lastEvictedIndex; // Pointer to keep track of the last evicted frame for circular algorithm

    LRUPolicy LRU; // LRU class

    boolean LRUpolicy; // If LRU algorithm is on

    /***
     * Constructor initializes bufferpool with number of frames, algorithm, and initializes all frames
     * @param numFrames the number of frames to initialize in bufferpool
     */
    public BufferPool (int numFrames) {
        this.buffers = new Frame[numFrames];
        this.LRUpolicy = false;
        this.LRU = new LRUPolicy();
        for (int i = 0; i < numFrames; i++) {
            this.buffers[i] = new Frame();
        }
    }

    /***
     * Searches if a certain block is in bufferpool
     * @param Id block ID
     * @return int, the position in bufferpool array or -1 if not in bufferpool
     */
    public int search(int Id) {
        for (int i = 0; i < this.buffers.length; i++) {
            byte[] curr = this.buffers[i].get();

            String fileString = "F" + String.format("%02d", Id); // Appending 'F' and formatting integer with leading zeros

            String recordString = new String(curr, StandardCharsets.UTF_8);
            String[] recordBlocks = recordString.split("\\.");

            // Iterate through record array
            for (String record : recordBlocks) {
                if (record.contains(fileString)) {
                    return i;
                }
            }
        }
        // Return -1 if not available
        return -1;
    }

    /***
     * Gets content in block if it is in the bufferpool.
     * @param blockId
     * @return String, content in block
     */
    public String searchContent(int blockId) {
        int exists = this.search(blockId);

        if (exists == -1) {
            return ""; // Empty array
        }

        byte[] myBytes = this.buffers[blockId].get();
        String content = new String(myBytes, StandardCharsets.UTF_8); // for UTF-8 encoding
        System.out.println(content);
        return content;
    }

    /***
     * Sets the LRU policy as true and will change the algorithm
     * @param setLRU, whether or not LRU is selected
     */
    public void LRUPolicy(boolean setLRU) {
        this.LRUpolicy = true;
    }

    /*handleGet - Reads in block into buffer if block is not in buffer already
    * Calls findRecord to find a specific record*/

    /***
     * Checks if block that the record is in, is in the buffer already
     * If it is not in the buffer it will try to make space for it by evicting a block from a frame
     * Then will try place block in frame and retrieve record
     * @param recordId, ID of record for retrieval
     */
    public void handleGet(int recordId) {

        int blockId = (recordId - 1) / 100 + 1;

        // Check if block is already in buffer
        for(int i = 0; i < this.buffers.length; i++) {
            if(this.buffers[i].getBlockId() == blockId) {
                byte[] recordData = this.buffers[i].findRecord(recordId);
                String recordString = new String(recordData);
                this.LRU.access(i);
                int actualFrameNum = i + 1;
                System.out.println(recordString);
                System.out.println("File " + blockId + " is already in memory");
                System.out.println("Located in Frame " + actualFrameNum + "\n");
                return;
            }
        }

        // Check if buffer is full
        int frame = this.findEmptyFrame();
        // Ensure that buffer is not full and if full, evict content from a frame
        if (frame == -1) {
            this.evict();
            // If frame is -1 again, then all frames are pinned
            frame = this.findEmptyFrame();
            if (frame == -1) {
                System.out.println("The corresponding block #" + blockId + " cannot be accessed from disk because the memory buffers are full" + "\n");
                return;
            }
        }

        // If not try to set data in buffer
        try {
            // Setting data in buffer
            Path path = findFilePath(blockId);
            byte[] data = Files.readAllBytes(path);
            this.buffers[frame].set(data, blockId);

            // Printing data
            byte[] recordData = this.buffers[frame].findRecord(recordId);
            String recordString = new String(recordData);
            this.LRU.access(frame);

            int actualFrameNum = frame + 1;
            System.out.println(recordString);
            System.out.println("Brought file " + blockId + " from disk");
            System.out.println("Placed in Frame " + actualFrameNum + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Checks if block that the record is in, is in the buffer already
     * If it is not in the buffer it will try to make space for it by evicting a block from a frame
     * Then will try place block in frame and update the content in the block for the record
     * @param recordId, record ID of record to change
     * @param newContent, new content to change to
     */
    public void handleSet(int recordId, String newContent) {
        int blockId = (recordId - 1) / 100 + 1;

        // Check if block is already in buffer
        for(int i = 0; i < this.buffers.length; i++) {
            if(this.buffers[i].getBlockId() == blockId) {
                this.buffers[i].updateRecord(recordId, newContent);
                int actualFrameNum = i + 1;
                this.LRU.access(i);
                System.out.println("Write was successful");
                System.out.println("File " + blockId + " already in memory");
                System.out.println("Located in Frame " + actualFrameNum + "\n");
                return;
            }
        }

        // Check if buffer is full
        int frame = this.findEmptyFrame();
        // Ensure that buffer is not full and if full, evict content from a frame
        if (frame == -1) {
            this.evict();
            // If frame is -1 again, then all frames are pinned
            frame = this.findEmptyFrame();
            if (frame == -1) {
                System.out.println("The corresponding block #" + blockId + " cannot be accessed from disk because the memory buffers are full" + "\n");
                System.out.println("Write was unsuccessful");
                return;
            }
        }

        // If not full try to set data in buffer
        try {
            // Setting data in buffer
            Path path = findFilePath(blockId);
            byte[] data = Files.readAllBytes(path);
            this.buffers[frame].set(data, blockId);

            // Printing data
            this.buffers[frame].updateRecord(recordId, newContent);
            int actualFrameNum = frame + 1;
            this.LRU.access(frame);
            System.out.println("Write was successful");
            System.out.println("Brought file " + blockId + " from disk");
            System.out.println("Placed in Frame " + actualFrameNum + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Checks if block that the record is in, is in the buffer already
     * If it is not in the buffer it will try to make space for it by evicting a block from a frame
     * Then will try place block in frame and set pinned to true
     * @param blockId, block ID of block to pin
     */
    public void handlePinBid(int blockId) {

        // Check if block is already in buffer
        for(int i = 0; i < this.buffers.length; i++) {
            if(this.buffers[i].getBlockId() == blockId) {
                int actualFrameNum = i + 1;
                System.out.println("File " + blockId + " pinned in frame " + actualFrameNum);
                if (this.buffers[i].isPinned()) {
                    System.out.println("Already pinned" + "\n");
                }else {
                    this.buffers[i].setPin();
                    System.out.println("Frame " + blockId + " was not already pinned" + "\n");
                }
                return;
            }
        }

        // Check if buffer is full
        int frame = this.findEmptyFrame();
        // Ensure that buffer is not full and if full, evict content from a frame
        if (frame == -1) {
            this.evict();
            // If frame is -1 again, then all frames are pinned
            frame = this.findEmptyFrame();
            if (frame == -1) {
                System.out.println("The corresponding block BID " + blockId + " cannot be pinned because the memory buffers are full" + "\n");
                return;
            }
        }

        // If not in buffer try to set data in buffer
        try {
            // Setting data in buffer
            Path path = findFilePath(blockId);
            byte[] data = Files.readAllBytes(path);
            this.buffers[frame].set(data, blockId);

            // Printing data
            int actualFrameNum = frame + 1;
            System.out.println("File " + blockId + " pinned in frame " + actualFrameNum);
            if (this.buffers[frame].isPinned()) {
                System.out.println("Already pinned" + "\n");
            }else {
                this.buffers[frame].setPin();
                System.out.println("Frame " + blockId + " was not already pinned" + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Checks if block is already in buffer,
     * If in buffer, set pinned to false
     * @param blockId, block ID of block to unpin
     */
    public void handleUnpinBid(int blockId) {
        // Check if block is already in buffer
        for(int i = 0; i < this.buffers.length; i++) {
            if(this.buffers[i].getBlockId() == blockId) {
                int actualFrameNum = i + 1;
                if (this.buffers[i].isPinned()) {
                    this.buffers[i].unpin();
                    System.out.println("File " + blockId + " in frame " + actualFrameNum + " is unpinned");
                    System.out.println("Frame " + actualFrameNum + " was not already unpinned" + "\n");
                }else {
                    this.buffers[i].unpin();
                    System.out.println("File " + blockId + " in frame " + actualFrameNum + " is unpinned");
                    System.out.println("Frame " + actualFrameNum + " was already unpinned" + "\n");
                }
                return;
            }
        }
        // If not in buffer
        System.out.println("The corresponding block " + blockId + " cannot be unpinned because it is not in memory" + "\n");
    }

    /***
     * Finds an empty frame in bufferpool
     * @return slot number of empty frame in bufferpool
     */
    public int findEmptyFrame() {
        for (int i = 0; i < this.buffers.length; i++) {
            if (this.buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1;
    }

    /***
     * Writes updated block to memory
     * @param changedBufferContent, updated content
     * @param blockId, block ID of block to write to
     */
    public void write(byte[] changedBufferContent, int blockId) {
        Path blockIdPath = findFilePath(blockId); // Find file path given file number
        try {
            Files.write(blockIdPath, changedBufferContent);
        } catch (IOException e) {
            System.err.println("Error replacing file content");
        }
    }

    /***
     * Selects eviction policy between base circular eviction and LRU
     */
    public void evict() {
       if (this.LRUpolicy) {
           this.LRUEvict();
       }else {
           this.circularEvict();
       }
    }

    /***
     * Removes block from frame if not enough open frames
     * Moves in circular fashion, starting with frame 1, then moving pointer to frame 2 and so on
     */
    public void circularEvict() {
        if (this.findEmptyFrame() == -1) {
            for(int i = lastEvictedIndex; i < this.buffers.length; i++) {
                int blockId = this.buffers[i].getBlockId();
                int actualFrameNum = i + 1;
                if (!this.buffers[i].isPinned()) {
                    if(this.buffers[i].isDirty()) {
                        this.write(this.buffers[i].get(), blockId); // Write to disk
                        this.buffers[i].initialize(); // Reinitialize as empty
                        lastEvictedIndex = i + 1; // Update the last evicted index
                        System.out.println("Evicted file " + blockId + " from Frame " + actualFrameNum);
                        break;
                    } else {
                        this.buffers[i].initialize(); // Reinitialize as empty
                        System.out.println("Evicted file " + blockId + " from Frame " + actualFrameNum);
                        lastEvictedIndex = i + 1; // Update the last evicted index
                        break;
                    }
                }
            }
            // If we've reached the end of buffers, start from the beginning next time
            if (lastEvictedIndex >= this.buffers.length) {
                lastEvictedIndex = 0;
            }
        }
    }


    /***
     * Implements Least Recently Used policy using a queue
     * Evicts block from the least recently used frame
     */
    public void LRUEvict() {
        if (this.findEmptyFrame() == -1) {
            while (!this.LRU.getQueue().isEmpty()) {
                int evictIndex = this.LRU.getQueue().removeLast(); // Get the least recently used frame
                if (!this.buffers[evictIndex].isPinned()) {
                    int actualFrameNum = evictIndex + 1;
                    int blockId = this.buffers[evictIndex].getBlockId();
                    if(this.buffers[evictIndex].isDirty()) {
                        this.write(this.buffers[evictIndex].get(), blockId); // Write to disk
                        this.buffers[evictIndex].initialize(); // Reinitialize as empty
                        lastEvictedIndex = evictIndex + 1; // Update the last evicted index
                        System.out.println("Evicted file " + blockId + " from Frame " + actualFrameNum);
                        break;
                    } else {
                        this.buffers[evictIndex].initialize(); // Reinitialize as empty
                        lastEvictedIndex = evictIndex + 1; // Update the last evicted index
                        System.out.println("Evicted file " + blockId + " from Frame " + actualFrameNum);
                        break;
                    }
                }
            }
        }
    }

    /***
     * Finds file path of data given block ID
     * @param blockId, block ID
     * @return Path object for block (file)
     */
    public Path findFilePath(int blockId) {
        String basePath = "C:\\Users\\karis\\WorcesterPolytechnicInstitute\\CS_4432\\Project1\\src\\data\\";
        String[] fileNames = {"F1.txt", "F2.txt", "F3.txt", "F4.txt", "F5.txt", "F6.txt", "F7.txt"};
        String filePath = basePath + fileNames[blockId - 1];
        return Paths.get(filePath);
        }



}
