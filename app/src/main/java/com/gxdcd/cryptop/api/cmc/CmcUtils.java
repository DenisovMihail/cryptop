package com.gxdcd.cryptop.api.cmc;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

// Общие методы для всех задач обращения к АПИ
class CmcUtils {
    // Тэг для отображения в логах
    static final String TAG = CmcProvider.class.getSimpleName();
    // Базовый таймаут подключения
    static int TIMEOUT_VALUE = 3 * 1000; // 3 секунды

    // Создаем соединение с сервером по заданному с помощью билдера алгоритму
    @NonNull
    public static HttpURLConnection CreateConnection(String apiKey, Uri.Builder builder) throws IOException {
        /*
        GET /v1/cryptocurrency/listings/latest?start=1&limit=2&convert=USD HTTP/1.1
        Host: https://pro-api.coinmarketcap.com
        X-CMC_PRO_API_KEY: efcbfb84-36dc-4da5-9d5e-7ff7ef800ff7
        */
        String uri = builder.build().toString();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT_VALUE);
        connection.setReadTimeout(TIMEOUT_VALUE);
        connection.addRequestProperty("X-CMC_PRO_API_KEY", apiKey);
        Log.i(TAG, "Строка запроса к серверу: " + connection.getURL());
        return connection;
    }

    // Создаем базовый билдер - при ображении к АПИ эта часть будет неизменной
    @NonNull
    public static Uri.Builder CreateUriBuilder(String version) throws IOException {
        // Адрес запроса
        return new Uri.Builder().scheme("https")
                .authority("pro-api.coinmarketcap.com")
                .appendPath(version);
    }
}
