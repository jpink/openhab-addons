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
package org.openhab.binding.entsoe.internal.common;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

/**
 * Logging extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Log {
    /**
     * One dynamic parameter
     * 
     * @see <a href="see http://www.slf4j.org/faq.html#logging_performance">Logging performance</a>
     */
    private static final String ONE = "{}";

    public static void debug(Logger logger, Object message) {
        logger.debug(ONE, message);
    }

    public static void error(Logger logger, Object message, Exception exception) {
        logger.error(ONE, message, exception);
    }

    public static void trace(Logger logger, Object message) {
        logger.trace(ONE, message);
    }
}
