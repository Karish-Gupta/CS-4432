import sun.security.ssl.Record;

public class RecordLocation {
    private int fileNumber;
    private int offset;

    /***
     * Constructor for Record Location object
     * @param fileNumber
     * @param offset
     */
    public RecordLocation(int fileNumber, int offset) {
        this.fileNumber = fileNumber;
        this.offset = offset;
    }

    /***
     * Gets file number of record
     * @return int, file number
     */
    public int getFileNumber() {
        return this.fileNumber;
    }

    /***
     * Gets record offset
     * @return int, record offset
     */
    public int getOffset() {
        return this.offset;
    }
}
