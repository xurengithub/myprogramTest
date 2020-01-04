package com.xuren.lock;

import com.xuren.utils.ZKclient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ZKLock implements Lock {

    private static final String ZK_PATH = "/test/lock";
    private static final String LOCK_PREFIX = ZK_PATH + "/";
    private static final long WAIT_TIME =1000;

    private CuratorFramework client;
    private String locked_short_path;
    private String locked_path;
    private String prior_path;
    final AtomicInteger lockCount = new AtomicInteger(0);
    private Thread thread;

    public ZKLock() throws Exception {
        ZKclient.getInstance().init();
        if(ZKclient.getInstance().isNodeExist(ZK_PATH)) {
            ZKclient.getInstance().createNode(ZK_PATH, null);
        }
        client = ZKclient.getInstance().getClient();
    }

    @Override
    public boolean lock() {
        synchronized (this) {
            if(lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            } else {
                if(!thread.equals(Thread.currentThread())) {
                    return false;
                }
                lockCount.incrementAndGet();
                return true;
            }
        }

        try {
            boolean locked = false;
            locked = tryLock();
            if(locked) {
                return true;
            }
            while (!locked) {
                await();
                List<String> waiters = getWaiters();
                if(checkLocked(waiters)) {
                    locked = true;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            unlock();
        }
        return false;
    }

    private void await() throws Exception {
        if(null == prior_path) {
            throw new Exception("prior_path error");
        }
        final CountDownLatch latch = new CountDownLatch(1);
        Watcher w = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                latch.countDown();
            }
        };

//        client.getData().usingWatcher(w).forPath(prior_path);
//        TreeCache treeCache = new TreeCache(client, prior_path);
//        TreeCacheListener listener = new TreeCacheListener() {
//            @Override
//            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
//                ChildData data = event.getData();
//                if(data != null) {
//                    switch (event.getType()) {
//                        case NODE_REMOVED:
//                            latch.countDown();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        };
//        treeCache.getListenable().addListener(listener);
//        treeCache.start();
        latch.await(WAIT_TIME, TimeUnit.SECONDS);
    }

    private List<String> getWaiters() {
        return null;
    }

    private boolean checkLocked(List<String> waiters) {
        Collections.sort(waiters);
        if(locked_short_path.equals(waiters.get(0))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean unlock() {
        return false;
    }

    private boolean tryLock() throws Exception {
        try {
            locked_path = ZKclient.getInstance().createEphemeralSeqNode(LOCK_PREFIX);
            if(null == locked_path) {
                throw new Exception("zk error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        locked_short_path = getShortPath(locked_path);
        List<String> waiters = getWaiters();
        if(checkLocked(waiters)) {
            return true;
        }
        int index = Collections.binarySearch(waiters, locked_short_path);
        if(index < 0) {
            throw new Exception("节点没有找到："+locked_short_path);
        }
        prior_path = ZK_PATH + "/" + waiters.get(index - 1);
        return false;
    }

    private String getShortPath(String locked_path) {
        return "";
    }
}
