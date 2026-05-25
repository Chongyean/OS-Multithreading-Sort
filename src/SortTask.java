import java.util.Arrays;

public class SortTask implements Runnable {

    private final int[] array;

    private final int start;

    private final int end;

    public SortTask(int[] array, int start, int end) {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null.");
        }
        if (start < 0 || end > array.length || start > end) {
            throw new IllegalArgumentException(
                    "Invalid range [" + start + ", " + end
                    + ") for array of length " + array.length + ".");
        }
        this.array = array;
        this.start = start;
        this.end   = end;
    }

    @Override
    public void run() {
        if (end - start > 1) {
            mergeSort(start, end - 1);
        }
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2; // avoids integer overflow
            mergeSort(left,  mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        // Copy both halves to temporary buffers
        int[] leftBuf  = Arrays.copyOfRange(array, left,    mid + 1);
        int[] rightBuf = Arrays.copyOfRange(array, mid + 1, right + 1);

        int i = 0, j = 0, k = left;
        while (i < leftBuf.length && j < rightBuf.length) {
            // '<=' keeps the sort stable
            array[k++] = (leftBuf[i] <= rightBuf[j]) ? leftBuf[i++] : rightBuf[j++];
        }
        while (i < leftBuf.length)  array[k++] = leftBuf[i++];
        while (j < rightBuf.length) array[k++] = rightBuf[j++];
    }


    public int getStart() { return start; }

    public int getEnd()   { return end;   }
}

