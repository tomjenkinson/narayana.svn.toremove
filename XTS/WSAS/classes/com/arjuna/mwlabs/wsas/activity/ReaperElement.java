/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
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
 * $Id: ReaperElement.java,v 1.3 2004/03/15 13:25:00 nmcl Exp $
 */

package com.arjuna.mwlabs.wsas.activity;

import com.arjuna.ats.internal.arjuna.template.OrderedListElement;

public class ReaperElement implements OrderedListElement
{

    /*
     * Currently, once created the reaper object and thread stay around
     * forever.
     * We could destroy both once the list of transactions is null. Depends
     * upon the relative cost of recreating them over keeping them around.
     */

    public ReaperElement (ActivityImple act, int timeout)
    {
	_activity = act;
	_timeout = timeout;

	/*
	 * Given a timeout period in seconds, calculate its absolute value
	 * from the current time of day in milliseconds.
	 */
	
	_absoluteTimeout = timeout*1000 + System.currentTimeMillis();
    }

    public final boolean equals (OrderedListElement e)
    {
	if (e instanceof ReaperElement)
	    return (_absoluteTimeout == ((ReaperElement) e)._absoluteTimeout);
	else
	    return false;
    }

    public final boolean lessThan (OrderedListElement e)
    {
	if (e instanceof ReaperElement)
	    return (_absoluteTimeout < ((ReaperElement)e)._absoluteTimeout);
	else
	    return false;
    }

    public final boolean greaterThan (OrderedListElement e)
    {
	if (e instanceof ReaperElement)
	    return (_absoluteTimeout > ((ReaperElement)e)._absoluteTimeout);
	else
	    return false;
    }

    public ActivityImple _activity;
    public long          _absoluteTimeout;
    public int           _timeout;
    
}



