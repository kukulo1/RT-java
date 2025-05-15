package taskNumberEight;

public class CreateTable extends TaskNumberEight {
    static void execute() {
        executeUpdate(CREATE_TABLE_QUERY);
        tableCreated = true;
        System.out.println("Таблица создана!");
    }
}
