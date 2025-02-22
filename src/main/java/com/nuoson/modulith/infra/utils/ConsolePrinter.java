package com.nuoson.modulith.infra.utils;

public class ConsolePrinter {
    public static void printRed(String message) {
        System.out.println(String.format("\u001B[31m%s\u001B[0m", message));
    }

    public static void printGreen(String message) {
        System.out.println(String.format("\u001B[32m%s\u001B[0m", message));
    }

    public static void printBlue(String message) {
        System.out.println(String.format("\u001B[34m%s\u001B[0m", message));
    }

    // 仿照上述函数，实现一个打印黄色文字的函数
    public static void printYellow(String message) {
        System.out.println(String.format("\u001B[33m%s\u001B[0m", message));
    }
    


    public static void print(String message) {
        System.out.println(message);
    }
}
