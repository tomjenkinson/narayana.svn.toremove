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
 * $Id: Coordinating.java,v 1.1 2002/11/25 10:56:35 nmcl Exp $
 */

package com.arjuna.mw.wscf.status;

import com.arjuna.mw.wsas.status.Status;

/**
 * The Coordinator is coordinating.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: Coordinating.java,v 1.1 2002/11/25 10:56:35 nmcl Exp $
 * @since 1.0.
 */

public class Coordinating implements Status
{

    public static Coordinating instance ()
    {
	return _instance;
    }
    
    public String toString ()
    {
	return "Status.Coordinating";
    }

    private Coordinating ()
    {
    }

    private static final Coordinating _instance = new Coordinating();
    
}
