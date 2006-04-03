/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and others contributors as indicated 
 * by the @authors tag. All rights reserved. 
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
package com.arjuna.ats.tools.objectstorebrowser.treenodes;

import com.arjuna.ats.arjuna.coordinator.AbstractRecord;

/*
 * Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004
 *
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: ListEntryNode.java 2342 2006-03-30 13:06:17Z  $
 */

public class ListEntryNode extends EntryNode
{
    private ListEntryNodeListener   _listener = null;
    private Object                  _associatedObject;

    public ListEntryNode(Object userObject, Object associatedObject, String type)
    {
        super(userObject, type);

        _associatedObject = associatedObject;
    }

    public ListEntryNode(Object userObject, boolean allowsChildren)
    {
        super(userObject, allowsChildren);
    }

    public void registerListEntryNodeListener(ListEntryNodeListener listener)
    {
        _listener = listener;
    }

    public ListEntryNodeListener getListEntryNodeListener()
    {
        return _listener;
    }

    public Object getAssociatedObject()
    {
        return _associatedObject;
    }
}
