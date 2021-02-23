/*
 * Copyright 2020 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.registry.examples.avro.bean;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.apicurio.registry.serde.SerdeConfigKeys;
import io.apicurio.registry.serde.avro.AvroDatumProvider;
import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import io.apicurio.registry.serde.avro.ReflectAvroDatumProvider;

/**
 * This example demonstrates how to use the Apicurio Registry in a very simple publish/subscribe
 * scenario with Avro as the serialization type.  The following aspects are demonstrated:
 *
 * <ol>
 *   <li>Configuring a Kafka Serializer for use with Apicurio Registry</li>
 *   <li>Configuring a Kafka Deserializer for use with Apicurio Registry</li>
 *   <li>Auto-register the Avro schema in the registry (registered by the producer)</li>
 *   <li>Data sent as a {@link GreetingBean}</li>
 * </ol>
 *
 * Pre-requisites:
 *
 * <ul>
 *   <li>Kafka must be running on localhost:9092</li>
 *   <li>Apicurio Registry must be running on localhost:8080</li>
 * </ul>
 *
 * @author eric.wittmann@gmail.com
 */
public class AvroBeanExample {

    private static final String REGISTRY_URL = "http://localhost:8080/apis/registry/v2";
    private static final String SERVERS = "localhost:9092";
    private static final String TOPIC_NAME = AvroBeanExample.class.getSimpleName();
    private static final String SUBJECT_NAME = "Greeting";


    public static final void main(String [] args) throws Exception {
        System.out.println("Starting example " + AvroBeanExample.class.getSimpleName());
        String topicName = TOPIC_NAME;
        String subjectName = SUBJECT_NAME;

        // Create the producer.
        Producer<Object, Object> producer = createKafkaProducer();
        // Produce 5 messages.
        int producedMessages = 0;
        try {
            System.out.println("Producing (5) messages.");
            for (int idx = 0; idx < 5; idx++) {
                GreetingBean greeting = new GreetingBean();
                greeting.setMessage("Hello (" + producedMessages++ + ")!");
                greeting.setTime(System.currentTimeMillis());


                // Send/produce the message on the Kafka Producer
                ProducerRecord<Object, Object> producedRecord = new ProducerRecord<>(topicName, subjectName, greeting);
                producer.send(producedRecord);

                Thread.sleep(100);
            }
            System.out.println("Messages successfully produced.");
        } finally {
            System.out.println("Closing the producer.");
            producer.flush();
            producer.close();
        }

        // Create the consumer
        System.out.println("Creating the consumer.");
        KafkaConsumer<Long, GreetingBean> consumer = createKafkaConsumer();

        // Subscribe to the topic
        System.out.println("Subscribing to topic " + topicName);
        consumer.subscribe(Collections.singletonList(topicName));

        // Consume the 5 messages.
        try {
            int messageCount = 0;
            System.out.println("Consuming (5) messages.");
            while (messageCount < 5) {
                final ConsumerRecords<Long, GreetingBean> records = consumer.poll(Duration.ofSeconds(1));
                messageCount += records.count();
                if (records.count() == 0) {
                    // Do nothing - no messages waiting.
                    System.out.println("No messages waiting...");
                } else records.forEach(record -> {
                    GreetingBean greeting = record.value();
                    System.out.println("Consumed a message: " + greeting.getMessage() + " @ " + new Date(greeting.getTime()));
                });
            }
        } finally {
            consumer.close();
        }

        System.out.println("Done (success).");
        System.exit(0);
    }

    /**
     * Creates the Kafka producer.
     */
    private static Producer<Object, Object> createKafkaProducer() {
        Properties props = new Properties();

        // Configure kafka settings
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, "Producer-" + TOPIC_NAME);
        props.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Use the Apicurio Registry provided Kafka Serializer for Avro
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());

        // Configure Service Registry location
        props.putIfAbsent(SerdeConfigKeys.REGISTRY_URL, REGISTRY_URL);
        props.putIfAbsent(SerdeConfigKeys.AUTO_REGISTER_ARTIFACT, Boolean.TRUE);
        // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
        props.putIfAbsent(AvroDatumProvider.REGISTRY_AVRO_DATUM_PROVIDER_CONFIG_PARAM, ReflectAvroDatumProvider.class.getName());

        // Create the Kafka producer
        Producer<Object, Object> producer = new KafkaProducer<>(props);
        return producer;
    }

    /**
     * Creates the Kafka consumer.
     */
    private static KafkaConsumer<Long, GreetingBean> createKafkaConsumer() {
        Properties props = new Properties();

        // Configure Kafka
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
        props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "Consumer-" + TOPIC_NAME);
        props.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.putIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // Use the Apicurio Registry provided Kafka Deserializer for Avro
        props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroKafkaDeserializer.class.getName());

        // Configure Service Registry location
        props.putIfAbsent(SerdeConfigKeys.REGISTRY_URL, REGISTRY_URL);
        // Use Java reflection as the Avro Datum Provider
        props.putIfAbsent(AvroDatumProvider.REGISTRY_AVRO_DATUM_PROVIDER_CONFIG_PARAM, ReflectAvroDatumProvider.class.getName());
        // No other configuration needed for the deserializer, because the globalId of the schema
        // the deserializer should use is sent as part of the payload.  So the deserializer simply
        // extracts that globalId and uses it to look up the Schema from the registry.

        // Create the Kafka Consumer
        KafkaConsumer<Long, GreetingBean> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

}
