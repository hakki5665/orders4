package com.example.orders;

import org.junit.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.*;

public class OrderParserTest {

    private static final Path FILE_PIPE = Path.of("discount_day.txt");
    private static final Path FILE_HASH = Path.of("discount_day_without_ext");
    private static final Path RESULT_FILE = Path.of("result.txt");

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
    }

    @Before
    public void beforeEach() {

    }

    @Test
    public void testFilesExist() {
        assertTrue("Файл discount_day.txt не найден", Files.exists(FILE_PIPE));
        assertTrue("Файл discount_day_without_ext не найден", Files.exists(FILE_HASH));
    }

    @Test
    public void testPipeOrderParser() throws IOException {
        assertTrue("Файл не найден", Files.exists(FILE_PIPE));

        List<String> lines = Files.readAllLines(FILE_PIPE);
        OrderParser parser = new PipeOrderParser();
        List<Order> orders = parser.parse(lines);

        assertFalse("Не удалось распарсить заказы", orders.isEmpty());
        assertEquals("Количество распарсенных заказов не совпадает", lines.size(), orders.size());

    }

    @Test
    public void testHashOrderParser() throws IOException {
        assertTrue("Файл не найден", Files.exists(FILE_HASH));

        List<String> lines = Files.readAllLines(FILE_HASH);
        OrderParser parser = new HashOrderParser();
        List<Order> orders = parser.parse(lines);

        assertFalse("Не удалось распарсить заказы", orders.isEmpty());
        assertEquals("Количество распарсенных заказов не совпадает", lines.size(), orders.size());

    }

    @Test
    public void testOrderParserFactory() throws IOException {
        List<String> pipeLines = Files.readAllLines(FILE_PIPE);
        OrderParser pipeParser = OrderParserFactory.getParser(FILE_PIPE, pipeLines);
        assertTrue("Для discount_day.txt должен быть PipeOrderParser", pipeParser instanceof PipeOrderParser);

        List<String> hashLines = Files.readAllLines(FILE_HASH);
        OrderParser hashParser = OrderParserFactory.getParser(FILE_HASH, hashLines);
        assertTrue("Для discount_day_without_ext должен быть HashOrderParser", hashParser instanceof HashOrderParser);
    }

    @Test
    public void testFullOrderProcessing() throws IOException {
        Files.deleteIfExists(RESULT_FILE);

        OrderProcessor.process("discount_day.txt", "discount_day_without_ext");

        assertTrue("Файл result.txt не создан", Files.exists(RESULT_FILE));

        List<String> results = Files.readAllLines(RESULT_FILE);
        assertFalse("Файл result.txt пустой", results.isEmpty());

    }

    @Test
    public void testOrderSorting() throws IOException {
        List<Order> allOrders = new java.util.ArrayList<>();

        List<String> pipeLines = Files.readAllLines(FILE_PIPE);
        OrderParser pipeParser = OrderParserFactory.getParser(FILE_PIPE, pipeLines);
        allOrders.addAll(pipeParser.parse(pipeLines));

        List<String> hashLines = Files.readAllLines(FILE_HASH);
        OrderParser hashParser = OrderParserFactory.getParser(FILE_HASH, hashLines);
        allOrders.addAll(hashParser.parse(hashLines));

        assertFalse("Нет заказов для сортировки", allOrders.isEmpty());

        List<Order> sorted = allOrders.stream()
                .sorted(java.util.Comparator.comparing(Order::timestamp))
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue("Заказы не отсортированы по времени",
                    sorted.get(i).timestamp().isBefore(sorted.get(i + 1).timestamp()) ||
                            sorted.get(i).timestamp().isEqual(sorted.get(i + 1).timestamp()));
        }

    }
}