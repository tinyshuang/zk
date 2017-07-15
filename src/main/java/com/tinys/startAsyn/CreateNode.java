package com.tinys.startAsyn;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 创建节点
 */
public class CreateNode implements Watcher {
    ZooKeeper zk;
    String hostport = "192.168.198.128:2222";

    /**
     * 连接zk
     * @throws IOException
     */
    private void startZk() throws IOException {
        zk = new ZooKeeper(hostport,15000,this);
    }

    /**
     * 关闭zk连接
     * @throws InterruptedException
     */
    private void stopZk() throws InterruptedException {
        zk.close();
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    /**
     * 创建父节点
     */
    private void create(){
        createParent("/workers",new  byte[0]);
        createParent("/assign",new  byte[0]);
        createParent("/tasks",new  byte[0]);
        createParent("/status",new  byte[0]);
    }


    /**
     * 创建节点的回调通知
     */
    AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    createParent(path, (byte[]) ctx);
                    break;
                case OK:
                    System.out.println("parent created");
                    break;
                case NODEEXISTS:
                    System.out.println("parent already register: " + path);
                    break;
                 default:
                     System.out.println("Something wrong" + path + KeeperException.create(KeeperException.Code.get(rc)));
            }
        }
    };

    /**
     * 创建永久性节点
     * @param path
     * @param data
     */
    private void createParent(String path, byte[] data) {
        zk.create(path,data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,createParentCallback,data);
    }

    public static  void main(String[] args) throws Exception {
      CreateNode cn = new CreateNode();
      cn.startZk();
      cn.create();
      cn.stopZk();
    }

}
