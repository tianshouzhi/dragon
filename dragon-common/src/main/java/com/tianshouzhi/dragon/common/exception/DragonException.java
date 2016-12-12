package com.tianshouzhi.dragon.common.exception;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/6.
 */
public class DragonException extends SQLException{
    public DragonException(String reason) {
        super(reason);
    }

    public DragonException(InterruptedException e) {
        super(e);
    }

    public DragonException(String message, SQLException e) {
        super(message,e);
    }
}
