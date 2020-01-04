import com.google.common.util.concurrent.RateLimiter;
import com.xuren.Application;
import com.xuren.config.ZookeeperConfig;
import com.xuren.factorys.ZookeeperClientFactory;
import com.xuren.utils.IDMaker;
import com.xuren.utils.RedisOperator;
import junit.framework.TestCase;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryForever;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class TestA {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Autowired
    RedisOperator redisOperator;

    @Test
    public void test() {



        StopWatch stopWatch = new StopWatch();
        stopWatch.start("test");
        redisOperator.set("name", "xuren");
        String name = redisOperator.get("name");
        TestCase.assertEquals("xuren",name);
        BigDecimal bigDecimal = new BigDecimal(20);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        RateLimiter r = RateLimiter.create(2);
        while (true){
            System.out.println(r.acquire());
        }
    }

    @Test
    public void testZookeeper() {

        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();

        String data= "hello";
        try {
            byte[] payload = data.getBytes("UTF-8");
            String zkPath = "/test/CRUD/node-";
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(zkPath, payload);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }

    }

    @Test
    public void readZookeeper() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        String zkPath = "/test/CRUD/node-1";
        try {
            if(client.checkExists().forPath(zkPath) != null) {
                byte[] bytes = client.getData().forPath(zkPath);
                String data = new String(bytes);
                System.out.println(data);

                List<String> list =  client.getChildren().forPath(zkPath);
                for(String str : list) {
                    logger.info(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void deleteZookeeper() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        String zkPath = "/test/CRUD/node-1";
        try {
            if(client.checkExists().forPath(zkPath) != null) {
                client.delete().forPath(zkPath);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void updateZookeeper() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        String zkPaht = "/test/CRUD/node-1";

        String h2 = "hello2";
        try {
            if(client.checkExists().forPath(zkPaht) != null) {
                client.setData().forPath(zkPaht, h2.getBytes("UTF-8"));
                logger.info("update succ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void updateAsyncZookeeper() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        String zkPaht = "/test/CRUD/node-1";

        String h2 = "hello3";

        AsyncCallback.StringCallback callback = new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                System.out.println("" +
                        "rc= " + rc + "|" +
                        "path= " + path + "|" +
                        "ctx= " + ctx + "|" +
                        "name= " + name + "|");
            }
        };

        try {
            client.setData().inBackground(callback).forPath(zkPaht, h2.getBytes(Charset.defaultCharset()));
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void testIDMaker() {
        IDMaker idMaker = new IDMaker();
        idMaker.init();
        String uid = idMaker.makeId("/test/id-test/uid-");
        logger.info(uid);
        idMaker.destory();
    }
    @Test
    public void testConfig() {
        logger.info(zookeeperConfig.getAddress());
    }

    @Test
    public void testWatcher() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        client.start();
        String path = "/test/watch";
        try {
//            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "000000".getBytes());

            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("监听到变化"+event);
                }
            };

            byte[] cotent = client.getData().usingWatcher(watcher).forPath(path);
            System.out.println("监听节点变化:");
            client.setData().forPath(path, "第一次更改内容".getBytes());
            client.setData().forPath(path, "第er次更改内容".getBytes());

            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Test
    public void testCacheWatacher() {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        String path = "/test/cachewatch";
        client.start();
        try {
            if(client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }

            NodeCache nodeCache = new NodeCache(client, path, false);
            NodeCacheListener listener = new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    ChildData childData = nodeCache.getCurrentData();
                    logger.info("path:"+ childData.getPath());
                    byte[] content = childData.getData();
                    logger.info("data:"+ new String(content));
                    logger.info("state:" + childData.getStat());
                }
            };

            nodeCache.getListenable().addListener(listener);
            nodeCache.start();

            client.setData().forPath(path, "dddd1".getBytes());
            Thread.sleep(1000);
            client.setData().forPath(path, "dddd2".getBytes());
            Thread.sleep(1000);
            client.setData().forPath(path, "dddd3".getBytes());
            Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPathChildrenCahce() throws Exception {
        CuratorFramework client = ZookeeperClientFactory.createSimple(zookeeperConfig.getAddress());
        String path = "/test/pathcache";
        String subPath = "/test/pathcache/id-";

        client.start();
        if(client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        }

        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        logger.info("add");
                        break;
                    case CHILD_REMOVED:
                        logger.info("remove");
                        break;
                    case CHILD_UPDATED:
                        logger.info("update");
                        break;
                    default:
                        break;
                }
            }
        };

        PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
        childrenCache.getListenable().addListener(listener);
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }



    @Before
    public void testBefore() {
        System.out.println("before");
    }

    @After
    public void testAfter() {
        System.out.println("after");
    }
}
