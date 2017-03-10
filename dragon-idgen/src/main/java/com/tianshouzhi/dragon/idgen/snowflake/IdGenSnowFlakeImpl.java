package com.tianshouzhi.dragon.idgen.snowflake;


import com.tianshouzhi.dragon.idgen.AbstractIdGen;

/**
 * Created by TIANSHOUZHI336 on 2017/3/9.
 */
public class IdGenSnowFlakeImpl extends AbstractIdGen {

    /** 自定义元年 (2015-01-01),需要设置为一个固定值 */
    public static final long DEFAULT_TWEPOCH =1420041600000L;
    public static final int WORKER_ID_BITS =10;//默认最多可以扩容到1024台机器
    public static final int SEQUENCE_BITS =12;   /**最大sequenceId 4095*/
    private long twepoch;
    private int workerIdBits;
    private int maxWorkerId;
    private long workerId;
    /**默认每毫秒最多可以产生4096个id*/
    private int sequenceBits;
    private long maxSequenceId;//
    /** 毫秒内序列(0~4095) */
    private long seqenceId = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp;

    public IdGenSnowFlakeImpl(int workerId) {
        this(DEFAULT_TWEPOCH, WORKER_ID_BITS, SEQUENCE_BITS,workerId);
    }

    public IdGenSnowFlakeImpl(long twepoch, int workerIdBits, int sequenceBits, int workerId) {
        if(workerId<0||twepoch<this.DEFAULT_TWEPOCH ||workerIdBits<=0||sequenceBits<=0){
            throw new IllegalArgumentException();
        }
        this.twepoch = twepoch;
        this.workerIdBits = workerIdBits;
        this.maxWorkerId=-1 ^ (-1 << workerIdBits);
        this.workerId = workerId;
        if(workerId>maxWorkerId){
            throw new IllegalArgumentException("workerId("+workerId+")must <= maxWorkerId"+maxWorkerId);
        }
        this.sequenceBits = sequenceBits;
        this.maxSequenceId=-1 ^ (-1 << sequenceBits);/** 生成序列的掩码，默认为4095 (0b111111111111=0xfff=4095) */

    }

    @Override
    public synchronized Long getAutoIncrementId() throws Exception {
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp));
        }

        if(currentTimestamp==lastTimestamp){
            seqenceId=(seqenceId+1) & maxSequenceId;
            if(seqenceId==0){
                //阻塞到下一个毫秒,获得新的时间戳
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        }else{
            seqenceId = 0;//currentTime>lastTimestamp
        }

        //上次生成ID的时间截
        lastTimestamp = currentTimestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((currentTimestamp - DEFAULT_TWEPOCH) << ( WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << WORKER_ID_BITS)
                | seqenceId;
    }
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
