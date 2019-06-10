package io.gcat.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class DateUtil {

    private static DateTimeFormatter ISOFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    private static DateTimeFormatter ISOFormatterWithMilli = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static String format(long t) {
        Instant instant = Instant.ofEpochMilli(t);
        //todo
        ZonedDateTime dt = instant.atZone(ZoneId.of("Asia/Shanghai"));

        return dt.format(ISOFormatterWithMilli);
    }

    public static String format(Duration duration) {
        StringBuilder sb = new StringBuilder();
        long h = duration.toHours();
        long m = duration.toMinutes() % 60;
        long s = duration.getSeconds() % 60;

        if (0 < h) {
            sb.append(" ").append(h).append("Hour");
        }
        if (0 < m) {
            sb.append(" ").append(m).append("min");
        }

        if (0 < s) {
            sb.append(" ").append(s).append("sec");
        }

        if (0 == sb.length()) {
            sb.append(" ").append(duration.getNano() / 1000_000).append("ms");
        }

        return sb.toString();
    }

    public static long parse(String s) {
        ZonedDateTime zonedDateTime = tryParse(s, ISOFormatterWithMilli);
        if (null == zonedDateTime) {
            zonedDateTime = tryParse(s, ISOFormatter);
        }
        Objects.requireNonNull(zonedDateTime);

        return zonedDateTime.toInstant().toEpochMilli();
    }

    private static ZonedDateTime tryParse(String s, DateTimeFormatter formatter) {
        try {
            return ZonedDateTime.parse(s, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
