package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
//spring файл
@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public OkHttpClient okHttpClient() {
		// Чистый клиент без каких-либо прокси
		return new OkHttpClient.Builder().build();
	}

	@Bean
	public TelegramClient telegramClient(OkHttpClient okHttpClient) {
		String botToken = token.getToken();
		return new OkHttpTelegramClient(okHttpClient, botToken);
	}

	@Bean
	public MyBot myBot(UpdateConsumer updateConsumer, OkHttpClient okHttpClient) {
		return new MyBot(updateConsumer, okHttpClient);
	}
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper(); // Spring возьмет этот объект и подставит в ваш Backup
	}

}