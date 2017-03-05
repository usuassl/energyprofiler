package assl.cs.usu.edu.energyprofiling;

public class BubbleSortEx {

    public void bubbleSort(int maxelements) {
        int input[] = new int[maxelements];
        int temp;
        for (int i = 0; i < input.length; i++)
            input[i] = (int) (Math.random() * 100);
        for (int i = input.length-1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (input[j] > input[j + 1]) {
                    temp = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = temp;
                }
            }

        }

    }

}
