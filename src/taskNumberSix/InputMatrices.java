package taskNumberSix;

import java.util.InputMismatchException;

public class InputMatrices extends TaskNumberSix{
    static void execute() {
        try {
            matrix = new Matrix();
            matrix.input();
            matrix.print();

            insertMatrixIntoDB(matrix.arrayA, "Matrix 1");
            insertMatrixIntoDB(matrix.arrayB, "Matrix 2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
