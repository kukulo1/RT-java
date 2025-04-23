package taskNumberSix;

import java.util.Scanner;

public class ArrayPI {
    public int[][] arrayA = new int[7][7];
    public int[][] arrayB = new int[7][7];

    public void input() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите значения для первой матрицы:");
        for (int i = 0; i < 7; i++) {
            System.out.print("Строка " + (i + 1) + ": ");
            for (int j = 0; j < 7; j++) {
                arrayA[i][j] = scanner.nextInt();
            }
        }

        System.out.println("Введите значения для второй матрицы:");
        for (int i = 0; i < 7; i++) {
            System.out.print("Строка " + (i + 1) + ": ");
            for (int j = 0; j < 7; j++) {
                arrayB[i][j] = scanner.nextInt();
            }
        }
    }
    public void print() {
        System.out.println("Матрица A:");
        for (int[] row : arrayA) {
            for (int value : row) {
                System.out.printf("%4d", value);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Матрица B:");
        for (int[] row : arrayB
        ) {
            for (int value : row) {
                System.out.printf("%4d", value);
            }
            System.out.println();
        }
    }
}
