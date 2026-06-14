package com.example.orders;
import java.time.LocalDateTime;

public record Order(LocalDateTime timestamp, String companyName, int amountKg) {
}
