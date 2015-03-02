package com.yegorov.vkfv.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    public static String unixTimeToStr(int unixTime) {
        Date d = new Date(unixTime * 1000L);
        //Date now = new Date();
        //Long time = System.currentTimeMillis();
        //Date yesterday = new Date(time - time % (24 * 60 * 60 * 1000) - 1);
        String str;
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");

        sdfTime.setTimeZone(TimeZone.getDefault());
        sdfDate.setTimeZone(TimeZone.getDefault());
        str = sdfDate.format(d) + " " + sdfTime.format(d);

        return str;
    }
}
