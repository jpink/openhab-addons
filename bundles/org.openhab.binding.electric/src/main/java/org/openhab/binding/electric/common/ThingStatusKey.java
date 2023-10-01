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
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;

import java.util.Map;

import static org.openhab.binding.electric.common.Text.toTranslationKey;
import static org.openhab.core.thing.ThingStatus.OFFLINE;
import static org.openhab.core.thing.ThingStatus.ONLINE;
import static org.openhab.core.thing.ThingStatusDetail.BRIDGE_OFFLINE;
import static org.openhab.core.thing.ThingStatusDetail.COMMUNICATION_ERROR;
import static org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_ERROR;
import static org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_PENDING;
import static org.openhab.core.thing.ThingStatusDetail.DUTY_CYCLE;
import static org.openhab.core.thing.ThingStatusDetail.FIRMWARE_UPDATING;
import static org.openhab.core.thing.ThingStatusDetail.GONE;

/**
 * Thing status key provides the translation key for the thing status description.
 * The keys should be collected to enumeration class by implementing this interface.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public interface ThingStatusKey {
    Map<ThingStatusDetail, ThingStatus> STATUS_BY_DETAIL = Map.of(
            COMMUNICATION_ERROR, OFFLINE,
            CONFIGURATION_ERROR, OFFLINE,
            CONFIGURATION_PENDING, ONLINE,
            BRIDGE_OFFLINE, OFFLINE,
            FIRMWARE_UPDATING, OFFLINE,
            DUTY_CYCLE, OFFLINE,
            GONE, OFFLINE);

    @Nullable ThingStatus getStatus();

    @Nullable ThingStatusDetail getStatusDetail();

    /**
     * The translation key suffix. Implemented in enumeration class.
     * Translated to lowercase and replacing underscores with dashes.
     */
    String name();

    default String getDescription() {
        var builder = new StringBuilder("@text/");
        var detail = getStatusDetail();
        var status = detail == null ? null : STATUS_BY_DETAIL.get(detail);
        if (status == null) {
            status = getStatus();
        }
        if (status != null) {
            builder.append(toTranslationKey(status)).append('.');
        }
        if (detail != null) {
            builder.append(toTranslationKey(detail)).append('.');
        }
        return builder.append(toTranslationKey(name())).toString();
    }
}
