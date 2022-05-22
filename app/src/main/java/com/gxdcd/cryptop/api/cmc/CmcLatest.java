package com.gxdcd.cryptop.api.cmc;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.List;

// Класс для описания данных получаемых от API
// coinmarketcap, необходимых для работы приложения
// Используем для отображения списка лидирующих криптовалют
//
// https://coinmarketcap.com/api/documentation/v1/#operation/getV1CryptocurrencyListingsLatest
// https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=2
public class CmcLatest {

    /*

    https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest

    {
        "status": {
            "timestamp": "2022-05-21T15:01:34.731Z",
            "error_code": 0,
            "error_message": null,
            "elapsed": 36,
            "credit_count": 1,
            "notice": null,
            "total_count": 10064
        },
        "data": [
            {
                "id": 1,
                "name": "Bitcoin",
                "symbol": "BTC",
                "slug": "bitcoin",
                "num_market_pairs": 9462,
                "date_added": "2013-04-28T00:00:00.000Z",
                "tags": [
                    "mineable",
                    "pow",
                    "sha-256",
                    "store-of-value",
                    "state-channel",
                    "coinbase-ventures-portfolio",
                    "three-arrows-capital-portfolio",
                    "polychain-capital-portfolio",
                    "binance-labs-portfolio",
                    "blockchain-capital-portfolio",
                    "boostvc-portfolio",
                    "cms-holdings-portfolio",
                    "dcg-portfolio",
                    "dragonfly-capital-portfolio",
                    "electric-capital-portfolio",
                    "fabric-ventures-portfolio",
                    "framework-ventures-portfolio",
                    "galaxy-digital-portfolio",
                    "huobi-capital-portfolio",
                    "alameda-research-portfolio",
                    "a16z-portfolio",
                    "1confirmation-portfolio",
                    "winklevoss-capital-portfolio",
                    "usv-portfolio",
                    "placeholder-ventures-portfolio",
                    "pantera-capital-portfolio",
                    "multicoin-capital-portfolio",
                    "paradigm-portfolio"
                ],
                "max_supply": 21000000,
                "circulating_supply": 19045643,
                "total_supply": 19045643,
                "platform": null,
                "cmc_rank": 1,
                "self_reported_circulating_supply": null,
                "self_reported_market_cap": null,
                "last_updated": "2022-05-21T15:01:00.000Z",
                "quote": {
                    "USD": {
                        "price": 29399.437482750534,
                        "volume_24h": 23524112355.38572,
                        "volume_change_24h": -30.2016,
                        "percent_change_1h": 0.3220439,
                        "percent_change_24h": -0.4084507,
                        "percent_change_7d": 1.72466966,
                        "percent_change_30d": -30.84611713,
                        "percent_change_60d": -31.29645913,
                        "percent_change_90d": -23.07640423,
                        "market_cap": 559931190697.2853,
                        "market_cap_dominance": 44.5593,
                        "fully_diluted_market_cap": 617388187137.76,
                        "last_updated": "2022-05-21T15:01:00.000Z"
                    }
                }
            },
            {
                "id": 1027,
                "name": "Ethereum",
                "symbol": "ETH",
                "slug": "ethereum",
                "num_market_pairs": 5726,
                "date_added": "2015-08-07T00:00:00.000Z",
                "tags": [
                    "mineable",
                    "pow",
                    "smart-contracts",
                    "ethereum-ecosystem",
                    "coinbase-ventures-portfolio",
                    "three-arrows-capital-portfolio",
                    "polychain-capital-portfolio",
                    "binance-labs-portfolio",
                    "blockchain-capital-portfolio",
                    "boostvc-portfolio",
                    "cms-holdings-portfolio",
                    "dcg-portfolio",
                    "dragonfly-capital-portfolio",
                    "electric-capital-portfolio",
                    "fabric-ventures-portfolio",
                    "framework-ventures-portfolio",
                    "hashkey-capital-portfolio",
                    "kenetic-capital-portfolio",
                    "huobi-capital-portfolio",
                    "alameda-research-portfolio",
                    "a16z-portfolio",
                    "1confirmation-portfolio",
                    "winklevoss-capital-portfolio",
                    "usv-portfolio",
                    "placeholder-ventures-portfolio",
                    "pantera-capital-portfolio",
                    "multicoin-capital-portfolio",
                    "paradigm-portfolio",
                    "injective-ecosystem",
                    "bnb-chain"
                ],
                "max_supply": null,
                "circulating_supply": 120867023.5615,
                "total_supply": 120867023.5615,
                "platform": {
                    "id": 2502,
                    "name": "Huobi Token",
                    "symbol": "HT",
                    "slug": "huobi-token",
                    "token_address": "0x64ff637fb478863b7468bc97d30a5bf3a428a1fd"
                },
                "cmc_rank": 2,
                "self_reported_circulating_supply": null,
                "self_reported_market_cap": null,
                "last_updated": "2022-05-21T15:00:00.000Z",
                "quote": {
                    "USD": {
                        "price": 1974.8549053014458,
                        "volume_24h": 12236555396.820942,
                        "volume_change_24h": -24.3189,
                        "percent_change_1h": 0.24763493,
                        "percent_change_24h": -0.73045843,
                        "percent_change_7d": 0.4628699,
                        "percent_change_30d": -37.24623376,
                        "percent_change_60d": -34.35775782,
                        "percent_change_90d": -24.77279557,
                        "market_cap": 238694834369.6137,
                        "market_cap_dominance": 18.9847,
                        "fully_diluted_market_cap": 238694834369.61,
                        "last_updated": "2022-05-21T15:00:00.000Z"
                    }
                }
            }
        ]
    }
     */

    private CmcStatus status;
    public List<CmcItem> data = null;

    // Показывает, есть ли ошибка в данных
    public boolean hasError() {
        return status != null && status.error_message != null;
    }

    // Описание ошибки, если она есть
    public String getErrorMessage() {
        return status != null ? status.error_message : "";
    }

    // Десериализуем объект из json-данных
    public static CmcLatest FromJson(String json) {
        Gson gson = new Gson();
        try {
            // Попытка воссоздать объект из json-данных
            CmcLatest obj = gson.fromJson(json, CmcLatest.class);
            // Если ошибка уже присутствует в json-объекте,
            // передаем ее в новый объект котрый собственно и возвращаем
            if (obj.hasError())
                return FromError(obj.status.error_message);
            // Всё ок - возвращаем воссозданный объект
            return obj;
        } catch (JsonParseException e) {
            // В случае ошибки при десериализации - возвращаем объект созданный из ошибки
            return FromError(e);
        }
    }

    // Создаем объект из перехваченного исключения
    public static CmcLatest FromError(Exception e) {
        e.printStackTrace();
        return FromError(e.getMessage());
    }

    // Создаем объект из описания ошибки
    public static CmcLatest FromError(String message) {
        Log.i(CmcLatest.class.getName(), message);
        CmcLatest cmc = new CmcLatest();
        CmcStatus status = new CmcStatus();
        status.error_message = message;
        cmc.status = status;
        return cmc;
    }
}

