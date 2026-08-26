package com.example.demo;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DateModer {
    // Используем CopyOnWriteArrayList для потокобезопасности (идеально, если чтений намного больше, чем записей)
    private final List<Long> moderList = new CopyOnWriteArrayList<>();
    private final Map<Long, Long> moderToClientMap = new ConcurrentHashMap<>();

    // Конструктор класса
    public DateModer() {
        moderList.add(7609386124L);
    }
    public void removeModer(long moderId) {
        moderList.remove(moderId);
    }

    // --- Работа с мапой чатов ---

    public void setModerForChat(long moderId, long clientId) {
        moderToClientMap.put(moderId, clientId);
    }

    public Long getChatForModer(long moderId) {
        return moderToClientMap.get(moderId);
    }

    public void removeModerChat(long moderId) {
        moderToClientMap.remove(moderId);
    }

    public Map<Long, Long> getModerToClientMap() {
        return Collections.unmodifiableMap(moderToClientMap);
    }

    // --- Работа со списком модераторов ---

    /**
     * Добавляет модератора во внутренний список класса.
     */
    public void addModer(long moderId) {
        if (!moderList.contains(moderId)) {
            moderList.add(moderId);
        }
    }


    public List<Long> getModerList() {
        return Collections.unmodifiableList(moderList);
    }
}
