/*
 * Copyright 2021 Red Hat
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

package io.apicurio.example.kstreams;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import io.apicurio.example.schema.avro.Event;
import io.apicurio.example.schema.avro.Order;

/**
 * @author Fabian Martinez
 */
public class OrdersTransformer implements Transformer<String, Order, KeyValue<String, Event>> {

    /**
     * @see org.apache.kafka.streams.kstream.Transformer#init(org.apache.kafka.streams.processor.ProcessorContext)
     */
    @Override
    public void init(ProcessorContext context) {
    }

    /**
     * @see org.apache.kafka.streams.kstream.Transformer#transform(java.lang.Object, java.lang.Object)
     */
    @Override
    public KeyValue<String, Event> transform(String key, Order value) {

        //Imagine we are doing something useful here :)

        Event event = new Event();
        event.setName("Order processed");
        event.setDescription(String.format("Item %s quantity %d", value.getItem(), value.getQuantity()));
        event.setSource("quarkus-kafka-streams");

        return KeyValue.pair(key, event);
    }

    /**
     * @see org.apache.kafka.streams.kstream.Transformer#close()
     */
    @Override
    public void close() {
    }

}
