package com.louis.spark.kafkademos;

public class KafkaClientApp {
    public static void main(String[] args) {

        new KafkaConsumer(KafkaProperties.TOPIC).start();
        new KafkaProducer(KafkaProperties.TOPIC).start();

    }
}
