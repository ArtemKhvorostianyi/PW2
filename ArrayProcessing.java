import java.util.*;
import java.util.concurrent.*;

public class ArrayProcessing {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        // Введення діапазону чисел
        System.out.println("Введіть мінімальне значення діапазону:");
        int minRange = scanner.nextInt();
        System.out.println("Введіть максимальне значення діапазону:");
        int maxRange = scanner.nextInt();

        // Введення розміру масиву
        int arraySize;
        do {
            System.out.println("Введіть розмір масиву (від 40 до 60):");
            arraySize = scanner.nextInt();
            if (arraySize < 40 || arraySize > 60) {
                System.out.println("Невірне значення. Розмір масиву має бути між 40 і 60.");
            }
        } while (arraySize < 40 || arraySize > 60);

        // Генерація масиву
        Random random = new Random();
        int[] numbers = new int[arraySize];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(maxRange - minRange + 1) + minRange;
        }
        System.out.println("Згенерований масив: " + Arrays.toString(numbers));

        // Параметри обробки
        int chunkSize = 10; // Розмір частини масиву
        ExecutorService executor = Executors.newFixedThreadPool(4); // Пул потоків
        List<Future<List<Integer>>> futures = new ArrayList<>();
        long startTime = System.nanoTime();

        // Розбиття масиву на частини
        for (int i = 0; i < numbers.length; i += chunkSize) {
            int[] chunk = Arrays.copyOfRange(numbers, i, Math.min(i + chunkSize, numbers.length));
            int chunkIndex = i / chunkSize + 1; // Номер частини
            Callable<List<Integer>> task = () -> {
                String threadName = Thread.currentThread().getName(); // Отримуємо ім'я потоку
                System.out.println("Потік " + threadName + " обробляє частину #" + chunkIndex + ": " + Arrays.toString(chunk));
                return processChunk(chunk);
            };
            futures.add(executor.submit(task));
        }

        // Збір результатів
        List<Integer> resultSet = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < futures.size(); i++) {
            Future<List<Integer>> future = futures.get(i);

            try {
                List<Integer> partialResult = future.get(); // Отримуємо результат виконання завдання
                System.out.println("Результати обробки частини #" + (i + 1) + ": " + partialResult);
                resultSet.addAll(partialResult); // Додаємо всі результати частини
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Помилка виконання завдання #" + (i + 1) + ": " + e.getMessage());
            }

            // Перевірка завершення завдання та виведення повідомлення з правильним іменем потоку
            if (future.isDone()) {
                System.out.println("Завдання #" + (i + 1) + " завершено потоком");
            }
        }

        executor.shutdown(); // Завершуємо роботу пулу потоків

        // Вивід результатів
        System.out.println("Результати обчислень: " + resultSet);
        // Підрахунок і вивід часу роботи програми
        long endTime = System.nanoTime();
        System.out.println("Час роботи програми: " + (endTime - startTime) / 1_000_000 + " мс");
    }


    private static List<Integer> processChunk(int[] chunk) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < chunk.length - 1; i += 2) {
            int product = chunk[i] * chunk[i + 1];
            System.out.println("Обробка: " + chunk[i] + " * " + chunk[i + 1] + " = " + product);
            result.add(product);
        }
        if (chunk.length % 2 != 0) {
            System.out.println("Останній елемент без пари: " + chunk[chunk.length - 1]);
        }
        return result;
    }
}
