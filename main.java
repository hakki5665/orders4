package com.example.orders;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main {
    public static void main(String[] args) throws IOException {
        Path file1 = Path.of("discount_day.txt");
        Path file2 = Path.of("discount_day_without_ext");

        List<Order> allOrders = new ArrayList<>();

        List<String> lines1 = Files.readAllLines(file1);
        OrderParser pipeParser = new PipeOrderParser();
        allOrders.addAll(pipeParser.parse(lines1));

        List<String> lines2 = Files.readAllLines(file2);
        OrderParser hashParser = new HashOrderParser();
        allOrders.addAll(hashParser.parse(lines2));

        List<Order> sortedOrders = allOrders.stream()
                .sorted(Comparator.comparing(Order::timestamp))
                .collect(Collectors.toList());

        sortedOrders.forEach(order ->
                System.out.println(order.timestamp() + " | " + order.companyName() + " | " + order.amountKg()));


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
        Files.write(Path.of("result.txt"), results);
    }
}

