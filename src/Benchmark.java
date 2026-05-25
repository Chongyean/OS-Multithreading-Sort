import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Benchmark {

    /** Number of timed trials per (size × implementation) combination. */
    private static final int TRIALS = 5;

    /** Number of un-timed JVM warm-up iterations. */
    private static final int WARMUP_RUNS = 3;

    /** Shared random number generator (seed fixed for reproducibility). */
    private static final Random RNG = new Random(42);

    /** Input sizes to benchmark (elements). */
    private static final int[] SIZES = {
        10_000,
        100_000,
        500_000,
        1_000_000,
        2_000_000,
        5_000_000,
        10_000_000
    };

    /** Number of logical CPUs — used to size the multi-thread sorter. */
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    // Entry Point
    public static void main(String[] args) {
        printHardwareInfo();
        warmUp();

        SingleThreadSorter single = new SingleThreadSorter();
        MultiThreadSorter  multi  = new MultiThreadSorter(CPU_CORES);

        List<BenchmarkResult> allResults = new ArrayList<>();

        printTableHeader();

        for (int size : SIZES) {
            BenchmarkResult singleResult = benchmark("Single-Thread", size,
                    arr -> single.sort(arr));

            BenchmarkResult multiResult  = benchmark("Multi-Thread (" + CPU_CORES + " cores)", size,
                    arr -> multi.sort(arr));

            allResults.add(singleResult);
            allResults.add(multiResult);

            printRow(singleResult, multiResult);
        }

        System.out.println("─".repeat(90));
        exportCsv(allResults);
    }

    private static BenchmarkResult benchmark(String label, int size, SorterFn sorter) {
        long[] times = new long[TRIALS];

        for (int t = 0; t < TRIALS; t++) {
            int[] input = randomArray(size);

            long start   = System.nanoTime();
            int[] output = sorter.sort(input);
            long elapsed = System.nanoTime() - start;

            verify(input, output, label, size);
            times[t] = elapsed;
        }

        Arrays.sort(times);                      // sort to find median
        long median = times[TRIALS / 2];
        return new BenchmarkResult(label, size, median);
    }

    private static void warmUp() {
        System.out.println("⏳  JVM warm-up (" + WARMUP_RUNS + " runs at size 50 000) ...");
        SingleThreadSorter s = new SingleThreadSorter();
        MultiThreadSorter  m = new MultiThreadSorter(CPU_CORES);
        for (int i = 0; i < WARMUP_RUNS; i++) {
            int[] arr = randomArray(50_000);
            s.sort(arr);
            m.sort(arr);
        }
        System.out.println(" Warm-up complete.\n");
    }

    private static void verify(int[] input, int[] output, String label, int size) {
        int[] expected = input.clone();
        Arrays.sort(expected);
        if (!Arrays.equals(expected, output)) {
            throw new AssertionError(
                    "Correctness check FAILED for " + label + " at size " + size);
        }
    }
    /** Prints hardware / runtime information. */
    private static void printHardwareInfo() {
        Runtime rt = Runtime.getRuntime();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          MULTITHREADED SORT — BENCHMARK REPORT           ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf ("║  JVM      : %-45s ║%n", System.getProperty("java.vm.name")
                + " " + System.getProperty("java.version"));
        System.out.printf ("║  OS       : %-45s ║%n", System.getProperty("os.name")
                + " " + System.getProperty("os.arch"));
        System.out.printf ("║  CPU cores: %-45s ║%n", CPU_CORES + " logical processors");
        System.out.printf ("║  Max RAM  : %-45s ║%n", (rt.maxMemory() / 1024 / 1024) + " MB");
        System.out.printf ("║  Trials   : %-45s ║%n", TRIALS + " (median reported)");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /** Prints formatted table header. */
    private static void printTableHeader() {
        System.out.println("─".repeat(90));
        System.out.printf("%-12s | %-22s | %-22s | %-10s%n",
                "Size", "Single-Thread (ms)", "Multi-Thread (ms)", "Speedup");
        System.out.println("─".repeat(90));
    }

    private static void printRow(BenchmarkResult s, BenchmarkResult m) {
        double speedup = s.getElapsedMs() / m.getElapsedMs();
        System.out.printf("%-12s | %-22.3f | %-22.3f | %.2fx%n",
                String.format("%,d", s.getInputSize()),
                s.getElapsedMs(),
                m.getElapsedMs(),
                speedup);
    }

    // CSV export
    private static void exportCsv(List<BenchmarkResult> results) {
        String filename = "benchmark_results.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("label,size,elapsed_ms");
            for (BenchmarkResult r : results) {
                pw.printf("%s,%d,%.3f%n", r.getLabel(), r.getInputSize(), r.getElapsedMs());
            }
            System.out.println("\n Results exported → " + filename);
        } catch (IOException e) {
            System.err.println("Warning: could not write CSV — " + e.getMessage());
        }
    }

    // Utility
    private static int[] randomArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = RNG.nextInt();
        return arr;
    }

    // Functional interface
    @FunctionalInterface
    private interface SorterFn {
        int[] sort(int[] input);
    }
}
