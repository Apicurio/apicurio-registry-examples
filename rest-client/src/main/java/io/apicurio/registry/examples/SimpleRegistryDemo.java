package io.apicurio.registry.examples;

import java.util.UUID;

import io.apicurio.registry.examples.util.RegistryDemoUtil;
import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;


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

    private static final RegistryClient client;

    static {
        // Create a Service Registry client
        String registryUrl = "http://localhost:8080/apis/registry/v2";
        client = RegistryClientFactory.create(registryUrl);
    }

    public static void main(String[] args) throws Exception {

        // Register the JSON Schema schema in the Apicurio registry.
        final String artifactId = UUID.randomUUID().toString();

        RegistryDemoUtil.createSchemaInServiceRegistry(client, artifactId, Constants.SCHEMA);

        //Wait for the artifact to be available.
        Thread.sleep(1000);

        RegistryDemoUtil.getSchemaFromRegistry(client, artifactId);

        RegistryDemoUtil.deleteSchema(client, artifactId);

        //TODO remove with the release of the closeable version of the registry
        System.exit(0);
    }
}
