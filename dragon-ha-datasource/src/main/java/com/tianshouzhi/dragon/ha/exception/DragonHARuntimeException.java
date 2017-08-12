package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.common.exception.DragonRuntimeException;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class DragonHARuntimeException extends DragonRuntimeException{
    public DragonHARuntimeException(String message) {
        super(message);
    }

    public DragonHARuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DragonHARuntimeException(Throwable cause) {
        super(cause);
    }
}
