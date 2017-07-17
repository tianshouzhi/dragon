package com.tianshouzhi.dragon.common.exception;

import java.sql.SQLException;

/**
 * Created by TIANSHOUZHI336 on 2016/12/6.
 */
public interface ExceptionSorter {
	/**
	 * Returns true or false whether or not the exception is fatal.
	 *
	 * @param e
	 *           the exception
	 * @return true or false if the exception is fatal.
	 */
	boolean isExceptionFatal(SQLException e);
}
