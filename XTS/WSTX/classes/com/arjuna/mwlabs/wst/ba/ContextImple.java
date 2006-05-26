/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag.  All rights reserved. 
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
 * $Id: ContextImple.java,v 1.3.6.1 2005/11/22 10:36:14 kconner Exp $
 */

package com.arjuna.mwlabs.wst.ba;

import com.arjuna.webservices.wscoor.CoordinationContextType;

public class ContextImple implements com.arjuna.mw.wsc.context.Context
{
    public ContextImple (CoordinationContextType ctx)
    {
        _coordContext = ctx;
    }

    public boolean equals (Object obj)
    {
        if (obj instanceof ContextImple)
        {
    	    ContextImple ci = (ContextImple) obj;
    	    
            return ci.getCoordinationContext().getIdentifier().getValue().equals(_coordContext.getIdentifier().getValue());
        }
        else
            return false;
    }

    public CoordinationContextType getCoordinationContext ()
    {
    	return _coordContext;
    }
    
    public void setCoordinationContext (CoordinationContextType cc)
    {
    	_coordContext = cc;
    }

    public String toString ()
    {
    	return "BusinessActivityIdentifier: "+_coordContext.getIdentifier().getValue();
    }

    private CoordinationContextType _coordContext;
}
