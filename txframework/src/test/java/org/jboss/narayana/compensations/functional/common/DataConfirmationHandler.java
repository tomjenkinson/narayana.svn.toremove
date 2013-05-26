/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.narayana.compensations.functional.common;

import org.jboss.narayana.compensations.api.CompensationHandler;
import org.jboss.narayana.compensations.api.ConfirmationHandler;
import org.jboss.narayana.txframework.api.exception.TransactionDataUnavailableException;
import org.jboss.narayana.txframework.api.management.TXDataMap;

import javax.inject.Inject;

/**
 * @author paul.robinson@redhat.com 22/04/2013
 */
public class DataConfirmationHandler implements ConfirmationHandler {

    private static Boolean dataAvailable = false;
    private static Boolean dataNotNull = false;

    @Inject
    TXDataMap<String, String> data;

    @Override
    public void confirm() {

        if (data != null) {
            dataNotNull = true;
            try {
                if (data.get("key") != null && data.get("key").equals("value")) {
                    dataAvailable = true;
                }
            } catch (TransactionDataUnavailableException e) {
                //unavailable
            }
        }
    }

    public static boolean getDataAvailable() {

        return dataAvailable;
    }

    public static void reset() {

        dataAvailable = false;
        dataNotNull = false;
    }

    public static Boolean getDataNotNull() {

        return dataNotNull;
    }
}
