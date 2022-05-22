package com.gxdcd.cryptop.api.cmc;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

// Класс для описания данных получаемых от API
// coinmarketcap, необходимых для работы приложения
// Используем для отображения списка лидирующих криптовалют
//
// https://coinmarketcap.com/api/documentation/v1/#operation/getV1CryptocurrencyListingsLatest
// https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=2
public class CmcItem /* implements Serializable */ {

    @Override
    public String toString() {
        // важное переопределение - используется для
        // упрощения построения списка идентификаторов
        // в методе CmcProvider.GetMetadata
        /*
            static CmcMetadata GetMetadata(List<CmcItem> list) {
                String ids = TextUtils.join(",", list);
        */
        return id.toString();
    }

    // Пример данных, получаемых от API
    /*

        "id": 1,
        "name": "Bitcoin",
        "symbol": "BTC",
        "slug": "bitcoin",
        "num_market_pairs": 9429,
        "date_added": "2013-04-28T00:00:00.000Z",
        "max_supply": 21000000,
        "circulating_supply": 19040268,
        "total_supply": 19040268,
        "platform": null,
        "cmc_rank": 1,
        "self_reported_circulating_supply": null,
        "self_reported_market_cap": null,
        "last_updated": "2022-05-15T07:43:00.000Z",
        "tags": [
            "mineable",
            "pow",
            "sha-256",
            "store-of-value",
            "state-channel",
            ...
        ],
        "quote": {
            "CmcQuote": {
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
        }
    */

    // Используем только необходимые данные
    // Так как поля данных java-класса и поля данных json-объекта
    // соответствуют по наименованию и типу данных, никаких
    // атрибутов для преобразования через библиотеку gson не требуется

    // "id": 1, - уникальный идентификатор coinmarketcap
    public Integer id;

    // "name": "Bitcoin",
    public String name;

    // "symbol": "BTC",
    public String symbol;

    // "date_added": "2013-04-28T00:00:00.000Z",
    public Date date_added;

    //"max_supply": 21000000,
    public Double max_supply;
    //"circulating_supply": 19040268,
    public Double circulating_supply;

    // "cmc_rank": 1, - ранг криптовалюты в рейтинге
    public Integer cmc_rank;

    // список тэгов может в дальнейшем использоваться
    // для классификации криптовалют в перечне
    // может содержать тэги mineable для добаваемых криптовалют
    // или например stablecoin для стабильных критповалют привязанных
    // например, к американскому доллару
    public List<String> tags;

    // Котировка криптовалюты по отнощению к заданной валюте
    // Данное поле десериализуется из json, полученного
    // от API но не используется напрямую
    private Hashtable<String, CmcQuote> quote;

    // Котировка криптовалюты по отношению к заданной валюте по умолчанию
    public CmcQuote getQuote() {
        return quote.get(CmcProvider.defaultQuoteSymbol);
    }

    // Котировка криптовалюты по отношению к заданной валюте по умолчанию
    public String getQuoteSymbol() {
        return CmcProvider.defaultQuoteSymbol;
    }

    public boolean isStablecoin() {
        return tags.contains("stablecoin");
    }

    public boolean isMineable() {
        return tags.contains("mineable");
    }
}