package taskNumberEight;

import java.sql.*;

public class DisplayAllWorkers extends TaskNumberEight {
    static void execute() {

        try (Connection con = getConnection();
             Statement stmt1 = con.createStatement();
             ResultSet rs = stmt1.executeQuery("SELECT * FROM TASK8")) {

            System.out.printf("%-3s | %-20s | %-4s | %-100s%n", "ID", "Name", "Age", "Salary");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                double salary = rs.getDouble("salary");

                System.out.printf("%-3d | %-20s | %-4d | %-100.2f%n", id, name, age, salary);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
