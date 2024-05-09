import java.util.ArrayDeque;
import java.util.Deque;

public class LRUPolicy {
    private Deque<Integer> queue; // Queue to maintain the order of frame usage

    /***
     * Constructor for initializing LRU policy
     */
    public LRUPolicy() {
        this.queue = new ArrayDeque<>();
    }

    /***
     * Whenever a frame is accessed, move it to the end of the queue
     * @param frameIndex Int, index in bufferpool
     */
    public void access(int frameIndex) {
        if (queue.contains(frameIndex)) {
            queue.remove(frameIndex); // Remove existing index from queue
        }
        queue.addFirst(frameIndex); // Add index to the front indicating recent usage
    }

    /***
     * Gets queue
     * @return Deque<Integer>, returns queue
     */
    public Deque<Integer> getQueue() {
        return this.queue;
    }

}
