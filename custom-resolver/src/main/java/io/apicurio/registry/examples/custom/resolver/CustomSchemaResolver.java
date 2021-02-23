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

package io.apicurio.registry.examples.custom.resolver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.kafka.common.header.Headers;

import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;
import io.apicurio.registry.rest.v2.beans.ArtifactMetaData;
import io.apicurio.registry.rest.v2.beans.IfExists;
import io.apicurio.registry.serde.ParsedSchema;
import io.apicurio.registry.serde.SchemaLookupResult;
import io.apicurio.registry.serde.SchemaParser;
import io.apicurio.registry.serde.SchemaResolver;
import io.apicurio.registry.serde.SerdeConfigKeys;
import io.apicurio.registry.serde.avro.AvroSchemaUtils;
import io.apicurio.registry.serde.strategy.ArtifactResolverStrategy;
import io.apicurio.registry.types.ArtifactType;

/**
 * A custom schema resolve that simply uses the Avro schema found in the {@link Config}
 * class - and ensures that the schema exists in the registry.
 * @author eric.wittmann@gmail.com
 */
public class CustomSchemaResolver<D> implements SchemaResolver<Schema, D> {

    private RegistryClient client;
    @SuppressWarnings("unused")
    private ArtifactResolverStrategy<Schema> resolverStrategy;

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#setClient(io.apicurio.registry.rest.client.RegistryClient)
     */
    @Override
    public void setClient(RegistryClient client) {
        this.client = client;
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#configure(java.util.Map, boolean, io.apicurio.registry.serde.SchemaParser)
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey, SchemaParser<Schema> schemaMapper) {
        if (client == null) {
            String baseUrl = (String) configs.get(SerdeConfigKeys.REGISTRY_URL);
            if (baseUrl == null) {
                throw new IllegalArgumentException("Missing registry base url, set " + SerdeConfigKeys.REGISTRY_URL);
            }

            try {
                client = RegistryClientFactory.create(baseUrl);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#setArtifactResolverStrategy(io.apicurio.registry.serde.strategy.ArtifactResolverStrategy)
     */
    @Override
    public void setArtifactResolverStrategy(ArtifactResolverStrategy<Schema> artifactResolverStrategy) {
        this.resolverStrategy = artifactResolverStrategy;
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#resolveSchema(java.lang.String, org.apache.kafka.common.header.Headers, java.lang.Object, java.util.Optional)
     */
    @SuppressWarnings("unchecked")
    @Override
    public SchemaLookupResult<Schema> resolveSchema(String topic, Headers headers, D data, Optional<ParsedSchema<Schema>> parsedSchema) {
        System.out.println("[CustomSchemaResolver] Resolving a schema for topic: " + topic);
        String schema = Config.SCHEMA;

        String groupId = "default";
        String artifactId = topic + "-value";
        Schema schemaObj = AvroSchemaUtils.parse(schema);

        ByteArrayInputStream schemaContent = new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8));
        // Ensure the schema exists in the schema registry.
        ArtifactMetaData metaData = client.createArtifact(groupId, artifactId, ArtifactType.AVRO, IfExists.RETURN_OR_UPDATE, schemaContent);
        // Note, we could be caching the globalId here rather than hit the registry every time.

        @SuppressWarnings("rawtypes")
        SchemaLookupResult result = SchemaLookupResult.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(String.valueOf(metaData.getVersion()))
                .globalId(metaData.getGlobalId())
                .schema(schemaObj)
                .rawSchema(schema.getBytes(StandardCharsets.UTF_8))
                .build();

        return result;
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#resolveSchemaByGlobalId(long)
     */
    @Override
    public SchemaLookupResult<Schema> resolveSchemaByGlobalId(long globalId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#resolveSchemaByCoordinates(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public SchemaLookupResult<Schema> resolveSchemaByCoordinates(String groupId, String artifactId,
            String version) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see io.apicurio.registry.serde.SchemaResolver#reset()
     */
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

}
