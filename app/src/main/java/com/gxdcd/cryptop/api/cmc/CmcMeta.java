package com.gxdcd.cryptop.api.cmc;

// Класс для описания данных получаемых от API
// coinmarketcap, необходимых для работы приложения
// Используем для отображения списка лидирующих криптовалют
//
// https://coinmarketcap.com/api/documentation/v1/#operation/getV2CryptocurrencyInfo
// https://pro-api.coinmarketcap.com/v2/cryptocurrency/info?id=1,1027&aux=logo
public class CmcMeta {

    // Пример данных, получаемых от API
    /*

    "id": 1,
    "name": "Bitcoin",
    "symbol": "BTC",
    "category": "coin",
    "slug": "bitcoin",
    "logo": "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
    "subreddit": "bitcoin",
    "tag-names": [
        "Mineable",
        "PoW",
        "SHA-256",
        "Store Of Value",
        "State Channel",
        ...
    ],
    "tag-groups": [
        "OTHERS",
        "ALGORITHM",
        "ALGORITHM",
        "CATEGORY",
        ...
    ],
    "twitter_username": "",
    "is_hidden": 0,
    "date_launched": null,
    "contract_address": [],
    "self_reported_circulating_supply": null,
    "self_reported_tags": null,
    "self_reported_market_cap": null
    */

    // Используем только необходимые данные id + logo
    // Так как поля данных java-класса и поля данных json-объекта
    // соответствуют по наименованию и типу данных, никаких
    // атрибутов для преобразования через библиотеку gson не требуется

    // "id": 1, - уникальный идентификатор coinmarketcap
    public Integer id;

    // "logo": "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
    // адрес изображения логотипа криптовалюты, хранящийся на сервере
    public String logo;
}