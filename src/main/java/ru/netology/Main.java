package ru.netology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static final String LETTERS = "abc";
    public static final int SIZE = 100;
    public static final int TEXT_LENGTH = 100_000;
    public static final int QUANTITY = 10_000;

    public static final List<BlockingQueue<String>> queues = Arrays.asList(
            new ArrayBlockingQueue<>(SIZE),
            new ArrayBlockingQueue<>(SIZE),
            new ArrayBlockingQueue<>(SIZE)
    );

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int i = 0; i < QUANTITY; i++) {
                String text = generateText(LETTERS, TEXT_LENGTH);
                try {
                    for (BlockingQueue<String> queue : queues) {
                        queue.put(text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < queues.size(); i++) {
            Thread consumer = getConsumer(i);
            consumers.add(consumer);
        }

        for (Thread consumer : consumers) {
            consumer.join();
        }

    }

    private static Thread getConsumer(int i) {
        char c = (char) ('a' + i);

        Thread consumer = new Thread(() -> {
            BlockingQueue<String> queue = queues.get(i);
            String maxString = "";
            long maxCount = 0;
            for (int j = 0; j < QUANTITY; j++) {
                try {
                    String s = queue.take();
                    long count = countChar(s, c);
                    if (count > maxCount) {
                        maxCount = count;
                        maxString = s;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.printf("Max count of letter %c - %d - found in the following string: " +
                    "%s\n", c, maxCount, maxString.substring(0, 75) + "...");
        });
        consumer.start();
        return consumer;
    }

    public static int countChar(String text, char c) {
        return (int) text.chars().filter(x -> x == c).count();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}