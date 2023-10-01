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

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.osgi.framework.Bundle;

/**
 * OpenHAB translator.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class OpenHabTranslator implements Translator {
    private final TranslationProvider translationProvider;
    private final Bundle bundle;
    private final LocaleProvider localeProvider;

    public OpenHabTranslator(TranslationProvider translationProvider, Bundle bundle, LocaleProvider localeProvider) {
        this.translationProvider = translationProvider;
        this.bundle = bundle;
        this.localeProvider = localeProvider;
    }

    @Override
    public Locale getLocale() {
        return localeProvider.getLocale();
    }

    @Override
    public String getText(String key) {
        var text = translationProvider.getText(bundle, key, key, getLocale());
        return text == null ? key : text;
    }

    @Override
    public String getText(String key, Object... arguments) {
        var text = translationProvider.getText(bundle, key, key, getLocale(), arguments);
        return text == null ? key : text;
    }
}
