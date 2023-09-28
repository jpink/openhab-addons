package org.openhab.binding.entsoe.internal.common;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.util.Locale;

@NonNullByDefault
public interface Translator {
    Locale getLocale();

    String getText(String key);

    String getText(String key, Object... arguments);
}
