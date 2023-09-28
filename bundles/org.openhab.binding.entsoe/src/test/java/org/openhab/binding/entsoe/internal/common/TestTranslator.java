package org.openhab.binding.entsoe.internal.common;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.entsoe.internal.Translations;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.openhab.binding.entsoe.internal.Constants.BINDING_ID;

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
