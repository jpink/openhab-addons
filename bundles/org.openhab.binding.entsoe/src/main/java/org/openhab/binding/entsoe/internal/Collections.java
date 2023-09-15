package org.openhab.binding.entsoe.internal;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

public class Collections {
    @SafeVarargs
    public static <E> @NonNull List<E> listOf(E... elements) {
        return unmodifiableList(Arrays.asList(elements));
    }

    public static <E extends Comparable<? super E>> @NonNull List<E> sort(@NonNull List<E> list) {
        var sorted = new ArrayList<>(list);
        java.util.Collections.sort(sorted);
        return unmodifiableList(sorted);
    }
}
