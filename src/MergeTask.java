import java.util.PriorityQueue;

public class MergeTask implements Runnable {

    /** Source array containing the independently sorted segments. */
    private final int[] source;

    /** Destination array where the merged output is written. */
    private final int[] result;

    private final int[][] segments;

    // -----------------------------------------------------------------------
    // Constructor & Validation
    // -----------------------------------------------------------------------

    public MergeTask(int[] source, int[] result, int[][] segments) {
        if (source == null || result == null || segments == null) {
            throw new IllegalArgumentException("Arguments must not be null.");
        }
        if (source.length != result.length) {
            throw new IllegalArgumentException(
                    "source.length (" + source.length
                    + ") != result.length (" + result.length + ").");
        }
        this.source   = source;
        this.result   = result;
        this.segments = segments;
    }

    // -----------------------------------------------------------------------
    // Runnable
    // -----------------------------------------------------------------------

    /** Performs the k-way merge. Invoked by the merging {link Thread}. */
    @Override
    public void run() {
        kWayMerge();
    }

    // -----------------------------------------------------------------------
    // Private merge logic
    // -----------------------------------------------------------------------

    private void kWayMerge() {
        // Min-heap ordered by element value (int[0])
        PriorityQueue<int[]> minHeap =
                new PriorityQueue<>(segments.length, (a, b) -> Integer.compare(a[0], b[0]));

        // Seed heap with the first element of each segment
        for (int seg = 0; seg < segments.length; seg++) {
            int segStart = segments[seg][0];
            int segEnd   = segments[seg][1];
            if (segStart < segEnd) {
                minHeap.offer(new int[]{ source[segStart], seg, segStart });
            }
        }

        int writeIdx = 0;
        while (!minHeap.isEmpty()) {
            int[] top = minHeap.poll();
            result[writeIdx++] = top[0];            // write minimum

            int seg      = top[1];                  // which segment it came from
            int nextIdx  = top[2] + 1;              // advance pointer in that segment
            int segEnd   = segments[seg][1];

            if (nextIdx < segEnd) {
                minHeap.offer(new int[]{ source[nextIdx], seg, nextIdx });
            }
        }
    }
}
