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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.apicurio.example.schema.avro.Event;
import io.apicurio.example.schema.avro.Order;
import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerdeConfig;
import io.apicurio.registry.serde.avro.AvroSerde;
import io.apicurio.registry.serde.avro.strategy.RecordIdStrategy;

/**
 * @author Fabian Martinez
 */
@ApplicationScoped
public class KStreamsTopologyProducer {

    private static final String ORDERS_TOPIC = "orders";
    private static final String EVENTS_TOPIC = "events";

    @ConfigProperty(name = "apicurio.registry.url")
    String registryUrl;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        Map<String, Object> ordersConfig = new HashMap<>();
        ordersConfig.put(SerdeConfig.REGISTRY_URL, registryUrl);
        ordersConfig.put(AvroKafkaSerdeConfig.USE_SPECIFIC_AVRO_READER, true);
        ordersConfig.put(SerdeConfig.ARTIFACT_RESOLVER_STRATEGY, RecordIdStrategy.class);

        AvroSerde<Order> ordersSerde = new AvroSerde<>();
        ordersSerde.configure(ordersConfig, false);

        Map<String, Object> eventsConfig = new HashMap<>();
        eventsConfig.put(SerdeConfig.REGISTRY_URL, registryUrl);
        eventsConfig.put(SerdeConfig.FIND_LATEST_ARTIFACT, true);
        eventsConfig.put(AvroKafkaSerdeConfig.USE_SPECIFIC_AVRO_READER, true);
        eventsConfig.put(SerdeConfig.ARTIFACT_RESOLVER_STRATEGY, RecordIdStrategy.class);

        AvroSerde<Event> eventsSerde = new AvroSerde<>();
        eventsSerde.configure(eventsConfig, false);

        builder.stream(
                    ORDERS_TOPIC,
                    Consumed.with(Serdes.String(), ordersSerde)
                )
                .transform(() -> new OrdersTransformer())
                .to(
                    EVENTS_TOPIC,
                    Produced.with(Serdes.String(), eventsSerde)
                );

        return builder.build();
    }

}