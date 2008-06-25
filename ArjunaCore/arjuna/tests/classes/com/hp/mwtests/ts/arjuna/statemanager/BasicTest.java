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
 * $Id: BasicTest.java 2342 2006-03-30 13:06:17Z  $
 */

package com.hp.mwtests.ts.arjuna.statemanager;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.coordinator.*;
import com.arjuna.ats.arjuna.common.*;

import com.hp.mwtests.ts.arjuna.resources.*;

import org.jboss.dtf.testframework.unittest.Test;

public class BasicTest extends Test
{

public void run(String[] args)
    {
        try
        {
            test();

            assertSuccess();
        }
        catch (Exception e)
        {
            logInformation("Unexpected exception thrown - "+e);
            e.printStackTrace(System.err);
            assertFailure();
        }

	System.gc();

	System.runFinalization();
    }

private void test () throws Exception
    {
	AtomicAction A = new AtomicAction();
	BasicObject bo = new BasicObject();

	A.begin();

	bo.set(2);

	A.commit();
    }

public static void main(String[] args)
    {
        BasicTest test = new BasicTest();
    	test.initialise(null, null, args, new org.jboss.dtf.testframework.unittest.LocalHarness());
    	test.run(args);
    }
};
