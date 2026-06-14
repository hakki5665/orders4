package com.example.orders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PipeOrderParser implements OrderParser {

    @Override
    public List<Order> parse(List<String> lines) {
        return lines.stream()
                .map(line -> line.split("\\|"))
                .filter(parts -> parts.length == 3)
                .map(parts -> {
                    LocalDateTime timestamp = LocalDateTime.parse(parts[0]);
                    String companyName = parts [1];
                    int amountKg = Integer.parseInt(parts[2]);
                    return new Order(timestamp, companyName, amountKg);
                })
                .collect(Collectors.toList());
    }
}
