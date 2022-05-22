package com.gxdcd.cryptop.api.binance;

public class BinParams {
    // Mandatory
    String symbol;
    // Mandatory
    BinInterval interval;
    // Optional Default 500; max 1000.
    Integer limit;

    public BinParams(String symbol, BinInterval interval, Integer limit) {
        this.symbol = symbol;
        this.limit = limit;
        this.interval = interval;
    }
    // startTime	LONG	Optional
    // endTime	    LONG	Optional
}
