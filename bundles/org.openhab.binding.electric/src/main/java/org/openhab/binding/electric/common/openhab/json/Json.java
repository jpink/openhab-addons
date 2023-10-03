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
package org.openhab.binding.electric.common.openhab.json;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * JSON serialization helper.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Json {
    public abstract static class NullSafeAdapter<T> extends TypeAdapter<T> {
        protected abstract void encode(JsonWriter out, T value) throws IOException;

        /**
         * Writes one JSON value (an array, object, string, number, boolean or null) for {@code value}.
         *
         * @param out JSON writer.
         * @param value the Java object to write. May be null.
         */
        @Override
        public final void write(JsonWriter out, @Nullable T value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                encode(out, value);
            }
        }

        protected abstract T decode(JsonReader in) throws IOException;

        /**
         * Reads one JSON value (an array, object, string, number, boolean or null) and converts it to a Java object.
         * Returns the converted object.
         *
         * @param in JSON reader.
         * @return the converted Java object. May be null.
         */
        @Override
        public final @Nullable T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return decode(in);
            }
        }
    }

    public static class StringAdapter<T> extends NullSafeAdapter<T> {
        private final Function<String, T> parser;

        public StringAdapter(Function<String, T> parser) {
            this.parser = parser;
        }

        @Override
        protected final void encode(JsonWriter out, T value) throws IOException {
            out.value(value == null ? "" : value.toString());
        }

        @Override
        protected final T decode(JsonReader in) throws IOException {
            return parser.apply(in.nextString());
        }
    }

    public record SubclassAdapterFactory<S> (Class<S> superType, TypeAdapter<S> adapter) implements TypeAdapterFactory {
        /**
         * Returns a type adapter for {@code type}, or null if this factory doesn't support {@code type}.
         *
         * @param gson Gson instance.
         * @param type Type token.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <T> @Nullable TypeAdapter<T> create(@Nullable Gson gson, @Nullable TypeToken<T> type) {
            return type != null && superType.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) adapter : null;
        }
    }

    public static final StringAdapter<Duration> DURATION = new StringAdapter<>(Duration::parse);
    public static final StringAdapter<LocalDateTime> LOCAL_DATE_TIME = new StringAdapter<>(LocalDateTime::parse);
    public static final StringAdapter<Object> STRING = new StringAdapter<>(value -> {
        throw new UnsupportedOperationException("Parsing not supported!");
    });
    public static final SubclassAdapterFactory<ZoneId> ZONE = new SubclassAdapterFactory<>(ZoneId.class,
            new StringAdapter<>(ZoneId::of));
    public static final StringAdapter<ZonedDateTime> ZONED_DATE_TIME = new StringAdapter<>(ZonedDateTime::parse);

    private static final Gson GSON = builder().create();

    public static GsonBuilder builder() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME)
                .registerTypeAdapter(ZonedDateTime.class, ZONED_DATE_TIME).registerTypeAdapterFactory(ZONE);
    }

    public static String encode(@Nullable Object src) {
        return GSON.toJson(src);
    }
}
