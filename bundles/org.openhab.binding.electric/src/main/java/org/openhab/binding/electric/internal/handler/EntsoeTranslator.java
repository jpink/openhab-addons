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
package org.openhab.binding.electric.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.openhab.OpenHabTranslator;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.osgi.framework.Bundle;

/**
 * Translator.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class EntsoeTranslator extends OpenHabTranslator implements Translations {
    public EntsoeTranslator(TranslationProvider translationProvider, Bundle bundle, LocaleProvider localeProvider) {
        super(translationProvider, bundle, localeProvider);
    }
}
