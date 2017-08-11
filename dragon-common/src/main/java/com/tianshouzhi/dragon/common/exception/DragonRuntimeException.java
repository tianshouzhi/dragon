package com.tianshouzhi.dragon.common.exception;

/**
 * Created by tianshouzhi on 2017/8/3.
 */
public class DragonRuntimeException extends RuntimeException{
    public DragonRuntimeException() {
    }

    public DragonRuntimeException(String message) {
        super(message);
    }

    public DragonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DragonRuntimeException(Throwable cause) {
        super(cause);
    }
}
