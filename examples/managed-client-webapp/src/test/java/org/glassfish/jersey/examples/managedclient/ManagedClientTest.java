/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jersey.examples.managedclient;

import java.net.URI;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Jersey managed client example tests.
 *
 * @author Marek Potociar
 */
public class ManagedClientTest extends JerseyTest {

    @Override
    protected ResourceConfig configure() {
        // mvn test -Djersey.config.test.container.factory=org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory
        // mvn test -Djersey.config.test.container.factory=org.glassfish.jersey.test.grizzly.GrizzlyTestContainerFactory
        // mvn test -Djersey.config.test.container.factory=org.glassfish.jersey.test.jdkhttp.JdkHttpServerTestContainerFactory
        enable(TestProperties.LOG_TRAFFIC);
        // enable(TestProperties.DUMP_ENTITY);

        final MyApplication app = new MyApplication();
        // overriding ClientA base Uri property for test purposes
        app.property(ClientA.class.getName() + ".baseUri", UriBuilder.fromUri(getBaseUri()).path("internal").build());
        return app;
    }

    @Override
    protected URI getBaseUri() {
       return UriBuilder.fromUri(super.getBaseUri()).path("managed-client-webapp").build();
    }

//    Uncomment to use Grizzly async client
//    @Override
//    protected void configureClient(ClientConfig clientConfig) {
//        clientConfig.connector(new GrizzlyConnector(clientConfig));
//    }

    /**
     * Test that a connection via managed clients works properly.
     *
     * @throws Exception in case of test failure.
     */
    @Test
    public void testManagedClient() throws Exception {
        final WebTarget resource = target().path("public").path("{name}");
        Response response;

        response = resource.resolveTemplate("name", "a").request(MediaType.TEXT_PLAIN).get();
        assertEquals(200, response.getStatus());
        assertEquals("a", response.readEntity(String.class));

        response = resource.resolveTemplate("name", "b").request(MediaType.TEXT_PLAIN).get();
        assertEquals(200, response.getStatus());
        assertEquals("b", response.readEntity(String.class));
    }
}
