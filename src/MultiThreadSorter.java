public class MultiThreadSorter {

    /** Number of parallel sorting threads. */
    private final int threadCount;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    public MultiThreadSorter(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException(
                    "threadCount must be at least 1, received: " + threadCount);
        }
        this.threadCount = threadCount;
    }

    public MultiThreadSorter() {
        this(Runtime.getRuntime().availableProcessors());
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public int[] sort(int[] input) {
        if (input == null) throw new IllegalArgumentException("Input array must not be null.");

        int n = input.length;
        if (n <= 1) return input.clone();

        // ── Global data arrays (shared across threads) ──
        int[] workArray   = input.clone();  // sorting threads write here
        int[] resultArray = new int[n];      // merge thread writes here

        // Clamp thread count: no point spawning more threads than elements
        int actualThreads = Math.min(threadCount, n);

        Thread[]  threads  = new Thread[actualThreads];
        int[][]   segments = new int[actualThreads][2]; // [start, end) per thread

        // ── Partition ────────────────────────────────────────────────────────
        int base  = n / actualThreads;
        int extra = n % actualThreads;   // first 'extra' segments get +1 element
        int start = 0;

        for (int i = 0; i < actualThreads; i++) {
            int size = base + (i < extra ? 1 : 0);
            int end  = start + size;

            segments[i][0] = start;
            segments[i][1] = end;

            threads[i] = new Thread(
                    new SortTask(workArray, start, end),
                    "SortThread-" + i          // meaningful thread name
            );
            start = end;
        }

        // ── Launch sorting threads ────────────────────────────────────────────
        for (Thread t : threads) t.start();

        // ── Barrier: wait for ALL sort threads to finish ──────────────────────
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for sort thread: " + t.getName(), e);
            }
        }

        // ── Merge phase (single dedicated merge thread) ───────────────────────
        Thread mergeThread = new Thread(
                new MergeTask(workArray, resultArray, segments),
                "MergeThread"
        );
        mergeThread.start();
        try {
            mergeThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for MergeThread.", e);
        }

        return resultArray;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /** return the configured number of sorting threads */
    public int getThreadCount() { return threadCount; }
}
