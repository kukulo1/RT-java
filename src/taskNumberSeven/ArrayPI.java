package taskNumberSeven;

import java.util.Scanner;

public class ArrayPI {
    public int[] array = new int[35];

    public void input() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите 35 целых чисел:");

        for (int i = 0; i < array.length; i++) {
            while (true) {
                System.out.print("Элемент " + (i + 1) + ": ");
                String input = scanner.nextLine().trim();

                // Проверка на пустую строку или строку, состоящую только из знака '-'
                if (input.isEmpty() || input.equals("-")) {
                    System.out.println("Ошибка: введите корректное целое число.");
                    continue;
                }

                // Проверка, что строка — целое число (допускаем знак минус)
                if (!input.matches("-?\\d+")) {
                    System.out.println("Ошибка: введите целое число.");
                    continue;
                }

                try {
                    int value = Integer.parseInt(input); // выбросит исключение, если число за пределами int
                    array[i] = value;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: число выходит за пределы допустимого диапазона int.");
                }
            }
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
