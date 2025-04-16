package com.grupo4.autocine.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvPropertySource extends EnumerablePropertySource<Map<String, String>> {

    private final Map<String, String> properties = new HashMap<>();

    public DotenvPropertySource() {
        super("dotenv");
        loadProperties();
    }

    private void loadProperties() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            
            dotenv.entries().forEach(entry -> 
                properties.put(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            // Log the error but don't fail startup
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }

    @Override
    public String[] getPropertyNames() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }
} 