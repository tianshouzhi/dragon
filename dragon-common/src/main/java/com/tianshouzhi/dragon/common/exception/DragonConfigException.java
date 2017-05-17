package com.tianshouzhi.dragon.common.exception;

/**
 * Created by tianshouzhi on 2017/5/17.
 */
public class DragonConfigException extends DragonException{
    public DragonConfigException(String reason) {
        super(reason);
    }

    public DragonConfigException(InterruptedException e) {
        super(e);
    }

    public DragonConfigException(String message, Exception e) {
        super(message, e);
    }
}
