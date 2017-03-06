package com.tianshouzhi.dragon.idgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by TIANSHOUZHI336 on 2017/3/3.
 */
public abstract class SnowFlakeIdGen extends AbstractDragonIdGen{
    private static final Logger LOGGER= LoggerFactory.getLogger(SnowFlakeIdGen.class);
    /** 自定义元年 (2015-01-01) */
    private static final long DEFAULT_TWEPOCH = 1420041600000L;
    private static final int DEFAULT_WORKER_ID_BITS =10;
    private static final int DEFAULT_SEQUENCE_BITS =12;
    private long twepoch;//需要设置为一个固定值
    private int workerIdBits;
    private int sequenceBits;
    private int workerId;

    public SnowFlakeIdGen(int workerId) {
       this(DEFAULT_TWEPOCH,DEFAULT_WORKER_ID_BITS,DEFAULT_SEQUENCE_BITS,workerId);
    }

    public SnowFlakeIdGen(long twepoch, int workerIdBits, int sequenceBits, int workerId) {
        check(twepoch,workerIdBits,sequenceBits,workerId);
        this.twepoch = twepoch;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;
        this.workerId = workerId;

    }

    private void check(long twepoch, int workerIdBits, int sequenceBits, int workerId) {
//        get
    }

}
