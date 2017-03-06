package com.tianshouzhi.dragon.idgen.tair;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.impl.DefaultTairManager;
import com.tianshouzhi.dragon.idgen.AbstractDragonIdGen;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/3/3.
 */
public abstract class AbstractTairIdGen extends AbstractDragonIdGen {
    private List<String> confServers;//CONFIG_SERVER_ADDRESS:PORT
    private String groupName;//p_group1
    private int namespace;
    private String key;
    private DefaultTairManager tairManager;
    @Override
    protected void doInit() {
        tairManager=new DefaultTairManager();
        tairManager.setConfigServerList(confServers);
        tairManager.setGroupName(groupName);
        tairManager.init();
    }

    @Override
    protected Long getId() {
        int expireTime = 0;//不失效
        long defaultValue = 1;//当计数器不存在时的初始化值
        long value = 1;//本次增加的数量
        Result<Long> result = tairManager.lincr(namespace, key, value, defaultValue, expireTime);
        if(result.isSuccess()){
            Long resultValue = result.getValue();
            return resultValue;
        }else{
            ResultCode rc = result.getRc();
            throw new RuntimeException(rc.toString());
        }
    }

    public List<String> getConfServers() {
        return confServers;
    }

    public void setConfServers(List<String> confServers) {
        this.confServers = confServers;
    }

    public DefaultTairManager getTairManager() {
        return tairManager;
    }

    public void setTairManager(DefaultTairManager tairManager) {
        this.tairManager = tairManager;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNamespace() {
        return namespace;
    }

    public void setNamespace(int namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
