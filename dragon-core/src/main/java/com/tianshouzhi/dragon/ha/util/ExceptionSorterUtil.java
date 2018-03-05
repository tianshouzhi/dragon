package com.tianshouzhi.dragon.ha.util;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/6.
 */
public class ExceptionSorterUtil {
    private static final int MAX_LOOP_TIMES = 20;

    public static boolean isExceptionFatal(final Exception e) {
        int i = 0;
        Throwable cause = e;
        while (cause != null && i < MAX_LOOP_TIMES) {
            if (!(cause instanceof SQLException)) {
                i++;
                cause = cause.getCause();
                continue;
            }

            String sqlState = ((SQLException) cause).getSQLState();
            if (sqlState != null && sqlState.startsWith("08")) {
                return true;
            }
            if (((SQLException) cause).getErrorCode() == 0) {
                return true;
            }
            i++;
            cause = ((SQLException) cause).getNextException();
        }
        return false;
    }
}
