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
 * Copyright (C) 2001, 2002,
 *
 * Hewlett-Packard Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: HammerServer.java 2342 2006-03-30 13:06:17Z  $
 */

package com.hp.mwtests.ts.jts.remote.servers;

import com.hp.mwtests.ts.jts.resources.*;
import com.hp.mwtests.ts.jts.orbspecific.resources.*;
import com.hp.mwtests.ts.jts.TestModule.*;

import com.arjuna.orbportability.*;

import com.arjuna.ats.jts.extensions.*;

import com.arjuna.ats.internal.jts.OTSImpleManager;
import com.arjuna.ats.internal.jts.ORBManager;
import com.arjuna.ats.internal.jts.orbspecific.TransactionFactoryImple;
import com.arjuna.ats.internal.jts.orbspecific.CurrentImple;
import com.arjuna.mwlabs.testframework.unittest.Test;
import com.arjuna.mwlabs.testframework.unittest.LocalHarness;

import org.omg.CosTransactions.*;

import org.omg.CosTransactions.Unavailable;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.INVALID_TRANSACTION;

public class HammerServer extends Test
{
    
    public void run(String[] args)
    {
	ORB myORB = null;
	RootOA myOA = null;

	try
	{
	    myORB = ORB.getInstance("test");
	    myOA = OA.getRootOA(myORB);
	    
	    myORB.initORB(args, null);
	    myOA.initOA();

	    ORBManager.setORB(myORB);
	    ORBManager.setPOA(myOA);
	}
	catch (Exception e)
	{
	    System.err.println("Initialisation failed: "+e);
            e.printStackTrace(System.err);
            assertFailure();
	}

	String refFile1 = "/tmp/hammer1.ref";
	String refFile2 = "/tmp/hammer2.ref";

	if (System.getProperty("os.name").startsWith("Windows"))
	{
	    refFile1 = "C:\\temp\\hammer1.ref";
	    refFile2 = "C:\\temp\\hammer2.ref";
	}

	String refFile = null;
	int number = 0;

	for (int i = 0; i < args.length; i++)
	{
	    if (args[i].compareTo("-reffile") == 0)
		refFile = args[i+1];
	    if (args[i].compareTo("-server") == 0)
	    {
		if (args[i+1].compareTo("1") == 0)
		    number = 1;
		else
		    number = 2;
	    }
	    if (args[i].compareTo("-help") == 0)
	    {
		System.out.println("Usage: HammerServer -server <1 | 2> [-reffile <file>] [-help]");
		assertFailure();
	    }
	}
	
	if ((number != 1) && (number != 2))
	{
	    System.out.println("Usage: HammerServer -server <1 | 2> [-reffile <file>] [-help]");
	    assertFailure();
	}

	if ( refFile == null )
	{
		if (number == 1)
			refFile = refFile1;
		else
			refFile = refFile2;
	}

	HammerPOATie theObject = new HammerPOATie(new HammerObject());

	myOA.objectIsReady(theObject);

	Services serv = new Services(myORB);
	
	try
	{
            registerService( refFile, myORB.orb().object_to_string(HammerHelper.narrow(myOA.corbaReference(theObject))) );

	    System.out.println("**HammerServer started**");

	    System.out.println("\nIOR file: "+refFile);

            assertSuccess();
            assertReady();
	    myOA.run();
	}
	catch (Exception e)
	{
	    System.err.println("HammerServer caught exception: "+e);
            e.printStackTrace(System.err);
	    assertFailure();
	}

	myOA.shutdownObject(theObject);

	System.out.println("**HammerServer exiting**");
    }

    public static void main(String[] args)
    {
        HammerServer server = new HammerServer();
        server.initialise(null, null, args, new LocalHarness());
        server.runTest();
    }
}

