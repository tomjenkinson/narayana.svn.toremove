/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
/*
 * Copyright (C) 2002,
 *
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: TransactionHandle.java,v 1.1 2002/11/25 11:00:53 nmcl Exp $
 */

package com.arjuna.mw.wstx.transaction;

/**
 * TransactionHandle is used as a representation of a single transaction
 * when it is suspended from a running thread and may be later
 * resumed. The implementation of the token can be as lightweight
 * as required by the underlying implementation in order that it
 * can uniquely represent all activity instances.
 *
 * Since this is a client-facing class, it is unlikely that the
 * application user will typically want to see the entire activity
 * context in order to simply suspend it from the thread.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: TransactionHandle.java,v 1.1 2002/11/25 11:00:53 nmcl Exp $
 * @since 1.0.
 */

public interface TransactionHandle
{
    /**
     * Although users won't typically care what the underlying implementation
     * of a context is, they will need to do comparisons.
     * So, although this method is provided by Java.Object we have it here
     * to ensure that we don't forget to implement it!
     *
     * Two instances are equal if the refer to the same transaction.
     */

    public boolean equals (Object obj);
    
    /**
     * Although users won't typically care what the underlying implementation
     * of a context is, they will need to do comparisons.
     * So, although this method is provided by Java.Object we have it here
     * to ensure that we don't forget to implement it!
     */

    public int hashCode ();

    /**
     * @return whether or not this is a valid handle.
     */

    public boolean valid ();

    /**
     * @return the activity identifier.
     */

    public String tid ();

}
