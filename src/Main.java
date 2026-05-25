import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Multithreaded Sorting — Demo     ║");
        System.out.println("╚════════════════════════════════════╝");

        int size = promptSize();

        System.out.printf("%nGenerating %,d random integers...%n", size);
        int[] input = new int[size];
        Random rng  = new Random();
        for (int i = 0; i < size; i++) input[i] = rng.nextInt();

        // ── Small array: show before/after ────────────────────────────────
        if (size <= 20) {
            System.out.println("Input  : " + Arrays.toString(input));
        }

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.printf("Detected %d logical CPU cores.%n%n", cores);

        // ── Single-thread ─────────────────────────────────────────────────
        SingleThreadSorter single = new SingleThreadSorter();
        long t0 = System.nanoTime();
        int[] singleOut = single.sort(input);
        long singleMs   = (System.nanoTime() - t0);
        System.out.printf("Single-thread : %,.3f ms%n", singleMs / 1e6);

        // ── Multi-thread ──────────────────────────────────────────────────
        MultiThreadSorter multi = new MultiThreadSorter(cores);
        t0 = System.nanoTime();
        int[] multiOut  = multi.sort(input);
        long multiMs    = (System.nanoTime() - t0);
        System.out.printf("Multi-thread  : %,.3f ms  (%d threads)%n",
                multiMs / 1e6, cores);

        // ── Speedup ───────────────────────────────────────────────────────
        System.out.printf("%nSpeedup : %.2fx%n", (double) singleMs / multiMs);

        // ── Correctness ───────────────────────────────────────────────────
        int[] reference = input.clone();
        Arrays.sort(reference);
        boolean singleOk = Arrays.equals(reference, singleOut);
        boolean multiOk  = Arrays.equals(reference, multiOut);
        System.out.printf("Correctness   : single=%s  multi=%s%n",
                singleOk ? " PASS" : " FAIL",
                multiOk  ? " PASS" : " FAIL");

        // ── Small array: show result ──────────────────────────────────────
        if (size <= 20) {
            System.out.println("Output : " + Arrays.toString(multiOut));
        }

        System.out.println("\nRun Benchmark.java for full multi-size analysis.");
    }

    private static int promptSize() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter array size (default 1 000 000): ");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) return 1_000_000;
        try {
            int n = Integer.parseInt(line.replace(",", "").replace("_", ""));
            if (n < 1) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input — using default 1 000 000.");
            return 1_000_000;
        }
    }
}
