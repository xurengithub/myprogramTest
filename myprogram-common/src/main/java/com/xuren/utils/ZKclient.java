package com.xuren.utils;

import com.xuren.factorys.ZookeeperClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;

import javax.script.ScriptEngine;
import java.io.UnsupportedEncodingException;

public class ZKclient {
    private CuratorFramework client;
    private static ZKclient instance;
    private ZKclient(){
        client = ZookeeperClientFactory.createSimple("dsdsdsds");
    }
    public static ZKclient getInstance() {
        if(instance == null) {
            instance = new ZKclient();
        }
        return instance;
    }

    public void init() {
        client.start();
    }

    public String createNode(String zkPath) throws Exception {
        return createNode(zkPath, null, CreateMode.PERSISTENT);
    }
    public String createNode(String zkPath, String s) throws Exception {
        return createNode(zkPath, s, CreateMode.PERSISTENT);
    }

    public String createEphemeralSeqNode(String zkPath) throws Exception {
        return createNode(zkPath, null, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public String createNode(String zkPath, String s, CreateMode createMode) throws Exception {
        byte[] payload = s.getBytes("UTF-8");
        String createPath = client
                .create()
                .creatingParentContainersIfNeeded()
                .withMode(createMode)
                .forPath(zkPath, payload);
        return createPath;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void destory() {
        if(null != client && client.getState() == CuratorFrameworkState.STARTED) {
            client.close();
        }
    }

    public boolean isNodeExist(String path) {

        return false;
    }


}
