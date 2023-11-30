package com.example.pain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
    private static final Logger logger = Logger.getLogger(MyLogger.class.getName());

    public static void main(String[] args) {
        setupLogger();
        logger.info("Logging Started");
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("mylog.txt");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logCustomMessage(String message) {
        logger.info(message);
    }
}
