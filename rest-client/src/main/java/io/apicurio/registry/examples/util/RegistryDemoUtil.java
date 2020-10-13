package io.apicurio.registry.examples.util;

import io.apicurio.registry.client.RegistryRestClient;
import io.apicurio.registry.rest.beans.ArtifactMetaData;
import io.apicurio.registry.rest.beans.IfExistsType;
import io.apicurio.registry.types.ArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class RegistryDemoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryDemoUtil.class);

    /**
     * Create the artifact in the registry (or update it if it already exists).
     *
     * @param artifactId
     * @param schema
     */
    public static void createSchemaInServiceRegistry(RegistryRestClient service, String artifactId, String schema) {

        LOGGER.info("---------------------------------------------------------");
        LOGGER.info("=====> Creating artifact in the registry for JSON Schema with ID: {}", artifactId);
        try {
            final ByteArrayInputStream content = new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8));
            final ArtifactMetaData metaData = service.createArtifact(artifactId, ArtifactType.JSON, IfExistsType.RETURN, content);
            assert metaData != null;
            LOGGER.info("=====> Successfully created JSON Schema artifact in Service Registry: {}", metaData);
            LOGGER.info("---------------------------------------------------------");
        } catch (Exception t) {
            throw t;
        }
    }

    /**
     * Get the artifact from the registry.
     *
     * @param artifactId
     */
    public static ArtifactMetaData getSchemaFromRegistry(RegistryRestClient service, String artifactId) {

        LOGGER.info("---------------------------------------------------------");
        LOGGER.info("=====> Fetching artifact from the registry for JSON Schema with ID: {}", artifactId);
        try {
            final ArtifactMetaData metaData = service.getArtifactMetaData(artifactId);
            assert metaData != null;
            LOGGER.info("=====> Successfully fetched JSON Schema artifact in Service Registry: {}", metaData);
            LOGGER.info("---------------------------------------------------------");
            return metaData;
        } catch (Exception t) {
            throw t;
        }
    }

    /**
     * Delete the artifact from the registry.
     *
     * @param artifactId
     */
    public static void deleteSchema(RegistryRestClient service, String artifactId) {

        LOGGER.info("---------------------------------------------------------");
        LOGGER.info("=====> Deleting artifact from the registry for JSON Schema with ID: {}", artifactId);
        try {
            service.deleteArtifact(artifactId);
            LOGGER.info("=====> Successfully deleted JSON Schema artifact in Service Registry.");
            LOGGER.info("---------------------------------------------------------");
        } catch (Exception t) {
            throw t;
        }
    }
}
