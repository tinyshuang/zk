package com.tinys.curator.elections;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * leader选举
 */
public class LeaderSelectorExample {
    private static final String ZK_ADDRESS = "192.168.198.128:2222";

    public static void main(String[] args) {

        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelectorClient> examples = Lists.newArrayList();
        try {
            for (int i = 0; i < 10; i++) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new RetryNTimes(10, 5000));

                LeaderSelectorClient example = new LeaderSelectorClient(client, "Client #" + i);
                clients.add(client);
                examples.add(example);

                client.start();
                example.start();
            }

            System.out.println("----------先观察一会选举的结果-----------");
            Thread.sleep(10000);

            System.out.println("----------关闭前5个客户端，再观察选举的结果-----------");
            for (int i = 0; i < 5; i++) {
                clients.get(i).close();
            }

            // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出
            new BufferedReader(new InputStreamReader(System.in)).readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (LeaderSelectorClient exampleClient : examples) {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
