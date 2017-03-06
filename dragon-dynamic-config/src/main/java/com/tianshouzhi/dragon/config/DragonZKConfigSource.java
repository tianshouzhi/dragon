package com.tianshouzhi.dragon.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by TIANSHOUZHI336 on 2017/2/19.
 */
public class DragonZKConfigSource extends AbstractConfigSource implements Watcher{
    private static final Logger LOGGER = LoggerFactory.getLogger(DragonZKConfigSource.class);
    private ZooKeeper zkClient = null;
    private static final int DEFAULT_ZK_SESSION_TIMEOUT = 5000;
    public static final String ROOT_PATH="/dragon-dynamic-config";
    public DragonZKConfigSource(String zkAddresses,DragonConfigListener dragonConfigListener) {
        super(dragonConfigListener);
        try {
            this.zkClient = new ZooKeeper(zkAddresses, DEFAULT_ZK_SESSION_TIMEOUT, this);
        } catch (IOException e) {
            throw new RuntimeException("initial zkCliant Exception");
        }
    }

    @Override
    public String getRemoteConf(String remoteKey) throws Exception{
        String path = ROOT_PATH + "/" + remoteKey;
        if(zkClient.exists(path,true)!=null){
            Stat stat = new Stat();
            byte[] data = zkClient.getData(path, true, stat);
            return new String(data);
        }else{
            throw new RuntimeException(remoteKey+" doesn't exsits!!!");
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            switch (event.getType()) {
                case None:
                    break;
               /* case NodeCreated:
                    tackleNodeCreated(event);
                    break;
                case NodeDeleted:
                    tackleNodeDeleted(event);
                    break;
                case NodeDataChanged:
                    tackleNodeDataChanged(event);
                    break;
                case NodeChildrenChanged:
                    tackleNodeChildrenChanged(event);
                    break;*/
            }
        } catch (Exception e) {
            LOGGER.error("TACKLE EVENT ERROR,event:{}",event,e);
        }
    }
}
