# OS-Multithreading-Sort

A Java console project for learning operating system multithreading concepts through single-threaded and multi-threaded merge sort implementations.

A Java console project that compares a classic single-threaded merge sort with a multi-threaded merge sort. It generates random integer arrays, sorts them with both approaches, checks correctness against Java's built-in sort, and measures the performance difference.

## What this project does

- Generates random integer arrays from a user-provided size.
- Sorts the same data with a single-threaded merge sort.
- Sorts the same data with a multi-threaded merge sort that splits the work across available CPU cores.
- Verifies both results against `Arrays.sort(...)`.
- Prints timing information and speedup for a quick comparison.
- Runs a larger benchmark and exports results to `benchmark_results.csv`.

## Main features

- `Main.java`: interactive demo for one array size.
- `Benchmark.java`: runs repeated tests across multiple input sizes.
- `SingleThreadSorter.java`: reference merge sort implementation.
- `MultiThreadSorter.java`: parallel sorter that uses worker threads plus a merge phase.

## How it works

The multi-threaded version divides the input array into segments, sorts each segment in parallel, then merges the sorted segments into a final array. The single-threaded version uses the same merge sort logic on one thread, which makes the comparison fair and easy to verify.

## Requirements

- Java 8 or newer
- A terminal or IDE that can compile and run plain Java source files

## Run the demo

From the project root:

```bash
javac src/*.java
java -cp src Main
```

When prompted, enter an array size or press Enter to use the default size of 1,000,000.

## Run the benchmark

```bash
javac src/*.java
java -cp src Benchmark
```

This produces a timing table for several input sizes and writes the results to `benchmark_results.csv`.

## Output files

- `benchmark_results.csv`: benchmark results exported by `Benchmark.java`
- `out/`: compiled class output if you build from an IDE

## Notes

- The program never mutates the original input array; each sorter works on a copy.
- The benchmark uses a fixed random seed for reproducible results.
- Multi-threaded performance depends on your CPU core count and JVM/runtime environment.
