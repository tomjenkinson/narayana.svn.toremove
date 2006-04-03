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
package com.hp.mwtests.ts.jta.jts.common;

import com.arjuna.ats.jta.resources.EndXAResource;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class LastXAResource implements XAResource, EndXAResource
{
    public void commit(Xid id, boolean onePhase) throws XAException
    {
        System.out.println("LastXAResource XA_COMMIT[" + id + "]");
    }

    public void end(Xid xid, int flags) throws XAException
    {
        System.out.println("LastXAResource XA_END[" + xid + "] Flags=" + flags);
    }

    public void forget(Xid xid) throws XAException
    {
        System.out.println("LastXAResource XA_FORGET[" + xid + "]");
    }

    public int getTransactionTimeout() throws XAException
    {
        return 0;
    }

    public boolean isSameRM(XAResource xares) throws XAException
    {
        return xares.equals(this);
    }

    public int prepare(Xid xid) throws XAException
    {
        System.out.println("LastXAResource XA_PREPARE[" + xid + "]");

        return XA_OK;
    }

    public Xid[] recover(int flag) throws XAException
    {
        System.out.println("LastXAResource RECOVER["+ flag +"]");

        return null;
    }

    public void rollback(Xid xid) throws XAException
    {
        System.out.println("LastXAResource XA_ROLLBACK[" + xid + "]");
    }

    public boolean setTransactionTimeout(int seconds) throws XAException
    {
        return true;
    }

    public void start(Xid xid, int flags) throws XAException
    {
        System.out.println("LastXAResource XA_START[" + xid + "] Flags=" + flags);
    }
    
}
