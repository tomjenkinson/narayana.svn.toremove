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
package com.hp.mwtests.ts.arjuna.atomicaction;

import java.util.Hashtable;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.coordinator.CheckedAction;

import org.junit.Test;
import static org.junit.Assert.*;

class DummyCheckedAction extends CheckedAction
{
    public void check (boolean isCommit, Uid actUid, Hashtable list)
    {
        _called = true;
    }
    
    public boolean called ()
    {
        return _called;
    }
    
    private boolean _called;
}

public class CheckedActionTest
{
    @Test
    public void test()
    {
        arjPropertyManager.getCoordinatorEnvironmentBean().setCheckedActionFactory(DummyCheckedActionFactory.class.getCanonicalName());

        AtomicAction A = new AtomicAction();

        A.begin();

        A.commit();

        assertTrue(success);
    }
    
    @Test
    public void testCheckedAction ()
    {
        AtomicAction A = new AtomicAction();
        DummyCheckedAction dca = new DummyCheckedAction();
        
        A.begin();
        
        /*
         * CheckedAction only called if there are multiple
         * threads active in the transaction. Simulate this.
         */
        
        A.addChildThread(new Thread());
        
        A.setCheckedAction(dca);

        A.commit();

        assertTrue(dca.called());
    }

    public static boolean success = false;
}
