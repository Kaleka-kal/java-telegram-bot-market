package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class Backup {

    private final ObjectMapper objectMapper;
    // путь к файлу бэкапа clientlist
    static final String FILE_PATH = "src/main/java/Data/data.json";

    public static String getFilePath() {
        return FILE_PATH;
    }
    //конструктор
    public Backup(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
