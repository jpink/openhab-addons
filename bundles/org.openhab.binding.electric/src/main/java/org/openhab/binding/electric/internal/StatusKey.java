package org.openhab.binding.electric.internal;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.ThingStatusKey;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;

public enum StatusKey implements ThingStatusKey {
    MISSING_PRICE(ThingStatusDetail.CONFIGURATION_ERROR);

    private final @Nullable ThingStatus status;
    private final @Nullable ThingStatusDetail detail;

    StatusKey() {
        this(null, null);
    }

    StatusKey(ThingStatusDetail detail) {
        this(STATUS_BY_DETAIL.get(detail), detail);
    }
    StatusKey(@Nullable ThingStatus status, @Nullable ThingStatusDetail detail) {
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
