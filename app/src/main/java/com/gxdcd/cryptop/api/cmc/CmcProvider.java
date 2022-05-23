package com.gxdcd.cryptop.api.cmc;

import android.content.Context;

import com.gxdcd.cryptop.api.Action;
import com.gxdcd.cryptop.api.ApiUtils;

// Провайдер и агрегатор данных полученных от АПИ coinmarketcap
// Предоставляет доступ к данным на уровне всего приложения
// и методы для инициализации и запуска получения данных
public class CmcProvider {

    // Обозначение валюты котрировки по умолчанию
    // USD в принципе является значением по умолчанию,
    // хотя в документации это не отражено
    private static String defaultQuoteSymbol = "USDT";

    public static String getDefaultQuoteSymbol() {
        return defaultQuoteSymbol;
    }

    private static ApiUtils.Watcher cmc_api_key_watcher;
    private static ApiUtils.Watcher quote_symbol_watcher;

    final static String cmc_api_key = "cmc_api_key";
    final static String quote_symbol = "quote_symbol";

    public static Integer getCmcPageSize() {
        return 100;
    }

    public static void hookQuoteSymbolChange(Context context, Action<String> changed)
    {
        if (quote_symbol_watcher == null) {
            quote_symbol_watcher = new ApiUtils.Watcher(quote_symbol, q -> {
                defaultQuoteSymbol = ApiUtils.getPreferenceValue(context, quote_symbol, defaultQuoteSymbol);
                changed.execute(defaultQuoteSymbol);
            });
            ApiUtils.registerWatcher(context, quote_symbol_watcher);
        }
    }

    public static void onApiKeyReady(Context context, Action<String> ready, Action<String> missing) {
        // Watcher используется для отслеживания изменения настроек
        // экземпляр необходимо сохранить как статическое поле или
        // любую другую внешнюю переменную чтоб спрятать его от уборки и уничтожения
        // - сохраняем как статическое поле данного класса
        if (cmc_api_key_watcher == null) {
            cmc_api_key_watcher = new ApiUtils.Watcher(cmc_api_key, ready);
            ApiUtils.registerWatcher(context, cmc_api_key_watcher);
        }
        String cmc_key = ApiUtils.getPreferenceValue(context, cmc_api_key, "");
        if (cmc_key != null && !"".equals(cmc_key)) {
            ready.execute(cmc_key); // = start(api_key)
        } else {
            missing.execute("Введите ключ API в настройках");
        }
    }

    public static void StartLoading(String apiKey, Integer page, Action<String> complete) {
        StartLoading(apiKey, page, getCmcPageSize(), complete);
    }

    // Запуск фоновой асинхронной задачи получения полных данных по криптовалютам
    public static void StartLoading(String apiKey, Integer page, Integer limit, Action<String> complete) {
        // Выполняем асинхронную задачу получения данных списка криптовалют
        // с сервера coinmarketcap, после завершения коротой будет вызван
        // код с параметром (CmcLatest latest), используя который вызываем
        // метод получения дополнительных данных, содержащих информацию о логотипе
        // каждой криптовалюты, включенной в список, встроенный в объект latest
        CmcLatestTask.Start(apiKey, limit,
                (CmcLatest latest) -> {
                    Latest = latest;
                    if (latest.hasError()) {
                        // если данные содержат ошибку, выпускаем сведения о ней выше на уровень
                        complete.execute(String.format(
                                "Ошибка загрузки данных coinmarketcap: %s", latest.getErrorMessage()));
                    } else {
                        // После получения ответа сервера, содержащего дополнительные данные
                        // вызывается код с параметром (CmcMetadata metadata), в котором данные
                        // от обоих запросов объединяются и отправляются для дальнейшей
                        // обработки в методе CmcLoadingComplete finished
                        CmcMetadataTask.Start(apiKey, latest,
                                (CmcMetadata metadata) -> {
                                    Metadata = metadata;
                                    // выполняем код, предоставленный вызывающей стороной
                                    complete.execute(metadata.hasError() ? String.format(
                                            "Ошибка загрузки метаданных coinmarketcap: %s", metadata.getErrorMessage()) : null);
                                });
                    }
                });
    }

    // Изначально данные отсутствуют - по умолчанию объекты с ошибкой
    public static CmcLatest Latest = CmcLatest.FromError("Данные отсутствуют");
    public static CmcMetadata Metadata = CmcMetadata.FromError("Данные отсутствуют");
}