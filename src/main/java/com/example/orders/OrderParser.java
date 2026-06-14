package com.example.orders;
import java.util.List;
//For PR

public interface OrderParser {
    List<Order> parse(List<String> lines);
}
