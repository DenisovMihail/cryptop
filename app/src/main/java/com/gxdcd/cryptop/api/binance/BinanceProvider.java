package com.gxdcd.cryptop.api.binance;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gxdcd.cryptop.api.Action;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class BinanceProvider {
    public static void Load(BinParams params, Action<BinCandles> finished) {
        new BinKlinesTask(finished).execute(params);
    }
}

// Общие методы для всех задач обращения к АПИ
class BinUtils {
    // Тэг для отображения в логах
    static final String TAG = BinanceProvider.class.getSimpleName();
    // Базовый таймаут подключения
    static int TIMEOUT_VALUE = 3 * 1000; // 3 секунды

    // Создаем соединение с сервером по заданному с помощью билдера алгоритму
    @NonNull
    static HttpURLConnection CreateConnection(Uri.Builder builder) throws IOException {
        String uri = builder.build().toString();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT_VALUE);
        connection.setReadTimeout(TIMEOUT_VALUE);
        Log.i(TAG, "Строка запроса к серверу: " + connection.getURL());
        return connection;
    }

    // Создаем базовый билдер - при ображении к АПИ эта часть будет неизменной
    @NonNull
    static Uri.Builder CreateUriBuilder(String endpoint) throws IOException {
        // Адрес запроса
        return new Uri.Builder().scheme("https")
                .authority("api.binance.com")
                .appendPath("api")
                .appendPath("v3")
                .appendPath(endpoint);
    }

    // Преобразуем потоковые данные от сервера в строку json
    static String ConvertStreamToJson(InputStream stream) {
        // https://stackoverflow.com/questions/6829801/httpurlconnection-setconnecttimeout-has-no-effect
        // можно использовать что-то похожее с [StringBuilder answer] но код со сканнером короче и проще
        Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}

// Вспомогательный класс асинхронной задачи для выполнения запроса
// к серверу и обратного вызова после завершения задачи
// через предоставленный объект CmcProvider.Action<CmcLatest>
class BinKlinesTask extends AsyncTask<BinParams, Void, BinCandles> {

    // сохраняем код, предоставленный вызывающей стороной
    Action<BinCandles> finished;

    BinKlinesTask(Action<BinCandles> finished) {
        super();
        this.finished = finished;
    }

    // Запуск фоновой асинхронной задачи получения данных списка криптовалют
    // Оформлено в виде отдельного метода для упрощения StartCmcCombinedFetchTask
    static void Start(BinParams params, Action<BinCandles> finished) {
        new BinKlinesTask(finished).execute(params);
    }

    static BinCandles GetKlines(BinParams params) {
        try {
            // Создаем соединение с сервером
            HttpURLConnection connection = BinUtils.CreateConnection(
                    BinUtils.CreateUriBuilder("klines")
                            .appendQueryParameter("symbol", params.symbol)
                            .appendQueryParameter("limit", params.limit.toString())
                            .appendQueryParameter("interval", params.interval.getIntervalId())
            );

            try {
                // Выполняем запрос к серверу
                int responseCode = connection.getResponseCode();
                // Логируем код ответа сервера - если вызов завершен
                // корректно, получаем responseCode HTTP_OK 200
                Log.i(BinUtils.TAG, "Сервер ответил кодом: " + responseCode);

                // В случае корректного выполнения запроса,
                // преобразовываем поток данных от сервера в строку json
                // и затем создаем объект CmcLatest из полученных данных
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Преобразуем данные от сервера в строку
                    InputStream is = connection.getInputStream();
                    String content = BinUtils.ConvertStreamToJson(is);
                    is.close();
                    // Создаем объект CmcMetadata из данных json
                    return BinCandles.FromJson(content);
                } else
                    // Если сервер вернул код ошибки, создаем
                    // объект CmcLatest содержащий код ошибки
                    // Однако, помимо кода ошибки, вервер может (должен) вернуть
                    // её описание - его также можно использовать для детализации
                    return BinCandles.FromError("Сервер вернул код ошибки: " + responseCode);
            } finally {
                // Гарантированно закрываем соединение с сервером
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            // В случае исключения возвращаем CmcLatest содержащий информацию об ошибке
            return BinCandles.FromError(e);
        } catch (IOException e) {
            // то же самое
            return BinCandles.FromError(e);
        }
    }

    protected BinCandles doInBackground(BinParams... params) {
        return GetKlines(params[0]);
    }

    @Override
    protected void onPostExecute(BinCandles result) {
        super.onPostExecute(result);
        this.finished.execute(result);
    }
}