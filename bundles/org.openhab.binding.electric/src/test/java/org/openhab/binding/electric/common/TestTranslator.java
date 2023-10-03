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

import static org.openhab.binding.electric.internal.old.Constants.BINDING_ID;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.internal.handler.Translations;

/**
 * Test translator.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class TestTranslator implements Translator, Translations {
    private final Locale locale;
    private final ResourceBundle bundle;

    public TestTranslator(Locale locale) {
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("OH-INF.i18n." + BINDING_ID, locale);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String getText(String key) {
        return bundle.getString(key);
    }

    @Override
    public String getText(String key, Object... arguments) {
        return MessageFormat.format(getText(key), arguments);
    }
}
