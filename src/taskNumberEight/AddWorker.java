package taskNumberEight;

public class AddWorker extends TaskNumberEight {
    static void execute() {
        Worker worker = new Worker();

        String name;
        while (true) {
            System.out.print("Введите имя студента: ");
            name = SCANNER.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Ошибка: имя не может быть пустым.");
            } else if (!name.matches("[а-яА-ЯёЁa-zA-Z\\-\\s]+")) {
                System.out.println("Ошибка: имя может содержать только буквы, дефис и пробел.");
            } else {
                break;
            }
        }
        worker.setName(name);

        int age = -1;
        while (true) {
            System.out.print("Введите возраст студента (0–100): ");
            String input = SCANNER.nextLine().trim();

            try {
                age = Integer.parseInt(input);
                if (age >= 0 && age <= 100) break;
                else System.out.println("Ошибка: возраст должен быть от 0 до 100.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число.");
            }
        }
        worker.setAge(age);

        double salary = -1;
        while (true) {
            System.out.print("Введите зарплату студента (неотрицательное число): ");
            String input = SCANNER.nextLine().trim();

            try {
                salary = Double.parseDouble(input);
                if (salary >= 0) break;
                else System.out.println("Ошибка: зарплата не может быть отрицательной.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }
        worker.setSalary(salary);

        workers.add(worker);
        executeUpdate(INSERT_QUERY, worker.getName(), worker.getAge(), worker.getSalary());

        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                worker.getName(), worker.getAge(), worker.getSalary());
    }
}
