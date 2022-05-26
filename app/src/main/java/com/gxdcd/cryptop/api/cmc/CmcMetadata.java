package com.gxdcd.cryptop.api.cmc;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.Hashtable;

// Класс для описания данных получаемых от API
// coinmarketcap, необходимых для работы приложения
// Используем для получения дополнительных данных по криптовалютам
// в нашем случае, интерес представляет логотип кпитовалюты -
// адрес по которму он может быть получен
//
// https://coinmarketcap.com/api/documentation/v1/#operation/getV2CryptocurrencyInfo
// https://pro-api.coinmarketcap.com/v2/cryptocurrency/info?id=1,1027&aux=logo
public class CmcMetadata {

    private CmcStatus status;
    public Hashtable<Integer, CmcMeta> data = null;

    // Показывает, есть ли ошибка в данных
    public boolean hasError() {
        return status != null && status.error_message != null;
    }

    // Описание ошибки, если она есть
    public String getErrorMessage() {
        return status != null ? status.error_message : "";
    }

    // Десериализуем объект из json-данных
    public static CmcMetadata FromJson(String json) {
        Gson gson = new Gson();
        try {
            // Попытка воссоздать объект из json-данных
            CmcMetadata obj = gson.fromJson(json, CmcMetadata.class);
            // Если ошибка уже присутствует в json-объекте,
            // передаем ее в новый объект котрый собственно и возвращаем
            if (obj.hasError()) {
                // для debug режима проверяем что data не null
                // в связи с тем что это может являться ошибкой
                // вылетающей в CoinRecyclerAdapter.getMeta
                assert (obj.data != null);
                return FromError(obj.status.error_message);
            }
            // Всё ок - возвращаем воссозданный объект
            return obj;
        } catch (JsonParseException e) {
            // В случае ошибки при десериализации - возвращаем объект созданный из ошибки
            return FromError(e);
        }
    }

    // Создаем объект из перехваченного исключения
    public static CmcMetadata FromError(Exception e) {
        e.printStackTrace();
        return FromError(e.getMessage());
    }

    // Создаем объект из описания ошибки
    public static CmcMetadata FromError(String message) {
        Log.i(CmcMetadata.class.getName(), message);
        CmcMetadata cmc = new CmcMetadata();
        CmcStatus status = new CmcStatus();
        status.error_message = message;
        cmc.status = status;
        return cmc;
    }
}