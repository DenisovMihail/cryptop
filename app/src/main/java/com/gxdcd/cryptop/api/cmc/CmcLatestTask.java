package com.gxdcd.cryptop.api.cmc;

import android.os.AsyncTask;
import android.util.Log;

import com.gxdcd.cryptop.api.Action;
import com.gxdcd.cryptop.api.ApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

// Вспомогательный класс асинхронной задачи для выполнения запроса
// к серверу и обратного вызова после завершения задачи
// через предоставленный объект CmcProvider.Action<CmcLatest>
class CmcLatestTask extends AsyncTask<Integer, Void, CmcLatest> {

    // сохраняем код, предоставленный вызывающей стороной
    Action<CmcLatest> finished;
    String apiKey;

    private CmcLatestTask(String apiKey, Action<CmcLatest> finished) {
        super();
        this.finished = finished;
        this.apiKey = apiKey;
    }

    // Запуск фоновой асинхронной задачи получения данных списка криптовалют
    // Оформлено в виде отдельного метода для упрощения StartCmcCombinedFetchTask
    static void Start(String apiKey, Integer limit, Action<CmcLatest> finished) {
        new CmcLatestTask(apiKey, finished).execute(limit);
    }

    CmcLatest GetLatest(Integer limit) {
        try {
            // Создаем соединение с сервером
            HttpURLConnection connection = CmcUtils.CreateConnection(
                    apiKey,
                    // https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=2&limit=2&convert=USD
                    CmcUtils.CreateUriBuilder("v1")
                            .appendPath("cryptocurrency")
                            .appendPath("listings")
                            .appendPath("latest")
                            // Параметры запроса
                            .appendQueryParameter("start", "1")
                            .appendQueryParameter("limit", limit.toString())
                            // Параметр "convert" позволяет при необходимости
                            // получать несколько котировок за один вызов.
                            .appendQueryParameter("convert", CmcProvider.defaultQuoteSymbol)
            );

            try {
                // Выполняем запрос к серверу
                int responseCode = connection.getResponseCode();
                // Логируем код ответа сервера - если вызов завершен
                // корректно, получаем responseCode HTTP_OK 200
                Log.i(CmcUtils.TAG, "Сервер ответил кодом: " + responseCode);

                // В случае корректного выполнения запроса,
                // преобразовываем поток данных от сервера в строку json
                // и затем создаем объект CmcLatest из полученных данных
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Преобразуем данные от сервера в строку
                    InputStream is = connection.getInputStream();
                    String content = ApiUtils.ConvertStreamToJson(is);
                    is.close();
                    // Создаем объект CmcMetadata из данных json
                    return CmcLatest.FromJson(content);
                } else
                    // Если сервер вернул код ошибки, создаем
                    // объект CmcLatest содержащий код ошибки
                    // Однако, помимо кода ошибки, вервер может (должен) вернуть
                    // её описание - его также можно использовать для детализации
                    return CmcLatest.FromError("Сервер вернул код ошибки: " + responseCode);
            } finally {
                // Гарантированно закрываем соединение с сервером
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            // В случае исключения возвращаем CmcLatest содержащий информацию об ошибке
            return CmcLatest.FromError(e);
        } catch (IOException e) {
            // то же самое
            return CmcLatest.FromError(e);
        }
    }

    @Override
    protected CmcLatest doInBackground(Integer... params) {
        // используем переданные параметры - максимальное
        // количество записей в возвращаемом списке криптовалют
        Integer limit = params[0];
        // выполняем запрос к серверу
        return GetLatest(limit);
    }

    @Override
    protected void onPostExecute(CmcLatest result) {
        super.onPostExecute(result);
        // выполняем код, предоставленный вызывающей стороной
        this.finished.execute(result);
    }
}
