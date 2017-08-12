package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.common.exception.DragonException;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHAException extends DragonException{
    public DragonHAException(String reason) {
        super(reason);
    }

    public DragonHAException(InterruptedException e) {
        super(e);
    }

    public DragonHAException(String message, Exception e) {
        super(message, e);
    }
}
