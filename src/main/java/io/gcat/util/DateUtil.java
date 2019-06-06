package io.gcat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static DateTimeFormatter ISOFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    public static DateTimeFormatter ISOFormatterWithMilli = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static String format(long t) {
        Instant instant = Instant.ofEpochMilli(t);
        ZonedDateTime dt = instant.atZone(ZoneId.of("Asia/Shanghai"));

        return dt.format(ISOFormatterWithMilli);
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
