package taskNumberSeven;

public class InputArray extends TaskNumberSeven {
    static void execute() {
        sort = new Sort();
        sort.input();
        sort.printArray(sort.array, "Original");

        for (int i = 0; i < sort.array.length; i++) {
            executeUpdate(INSERT_QUERY, "original", i, sort.array[i]);
        }
    }
}
