package com.example.demo;
//файл с текстом, чтоб не сильно засорять файл с обработкой ответов и для быстрой переделки бота
public class message_text {
    public static final String assortiment = """
    Клубника:
    клубника Мальго = 750 рублей за кг 
    клубника Сан-Андресс = 3000 рублей за кг 
    клубника Флоренс = 1500 рублей за кг
    клубника Априка = 2000 рублей за кг
    клубника Моришку = 1300 рублей за кг
    клубника Мурано = 500 рублей за кг""";

    public static final String menu = """
    menu:
    /buy  -- сделать заказ
    /menu  -- вызвать меню
    /assortiment --узнать ассортимент""";
    public static final String numberfail = "Вы ввели не число, либо ввели его не правильно";

    public static final String BuyStep1 = "Выберите товар из списка\nвы можете закончить покупку по команде /cancel";
    public static final String BuyStep2 = "Напишите его вес в граммах\nвы можете закончить покупку по команде /cancel";
    public static final String BuyStep3 = "Напишите ваш адрес \nвы можете закончить покупку по команде /cancel";
    public static final String failMessage = "Я не расспознал что вы написали, попробуйте еще раз";
    public static final String ModerHub = "Выберите функцию";
    public static final String ClientNotFonund ="Ошибка: не удалось определить целевого клиента для отправки.";
    public static final String NewModMessage ="напишите id нового модератора";
    public static final String UnmodMessage ="Выберите участника, который будет исключен";
    public static final String WriteMessage = "Выберите клиента для отправки сообщения:";
    public static final String NotModer = "Вас походу сняли с поста(";

    public static String UpdateMessage(String text, Long chatId)
    {
        String updateMessage= String.format("\"Пришло сообщение: %s от ID: %d\\n\"", text, chatId);
        return updateMessage;
    } public static String SupprotMessage(String text)
    {
        String supprotMessage= String.format("Сообщение от поддержки:\n" + text);
        return supprotMessage;
    }
    public static String EmptyInlist (Long targetClientId){
       String emptyInList =String.format("Кнопка нажата, но клиент/модератор с ID: %d  не найден.",targetClientId);
        return emptyInList;
    }
    public static String SucccesMessage (Long targetClientId){
       String succcesMessage =String.format("Сообщение успешно отправлено клиенту " + targetClientId);
        return succcesMessage;
    }
    public static String FailMessage (Long targetClientId){
       String failMessage =String.format("Не удалось отправить сообщение клиенту: " + targetClientId);
        return failMessage;}

    public static String EndBuy(String task, int weight, String address,int price)
    {
        float weightf = (float) weight / 1000;
        price = price * weight / 1000;
        String endbuy = String.format(" заказали %s %2f кг на адрес %s\n цена:%d",task,weightf,address,price);
        return endbuy;
    }
    public static String PersonalymessageConnect(Long var){
        String connectModer =String.format("\"Вы начали диалог с клиентом (ID: \" %d \"). Введите текст сообщения, которое хотите ему отправить:\"", var);
        return connectModer;
    }public static String GlobalmessageConnect(Long var){
        String connectModer =String.format("Модератор %d выбрал клиента ", var);
        return connectModer;
    }
}
