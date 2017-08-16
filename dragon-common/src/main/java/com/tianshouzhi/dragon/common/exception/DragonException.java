package com.tianshouzhi.dragon.common.exception;

/**
 * Created by TIANSHOUZHI336 on 2016/12/6.
 */
public class DragonException extends Exception {
	public DragonException(String reason) {
		super(reason);
	}

	public DragonException(Throwable e) {
		super(e);
	}

	public DragonException(String message, Throwable e) {
		super(message, e);
	}
}
