package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.common.exception.DragonException;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class HASQLException extends DragonException{
    public HASQLException(String reason) {
        super(reason);
    }

    public HASQLException(Throwable e) {
        super(e);
    }

    public HASQLException(String message, Throwable e) {
        super(message, e);
    }
}
