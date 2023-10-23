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
package org.openhab.binding.electric.common.openhab;

import javax.measure.Unit;
import javax.measure.format.UnitFormat;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * XStream adapter.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Xml {
    private static class UnitConverter implements SingleValueConverter {
        private final UnitFormat format = tech.units.indriya.format.SimpleUnitFormat.getInstance();

        @Override
        public @Nullable String toString(@Nullable Object obj) {
            return obj == null ? null : obj.toString();
        }

        @Override
        public @Nullable Object fromString(@Nullable String str) {
            if (str == null) {
                return null;
            }
            // ENTSO-E units are for some reason all in capitals.
            return format.parse(str.replace("KW", "kW").replace("WH", "Wh"));
        }

        @Override
        @SuppressWarnings("rawtypes")
        public boolean canConvert(@Nullable Class type) {
            return type != null && Unit.class.isAssignableFrom(type);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final XStream xstream = new XStream(new StaxDriver());

    public Xml(Class<?>... models) {
        if (models.length < 1)
            throw new IllegalArgumentException("At least one DTO needed!");
        xstream.setClassLoader(models[0].getClassLoader());
        xstream.registerConverter(new UnitConverter());
        for (Class<?> model : models) {
            logger.debug("Allowing and processing {} annotations.", model);
            xstream.allowTypeHierarchy(model);
            xstream.processAnnotations(model);
        }
        xstream.ignoreUnknownElements();
    }

    public Object deserialize(String xml) {
        logger.trace("Deserializing:\n{}", xml);
        return xstream.fromXML(xml);
    }
}
