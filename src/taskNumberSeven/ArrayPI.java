package taskNumberSeven;

import java.util.Scanner;

public class ArrayPI {
    public int[] array = new int[35];

    public void input() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите 35 целых чисел:");
        for (int i = 0; i < array.length; i++) {
            array[i] = scanner.nextInt();
        }
    }
    public void printArray(int[] array, String name) {
        System.out.println(name + ":");
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
