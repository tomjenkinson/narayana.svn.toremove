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
 * Copyright (C) 2005,
 *
 * Arjuna Technologies Ltd,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: TxImporter.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.internal.jta.transaction.jts.jca;

import java.security.InvalidParameterException;
import java.util.HashMap;

import javax.transaction.xa.*;

import com.arjuna.ats.internal.jta.transaction.jts.subordinate.jca.TransactionImple;

public class TxImporter
{
	
	/**
	 * Create a subordinate transaction associated with the
	 * global transaction inflow. No timeout is associated with the
	 * transaction.
	 * 
	 * @param xid the global transaction.
	 * 
	 * @return the subordinate transaction.
	 * 
	 * @throws XAException thrown if there are any errors.
	 */
	
	public static TransactionImple importTransaction (Xid xid) throws XAException
	{
		return importTransaction(xid, 0);
	}

	/**
	 * Create a subordinate transaction associated with the
	 * global transaction inflow and having a specified timeout.
	 * 
	 * @param xid the global transaction.
	 * @param timeout the timeout associated with the global transaction.
	 * 
	 * @return the subordinate transaction.
	 * 
	 * @throws XAException thrown if there are any errors.
	 */
	
	public static TransactionImple importTransaction (Xid xid, int timeout) throws XAException
	{
		if (xid == null)
			throw new InvalidParameterException();
		
		/*
		 * Check to see if we haven't already imported this thing.
		 */
		
		TransactionImple imported = getImportedTransaction(xid);
		
		if (imported == null)
		{	
			imported = new TransactionImple(timeout, xid);
			
			_transactions.put(xid, imported);
		}
		
		return imported;
	}

	/**
	 * Get the subordinate (imported) transaction associated with the
	 * global transaction.
	 * 
	 * @param xid the global transaction.
	 * 
	 * @return the subordinate transaction or <code>null</code> if there
	 * is none.
	 * 
	 * @throws XAException thrown if there are any errors.
	 */
	
	public static TransactionImple getImportedTransaction (Xid xid) throws XAException
	{
		if (xid == null)
			throw new InvalidParameterException();
		
		return (TransactionImple) _transactions.get(xid);
	}

	/**
	 * Remove the subordinate (imported) transaction.
	 * 
	 * @param xid the global transactin.
	 * 
	 * @throws XAException thrown if there are any errors.
	 */
	
	public static void removeImportedTransaction (Xid xid) throws XAException
	{
		if (xid == null)
			throw new InvalidParameterException();
		
		_transactions.remove(xid);
	}
	
	private static HashMap _transactions = new HashMap();
	
}
