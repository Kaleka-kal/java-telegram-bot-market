package com.example.demo;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import tools.jackson.databind.util.Converter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    //переменные, конкретно по каждому проходится не буду, тк у них итак имена звонкие
    private final TelegramClient telegramClient;
    private final Map<Long, RequestStateBuy> userStates = new ConcurrentHashMap<>();
    private final Map<Long, List<RequestDate>> userDataMap = new ConcurrentHashMap<>();
    private final Map<Long, RequestDate> currentDraft = new ConcurrentHashMap<>();
    private final Clientlist clientlist;
    private final DateModer dateModer;

    public UpdateConsumer(TelegramClient telegramClient, Clientlist clientlist, DateModer dateModer, Backup backup) {
        this.telegramClient = telegramClient;
        this.clientlist = clientlist;
        this.dateModer = dateModer;
    }
    // обработка апдейтов
    @Override
    public void consume(Update update) {
        // 1. ОБРАБОТКА ТЕКСТОВЫХ СООБЩЕНИЙ
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            //логирование все сообщения в строку и создаем профиль(под этим я имею ввиду привязку к этапу диалога и деталей заказа)
            System.out.printf(message_text.UpdateMessage(text,chatId));
            RequestDate requestdate = currentDraft.computeIfAbsent(chatId, k -> new RequestDate());
            RequestStateBuy stateMessage = userStates.getOrDefault(chatId, RequestStateBuy.WAIT_MENU);
            //создание дефолтных кнопок и сообщений
            InlineKeyboardMarkup markupInline = null;
            String responseText = message_text.failMessage;

            // обработка этапа диалога
            switch (stateMessage) {
                //этап меню
                case WAIT_MENU:
                    //проверка наличия "/assortiment" в тексте,оно может быть в любой его части, но .contains его засекет
                    if (text.contains("/assortiment")) {
                        //делаем пометку, чтоб не собирать текстовое сообщение
                        responseText = "None";
                        //сборка фото с текстом
                        SendPhoto sendPhoto = SendPhoto.builder()
                                .chatId(chatId.toString())
                                .photo(new InputFile("https://telegra.ph/file/8750e0ea225901e6433e5.jpg"))
                                .build();
                        sendPhoto.setCaption(message_text.assortiment);
                        //отправка
                        try {
                            telegramClient.execute(sendPhoto);
                        } catch (TelegramApiException e) {
                            System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
                        }
                    } else if (text.contains("/menu") || text.contains("/help") || text.contains("/start")) {
                        responseText = message_text.menu;
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                    } else if (text.contains("/buy")) {
                        //меняем статус
                        userStates.put(chatId, RequestStateBuy.WAIT_TASK);
                        responseText = message_text.BuyStep1;
                        markupInline = Button.ListAssort();
                    } else if (text.contains("/write"))
                    {
                        //проверка на модератора
                        if (dateModer.getModerList().contains(userId)) {
                            responseText = message_text.WriteMessage;
                            InlineKeyboardMarkup inlineMarkup = Button.ListClient(clientlist);
                            markupInline = inlineMarkup;
                        }
                        responseText = message_text.NotModer;
                    } else if (text.contains("/moder")) {
                        if (dateModer.getModerList().contains(userId)) {
                            responseText = message_text.ModerHub;
                            markupInline = Button.Moderhub(dateModer);
                            userStates.put(chatId, RequestStateBuy.WAIT_MODERHUB);
                        }
                        responseText = message_text.NotModer;

                    }
                    else if (text.contains("/cancel")) {
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        responseText = message_text.menu;
                    }else {
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                    }
                    break;

                case WAIT_TASK:
                    if (text.contains("/cancel")) {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        responseText = message_text.menu;
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                    } else {
                        responseText = message_text.BuyStep1;
                        markupInline = Button.ListAssort();
                    }
                    break;
                case WAIT_WEIGHT:
                    if (text.contains("/cancel")) {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        responseText = message_text.menu;
                        markupInline = Button.defaultMenuRow(dateModer,userId);

                    } else if (text.contains("/start") || text.contains("/buy") || text.contains("/assortiment") || text.contains("/menu") || text.contains("/help")) {
                        responseText = message_text.BuyStep2;
                    } else {
                        try {
                            int number = Integer.parseInt(text);
                            userStates.put(chatId, RequestStateBuy.WAIT_ADDRESS);
                            requestdate.setWeight(number);
                            responseText = message_text.BuyStep3;
                            System.out.println(userId + text);
                        } catch (NumberFormatException e) {
                            responseText = message_text.BuyStep2;
                            SendMessage message = SendMessage.builder()
                                    .chatId(chatId.toString())
                                    .text(message_text.numberfail)
                                    .build();
                            try {
                                telegramClient.execute(message);
                            } catch (TelegramApiException b) {
                                responseText = message_text.numberfail;
                            }
                        }

                    }
                    break;

                case WAIT_ADDRESS:
                    if (text.contains("/cancel")) {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                        responseText = message_text.menu;
                    } else if (text.contains("/buy") || text.contains("/start") || text.contains("/assortiment") || text.contains("/menu") || text.contains("/help")) {
                        responseText = message_text.BuyStep3;
                    } else {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        requestdate.setAddress(text);
                        clientlist.addClient(chatId);
                        responseText = "Вы" + message_text.EndBuy(requestdate.getTask().getName(), requestdate.getWeight(), requestdate.getAddress(), requestdate.getTask().getPrice());
                        System.out.println(message_text.EndBuy(requestdate.getTask().getName(), requestdate.getWeight(), requestdate.getAddress(), requestdate.getTask().getPrice()));
                        for (Long moder : dateModer.getModerList()) {
                            SendMessage message = SendMessage.builder()
                                    .chatId(moder)
                                    .text("Пришло сообщение:\n" + message_text.EndBuy(requestdate.getTask().getName(),
                                            requestdate.getWeight(), requestdate.getAddress(), requestdate.getTask().getPrice()) + "\n           " + chatId)
                                    .build();
                            try {
                                telegramClient.execute(message);
                            } catch (TelegramApiException e) {
                                System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
                            }
                        }
                        userDataMap.computeIfAbsent(chatId, k -> Collections.synchronizedList(new ArrayList<>())).add(requestdate);
                        currentDraft.remove(chatId);
                    }
                    break;
                case WAIT_WRITERMOD:
                    if (text.contains("/cancel")) {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        responseText = message_text.menu;
                        markupInline = Button.defaultMenuRow(dateModer,userId);

                    } else {
                        // Получаем привязанного клиента через исправленный getChatForModer
                        Long targetClientId = dateModer.getChatForModer(chatId);
                        if (targetClientId != null) {
                            SendMessage clientMessage = SendMessage.builder()
                                    .chatId(String.valueOf(targetClientId))
                                    .text(message_text.SupprotMessage(text))
                                    .build();
                            try {
                                telegramClient.execute(clientMessage);
                                responseText = message_text.SucccesMessage(targetClientId);
                            } catch (TelegramApiException e) {
                                responseText = message_text.FailMessage(targetClientId);
                            }
                            // Удаляем связь, так как сообщение отправлено
                            dateModer.removeModerChat(chatId);
                        } else {
                            responseText = message_text.ClientNotFonund;
                        }
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                    }
                    break;
                case WAIT_MODERHUB:
                    if (text.contains("/newmod")) {
                        responseText = message_text.NewModMessage;
                        userStates.put(chatId, RequestStateBuy.WAIT_MODERHUB_NEWMOD);
                    } else if (text.contains("/unmod")) {
                        responseText = message_text.ModerHub;
                        markupInline = Button.ListMod(dateModer);

                    }
                    else if (text.contains("/cancel")) {
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        responseText = message_text.menu;
                        markupInline = Button.defaultMenuRow(dateModer,userId);

                    }else
                    {
                        responseText = message_text.ModerHub;
                        markupInline = Button.Moderhub(dateModer);
                    }
                    break;

                case WAIT_MODERHUB_NEWMOD:
                    try {
                        // Конвертируем текст в Long (ID модератора) и добавляем
                        dateModer.addModer(Long.parseLong(text));
                        responseText = message_text.menu;
                        System.out.println(Long.valueOf(text));
                        userStates.put(chatId, RequestStateBuy.WAIT_MENU);
                        markupInline = Button.defaultMenuRow(dateModer,userId);
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + stateMessage);
            }

            // сборка сообщения
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(responseText)
                    .build();

            // добавление кнопок, если они заданы
            if (markupInline != null) {
                message.setReplyMarkup(markupInline);
            }
            // отправка сообщения, исключая случай ввода "/assortiment", где будет отправляться фото
            if (!responseText.equals("None")) {
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
                }
            }

            if (clientlist.getClientList().contains(Map.entry(chatId, true))) {
                for (Long moder : dateModer.getModerList()) {
                    SendMessage Text = SendMessage.builder()
                            .chatId(moder)
                            .text(text)
                            .build();
                    try {
                        telegramClient.execute(Text);
                    } catch (TelegramApiException e) {
                        System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
                    }
                }
            }
            // 2. ОБРАБОТКА НАЖАТИЙ НА КНОПКИ (CALLBACK QUERY)
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            // 1. Проверяем совпадение с ассортиментом
            boolean isAssortment = false;
            for (listassortiment var : listassortiment.values()) {
                if (callbackData.equals(var.getKey())) {
                    Asssorrtimentlist(chatId, messageId, var);
                    isAssortment = true;
                    break;
                }
            }

            // 2. Если это не ассортимент, пробуем обработать как ID клиента или модератора
            if (!isAssortment) {
                try {
                    long targetClientId = Long.parseLong(callbackData);

                    if (targetClientId == 7609386124L || dateModer.getModerList().contains(targetClientId)) {
                        // Логика для модераторов
                        moderhub(chatId, messageId, targetClientId);
                    } else if (clientlist.getClientList().contains(targetClientId)) {
                        // Логика для обычных клиентов
                        WriteButton(chatId, targetClientId);
                    } else {
                        System.out.println(message_text.EmptyInlist(targetClientId));
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Неизвестный формат callbackData: " + callbackData);
                }
            }
        }
    }
    //обработка сигнала с кнопок
    void Asssorrtimentlist(long chatId, int messageId, listassortiment var) {
        userStates.put(chatId, RequestStateBuy.WAIT_WEIGHT);
        RequestDate requestdate = currentDraft.computeIfAbsent(chatId, k -> new RequestDate());
        requestdate.setTask(var);
        EditMessageText messageEdit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("вы выбрали " + var.name())
                .build();
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(message_text.BuyStep2)
                .build();
        System.out.println("выбрали " + var.name());
        try {
            telegramClient.execute(messageEdit);
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }    void moderhub(long chatId, int messageId, Long var) {
        userStates.put(chatId, RequestStateBuy.WAIT_MODERHUB);
        dateModer.removeModer(var);
        EditMessageText messageEdit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("вы удалили " + var)
                .build();
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(message_text.ModerHub)
                .build();
        InlineKeyboardMarkup markupInline = Button.Moderhub(dateModer);
        message.setReplyMarkup(markupInline);
        try {
            telegramClient.execute(messageEdit);
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void WriteButton(long chatId, long var) {
        dateModer.setModerForChat(chatId, var);
        userStates.put(chatId, RequestStateBuy.WAIT_WRITERMOD);
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message_text.PersonalymessageConnect(var))
                .build();
        System.out.println(message_text.GlobalmessageConnect(var));
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения модератору: " + e.getMessage());
        }
    }
}