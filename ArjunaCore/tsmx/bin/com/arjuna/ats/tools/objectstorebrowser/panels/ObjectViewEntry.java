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
/*
 * Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003
 *
 * Arjuna Technologies Ltd.
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: ObjectViewEntry.java 2342 2006-03-30 13:06:17Z  $
 */
package com.arjuna.ats.tools.objectstorebrowser.panels;

import com.arjuna.ats.tools.toolsframework.iconpanel.IconPanelEntry;
import com.arjuna.ats.tools.objectstorebrowser.treenodes.*;

public class ObjectViewEntry extends IconPanelEntry
{
	private final static String OBJECT_VIEW_ICON_FILENAME = "object-icon.gif";

	private String	_tn;
    private ObjectStoreBrowserNode _node;

	public ObjectViewEntry(String tn, String label, int state, ObjectStoreBrowserNode node)
	{
		super(label, OBJECT_VIEW_ICON_FILENAME);

		_tn = tn;
        _node = node;

		/** Set tooltip to the state of the underlying object **/
		this.setToolTipText(com.arjuna.ats.arjuna.objectstore.ObjectStore.stateStatusString(state));
	}

	public String getUID()
	{
		return getLabelText();
	}

    public ObjectStoreBrowserNode getNode()
    {
        return _node;
    }

	public String getTypeName()
	{
		return _tn;
	}
}
