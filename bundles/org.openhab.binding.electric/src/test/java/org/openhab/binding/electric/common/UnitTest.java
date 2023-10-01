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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Abstract unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@ExtendWith(MockitoExtension.class)
@NonNullByDefault
public abstract class UnitTest<I> {
    /** An instance of the class to be unit tested. The mock is only to avoid NPE warnings. */
    @SuppressWarnings("unchecked")
    private @Nullable I instance;

    /**
     * Create a new instance before each test.
     *
     * @return The instance to be tested.
     */
    protected abstract I create();

    protected I getInstance() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }
}
