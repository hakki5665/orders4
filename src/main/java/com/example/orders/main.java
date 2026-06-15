package com.example.orders;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        OrderProcessor.process("discount_day.txt", "discount_day_without_ext");
    }
}

