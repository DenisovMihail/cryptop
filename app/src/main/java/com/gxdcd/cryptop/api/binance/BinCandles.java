package com.gxdcd.cryptop.api.binance;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class BinCandles {
    public BinCandle[] data = {};

    public static BinCandles FromJson(String json) {
        Gson gson = new Gson();
        try {
            Type type = new TypeToken<BinCandle[]>() {
            }.getType();
            BinCandle[] bin = gson.fromJson(json, type);
            BinCandles b = new BinCandles();
            b.data = bin;
            return b;
        } catch (JsonParseException e) {
            return FromError(e);
        }
    }

    // Создаем объект из перехваченного исключения
    public static BinCandles FromError(Exception e) {
        e.printStackTrace();
        return FromError(e.getMessage());
    }

    // Создаем объект из описания ошибки
    public static BinCandles FromError(String message) {
        BinCandles bin = new BinCandles();
        return bin;
    }
}
