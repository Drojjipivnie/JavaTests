package com.drojj.javatests.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
    public static String formatData(long time) {
        Date date = new Date(time);

        Locale locale = new Locale("ru", "RU");
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        return df.format(date);
    }
}
