package com.gxdcd.cryptop.api.binance;

import com.google.gson.annotations.JsonAdapter;

/*
Ответ binance состоит из массива массивов

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

// для преобразования элемента массива
// используем JsonAdapter
@JsonAdapter(BinCandleAdapter.class)
public class BinCandle {
    // 1499040000000,      // Open time
    public long openTime;
    // "0.01634790",       // Open
    public double open;
    // "0.80000000",       // High
    public double high;
    // "0.01575800",       // Low
    public double low;
    // "0.01577100",       // Close
    public double close;
    // "148976.11427815",  // Volume
    public double volume;
    // 1499644799999,      // Close time
    public long closeTime; // 1st to fail
    // "2434.19055334",    // Quote asset volume
    public double quoteAssetVolume;
    // 308,                // Number of trades
    public long numberOfTrades; // 2nd to fail
    // "1756.87402397",    // Taker buy base asset volume
    public double takerBuyBaseAssetVolume;
    // "28.46694368",      // Taker buy quote asset volume
    public double takerBuyQuoteAssetVolume;
    // "17928899.62484339" // Ignore.
    //  private String ignore;
}
