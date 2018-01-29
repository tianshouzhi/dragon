package com.tianshouzhi.dragon.shard.route;

import java.text.Format;
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
        String pattern = "welcome ,current time is ";
        Object[] arguments = {"tianshouzhi", new Date()};
        MessageFormat messageFormat=new MessageFormat(pattern, Locale.CHINA);
        Format[] formats = messageFormat.getFormats();
        System.out.println(formats.length);
        for (Format format : formats) {

        }
        System.out.println(messageFormat.toPattern());
        String format = messageFormat.format(pattern, arguments);
        System.out.println(format);
        String source="welcome tianshouzhi,current time is 1972";
        Object[] parse = messageFormat.parse(source, new ParsePosition(0));
        System.out.println(parse[0]);
    }
}
