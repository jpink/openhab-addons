package org.openhab.binding.entsoe.internal;

import org.openhab.binding.entsoe.internal.common.OpenHabTranslator;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.osgi.framework.Bundle;

public class EntsoeTranslator extends OpenHabTranslator implements Translations {
    public EntsoeTranslator(TranslationProvider translationProvider, Bundle bundle, LocaleProvider localeProvider) {
        super(translationProvider, bundle, localeProvider);
    }
}
