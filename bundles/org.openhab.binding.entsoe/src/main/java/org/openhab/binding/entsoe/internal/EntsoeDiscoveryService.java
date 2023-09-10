package org.openhab.binding.entsoe.internal;

import org.openhab.binding.entsoe.internal.client.dto.Area;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Set;

import static org.openhab.binding.entsoe.internal.EntsoeBindingConstants.THING_TYPE_PRICE;

@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery." + EntsoeBindingConstants.BINDING_ID)
public class EntsoeDiscoveryService extends AbstractDiscoveryService {
    public EntsoeDiscoveryService(
            @Reference TranslationProvider i18nProvider,
            @Reference LocaleProvider localeProvider
    ) throws IllegalArgumentException {
        super(Set.of(THING_TYPE_PRICE), 10, false);
        this.i18nProvider = i18nProvider;
        this.localeProvider = localeProvider;
    }

    /**
     * This method is called by the {@link #startScan(ScanListener))} implementation of the
     * {@link AbstractDiscoveryService}.
     * The abstract class schedules a call of {@link #stopScan()} after {@link #getScanTimeout()} seconds. If this
     * behavior is not appropriate, the {@link #startScan(ScanListener))} method should be overridden.
     */
    @Override
    protected void startScan() {
        var locale = localeProvider.getLocale();
        var area = Area.of(locale);
        if (area == null) return;
        var country = locale.getCountry();
        var builder = DiscoveryResultBuilder
                .create(new ThingUID(THING_TYPE_PRICE, country))
                .withProperty("area", area.code)
                .withRepresentationProperty("area");
        if ("FI".equals(country)) builder.withProperty("tax", 2.79372F); // TODO move to resource bundle
        thingDiscovered(builder.build());
    }
}
