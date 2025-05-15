package taskNumberSeven;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLToExcel extends TaskNumberSeven{
    static void execute() {
        System.out.print("Введите название файла с расширением (.xls): ");
        SCANNER.nextLine();
        String fileName = SCANNER.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = SCANNER.nextLine().trim();
        }

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String exportQuery =
                "SELECT 'id', 'array_name', 'index_pos', 'value' " +
                        "UNION ALL " +
                        "SELECT id, array_name, index_pos, value FROM TASK7 " +
                        "INTO OUTFILE '" + filePath + "' " +
                        "CHARACTER SET cp1251";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(exportQuery)) {

            stmt.executeQuery();

            System.out.println("Данные успешно экспортированы в файл: " + filePath);
            printTable();

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
            } else {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
            }
        }

    }
}
