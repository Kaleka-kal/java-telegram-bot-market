package com.example.demo;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Button {
    //метод для создания кнопок с вводам текста в строку пользователя
    public static InlineKeyboardButton CommandButtonMaid(String ButtonText, String returntext) {
        InlineKeyboardButton CommandButton = InlineKeyboardButton.builder()
                .text(ButtonText)

                .switchInlineQueryCurrentChat(returntext)
                .build();
        return CommandButton;
    }
    // метод для создания кнопок с сигналом
    public static InlineKeyboardButton DataCommandButtonMaid(String ButtonText, String callbackId) {
        InlineKeyboardButton CommandButton = InlineKeyboardButton.builder()
                .text(ButtonText)
                .callbackData(callbackId)
                .build();
        return CommandButton;
    }
    // вывод товара в кнопки
    public static InlineKeyboardMarkup ListAssort() {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        InlineKeyboardRow rowInline = new InlineKeyboardRow();
        for (listassortiment obj : listassortiment.values()) {
            InlineKeyboardButton Button = com.example.demo.Button.DataCommandButtonMaid(obj.name(), obj.getKey());
            rowInline.add(Button);
            if (rowInline.size() == 2) {
                keyboardRows.add(rowInline);
                rowInline = new InlineKeyboardRow();}
        }
        if (!rowInline.isEmpty()) {
            keyboardRows.add(rowInline);
        }
        return new InlineKeyboardMarkup(keyboardRows);
    }
    //вывод всех клиентов, которые сделали заказ в кнопки
    public static InlineKeyboardMarkup ListClient(Clientlist clientlist) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        InlineKeyboardRow rowInline = new InlineKeyboardRow();
        for (Long var : clientlist.getClientList()) {
            rowInline.add(Button.DataCommandButtonMaid(var.toString(),var.toString()));
            if (rowInline.size() == 2) {
                keyboardRows.add(rowInline);
                rowInline = new InlineKeyboardRow();}
        }
        if (!rowInline.isEmpty()) {
            keyboardRows.add(rowInline);
        }
        return new InlineKeyboardMarkup(keyboardRows);
    }
    public static InlineKeyboardMarkup defaultMenuRow(DateModer dateModer,Long userId)
    {
        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(new ArrayList<>())
                .build();
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        InlineKeyboardRow defaultMenuRow = new InlineKeyboardRow();
        InlineKeyboardRow ModtMenuRow = new InlineKeyboardRow();

        // Стандартная дефолтная строка кнопок
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(Button.CommandButtonMaid("/menu", "/menu"));
        buttons.add(Button.CommandButtonMaid("/buy", "/buy"));
        buttons.add(Button.CommandButtonMaid("/assortiment", "/assortiment"));
        defaultMenuRow = new InlineKeyboardRow(buttons);
        keyboardRows.add(defaultMenuRow);
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(defaultMenuRow));
        if (dateModer.getModerList().contains(userId)) {
            // модерская строка кнопок
            List<InlineKeyboardButton> modbuttons = new ArrayList<>();
            modbuttons.add(Button.CommandButtonMaid("/moder", "/moder"));
            modbuttons.add(Button.CommandButtonMaid("/write", "/write"));
            ModtMenuRow = new InlineKeyboardRow(modbuttons);
            keyboardRows.add(ModtMenuRow);
            inlineKeyboardMarkup.setKeyboard(keyboardRows);
        }
        return inlineKeyboardMarkup;
    }
    //вывод всех модеров в кнопках
    public static InlineKeyboardMarkup ListMod (DateModer moderList) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        InlineKeyboardRow rowInline = new InlineKeyboardRow();
        for (Long obj : moderList.getModerList()) {
            InlineKeyboardButton Button = com.example.demo.Button.DataCommandButtonMaid(obj.toString(), obj.toString());
            rowInline.add(Button);
            if (rowInline.size() == 2) {
                keyboardRows.add(rowInline);
                rowInline = new InlineKeyboardRow();}
        }
        if (!rowInline.isEmpty()) {
            keyboardRows.add(rowInline);
        }
        //кнопки для moderhub
        return new InlineKeyboardMarkup(keyboardRows);
    }    public static InlineKeyboardMarkup Moderhub (DateModer moderList) {
         InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(new ArrayList<>())
                .build();
         List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
         InlineKeyboardRow defaultMenuRow = new InlineKeyboardRow();
         InlineKeyboardRow ModtMenuRow = new InlineKeyboardRow();

        // Стандартное дефолтное меню кнопок
         List<InlineKeyboardButton> buttons = new ArrayList<>();
         buttons.add(Button.CommandButtonMaid("/newmod", "/newmod"));
         buttons.add(Button.CommandButtonMaid("/unmod", "/unmod"));
         buttons.add(Button.CommandButtonMaid("/cancel", "/cancel"));
         defaultMenuRow = new InlineKeyboardRow(buttons);
         keyboardRows.add(defaultMenuRow);
         inlineKeyboardMarkup.setKeyboard(Collections.singletonList(defaultMenuRow));
         return inlineKeyboardMarkup;
    }
}