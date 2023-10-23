/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.electric.common.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Dictionary;
import java.util.Map;
import java.util.jar.Manifest;

import javax.xml.stream.XMLInputFactory;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.openhab.binding.electric.common.UnitTest;
import org.openhab.binding.electric.common.osgi.mock.MockBundle;
import org.openhab.binding.electric.common.osgi.mock.MockBundleContext;
import org.openhab.binding.electric.common.osgi.mock.MockFramework;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * Abstract OSGi unit tests which needs bundle context.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class BundleTest<I> extends UnitTest<I> implements FrameworkFactory {
    private static final String LOCATION = "pom.xml";
    private static final byte[] MANIFEST;

    static {
        try {
            // Read manifest data from POM.
            String artifactId = null, name = null, version = null;
            var reader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(LOCATION));
            while (reader.hasNext() && (artifactId == null || name == null || version == null)) {
                var event = reader.nextEvent();
                if (event.isStartElement()) {
                    switch (event.asStartElement().getName().getLocalPart()) {
                        case "artifactId" -> artifactId = event.asCharacters().getData();
                        case "name" -> name = event.asCharacters().getData();
                        case "version" -> version = event.asCharacters().getData();
                    }
                }
            }
            var manifest = new Manifest();
            var attributes = manifest.getMainAttributes();
            attributes.put(Constants.BUNDLE_SYMBOLICNAME, artifactId);
            attributes.put(Constants.BUNDLE_NAME, name);
            attributes.put(Constants.BUNDLE_VERSION, version);
            var output = new ByteArrayOutputStream();
            manifest.write(output);
            MANIFEST = output.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Can't build manifest from POM!", e);
        }
    }

    private final MockFramework framework = newFramework(null);
    protected final MockBundle bundle;
    protected final MockBundleContext bundleContext;

    protected BundleTest() {
        try {
            framework.init();
            bundle = framework.context().installBundle(LOCATION, new ByteArrayInputStream(MANIFEST));
            bundleContext = bundle.context();
        } catch (BundleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NonNullByDefault({})
    public MockFramework newFramework(@Nullable Map<String, String> configuration) {
        return new MockFramework();
    }

    protected <S> ServiceRegistration<? super S> registerService(Class<S> type, S service) {
        return registerService(type, service, null);
    }

    protected <S> ServiceRegistration<? super S> registerService(Class<S> type, S service,
            @Nullable Dictionary<String, ?> properties) {
        return bundleContext.registerService(type, service, properties);
    }

    @AfterEach
    public void tearDown() throws Exception {
        framework.stop();
        framework.waitForStop(0);
    }
}
