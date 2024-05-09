import java.nio.charset.StandardCharsets;

public class Frame {
    private byte[] content;
    private boolean dirty;
    private boolean pinned;
    private int blockId;


    /***
     * Constructor initializes frame as empty frame
     */
    public Frame() {
        this.content = new byte[4000];
        this.dirty = false;
        this.pinned = false;
        this.blockId = -1;
    }

    /***
     * Sets frame to initial conditions
     */
    public void initialize () {
        this.content = new byte[4000];
        this.dirty = false;
        this.pinned = false;
        this.blockId = -1;
    }

    /***
     * Gets block ID
     * @return Int, block ID
     */
    public int getBlockId() {
        return this.blockId;
    }

    /***
     * Checks if frame is dirty
     * @return boolean, whether or not frame is dirty
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /***
     * Checks if frame is pinned
     * @return boolean, whether or not is pinned
     */
    public boolean isPinned() {
        return this.pinned;
    }

    /***
     * Gets block content
     * @return byte[], content
     */
    public byte[] get() {
        return this.content;
    }

    /***
     * Sets content and block ID
     * @param newContent
     * @param blockId
     */
    public void set(byte[] newContent, int blockId) {
        this.blockId = blockId;
        this.content = newContent;
    }

    /***
     * Set pinned
     */
    public void setPin() {
        this.pinned = true;
    }

    /***
     * Set unpinned
     */
    public void unpin() {
        this.pinned = false;
    }

    /***
     * Returns a specific record given record number given its record ID
     * @param recordNum
     * @return byte[], record
     */
    public byte[] findRecord(int recordNum) {
        // Find record by finding the correct 40 bytes of the block
        int startPoint = ((recordNum - 1) * 40) - ((this.blockId - 1) * 4000);


        if (startPoint >= 0 && (startPoint + 40) <= this.content.length) {
            byte[] record = new byte[40];
            System.arraycopy(this.content, startPoint , record, 0, 40);
            return record;
        }
        return new byte[0];
    }

    /***
     * UpdateRecord sets the record data to new record data
     * Updates dirty bit flag to true
     * @param recordNum Int, record ID
     * @param newContent String, new content
     */
    public void updateRecord(int recordNum, String newContent) {
        byte[] newContnentBytes = newContent.getBytes();

        int startPoint = ((recordNum - 1) * 40) - ((this.blockId - 1) * 4000);
        if (startPoint >= 0 && (startPoint + 40) <= this.content.length) {
            System.arraycopy(newContnentBytes, 0 , this.content, startPoint, 40);
        }
        this.dirty = true;
    }

}