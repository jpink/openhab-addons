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
package org.openhab.binding.entsoe.internal.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opentest4j.AssertionFailedError;

/**
 * Abstract unit test class containing helper methods.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractTest {
    private String readResource(String name) {
        try (var input = getClass().getResourceAsStream(name)) {
            if (input == null) {
                throw new AssertionFailedError("Resource '" + name + "' not found!");
            } else {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            throw new AssertionFailedError(null, ex);
        }
    }

    protected String readJson(String name) {
        return readResource(name + ".json");
    }

    public String readXml(String name) {
        return readResource(name + ".xml");
    }
}
