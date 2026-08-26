package com.example.demo;
//список с названием продукта и его ценной
public enum listassortiment {
    // 1. Константы с передачей ключа и названия в конструктор
    мальго("Malgo", 750),
    сан_Андресс("San_andreass", 3000),
    флоренс("Florens", 1500),
    априка("Aprik", 2000),
    моришка("Morishka", 1300),
    мурано("Murino", 500);

    // 2. Приватные поля для хранения данных
    private final String key;
    private final int price;

    // 3. Конструктор (всегда приватный по умолчанию)
    listassortiment(String key, int price) {
        this.key = key;
        this.price = price;
    }

    // 4. Геттеры для получения значений
    public String getKey() {
        return key;
    }

    public int getPrice() {
        return price;
    }
    public String getName(){return name();}
}