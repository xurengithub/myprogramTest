package com.xuren.utils;

import com.xuren.config.ZookeeperConfig;
import com.xuren.factorys.ZookeeperClientFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeerNode {
    private CuratorFramework client = null;
    private String pathRegistered = null;
    private static PeerNode instance = null;

    private String pathPrefix = "";
    private static String conStr = "";

    private PeerNode(){}

    public static PeerNode getInstance() {
        if(null == instance) {
            synchronized (PeerNode.class) {
                if(null == instance) {
                    instance = new PeerNode();
                    instance.client = ZookeeperClientFactory.createSimple(conStr);
                    instance.init();
                }
            }
        }
        return instance;
    }

    public void init() {
        client.start();
        try {
            pathRegistered = client
                    .create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(pathPrefix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        String sid = null;
        if(null == pathRegistered) {
            throw new RuntimeException("节点注册失败");
        }
        int index = pathRegistered.lastIndexOf(pathPrefix);
        if(index >= 0) {
            index += pathPrefix.length();
            sid =  index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }
        if(null == sid) {
            throw new RuntimeException("分布式节点错误");
        }

        return Long.parseLong(sid);
    }
}
