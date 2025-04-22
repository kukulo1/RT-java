import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberThree {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true"; //��� �� ������, ��� ������ ��� ����������� � ��; ?createDatabaseIfNotExist=true ���� ��� ��������������� �������� ��, ���� �� ��� ����
    public final static String DATABASE_LOGIN = "root"; //���� �����
    public final static String DATABASE_PASSWORD = "kukulo1"; //���� ���� ������ ���
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK3 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "number INT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK3 (operation, number, result) VALUES (?, ?, ?)";


    public static void main(String[] args) {
        int choice = 0;

        while (choice != -1) {
            printConsoleMenu();
            try {
                choice = SCANNER.nextInt();
                doAction(choice);
            } catch (NumberFormatException e) {
                System.out.println("������� ����� ��������!");
            }
        }
    }

    static void printConsoleMenu() {
        System.out.println("1. ������� ��� ������� �� MySQL.");
        System.out.println("2. ������� ������� � MySQL.");
        System.out.println("3. ��������� ����� �� ��������������� � ��������, ��������� ��������� � MySQL � ������� � �������.");
        System.out.println("4. ��������� ��� ������ �� MySQL � Excel � ������� �� �����.");
        System.out.println("-1. ��������� ���������� ���������");
        System.out.print("�������� ��������: ");
    }

    static void doAction(int choice) {
        switch (choice) {
            case 1 -> {
                List<String> tables = new ArrayList<>();
                try (ResultSet resultSet = executeQuery("SHOW TABLES")) {
                    while (resultSet.next()) {
                        tables.add(resultSet.getString(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (tables.isEmpty()) {
                    System.out.println("������� �� �������.");
                } else {
                    tables.forEach(System.out::println);
                }
            }
            case 2 -> {
                executeUpdate(CREATE_TABLE_QUERY);
                System.out.println("������� �������!");
            }
            case 3 -> {
                System.out.println("������� ����� ����� ������ (��� ���������� ������� 'q'):");
                while (true) {
                    String input = SCANNER.next();
                    if (input.equalsIgnoreCase("q")) {
                        break;
                    }
                    try {
                        double number = Double.parseDouble(input);
                        if (number % 1 != 0) {
                            System.out.println("������! " + number + " � ������� �����");
                            continue;
                        }
                        int intValue = (int) number;
                        boolean even = intValue % 2 == 0;
                        String result = even ? "Even" : "Odd";
                        System.out.println(intValue + " � ����� " + (even ? "������" : "��������") + " �����");
                        executeUpdate(INSERT_QUERY, "input", intValue, result);
                    } catch (NumberFormatException e) {
                        System.out.println("������: '" + input + "' �� �������� ������");
                    }
                }
            }
            case 4 -> {
                String filePath = "src/resources/task3.csv";
                String query = "SELECT * FROM TASK3";
                try (FileWriter fileWriter = new FileWriter(filePath);
                     ResultSet resultSet = executeQuery(query)) {                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        fileWriter.append('"').append(metaData.getColumnName(i)).append('"');
                        if (i < columnCount) fileWriter.append(";");
                    }
                    fileWriter.append("\n");

                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = resultSet.getString(i);
                            fileWriter.append('"');
                            if (value != null) {
                                fileWriter.append(value.replace("\"", "\"\""));
                            }
                            fileWriter.append('"');
                            if (i < columnCount) fileWriter.append(";");
                        }
                        fileWriter.append("\n");
                    }

                    System.out.println("������ ������� �������������� � ���� CSV: " + filePath);

                    printTable();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    System.out.println("������ ��� �������� ������ � CSV.");
                }
            }
            case -1 -> {
                System.out.println("����� �� ���������...");
            }
            default -> {
                System.out.println("�������� �����. ���������.");
            }
        }
    }
    static double scanDouble() {
        while (!SCANNER.hasNextDouble()) {
            System.out.println("������ �����. ������� �����!");
            SCANNER.next();
        }
        return SCANNER.nextDouble();
    }

    static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    static int executeUpdate(String query, Object... params) {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    static void printTable() {
        String query = "SELECT * FROM TASK3";
        try (ResultSet resultSet = executeQuery(query)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
