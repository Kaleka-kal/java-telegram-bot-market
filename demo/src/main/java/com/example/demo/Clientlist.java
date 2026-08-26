package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class Clientlist {

    // Используем CopyOnWriteArrayList, так как планировщик и другие потоки работают со списком одновременно
    private List<Long> clientList = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Метод для добавления клиентов из других сервисов/контроллеров

    public void addClient(Long userId) {
        clientList.add(userId);
    }
    public void removeClient(Long userId)
    {
        scheduler.schedule(() -> {
            // Код, который выполнится через полторы недели секунд
            clientList.remove(userId);
        }, 904800 , TimeUnit.SECONDS);
    }

    public List<Long> getClientList() {
        return clientList;
    }
    // записm в файл
    @Scheduled(cron = "0 0 3 * * MON")
    public void saveListToFileScheduled() {
        try {
            List<String> stringList = clientList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            Files.write(Path.of(Backup.getFilePath()), stringList);
            System.out.println("Список клиентов успешно сохранен в файл. Текущий размер: " + clientList.size());
        } catch (IOException e) {
            System.err.println("Не удалось записать список в файл: " + e.getMessage());
        }
    }
}