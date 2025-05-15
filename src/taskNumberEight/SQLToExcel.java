package taskNumberEight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLToExcel extends TaskNumberEight{
    static void execute() {
        System.out.print("Введите название файла с расширением (.xls): ");
        String fileName = SCANNER.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = SCANNER.nextLine().trim();
        }

        String filePath = "C:/Users/User/Desktop/" + fileName;
        String query1 = "SET SQL_SAFE_UPDATES = 0";
        String query2 = "UPDATE TASK8 SET salary = ROUND(salary, 2)";
        String query3 = "SET SQL_SAFE_UPDATES = 1";

        String query4 =
                "SELECT 'id', 'name', 'age', 'salary' " +
                        "UNION ALL " +
                        "SELECT id, name, age, salary FROM TASK8 " +
                        "INTO OUTFILE '" + filePath + "' " +
                        "CHARACTER SET cp1251";

        try (Connection conn = getConnection()) {
            PreparedStatement stmt1 = conn.prepareStatement(query1);
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            PreparedStatement stmt3 = conn.prepareStatement(query3);
            PreparedStatement stmt4 = conn.prepareStatement(query4);

            stmt1.executeUpdate();
            stmt2.executeUpdate();
            stmt3.executeUpdate();
            stmt4.executeQuery();

            System.out.println("Данные успешно экспортированы в файл: " + filePath);

            DisplayAllWorkers.execute();

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
            } else {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
            }
        }

    }
}
