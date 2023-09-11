package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.MarketDocument;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.client.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/** Electricity Price Service */
@NonNullByDefault
public class PriceService {
    /**
     * The fixed price parameters for price service.
     *
     * @param zone The target zone for date times.
     * @param transfer The fixed electricity transfer fee.
     * @param tax The fixed energy tax amount.
     * @param sellersVatRate Value added tax rate for spot price. Usually the general one.
     * @param margin The fixed sellers margin price.
     */
    @NonNullByDefault
    public record Parameters(ZoneId zone, ProductPrice transfer, ProductPrice tax, VatRate sellersVatRate,
                             ProductPrice margin) {
    }

    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final Parameters parameters;
    private final EntsoeClient client;

    public PriceService(Parameters parameters, EntsoeClient client) {
        this.parameters = parameters;
        this.client = client;
    }

    private <T> T bug(Throwable t) throws Bug {
        logger.error("Please report the following bug!", t);
        throw new Bug(t);
    }

    private MarketDocument searchDayAheadPrices(ZonedDateTime start)
            throws Bug, InterruptedException, TimeoutException, Unauthorized {
        try {
            return client.getDayAheadPrices(start, start.plusDays(1));
        } catch (ExecutionException | InvalidParameter | TooLong | TooMany | TooShort | UnknownResponse e) {
            return bug(e);
        }
    }

    private Publication getDayAheadPrices(ZonedDateTime start)
            throws Bug, InterruptedException, TimeoutException, Unauthorized {
        try {
            return (Publication) searchDayAheadPrices(start);
        } catch (ClassCastException e) {
            return bug(e);
        }
    }

    public void refresh() throws Bug, InterruptedException, TimeoutException, Unauthorized {
        //TODO check current status
        var publication = getDayAheadPrices(ZonedDateTime.now());
    }

}
