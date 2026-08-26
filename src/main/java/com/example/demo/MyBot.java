package com.example.demo;

import okhttp3.OkHttpClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
// настройки самого бота
public class MyBot implements SpringLongPollingBot {

    private final UpdateConsumer updateConsumer;
    private final OkHttpClient okHttpClient; // Внедряем настроенный OkHttpClient

    // Добавляем okHttpClient в конструктор (Spring автоматически подставит ваш Bean)
    public MyBot(UpdateConsumer updateConsumer, OkHttpClient okHttpClient) {
        this.updateConsumer = updateConsumer;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public String getBotToken() {
        return token.getToken();
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }

}

