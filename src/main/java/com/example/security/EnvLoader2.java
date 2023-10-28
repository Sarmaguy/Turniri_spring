package com.example.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader2 {
    private static final Map<String, String> envMap = new HashMap<>();

    static {;

        try (BufferedReader br = new BufferedReader(new FileReader("/etc/secrets/.env"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envMap.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle this properly in your application
        }
    }

    public static String getVariable(String key) {
        return envMap.get(key);
    }
}
