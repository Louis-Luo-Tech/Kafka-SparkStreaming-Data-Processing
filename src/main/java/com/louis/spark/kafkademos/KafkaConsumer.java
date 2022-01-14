package com.louis.spark.kafkademos;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumer extends Thread{
    private String topic;

    public KafkaConsumer(String topic){
        this.topic = topic;
    }

    private ConsumerConnector createconnector(){
        Properties properties = new Properties();
        properties.put("zookeeper.connect",KafkaProperties.ZK);
        properties.put("group.id",KafkaProperties.GROUP_ID);
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    @Override
    public void run() {
        ConsumerConnector consumer = createconnector();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic,1);
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStream =  consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = messageStream.get(topic).get(0); //get the message
        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        while(iterator.hasNext()){
            String message = new String(iterator.next().message());
            System.out.println("rec: " + message);
        }
    }
}
