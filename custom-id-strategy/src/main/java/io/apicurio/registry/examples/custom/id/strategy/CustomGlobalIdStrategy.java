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

package io.apicurio.registry.examples.custom.id.strategy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import io.apicurio.registry.client.RegistryService;
import io.apicurio.registry.rest.beans.ArtifactMetaData;
import io.apicurio.registry.rest.beans.IfExistsType;
import io.apicurio.registry.types.ArtifactType;
import io.apicurio.registry.utils.serde.strategy.GlobalIdStrategy;

/**
 * A custom global id strategy that simply uses the Avro schema found in the {@link Config}
 * class - and ensures that the schema exists in the registry.
 * @author eric.wittmann@gmail.com
 */
@SuppressWarnings("deprecation")
public class CustomGlobalIdStrategy<T> implements GlobalIdStrategy<T> {

    /**
     * @see io.apicurio.registry.utils.serde.strategy.GlobalIdStrategy#findId(io.apicurio.registry.client.RegistryService, java.lang.String, io.apicurio.registry.types.ArtifactType, java.lang.Object)
     */
    @Override
    public long findId(RegistryService service, String artifactId, ArtifactType artifactType, T t) {
        try {
            String schema = Config.SCHEMA;
            ByteArrayInputStream schemaContent = new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8));
            // Ensure the schema exists in the schema registry.
            ArtifactMetaData metaData = service.createArtifact(ArtifactType.AVRO, artifactId, IfExistsType.RETURN_OR_UPDATE, schemaContent).toCompletableFuture().get();
            // Note, we could be caching the globalId here rather than hit the registry every time.
            return metaData.getGlobalId();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
