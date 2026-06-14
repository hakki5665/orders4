package com.example.orders;

import java.nio.file.Path;
import java.util.List;

public class OrderParserFactory {

    public static OrderParser getParser(Path filePath, List<String> lines) {
        String fileName = filePath.toString().toLowerCase();

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст: " + filePath);
        }

        String firstLine = lines.get(0);

        if (firstLine.contains("#")) {
            System.out.println("Используем HashOrderParser для: " + filePath);
            return new HashOrderParser();
        }

        if (firstLine.contains("|")) {
            System.out.println("Используем PipeOrderParser (с разделителем |) для: " + filePath);
            return new PipeOrderParser();
        }

        throw new IllegalArgumentException("Не удалось определить формат файла: " + filePath);
    }
}