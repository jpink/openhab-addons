package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

/** Generic holder to edit record values. */
@NonNullByDefault
public class Holder<T> {
    @NonNull
    public T value;

    public Holder(@NonNull T value) {
        this.value = value;
    }

}
