package taskNumberSix;

public class MultiplyMatrices extends TaskNumberSix {
    static void execute() {
        if (matrix != null) {
            int[][] result = matrix.multiply();
            printMatrix(result, "Результат (A x B)");
            insertMatrixIntoDB(result, "result of multiplication");
        } else {
            System.out.println("Ошибка: матрицы не были введены.");
        }
    }
}
