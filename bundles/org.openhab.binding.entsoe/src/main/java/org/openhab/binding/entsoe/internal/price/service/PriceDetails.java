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
package org.openhab.binding.entsoe.internal.price.service;

import java.time.ZoneId;

import org.eclipse.jdt.annotation.NonNullByDefault;

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
public record PriceDetails(ZoneId zone, ProductPrice transfer, ProductPrice tax, VatRate sellersVatRate,
        ProductPrice margin) {
}
