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
 * $Id: wstxLogger.java,v 1.2 2005/05/19 12:13:40 nmcl Exp $
 */

package com.arjuna.mw.wstx.logging;

import com.arjuna.common.util.logging.*;

import com.arjuna.common.internal.util.logging.commonPropertyManager;
import com.arjuna.ats.arjuna.common.arjPropertyManager;

import java.util.*;

public class wstxLogger
{

    public static LogNoi18n      arjLogger;
    public static Logi18n        arjLoggerI18N;
    public static ResourceBundle log_mesg;

    private static String language;
    private static String country;
    private static Locale currentLocale;
    private static String dirLocale;

    static
    {
    /** Ensure the properties are loaded before initialising the logger **/
    arjPropertyManager.getPropertyManager();
        
	arjLogger = LogFactory.getLogNoi18n("com.arjuna.mw.wstx.logging.wstxLogger");

	language = commonPropertyManager.propertyManager.getProperty("language","en");
	country  = commonPropertyManager.propertyManager.getProperty("country","US");

	currentLocale = new Locale(language,country);
	log_mesg = ResourceBundle.getBundle("wstx_msg",currentLocale);
	
	arjLoggerI18N = LogFactory.getLogi18n("com.arjuna.mw.wstx.logging.wstxLoggerI18N",
					     "wstx_msg_"+language+"_"+country);
	
    }

}
