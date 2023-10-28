package com.example.security;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}