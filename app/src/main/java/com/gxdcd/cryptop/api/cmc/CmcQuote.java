package com.gxdcd.cryptop.api.cmc;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CmcQuote {

    // Пример данных, получаемых от API
    /*
        "USD": {
            "price": 29892.721662445376,
            "volume_24h": 27457474376.14198,
            "volume_change_24h": -24.109,
            "percent_change_1h": 0.45449173,
            "percent_change_24h": 1.42050675,
            "percent_change_7d": -13.94175975,
            "percent_change_30d": -25.50696239,
            "percent_change_60d": -24.35585713,
            "percent_change_90d": -29.16920752,
            "market_cap": 569165431702.3655,
            "market_cap_dominance": 44.4731,
            "fully_diluted_market_cap": 627747154911.35,
            "last_updated": "2022-05-15T07:43:00.000Z"
        }
    */

    // В принципе, используем только необходимые данные
    // Но оставляем некоторые для возможности использования
    //
    // Так как некоторые поля данных java-класса и поля данных json-объекта
    // не соответствуют по наименованию,
    // используем атрибуты SerializedName библиотеки gson
    // для преобразования имён json-объекта в имена java-класса

    // "price": 29892.721662445376,
    public Double price;

    //
    // Объем и доля рынка
    //

    // "market_cap_dominance": 44.4731,
    @SerializedName("market_cap_dominance")
    public Double marketCapDominance;

    // "market_cap": 569165431702.3655,
    @SerializedName("market_cap")
    public Double marketCap;

    //
    // Объемы торгов
    //

    // "volume_24h": 27457474376.14198,
    @SerializedName("volume_24h")
    public Double volume24h;

    // "volume_change_24h": -24.109,
    @SerializedName("volume_change_24h")
    public Double volumeChange24h;

    //
    // Процентное изменение цены
    //

    // "percent_change_1h": 0.45449173,
    @SerializedName("percent_change_1h")
    public Double percentChange1h;

    // "percent_change_24h": 1.42050675,
    @SerializedName("percent_change_24h")
    public Double percentChange24h;

    // "percent_change_7d": -13.94175975,
    @SerializedName("percent_change_7d")
    public Double percentChange7d;

    // "percent_change_30d": -25.50696239,
    @SerializedName("percent_change_30d")
    public Double percentChange30d;

    // "percent_change_60d": -24.35585713,
    @SerializedName("percent_change_60d")
    public Double percentChange60d;

    // "percent_change_90d": -29.16920752,
    @SerializedName("percent_change_90d")
    public Double percentChange90d;

}
