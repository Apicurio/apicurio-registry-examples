package io.apicurio.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import io.apicurio.example.schema.avro.Event;

@Service
public class Consumer {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = ApicurioKafkaDemoApp.EVENTS_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event message) {
        log.info("Consumer consumed message {} from topic {}", message, ApicurioKafkaDemoApp.EVENTS_TOPIC);
    }
}
