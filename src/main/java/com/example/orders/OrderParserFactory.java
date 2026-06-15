package com.example.orders;

import java.nio.file.Path;
import java.util.List;

public class OrderParserFactory {

    public static OrderParser getParser(Path filePath, List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст: " + filePath);
        }

        String firstLine = lines.get(0);

        if (firstLine.contains("#")) {
            return new HashOrderParser();
        }

        if (firstLine.contains("|")) {
            return new PipeOrderParser();
        }

        throw new IllegalArgumentException("Не удалось определить формат файла: " + filePath);
    }
}