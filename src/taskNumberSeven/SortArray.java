package taskNumberSeven;

public class SortArray extends TaskNumberSeven {
    static void execute() {
        if (sort != null) {
            int[] asc = sort.sortAscending();
            sort.printArray(asc, "Отсортированный по возрастанию массив");
            for (int i = 0; i < asc.length; i++) {
                executeUpdate(INSERT_QUERY, "ascending sort", i, sort.array[i]);
            }
            int[] desc = sort.sortDescending();
            sort.printArray(desc, "Отсортированный по убыванию массив");
            for (int i = 0; i < desc.length; i++) {
                executeUpdate(INSERT_QUERY, "descending sort", i, sort.array[i]);
            }
        } else {
            System.out.println("Ошибка: массив не был введён ранее.");
        }
    }
}
