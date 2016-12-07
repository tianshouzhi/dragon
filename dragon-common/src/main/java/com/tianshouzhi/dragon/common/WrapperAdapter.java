package com.tianshouzhi.dragon.common;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Created by TIANSHOUZHI336 on 2016/11/30.
 */
public class WrapperAdapter implements Wrapper{
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
