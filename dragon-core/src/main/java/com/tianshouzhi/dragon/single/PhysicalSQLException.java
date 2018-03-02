package com.tianshouzhi.dragon.single;

import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2018/2/9.
 */
public class PhysicalSQLException extends SQLException{
    public PhysicalSQLException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public PhysicalSQLException(String reason, String SQLState) {
        super(reason, SQLState);
    }

    public PhysicalSQLException(String reason) {
        super(reason);
    }

    public PhysicalSQLException() {
    }

    public PhysicalSQLException(Throwable cause) {
        super(cause);
    }

    public PhysicalSQLException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public PhysicalSQLException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public PhysicalSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
