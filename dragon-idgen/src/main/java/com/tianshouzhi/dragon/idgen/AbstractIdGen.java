package com.tianshouzhi.dragon.idgen;

/**
 * Created by TIANSHOUZHI336 on 2017/3/9.
 */
public abstract class AbstractIdGen implements IdGen{

    protected boolean isInit=false;

    private void init(){
        if(isInit){
            return;
        }
        synchronized (this){
            if(isInit){
                return;
            }
            doInit();
            isInit=true;

        }
    }

    protected void doInit(){};

    @Override
    public Long getAutoIncrementId(IdDecorator idDecorator) throws Exception {
        init();
        if(idDecorator==null){
            throw new NullPointerException();
        }
        Long autoIncrementId = getAutoIncrementId();
        return idDecorator.decorate(autoIncrementId);
    }
}
