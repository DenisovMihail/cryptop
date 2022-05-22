package com.gxdcd.cryptop.api;

//public class TimeSpan {
//    public final Long start;
//    public final Long end;
//
//    TimeSpan(Long start, Long end) {
//        this.start = start;
//        this.end = end;
//    }
//}

//    static TimeSpan GetUtcMsRangeFromNow(Integer days) {
//        return GetUtcMsRangeFromNow(days, 0, 0);
//    }
//
//    static TimeSpan GetUtcMsRangeFromNow(Integer days, Integer hours) {
//        return GetUtcMsRangeFromNow(days, hours, 0);
//    }
//
//    static TimeSpan GetUtcMsRangeFromNow(Integer days, Integer hours, Integer minutes) {
//        Long endTime = GetUtcNow();
//        // delta это разница, или продолжительность периода в секундах
//        Long delta = 24L * 60 * 60 * days + 60 * 60 * hours + 60 * minutes;
//        return new TimeSpan(1000L * (endTime - delta), 1000L * endTime);
//    }

//    @SuppressLint("NewApi")
//    static Long GetUtcNow() {
//        return Instant.now().getEpochSecond();
//    }