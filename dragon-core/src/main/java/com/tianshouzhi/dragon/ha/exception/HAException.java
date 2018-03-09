package com.tianshouzhi.dragon.ha.exception;

import com.tianshouzhi.dragon.common.exception.DragonException;

/**
 * Created by tianshouzhi on 2017/8/12.
 */
public class HAException extends DragonException{
    public HAException(String reason) {
        super(reason);
    }

    public HAException(Throwable e) {
        super(e);
    }

    public HAException(String message, Throwable e) {
        super(message, e);
    }
}
