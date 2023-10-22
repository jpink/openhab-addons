package org.openhab.binding.electric.common.openhab.thing;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

@NonNullByDefault
public abstract class AbstractThingHandlerFactory extends BaseThingHandlerFactory {
    private final Map<ThingTypeUID, Function<Thing, ThingHandler>> factories;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractThingHandlerFactory(Map<ThingTypeUID, Function<Thing, ThingHandler>> factories) {
        this.factories = factories;
    }

    @Override
    public void activate(ComponentContext componentContext) {
        super.activate(componentContext);
    }

    @Override
    public void deactivate(ComponentContext componentContext) {
        super.deactivate(componentContext);
    }

    @Override
    public final boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return factories.containsKey(thingTypeUID);
    }

    @Override
    protected final @Nullable ThingHandler createHandler(Thing thing) {
        var type = thing.getThingTypeUID();
        logger.debug("Creating handler for {} type.", type);
        var factory = factories.get(type);
        return factory == null ? null : factory.apply(thing);
    }
}
