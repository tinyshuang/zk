package com.tinys.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.util.Collection;

/**
 * 事务的简单操作
 */
public class TransactionExamples {
    private static final String ZK_ADDRESS = "192.168.198.128:2222";
     private static CuratorFramework client = CuratorFrameworkFactory.newClient(
            ZK_ADDRESS,
            new RetryNTimes(10, 5000)
    );

    public static void main(String[] args) {
        try {
            client.start();
            // 开启事务
            CuratorTransaction transaction = client.inTransaction();

            Collection<CuratorTransactionResult> results = transaction.create()
                    .forPath("/a/path", "some data".getBytes()).and().setData()
                    .forPath("/another/path", "other data".getBytes()).and().delete().forPath("/yet/another/path")
                    .and().commit();

            for (CuratorTransactionResult result : results) {
                System.out.println(result.getForPath() + " - " + result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放客户端连接
            CloseableUtils.closeQuietly(client);
        }

    }
}
