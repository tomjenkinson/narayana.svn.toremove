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
 * Copyright (C) 1998, 1999, 2000, 2001, 2002
 *
 * Arjuna Technologies Ltd.
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: AgentInterface.java 2342 2006-03-30 13:06:17Z  $
 */
package com.arjuna.ats.tsmx.agent;

import com.arjuna.ats.tsmx.agent.exceptions.AgentNotFoundException;

import javax.management.*;

/**
 * This interface is implemented to allow different MBeanServers to be used.
 *
 * @author Richard A. Begg (richard.begg@arjuna.com)
 * @version $Id: AgentInterface.java 2342 2006-03-30 13:06:17Z  $
 */
public interface AgentInterface
{
	public MBeanServer getAgent() throws AgentNotFoundException;
}
