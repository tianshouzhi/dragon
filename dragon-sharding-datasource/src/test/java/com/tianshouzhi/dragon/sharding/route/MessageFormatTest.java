package com.tianshouzhi.dragon.sharding.route;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TIANSHOUZHI336 on 2017/2/24.
 */
public class MessageFormatTest {
    public static void main(String[] args) throws ParseException {
        String pattern = "welcome {0},current time is {1}";
        Object[] arguments = {"tianshouzhi", new Date()};
        MessageFormat messageFormat=new MessageFormat(pattern, Locale.CHINA);
        System.out.println(messageFormat.toPattern());
        String format = messageFormat.format(pattern, arguments);
        System.out.println(format);
        String source="welcome tianshouzhi,current time is 1972";
        Object[] parse = messageFormat.parse(source, new ParsePosition(0));
        System.out.println(parse[0]);
    }
}
