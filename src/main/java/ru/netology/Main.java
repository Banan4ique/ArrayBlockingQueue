package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static final String letters = "abc";
    public static final int textLength = 100_000;
    public static String maxA = "";
    public static int maxCountA = -1;
    public static String maxB = "";
    public static int maxCountB = -1;
    public static String maxC = "";
    public static int maxCountC = -1;

    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            String text = generateText(letters, textLength);
            try {
                queueA.put(text);
                queueB.put(text);
                queueC.put(text);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        Thread threadA = analyzeA(queueA.take());
        Thread threadB = analyzeB(queueB.take());
        Thread threadC = analyzeC(queueC.take());

        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("С символом a: " + maxCountA + maxA);
        System.out.println("С символом b: " + maxCountB + maxB);
        System.out.println("С символом c: " + maxCountC + maxC);
    }

    public static Thread analyzeA(String text) {
        return new Thread(() -> {
            int count = (int) text.chars().filter(x -> (char) x == 'a').count();
            if (count > maxCountA) {
                maxA = text;
                maxCountA = count;
            }
        });
    }

    public static Thread analyzeB(String text) {
        return new Thread(() -> {
            int count = (int) text.chars().filter(x -> (char) x == 'b').count();
            if (count > maxCountB) {
                maxB = text;
                maxCountB = count;
            }
        });
    }

    public static Thread analyzeC(String text) {
        return new Thread(() -> {
            int count = (int) text.chars().filter(x -> (char) x == 'c').count();
            if (count > maxCountC) {
                maxC = text;
                maxCountC = count;
            }
        });
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