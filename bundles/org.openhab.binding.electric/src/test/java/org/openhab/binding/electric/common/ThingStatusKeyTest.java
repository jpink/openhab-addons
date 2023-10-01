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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.core.thing.ThingStatus.OFFLINE;
import static org.openhab.core.thing.ThingStatusDetail.COMMUNICATION_ERROR;
import static org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_ERROR;

/**
 * Thing status key unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class ThingStatusKeyTest {
    enum TestKey implements ThingStatusKey {
        SYSTEM_HALTED(null, null),
        UNKNOWN_PROPERTY(null, CONFIGURATION_ERROR),
        POWERED_OFF(OFFLINE, null),
        BROKEN_WIRE(OFFLINE, COMMUNICATION_ERROR);

        @Nullable final ThingStatus status;
        @Nullable final ThingStatusDetail detail;

        TestKey(@Nullable ThingStatus status, @Nullable ThingStatusDetail detail) {
            this.status = status;
            this.detail = detail;
        }

        @Override
        public @Nullable ThingStatus getStatus() {
            return status;
        }

        @Override
        public @Nullable ThingStatusDetail getStatusDetail() {
            return detail;
        }
    }

    static Stream<Arguments> getDescription() {
        return Stream.of(
                Arguments.of("@text/system-halted", TestKey.SYSTEM_HALTED),
                Arguments.of("@text/offline.configuration-error.unknown-property", TestKey.UNKNOWN_PROPERTY),
                Arguments.of("@text/offline.powered-off", TestKey.POWERED_OFF),
                Arguments.of("@text/offline.communication-error.broken-wire", TestKey.BROKEN_WIRE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getDescription(String expected, ThingStatusKey instance) {
        assertEquals(expected, instance.getDescription());
    }
}