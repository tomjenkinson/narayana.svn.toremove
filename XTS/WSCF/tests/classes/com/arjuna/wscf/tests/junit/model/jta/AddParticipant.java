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
 * $Id: AddParticipant.java,v 1.1 2003/01/07 10:33:59 nmcl Exp $
 */

package com.arjuna.wscf.tests.junit.model.jta;

import com.arjuna.mw.wscf.model.xa.*;

import com.arjuna.wscf.tests.DemoXAResource;

/**
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: AddParticipant.java,v 1.1 2003/01/07 10:33:59 nmcl Exp $
 * @since 1.0.
 */

public class AddParticipant
{

    public static void main (String[] args)
    {
	boolean passed = false;
	
	try
	{
	    javax.transaction.TransactionManager tm = TransactionManagerFactory.transactionManager();
	    
	    tm.begin();

	    tm.getTransaction().enlistResource(new DemoXAResource());
	    
	    System.out.println("Started: "+tm+"\n");

	    tm.commit();

	    passed = true;
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	
	if (passed)
	    System.out.println("\nPassed.");
	else
	    System.out.println("\nFailed.");
    }

}
