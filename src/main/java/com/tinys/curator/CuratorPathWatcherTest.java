package com.tinys.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;

/**
 * 监听器的简单例子
 */
public class CuratorPathWatcherTest {
    private static final String ZK_ADDRESS = "192.168.198.128:2222";
    private static final String ZK_PATH = "/zktest";

    public static void main(String[] args) throws Exception {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();
        System.out.println("zk client start successfully!");

        // 2.Register watcher : 会监视一个路径下1）孩子结点的创建、2）删除，3）以及结点数据的更新
        PathChildrenCache watcher = new PathChildrenCache(
                client,
                ZK_PATH,
                true    // if cache data
        );

        //产生的事件会通知到listener
        watcher.getListenable().addListener(
                new PathChildrenCacheListener() {
                    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                        ChildData data = event.getData();
                        if (data == null) {
                            System.out.println("No data in event[" + event + "]");
                        } else {
                            System.out.println("Receive event: "
                                    + "type=[" + event.getType() + "]"
                                    + ", path=[" + data.getPath() + "]"
                                    + ", data=[" + new String(data.getData()) + "]"
                                    + ", stat=[" + data.getStat() +"]");
                        }
                    }
                }
        );


        watcher.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        System.out.println("Register zk watcher successfully!");

        Thread.sleep(Integer.MAX_VALUE);
    }
}
