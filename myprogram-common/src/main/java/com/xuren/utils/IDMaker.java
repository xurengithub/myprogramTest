package com.xuren.utils;

import com.xuren.config.ZookeeperConfig;
import com.xuren.factorys.ZookeeperClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IDMaker {

    private Logger logger = LogManager.getLogger(this.getClass());
    private  CuratorFramework client;
    private String conStr = "";
    @Autowired
    private ZookeeperConfig zookeeperConfig;

    public void init() {
        client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        logger.info("succ");
    }


    private String createSqlNode(String pathPefix) {
        try {
            logger.info(client.getState().name());
            String path = client
                    .create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(pathPefix);
            logger.info(path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String makeId(String nodeName) {
        String str = createSqlNode(nodeName);
        if(null == str) {
            return null;
        }
        int index = str.lastIndexOf(nodeName);
        if(index >= 0) {
            index += nodeName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

    public void destory() {
        if(null != client && client.getState() == CuratorFrameworkState.STARTED) {
            client.close();
        }
    }
}
