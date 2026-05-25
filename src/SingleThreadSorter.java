import java.util.Arrays;

public class SingleThreadSorter {

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public SingleThreadSorter() { /* default */ }

    public int[] sort(int[] input) {
        if (input == null) throw new IllegalArgumentException("Input array must not be null.");
        int[] arr = input.clone();          // defensive copy — never mutate caller's data
        mergeSort(arr, 0, arr.length - 1);
        return arr;
    }

    // -----------------------------------------------------------------------
    // Private sorting logic (identical algorithm used inside SortTask)
    // -----------------------------------------------------------------------

    private void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left,  mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int[] leftBuf  = Arrays.copyOfRange(arr, left,    mid + 1);
        int[] rightBuf = Arrays.copyOfRange(arr, mid + 1, right + 1);
        int i = 0, j = 0, k = left;
        while (i < leftBuf.length && j < rightBuf.length) {
            arr[k++] = (leftBuf[i] <= rightBuf[j]) ? leftBuf[i++] : rightBuf[j++];
        }
        while (i < leftBuf.length)  arr[k++] = leftBuf[i++];
        while (j < rightBuf.length) arr[k++] = rightBuf[j++];
    }
}
