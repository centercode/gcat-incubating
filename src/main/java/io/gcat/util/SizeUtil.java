package io.gcat.util;

public class SizeUtil {

    private static String[] arr = {"K", "M", "G"};

    private SizeUtil() {
    }

    public static String format(int kb) {
        return String.format("%.2fG", kb * 1.0 / 1024 / 1024);
    }

}
