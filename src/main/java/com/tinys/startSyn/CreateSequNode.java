package com.tinys.startSyn;

import com.tinys.startAsyn.CreateNode;
import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 创建有序节点
 */
public class CreateSequNode implements Watcher{
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    ZooKeeper zk;
    String hostPort;

    CreateSequNode(String hostPort){
        this.hostPort = hostPort;
    }


    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort,15000,this);
    }


    String createNode(String command) throws Exception {
        String name = null;
        while(true) try {
            name = zk.create("/tasks/task-", command.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            return name;
        } catch (KeeperException.NodeExistsException e) {
           throw  new Exception(name +" 节点已存在");
        } catch (KeeperException.ConnectionLossException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static  void main(String args[]) throws Exception{
        CreateSequNode create = new CreateSequNode("192.168.198.128:2222");
        create.startZk();

        String name = create.createNode("tinys");
        System.out.println("creatad" + name);
    }
}
