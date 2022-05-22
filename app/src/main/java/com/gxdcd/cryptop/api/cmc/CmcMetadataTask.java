package com.gxdcd.cryptop.api.cmc;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.gxdcd.cryptop.api.Action;
import com.gxdcd.cryptop.api.ApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

// Вспомогательный класс асинхронной задачи для выполнения запроса
// к серверу и обратного вызова после завершения задачи
// через предоставленный объект CmcProvider.Action<CmcMetadata>
//
// https://coinmarketcap.com/api/documentation/v1/#operation/getV2CryptocurrencyInfo
// https://pro-api.coinmarketcap.com/v2/cryptocurrency/info?id=1,1027&aux=logo
class CmcMetadataTask extends AsyncTask<CmcLatest, Void, CmcMetadata> {

    // сохраняем код, предоставленный вызывающей стороной
    Action<CmcMetadata> finished;
    String apiKey;

    private CmcMetadataTask(String apiKey, Action<CmcMetadata> finished) {
        super();
        this.finished = finished;
        this.apiKey = apiKey;
    }

    // Запуск фоновой асинхронной задачи получения дополнительных данных по криптовалютам
    // Оформлено в виде отдельного метода для упрощения StartCmcCombinedFetchTask
    static void Start(String apiKey, CmcLatest latest, Action<CmcMetadata> finished) {
        new CmcMetadataTask(apiKey, finished).execute(latest);
    }

    CmcMetadata GetMetadata(List<CmcItem> list) {
        // проверяем на случай если данные в списке отсутствуют или он не предоставлен
        if (list == null || list.isEmpty()) {
            return CmcMetadata.FromError(
                    "Для выполнения запроса метаданных не предоставлены параметры");
        }

        try {
            // Получаем список идентификаторов, используемых
            // в параметрах запроса для получения метаданных от API
            // При этом вызывается метод toString в случае преобразования
            // каждого элемента списка в строку
            String ids = TextUtils.join(",", list);
            // Создаем соединение с сервером
            HttpURLConnection connection = CmcUtils.CreateConnection(
                    apiKey,
                    CmcUtils.CreateUriBuilder("v2")
                            .appendPath("cryptocurrency")
                            .appendPath("info")
                            // Параметры запроса
                            .appendQueryParameter("id", ids)
                            .appendQueryParameter("aux", "logo")
            );

            try {
                // Выполняем запрос к серверу
                int responseCode = connection.getResponseCode();
                // Логируем код ответа сервера - если вызов завершен
                // корректно, получаем responseCode HTTP_OK 200
                Log.i(CmcUtils.TAG, "Сервер ответил кодом: " + responseCode);

                // В случае корректного выполнения запроса,
                // преобразовываем поток данных от сервера в строку json
                // и затем создаем объект CmcMetadata из полученных данных
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Преобразуем данные от сервера в строку
                    InputStream is = connection.getInputStream();
                    String content = ApiUtils.ConvertStreamToJson(is);
                    is.close();
                    // Создаем объект CmcMetadata из данных json
                    return CmcMetadata.FromJson(content);
                } else
                    // Если сервер вернул код ошибки, создаем
                    // объект CmcMetadata содержащий код ошибки
                    return CmcMetadata.FromError("Сервер вернул код ошибки: " + responseCode);
            } finally {
                // Гарантированно закрываем соединение с сервером
                connection.disconnect();
            }

        } catch (MalformedURLException e) {
            // В случае исключения возвращаем CmcMetadata содержащий информацию об ошибке
            return CmcMetadata.FromError(e);
        } catch (IOException e) {
            // то же самое
            return CmcMetadata.FromError(e);
        }
    }

    @Override
    protected CmcMetadata doInBackground(CmcLatest... params) {
        // используем переданные параметры - список криптовалют
        CmcLatest latest = params[0];
        List<CmcItem> list = latest.data;
        // выполняем запрос к серверу
        return GetMetadata(list);
    }

    @Override
    protected void onPostExecute(CmcMetadata result) {
        super.onPostExecute(result);
        // выполняем код, предоставленный вызывающей стороной
        this.finished.execute(result);
    }
}
