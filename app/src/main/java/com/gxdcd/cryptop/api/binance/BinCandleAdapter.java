package com.gxdcd.cryptop.api.binance;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class BinCandleAdapter extends TypeAdapter<BinCandle> {
    @Override
    public void write(JsonWriter out, BinCandle candle) throws IOException {
//        // implement write: combine firstName and lastName into name
//        out.beginObject();
//        out.name("name");
//        out.value(user.firstName + " " + user.lastName);
//        out.endObject();
//        // implement the write method
    }

    @Override
    public BinCandle read(JsonReader in) throws IOException {
    /*
    полный ответ binance состоит из массива массивов

    [
        [
            1652904000000,
            "29276.55000000",
            "29293.40000000",
            "28960.87000000",
            "29236.47000000",
            "2141.94198000",
            1652907599999,
            "62409741.60928480",
            62577,
            "1017.20913000",
            "29636955.09006020",
            "0"
        ],
        [
            1652907600000,
            "29236.48000000",
            "29349.21000000",
            "29178.56000000",
            "29307.96000000",
            "615.53843000",
            1652911199999,
            "18005825.00297050",
            21493,
            "293.47504000",
            "8585686.19193550",
            "0"
        ]
    ]
    */

    /*
    данный метод отрабатывает элемент полного массива
    [
        1652904000000,
        "29276.55000000",
        "29293.40000000",
        "28960.87000000",
        "29236.47000000",
        "2141.94198000",
        1652907599999,
        "62409741.60928480",
        62577,
        "1017.20913000",
        "29636955.09006020",
        "0"
    ]
    */
        in.beginArray();
        BinCandle candle = new BinCandle();
        candle.openTime = in.nextLong();
        candle.open = in.nextDouble();
        candle.high = in.nextDouble();
        candle.low = in.nextDouble();
        candle.close = in.nextDouble();
        candle.volume = in.nextDouble();
        candle.closeTime = in.nextLong();
        candle.quoteAssetVolume = in.nextDouble();
        candle.numberOfTrades = in.nextLong();
        candle.takerBuyBaseAssetVolume = in.nextDouble();
        candle.takerBuyQuoteAssetVolume = in.nextDouble();
        in.nextDouble(); // читаем поле "ignored"
        in.endArray();
        return candle;
    }
}
