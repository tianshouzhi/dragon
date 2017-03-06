package com.tianshouzhi.dragon.sharding.route;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class MessageFormatTest {
    public static void main(String[] args) {
        String pattern = "welcome {0},current time is {1}";
        Object[] arguments = {"tianshouzhi", new Date()};
        MessageFormat messageFormat=new MessageFormat(pattern, Locale.CHINA);
        String format = messageFormat.format(pattern, arguments);
        System.out.println(format);
    }
}
