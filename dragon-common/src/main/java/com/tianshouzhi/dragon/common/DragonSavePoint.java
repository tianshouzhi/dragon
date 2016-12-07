package com.tianshouzhi.dragon.common;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class DragonSavePoint implements Savepoint {

    private static AtomicInteger savepointIdGenerater=new AtomicInteger();

    private int savepointId;

    private String savepointName;

    private boolean isRemoved=false;

    public DragonSavePoint() {
        this(null);
    }

    public DragonSavePoint(String savepointName) {
        this.savepointId = savepointIdGenerater.incrementAndGet();
        this.savepointName = savepointName;
    }

    @Override
    public int getSavepointId() throws SQLException {
        checkRemoved();
        return savepointId;
    }

    private void checkRemoved() throws SQLException {
        if(isRemoved){
            throw new SQLException(this+" has been removed");
        }
    }

    @Override
    public String getSavepointName() throws SQLException {
        checkRemoved();
        return savepointName;
    }

    void remove() {
        isRemoved = true;
    }

    @Override
    public String toString() {
        return "DragonSavePoint{" +
                "savepointId=" + savepointId +
                ", savepointName='" + savepointName + '\'' +
                ", isRemoved=" + isRemoved +
                '}';
    }
}
