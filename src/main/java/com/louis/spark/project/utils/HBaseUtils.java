package com.louis.spark.project.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Hbase operation
 */
public class HBaseUtils {
    Admin admin = null;
    Connection conn = null;
    Configuration configuration = null;

    private HBaseUtils() {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "localhost");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.rootdir", "hdfs://localhost:9000/hbase");
        try {
            conn = ConnectionFactory.createConnection(configuration);
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HBaseUtils instance = null;

    public static synchronized HBaseUtils getInstance() {
        if (instance == null) {
            instance = new HBaseUtils();
        }
        return instance;
    }

    public void insert(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        Table table = conn.getTable(TableName.valueOf(tableName));
        table.put(put);
    }

    public List<TableDescriptor> listTables() throws IOException {
        List<TableDescriptor> tableDescriptors = admin.listTableDescriptors();
        return tableDescriptors;
    }

//    public HTable getTable(String tablename){
//        HTable table = null;
//        try {
//            table = new HTable(configuration, tablename);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        return table;

    public Table getTableName(String tableName) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;

    }

    public static void main(String[] args) throws IOException {
//        List tables = HBaseUtils.getInstance().listTables();
////        Iterator it1 = tables.iterator();
////        while(it1.hasNext()){
////            System.out.println(it1.next());
////        }
//        Table table = HBaseUtils.getInstance().getTableName("product_clickcount");
//        System.out.println(table.getName().getNameAsString());
//
//        String tableName = "product_clickcount";
//        String rowKey = "20191111_3223442";
//        String columnFamily = "info";
//        String column = "click_count";
//        String value = "2";
//
//        HBaseUtils.getInstance().insert(tableName, rowKey, columnFamily, column, value);

    }


}


