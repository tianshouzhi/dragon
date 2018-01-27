package com.tianshouzhi.dragon.physical;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Created by tianshouzhi on 2018/1/23.
 */
public class WrapperAdapter implements Wrapper {
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
