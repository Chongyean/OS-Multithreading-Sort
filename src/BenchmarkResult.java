
public final class BenchmarkResult {

    /** Human-readable label for the implementation (e.g. "Single-Thread"). */
    private final String label;

    /** Number of integers sorted in this run. */
    private final int inputSize;

    /** Elapsed wall-clock time in nanoseconds (measured by System.nanoTime). */
    private final long elapsedNano;

    // -----------------------------------------------------------------------
    // Constructor & Validation
    // -----------------------------------------------------------------------

    public BenchmarkResult(String label, int inputSize, long elapsedNano) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be null or blank.");
        }
        if (inputSize <= 0) {
            throw new IllegalArgumentException("inputSize must be positive, got: " + inputSize);
        }
        if (elapsedNano < 0) {
            throw new IllegalArgumentException("elapsedNano must be non-negative, got: " + elapsedNano);
        }
        this.label       = label;
        this.inputSize   = inputSize;
        this.elapsedNano = elapsedNano;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /** return implementation label */
    public String getLabel()       { return label; }

    /** return number of elements sorted */
    public int    getInputSize()   { return inputSize; }

    /** return elapsed time in nanoseconds */
    public long   getElapsedNano() { return elapsedNano; }

    /** return elapsed time in milliseconds (double precision) */
    public double getElapsedMs()   { return elapsedNano / 1_000_000.0; }

    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("%-22s | size: %,10d | time: %,12.3f ms",
                label, inputSize, getElapsedMs());
    }
}
