package com.tianshouzhi.dragon.sharding.exception;

import com.tianshouzhi.dragon.common.exception.DragonException;

/**
 * Created by tianshouzhi on 2017/11/5.
 */
public class DragonShardException extends DragonException{
    public DragonShardException() {
    }

    public DragonShardException(String message) {
        super(message);
    }

    public DragonShardException(String message, Throwable cause) {
        super(message, cause);
    }

    public DragonShardException(Throwable cause) {
        super(cause);
    }
}
