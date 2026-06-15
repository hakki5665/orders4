package com.example.orders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class OrderProcessor {

    public static void process(String file1Path, String file2Path) throws IOException {
        Path file1 = Path.of(file1Path);
        Path file2 = Path.of(file2Path);

        List<Order> allOrders = new ArrayList<>();

        processFile(file1, allOrders);
        processFile(file2, allOrders);

        List<Order> sortedOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::timestamp))
                .collect(Collectors.toList());

        final int PRICE_PER_KG = 10;
        List<String> results = new ArrayList<>();
        int discountStep = 0;

        for (Order order : sortedOrders) {
            int discount = 50 - (discountStep * 5);
            if (discount < 0) discount = 0;

            double cost = order.amountKg() * PRICE_PER_KG * (100 - discount) / 100.0;
            results.add(order.companyName() + " - " + String.format("%.2f", cost));
            discountStep++;
        }

        Files.write(Path.of("result.txt"), results,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void processFile(Path filePath, List<Order> allOrders) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            return;
        }

        OrderParser parser = OrderParserFactory.getParser(filePath, lines);
        List<Order> orders = parser.parse(lines);
        allOrders.addAll(orders);
    }
}