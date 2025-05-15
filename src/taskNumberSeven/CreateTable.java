package taskNumberSeven;

public class CreateTable extends TaskNumberSeven {
    static void execute() {
        executeUpdate(CREATE_TABLE_QUERY);
        tableCreated = true;
        System.out.println("Таблица создана!");
    }
}
