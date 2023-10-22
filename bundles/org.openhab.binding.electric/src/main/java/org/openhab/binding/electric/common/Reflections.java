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

import java.io.Serial;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Reflection helpers which throw only runtime exceptions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
@SuppressWarnings("unchecked")
public class Reflections {
    private static final class DoesNotReflect extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1;

        private DoesNotReflect(Exception cause) {
            super(cause);
        }
    }

    public static <T> Constructor<T> constructor(Class<T> type, Class<?>... parameters) {
        try {
            return type.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            throw new DoesNotReflect(e);
        }
    }

    public static <T> Constructor<T> constructor(String type, Class<?>... parameters) {
        return constructor(type(type), parameters);
    }

    public static <T> T create(String type, Object... arguments) {
        return create(type(type), arguments);
    }

    public static <T> T create(Class<T> type, Object... arguments) {
        return create(constructor(type, Arrays.stream(arguments).map(Object::getClass).toArray(Class[]::new)), arguments);
    }

    public static <T> T create(Constructor<T> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new DoesNotReflect(e);
        }
    }

    public static <T> Class<T> type(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new DoesNotReflect(e);
        }
    }

    public static Set<Class<?>> types(String[] names) {
        return Arrays.stream(names).map(Reflections::type).collect(Collectors.toSet());
    }

    public static Method method(String type, String method, Class<?>... parameters) {
        return method(type(type), method, parameters);
    }

    public static Method method(Class<?> type, String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            throw new DoesNotReflect(e);
        }
    }

    /** Invoke instance method. */
    public static <T> T invoke(Object instance, Method method, Object... arguments) {
        try {
            return (T) method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DoesNotReflect(e);
        }
    }

    /** Invoke static method. */
    public static <T> T invoke(Method method, Object... arguments) {
        try {
            return (T) method.invoke(null, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DoesNotReflect(e);
        }
    }

    /** Invoke static method. */
    public static <T> T invoke(Class<?> type, String name) {
        return invoke(method(type, name));
    }
}
