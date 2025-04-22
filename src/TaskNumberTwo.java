import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskNumberTwo {
    public final static Scanner SCANNER = new Scanner(System.in);
    public final static String DATABASE_CONNECTION_URL = "jdbc:mysql://localhost:3306/java?createDatabaseIfNotExist=true"; //��� �� ������, ��� ������ ��� ����������� � ��; ?createDatabaseIfNotExist=true ���� ��� ��������������� �������� ��, ���� �� ��� ����
    public final static String DATABASE_LOGIN = "root"; //���� �����
    public final static String DATABASE_PASSWORD = "kukulo1"; //���� ���� ������ ���
    public final static String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS TASK2 (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "operation VARCHAR(255), " +
            "string1 TEXT, " +
            "string2 TEXT, " +
            "result TEXT)";
    public final static String INSERT_QUERY = "INSERT INTO TASK2 (operation, string1, string2, result) VALUES (?, ?, ?, ?)";
    public static String string1, string2;


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
        System.out.println("3. ������ ��� ������ � ����������, ��������� ��������� � MySQL � ����������� ������� � �������.");
        System.out.println("4. ���������� ������ ����� ��������� �����, ��������� ��������� � MySQL � ����������� ������� � �������.");
        System.out.println("5. ���������� ��� ������ � ������ �����, ��������� ��������� � MySQL � ����������� ������� � �������.");
        System.out.println("6. �������� ��� ����� ��������� ������, ��������� ��������� � MySQL � ����������� ������� � �������.");
        System.out.println("7. ��������� ��� ������ (�������������� ����������) �� MySQL � Excel � ������� �� �����.");
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
                System.out.print("������� ������ ������ (�� ����� 50 ��������): ");
                string1 = scanString();
                System.out.print("������� ������ ������ (�� ����� 50 ��������): ");
                string2 = scanString();
                executeUpdate(INSERT_QUERY, "input", string1, string2, null);
                System.out.println("������ ���������:");
                System.out.println("1: " + string1);
                System.out.println("2: " + string2);
            }
            case 4 -> {
                if (!stringsEntered()) break;
                int length1 = string1.length();
                int length2 = string2.length();
                String result = "Length1: " + length1 + ", Length2: " + length2;
                executeUpdate(INSERT_QUERY, "length", string1, string2, result);
                System.out.println("����� �����: " + result);
            }
            case 5 -> {
                if (!stringsEntered()) break;
                String result = string1 + string2;
                System.out.println("����������� �����: " + result);
                executeUpdate(INSERT_QUERY, "concatenation", string1, string2, result);
            }
            case 6 -> {
                if (!stringsEntered()) break;
                String result = string1.equals(string2) ? "equal" : "not equal";
                System.out.println("���������: " + result);
                executeUpdate(INSERT_QUERY, "comparison", string1, string2, result);
            }
            case 7 -> {
                String filePath = "src/resources/task2.csv";
                String query = "SELECT * FROM TASK2";
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
    static String scanString() {
        String input;
        do {
            input = SCANNER.nextLine();
            if (input.length() < 50) {
                System.out.println("������ ������� ��������. ����� �� ����� 50 ��������.");
            }
        } while (input.length() < 50);
        return input;
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
        String query = "SELECT * FROM TASK2";
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
    static boolean stringsEntered() {
        if (string1 == null || string2 == null) {
            System.out.println("������� ������� ������ (����� 3).");
            return false;
        }
        return true;
    }
}
