package com.xuren.factorys;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperClientFactory {

    private ZookeeperClientFactory(){

    }

    public static CuratorFramework createSimple(String connectStr) {
        ExponentialBackoffRetry policy  = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connectStr, policy);
    }

    public static CuratorFramework createWithOptions(String connectStr, RetryPolicy retryPolicy, int connectTimeoutMs, int sessionTimeoutMs) {
        return CuratorFrameworkFactory
                .builder()
                .retryPolicy(retryPolicy)
                .connectString(connectStr)
                .connectionTimeoutMs(connectTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }

}
