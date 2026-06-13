package com.example.orders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class OrderParserTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("  ТЕСТИРОВАНИЕ ОБРАБОТКИ ЗАКАЗОВ  \n");

        checkFilesExist();

        testPipeOrderParser();

        testHashOrderParser();

        testOrderParserFactory();

        testFullOrderProcessing();

        testOrderSorting();

        printResults();
    }

    static void checkFilesExist() {
        System.out.println(" ПРОВЕРКА ФАЙЛОВ: ");
        Path file1 = Path.of("discount_day.txt");
        Path file2 = Path.of("discount_day_without_ext");

        if (Files.exists(file1)) {
            System.out.println(" discount_day.txt существует ");
        } else {
            System.out.println(" discount_day.txt НЕ НАЙДЕН  ");
            testsFailed++;
        }

        if (Files.exists(file2)) {
            System.out.println(" discount_day_without_ext существует ");
        } else {
            System.out.println(" discount_day_without_ext НЕ НАЙДЕН ");
            testsFailed++;
        }
        System.out.println();
    }

    static void testPipeOrderParser() throws IOException {
        System.out.println(" ТЕСТ 1: PipeOrderParser (разделитель |)");
        System.out.println("  Файл: discount_day.txt");

        try {
            Path file = Path.of("discount_day.txt");
            if (!Files.exists(file)) {
                System.out.println("  Файл не найден, тест пропущен\n");
                return;
            }

            List<String> lines = Files.readAllLines(file);
            OrderParser parser = new PipeOrderParser();
            List<Order> orders = parser.parse(lines);

            System.out.println("   Всего строк в файле: " + lines.size());
            System.out.println("   Успешно распарсено: " + orders.size() + " заказов");

            if (!orders.isEmpty()) {
                System.out.println("\n  ПЕРВЫЕ 5 ЗАКАЗОВ ИЗ ФАЙЛА:");
                int count = Math.min(5, orders.size());
                for (int i = 0; i < count; i++) {
                    Order o = orders.get(i);
                    System.out.printf("   %d. %s | %s | %d кг%n",
                            i+1, o.timestamp(), o.companyName(), o.amountKg());
                }
                if (orders.size() > 5) {
                    System.out.println("   ... и ещё " + (orders.size() - 5) + " заказов");
                }
            }

            if (orders.size() > 0) {
                System.out.println("  ТЕСТ ПРОЙДЕН\n");
                testsPassed++;
            } else {
                System.out.println("  ТЕСТ ПРОВАЛЕН: не удалось распарсить заказы\n");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println(" ОШИБКА: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    static void testHashOrderParser() throws IOException {
        System.out.println(" ТЕСТ 2: HashOrderParser (разделитель #)");
        System.out.println("   Файл: discount_day_without_ext");

        try {
            Path file = Path.of("discount_day_without_ext");
            if (!Files.exists(file)) {
                System.out.println("  Файл не найден, тест пропущен\n");
                return;
            }

            List<String> lines = Files.readAllLines(file);
            OrderParser parser = new HashOrderParser();
            List<Order> orders = parser.parse(lines);

            System.out.println("   Всего строк в файле: " + lines.size());
            System.out.println("   Успешно распарсено: " + orders.size() + " заказов");

            if (!orders.isEmpty()) {
                System.out.println("\n ПЕРВЫЕ 5 ЗАКАЗОВ ИЗ ФАЙЛА:");
                int count = Math.min(5, orders.size());
                for (int i = 0; i < count; i++) {
                    Order o = orders.get(i);
                    System.out.printf("   %d. %s | %s | %d кг%n",
                            i+1, o.timestamp(), o.companyName(), o.amountKg());
                }
                if (orders.size() > 5) {
                    System.out.println("   ... и ещё " + (orders.size() - 5) + " заказов");
                }
            }

            if (orders.size() > 0) {
                System.out.println(" ТЕСТ ПРОЙДЕН\n");
                testsPassed++;
            } else {
                System.out.println("  ТЕСТ ПРОВАЛЕН: не удалось распарсить заказы\n");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("  ОШИБКА: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    static void testOrderParserFactory() throws IOException {
        System.out.println(" ТЕСТ 3: OrderParserFactory");

        try {
            Path file1 = Path.of("discount_day.txt");
            if (Files.exists(file1)) {
                List<String> lines1 = Files.readAllLines(file1);
                if (!lines1.isEmpty()) {
                    OrderParser parser1 = OrderParserFactory.getParser(file1, lines1);
                    boolean isPipeParser = parser1 instanceof PipeOrderParser;
                    System.out.println("   discount_day.txt → " + parser1.getClass().getSimpleName() +
                            (isPipeParser ? " Пройден " : " Не пройден "));
                }
            }

            Path file2 = Path.of("discount_day_without_ext");
            if (Files.exists(file2)) {
                List<String> lines2 = Files.readAllLines(file2);
                if (!lines2.isEmpty()) {
                    OrderParser parser2 = OrderParserFactory.getParser(file2, lines2);
                    boolean isHashParser = parser2 instanceof HashOrderParser;
                    System.out.println("   discount_day_without_ext → " + parser2.getClass().getSimpleName() +
                            (isHashParser ? " Пройден " : " Не пройден "));
                }
            }

            System.out.println(" ТЕСТ ПРОЙДЕН\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println(" ОШИБКА: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    static void testFullOrderProcessing() throws IOException {
        System.out.println(" ТЕСТ 4: Полная обработка заказов");
        System.out.println("   Запуск OrderProcessor.process()");

        try {

            Path resultPath = Path.of("result.txt");
            if (Files.exists(resultPath)) {
                Files.delete(resultPath);
            }

            OrderProcessor.process("discount_day.txt", "discount_day_without_ext");

            if (Files.exists(resultPath)) {
                List<String> results = Files.readAllLines(resultPath);
                System.out.println(" Файл result.txt создан");
                System.out.println(" Всего обработано заказов: " + results.size());

                if (!results.isEmpty()) {
                    System.out.println("\n ПЕРВЫЕ 10 РЕЗУЛЬТАТОВ:");
                    int count = Math.min(10, results.size());
                    for (int i = 0; i < count; i++) {
                        System.out.println("   " + (i+1) + ". " + results.get(i));
                    }
                    if (results.size() > 10) {
                        System.out.println("   ... и ещё " + (results.size() - 10) + " результатов");
                    }
                }

                System.out.println("\n  ТЕСТ ПРОЙДЕН\n");
                testsPassed++;
            } else {
                System.out.println(" Файл result.txt НЕ СОЗДАН\n");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println(" ОШИБКА: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    static void testOrderSorting() throws IOException {
        System.out.println(" ТЕСТ 5: Проверка сортировки по дате");

        try {

            List<Order> allOrders = new java.util.ArrayList<>();

            Path file1 = Path.of("discount_day.txt");
            if (Files.exists(file1)) {
                OrderParser parser1 = OrderParserFactory.getParser(file1, Files.readAllLines(file1));
                allOrders.addAll(parser1.parse(Files.readAllLines(file1)));
            }

            Path file2 = Path.of("discount_day_without_ext");
            if (Files.exists(file2)) {
                OrderParser parser2 = OrderParserFactory.getParser(file2, Files.readAllLines(file2));
                allOrders.addAll(parser2.parse(Files.readAllLines(file2)));
            }

            if (!allOrders.isEmpty()) {

                List<Order> sorted = allOrders.stream()
                        .sorted(java.util.Comparator.comparing(Order::timestamp))
                        .collect(Collectors.toList());

                System.out.println("   Всего заказов: " + allOrders.size());
                System.out.println("   Самая ранняя дата: " + sorted.get(0).timestamp());
                System.out.println("   Самая поздняя дата: " + sorted.get(sorted.size() - 1).timestamp());

                boolean isSorted = true;
                for (int i = 0; i < sorted.size() - 1; i++) {
                    if (sorted.get(i).timestamp().isAfter(sorted.get(i + 1).timestamp())) {
                        isSorted = false;
                        break;
                    }
                }

                if (isSorted) {
                    System.out.println(" Сортировка работает корректно");
                } else {
                    System.out.println(" Проблема с сортировкой");
                }
            }

            System.out.println(" ТЕСТ ПРОЙДЕН\n");
            testsPassed++;
        } catch (Exception e) {
            System.out.println(" ОШИБКА: " + e.getMessage() + "\n");
            testsFailed++;
        }
    }

    static void printResults() {
        System.out.println(" ИТОГИ ТЕСТИРОВАНИЯ ");
        System.out.println(" УСПЕШНО: " + testsPassed);
        System.out.println(" ПРОВАЛЕНО: " + testsFailed);
        System.out.println(" ВСЕГО ТЕСТОВ: " + (testsPassed + testsFailed));

        if (testsFailed == 0) {
            System.out.println(" ВСЕ ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!");
        } else {
            System.out.println(" ЕСТЬ ПРОБЛЕМЫ ");
        }
    }
}