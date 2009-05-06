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
 * (C) 2006,
 * @author JBoss Inc.
 */
package com.arjuna.common.internal.util.logging.jakarta;

import com.arjuna.common.internal.util.logging.LogFactoryInterface;
import com.arjuna.common.internal.util.logging.AbstractLogInterface;
import com.arjuna.common.util.exceptions.LogConfigurationException;

/**
 * JavaDoc
 *
 * @author Jonathan Halliday <jonathan.halliday@redhat.com>
 * @version $Revision: 2342 $ $Date: 2006-03-30 14:06:17 +0100 (Thu, 30 Mar 2006) $
 */
public class JakartaRelevelingLogFactory implements LogFactoryInterface
{
   /**
    * Convenience method to return a named logger, without the application
    * having to care about factories.
    *
    * @param clazz Class for which a log name will be derived
    *
    * @exception com.arjuna.common.util.exceptions.LogConfigurationException if a suitable <code>Log</code>
    *  instance cannot be returned
    */
   public AbstractLogInterface getLog(Class clazz) throws LogConfigurationException
   {
      Object oldConfig = null;
      try
      {
         // configure the underlying apache factory
         oldConfig = configureFactory();
         // get a new logger from the log subsystem's factory and wrap it into a LogInterface
          return new JakartaRelevelingLogger(org.apache.commons.logging.LogFactory.getLog(clazz));
      }
      catch (org.apache.commons.logging.LogConfigurationException lce)
      {
         throw new LogConfigurationException(lce.getMessage(), lce);
      }
      finally
      {
          resetFactory(oldConfig);
      }
   }

   /**
    * Convenience method to return a named logger, without the application
    * having to care about factories.
    *
    * @param name Logical name of the <code>Log</code> instance to be
    *  returned (the meaning of this name is only known to the underlying
    *  logging implementation that is being wrapped)
    *
    * @exception LogConfigurationException if a suitable <code>Log</code>
    *  instance cannot be returned
    */
   public AbstractLogInterface getLog(String name) throws LogConfigurationException
   {
      Object oldConfig = null;
      try
      {
         // configure the underlying apache factory
         oldConfig = configureFactory();
         // get a new logger from the log subsystem's factory and wrap it into a LogInterface
          return new JakartaRelevelingLogger(org.apache.commons.logging.LogFactory.getLog(name));
      }
      catch (org.apache.commons.logging.LogConfigurationException lce)
      {
         throw new LogConfigurationException(lce.getMessage(), lce);
      }
      finally
      {
          resetFactory(oldConfig);
      }
   }

   /*
        Note: the apache LogFactory configuration is basically global to the JVM (actually the classloader)
        This braindead design decision has the potential to cause trouble, since multiple users may each
        set factory attributes and there is no thread safety.  This occurs e.g. when we run embedded in
        JBossAS.  To minimise the risks (we can't eliminate then entirely without using an isolated classloader)
        we set the configuration we need on each factory use and reset it afterwards, so as to try and
        minimise interference with anything else that may be overriding the same factory attributes.
    */


   /**
    * Install our custom logger by setting the factory attribute
    */
   private Object configureFactory()
   {
       Object oldValue = org.apache.commons.logging.LogFactory.getFactory().getAttribute("org.apache.commons.logging.Log");
       org.apache.commons.logging.LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", Log4JLogger.class.getName());
       return oldValue;
   }

   /**
     * Restore the factory configuration to the provided value.
     */
   private void resetFactory(Object value)
   {
           org.apache.commons.logging.LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", value);
   }
}
