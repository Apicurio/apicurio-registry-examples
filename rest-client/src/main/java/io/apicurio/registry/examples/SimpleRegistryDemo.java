package io.apicurio.registry.examples;

import io.apicurio.registry.client.RegistryRestClient;
import io.apicurio.registry.client.RegistryRestClientFactory;
import io.apicurio.registry.examples.util.RegistryDemoUtil;

import java.util.UUID;


/**
 * Simple demo app that shows how to use the client.
 * <p>
 * 1) Register a new schema in the Registry.
 * 2) Fetch the newly created schema.
 * 3) Delete the schema.
 *
 * @author Carles Arnal <carnalca@redhat.com>
 */
public class SimpleRegistryDemo {

    private static final RegistryRestClient service;

    static {
        // Create a Service Registry client
        String registryUrl = "http://localhost:8080/api/";
        service = RegistryRestClientFactory.create(registryUrl);
    }

    public static void main(String[] args) throws Exception {

        // Register the JSON Schema schema in the Apicurio registry.
        final String artifactId = UUID.randomUUID().toString();

        RegistryDemoUtil.createSchemaInServiceRegistry(service, artifactId, Constants.SCHEMA);

        //Wait for the artifact to be available.
        Thread.sleep(1000);

        RegistryDemoUtil.getSchemaFromRegistry(service, artifactId);

        RegistryDemoUtil.deleteSchema(service, artifactId);

        //TODO remove with the release of the closeable version of the registry
        System.exit(0);
    }
}
