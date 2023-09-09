package org.openhab.binding.entsoe.internal;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.thing.ThingTypeUID;

import java.util.Set;

public class EntsoeDiscoveryService extends AbstractDiscoveryService {
    /**
     * Creates a new instance of this class with the specified parameters.
     *
     * @param supportedThingTypes                 the list of Thing types which are supported (can be null)
     * @param timeout                             the discovery timeout in seconds after which the discovery
     *                                            service automatically stops its forced discovery process (>= 0).
     * @param backgroundDiscoveryEnabledByDefault defines, whether the default for this discovery service is to
     *                                            enable background discovery or not.
     * @throws IllegalArgumentException if the timeout < 0
     */
    public EntsoeDiscoveryService(@Nullable Set<ThingTypeUID> supportedThingTypes, int timeout, boolean backgroundDiscoveryEnabledByDefault) throws IllegalArgumentException {
        super(supportedThingTypes, timeout, backgroundDiscoveryEnabledByDefault);
    }

    /**
     * This method is called by the {@link #startScan(ScanListener))} implementation of the
     * {@link AbstractDiscoveryService}.
     * The abstract class schedules a call of {@link #stopScan()} after {@link #getScanTimeout()} seconds. If this
     * behavior is not appropriate, the {@link #startScan(ScanListener))} method should be overridden.
     */
    @Override
    protected void startScan() {

    }
}
