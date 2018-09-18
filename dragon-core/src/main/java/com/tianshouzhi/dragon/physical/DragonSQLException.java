package com.tianshouzhi.dragon.physical;

import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2018/2/9.
 */
public class DragonSQLException extends SQLException{
    public DragonSQLException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public DragonSQLException(String reason, String SQLState) {
        super(reason, SQLState);
    }

    public DragonSQLException(String reason) {
        super(reason);
    }

    public DragonSQLException() {
    }

    public DragonSQLException(Throwable cause) {
        super(cause);
    }

    public DragonSQLException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public DragonSQLException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public DragonSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
