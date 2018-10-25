package com.zjh.util.unique.generate;

/**
 * @author:jinhui.zhao
 * @description:
 * @date: created in 下午12:34 2018/10/15
 */

import java.util.Date;

/**
 * 0-0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 一个long整形
 * 0是标识位，1-41是毫秒级的时间戳，42-51 数据中心和机器id 52-63毫秒内计数
 * 整体上按照时间自增，不会产生id碰撞，不会引入第三方插件
 */
public class SnowFlake {

    /**
     * 开始时间错
     */
    private final static Long twePoch = 1483200000000L;
    /**
     * 机器id
     */
    private static final Long workIdBits = 5L;
    /**
     * 数据标示id所占位数
     */
    private static final Long dataCenterIdBits = 5L;

    /**
     * 支持最大机器数
     */
    private static final Long maxWorkers = ~( -1L << workIdBits );

    /**
     * 支持最大机器数
     */
    private static final Long maxDataCenters= ~( -1L << dataCenterIdBits );


    /**
     * 序列在id所占的位数
     */
    private static final Long sequenceBits= 12L;

    /**
     * 机器id左移12位
     */
    private static final Long workerIdShift = sequenceBits;

    /**
     * 数据中心左移
     */
    private static final Long dataCenterShift = sequenceBits+workIdBits;


    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timeStampLeftShift = sequenceBits + workIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    public SnowFlake(Long workId,Long datacenterId){

        if(maxWorkers<workId || 0>workId){
            throw  new IllegalArgumentException(String.format("work id cant be greater than %d , or less than 0",workId));
        }
        if(maxDataCenters<datacenterId || 0>datacenterId){
            throw  new IllegalArgumentException(String.format("work id cant be greater than %d , or less than 0",datacenterId));
        }
        this.workerId = workId;
        this.datacenterId = datacenterId;

    }


    public long nextId(){
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if(lastTimestamp==timestamp){
            sequence = (sequence + 1) & sequenceMask;
            if(sequence==0){
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twePoch) << timeStampLeftShift)
                | (datacenterId << dataCenterShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    public long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     *
     * @param lastTimestamp 上一次生成id的时间搓
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2l,5l);
        while(true){
            System.out.println(snowFlake.nextId());
        }
    }










}
