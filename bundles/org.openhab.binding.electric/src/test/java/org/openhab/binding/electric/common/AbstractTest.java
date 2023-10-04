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
package org.openhab.binding.electric.common;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.internal.handler.Translations;
import org.opentest4j.AssertionFailedError;

/**
 * Abstract test class containing helper methods.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractTest {
    public Path generatedResourcePath(String name) {
        var packages = getClass().getPackageName().split("\\.");
        var more = new String[packages.length + 1];
        more[0] = "generated-test-resources";
        System.arraycopy(packages, 0, more, 1, packages.length);
        var path = Path.of("target", more);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            fail(e);
        }
        return path.resolve(name);
    }

    public File generatedResourceFile(String name) {
        return generatedResourcePath(name).toFile();
    }

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

    protected Translations translations() {
        return translations(Locale.ENGLISH);
    }

    protected Translations translations(Locale locale) {
        return new TestTranslator(locale);
    }
}
