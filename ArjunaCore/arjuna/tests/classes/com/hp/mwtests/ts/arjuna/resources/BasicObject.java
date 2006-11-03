/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors 
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
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
 * Copyright (C) 1998, 1999, 2000,
 *
 * Arjuna Solutions Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: BasicObject.java 2342 2006-03-30 13:06:17Z  $
 */

package com.hp.mwtests.ts.arjuna.resources;

import com.arjuna.ats.arjuna.*;
import com.arjuna.ats.arjuna.common.*;
import com.arjuna.ats.arjuna.state.*;

public class BasicObject extends StateManager
{

    public BasicObject ()
    {
	super(ObjectType.ANDPERSISTENT);

	state = 0;

	System.out.println("Created basic object.");

	activate();
	modified();
	deactivate();
    }

    public BasicObject (Uid u)
    {
	super(u, ObjectType.ANDPERSISTENT);

	state = -1;
	
	activate();
    }
    
    public void incr (int value)
    {
	modified();
	
	state += value;
    }
	
    public void set (int value)
    {
	modified();
	
	state = value;
    }

    public int get ()
    {
	activate();
	
	return state;
    }

    public String type ()
    {
	return "StateManager/BasicObject";
    }
    
    public boolean save_state (OutputObjectState os, int type)
    {
	try
	{
	    os.packInt(state);
	}
	catch (Exception ex)
	{
	    return false;
	}

	return true;
    }
    
    public boolean restore_state (InputObjectState os, int type)
    {
	try
	{
	    state = os.unpackInt();
	}
	catch (Exception ex)
	{
	    return false;
	}
	
	return true;
    }
    
    private int state;
    
}
