package com.tianshouzhi.dragon.common.exception;

/**
 * Created by tianshouzhi on 2017/8/3.
 */
public class DragonException extends RuntimeException{
    public DragonException() {
    }

    public DragonException(String message) {
        super(message);
    }

    public DragonException(String message, Throwable cause) {
        super(message, cause);
    }

    public DragonException(Throwable cause) {
        super(cause);
    }
}
