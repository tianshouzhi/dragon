package com.tianshouzhi.dragon.ha.exception;

/**
 * Created by tianshouzhi on 2017/8/3.
 */
public class DragonHAConfigException extends DragonHAException {
    public DragonHAConfigException(String reason) {
        super(reason);
    }

    public DragonHAConfigException(Throwable e) {
        super(e);
    }

    public DragonHAConfigException(String message, Throwable e) {
        super(message, e);
    }
}
