package taskNumberSix;

public class CreateTable extends TaskNumberSix{
    static void execute() {
        executeUpdate(CREATE_TABLE_QUERY);
        tableCreated = true;
        System.out.println("Таблица создана!");
    }
}
