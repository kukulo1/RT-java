package taskNumberSix;

public class Matrix extends ArrayPI{
    public int[][] multiply() {
        int[][] result = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    result[i][j] += arrayA[i][k] * arrayB[k][j];
                }
            }
        }
        return result;
    }
}
