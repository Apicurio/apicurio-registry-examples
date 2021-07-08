package io.apicurio.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.apicurio.example.schema.avro.Event;

@RestController
@RequestMapping(value = "/kafka")
public class Resource {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Producer producer;

    @PostMapping(value = "/publish")
    public void publish(@RequestBody InputEvent event) {
        log.info("REST Controller has received entity: {}", event);
        Event avroEvent = new Event();
        avroEvent.setName(event.getName());
        avroEvent.setDescription(event.getDescription());
        this.producer.send(avroEvent);
    }

}