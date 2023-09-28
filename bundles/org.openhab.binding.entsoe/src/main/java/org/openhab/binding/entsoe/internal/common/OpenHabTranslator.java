package org.openhab.binding.entsoe.internal.common;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.osgi.framework.Bundle;

import java.util.Locale;

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
