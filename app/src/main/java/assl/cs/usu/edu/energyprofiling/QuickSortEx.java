package assl.cs.usu.edu.energyprofiling;

public class QuickSortEx {
    private int input[];
    private int length;

    public void arrange() {
        if (input == null || input.length == 0) {
            return;
        }
        this.input = input;
        length = input.length;
        quickSort(0, length - 1);
    }
    private void quickSort(int lowerIndex, int higherIndex) {
        int i = lowerIndex;
        int j = higherIndex;
        int pivot = input[lowerIndex + (higherIndex - lowerIndex) / 2];
        while (i <= j) {
            while (input[i] < pivot) {
                i++;
            }
            while (input[j] > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }
    private void exchangeNumbers(int i, int j) {
        int temp = input[i];
        input[i] = input[j];
        input[j] = temp;
    }

    public QuickSortEx(int maxElemets) {

        int[] input = new int[maxElemets];
        for (int i = 0; i < input.length; i++)
            input[i] = (int) (Math.random() * 100);

    }
}
