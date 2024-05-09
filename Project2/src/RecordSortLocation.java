public class RecordSortLocation {
    private int randomV;
    private int fileNum;
    private int offset;

    /***
     * Constructor for record location tuple
     * @param randomV, int
     * @param fileNum, int
     * @param offset, int
     */
    public RecordSortLocation(int randomV, int fileNum, int offset) {
        this.randomV = randomV;
        this.fileNum = fileNum;
        this.offset = offset;
    }

    /***
     * Gets randomV
     * @return int, randomV
     */
    public int getRandomV() {
        return this.randomV;
    }


    public int getOffset() {
        return this.offset;
    }

    public int getFileNum() {
        return this.fileNum;
    }
}