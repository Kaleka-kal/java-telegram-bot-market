package com.example.demo;
// детали заказа
class RequestDate {
    private static listassortiment  task;
    private static String address;
    private static int weight;

    public listassortiment getTask() {
        return this.task;
    }

    public void setTask(listassortiment task) {
        this.task = task;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String adres) {
        this.address = adres;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
