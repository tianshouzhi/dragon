package com.tianshouzhi.dragon.common.exception;

import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/7/14.
 */
public class DragonShardException extends SQLException {
    public DragonShardException(String reason) {
        super(reason);
    }
    public DragonShardException(InterruptedException e) {
        super(e);
    }
    public DragonShardException(String message, Exception e) {
        super(message,e);
    }
}
