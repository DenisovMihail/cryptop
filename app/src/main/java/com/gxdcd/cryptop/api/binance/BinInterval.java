package com.gxdcd.cryptop.api.binance;

public enum BinInterval {
    ONE_MINUTE("1m", 1L*60*1),
    THREE_MINUTES("3m", 1L*60*3),
    FIVE_MINUTES("5m", 1L*60*5),
    FIFTEEN_MINUTES("15m", 1L*60*15),
    HALF_HOURLY("30m", 1L*60*30),
    HOURLY("1h", 1L*60*60),
    TWO_HOURLY("2h", 1L*60*60*2),
    FOUR_HOURLY("4h", 1L*60*60*4),
    SIX_HOURLY("6h", 1L*60*60*6),
    EIGHT_HOURLY("8h", 1L*60*60*8),
    TWELVE_HOURLY("12h", 1L*60*60*12),
    DAILY("1d", 1L*60*60*24),
    THREE_DAILY("3d", 1L*60*60*24*3),
    WEEKLY("1w", 1L*60*60*24*7),
    MONTHLY("1M", 1L*60*60*24*30);

    private String intervalId;
    private Long seconds;

    BinInterval(String intervalId, Long seconds) {
        this.intervalId = intervalId;
        this.seconds = seconds;
    }

    public Long getSeconds() {
        return seconds;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public String translate() {
        return getIntervalId();
    }

    public static BinInterval from(String intervalId) {
        switch (intervalId) {
            case "1m": return BinInterval.ONE_MINUTE;
            case "3m": return BinInterval.THREE_MINUTES;
            case "5m": return BinInterval.FIVE_MINUTES;
            case "15m": return BinInterval.FIFTEEN_MINUTES;
            case "30m": return BinInterval.HALF_HOURLY;
            case "1h": return BinInterval.HOURLY;
            case "2h": return BinInterval.TWO_HOURLY;
            case "4h": return BinInterval.FOUR_HOURLY;
            case "6h": return BinInterval.SIX_HOURLY;
            case "8h": return BinInterval.EIGHT_HOURLY;
            case "12h": return BinInterval.TWELVE_HOURLY;
            // case "1d": return BinInterval.DAILY;
            case "3d": return BinInterval.THREE_DAILY;
            case "1w": return BinInterval.WEEKLY;
            case "1M": return BinInterval.MONTHLY;
            default: return BinInterval.DAILY;
        }
    }
}
