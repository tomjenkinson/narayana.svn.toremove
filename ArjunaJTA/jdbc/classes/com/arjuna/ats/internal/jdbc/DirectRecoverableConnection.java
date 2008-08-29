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
 * Copyright (C) 1998, 1999, 2000, 2001,
 *
 * Arjuna Solutions Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.  
 *
 * $Id: DirectRecoverableConnection.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.internal.jdbc;

import com.arjuna.ats.jdbc.logging.*;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.ModifierFactory;
import com.arjuna.ats.internal.jdbc.drivers.modifiers.ConnectionModifier;

import com.arjuna.ats.arjuna.common.*;
import com.arjuna.ats.arjuna.state.*;

import com.arjuna.ats.jta.*;
import com.arjuna.ats.jta.xa.RecoverableXAConnection;
import com.arjuna.ats.jta.xa.XAModifier;
import com.arjuna.ats.jta.exceptions.NotImplementedException;

import com.arjuna.common.util.logging.*;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.transaction.*;
import javax.transaction.xa.*;

/**
 * This class is responsible for maintaining connection information
 * in such a manner that we can recover the connection to the XA
 * database in the event of a failure.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: DirectRecoverableConnection.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 2.0.
 */

public class DirectRecoverableConnection implements RecoverableXAConnection, ConnectionControl
{

    public DirectRecoverableConnection () throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.CONSTRUCTORS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.DirectRecoverableConnection()");
	}

	_dbName = null;
	_user = null;
	_passwd = null;
	_dynamic = null;
	_theConnection = null;
	_theDataSource = null;
	_dynamicConnection = null;
	_theXAResource = null;
	_theTransaction = null;
	_theArjunaConnection = null;
	_theModifier = null;
    }
    
    public DirectRecoverableConnection (String dbName, String user,
				      String passwd, String dynamic,
				      ConnectionImple conn) throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.CONSTRUCTORS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.DirectRecoverableConnection( "+dbName+", "+user+", "+passwd+", "+dynamic+" )");
	}

	_dbName = dbName;
	_user = user;
	_passwd = passwd;
	_dynamic = dynamic;
	_theConnection = null;
	_theDataSource = null;
	_dynamicConnection = null;
	_theXAResource = null;
	_theTransaction = null;
	_theArjunaConnection = conn;
	_theModifier = null;
    }

    /**
     * @message com.arjuna.ats.internal.jdbc.drcdest Caught exception
     */

    public void finalize ()
    {
	try
	{
	    if (_theConnection != null)
	    {
		_theConnection.close();
		_theConnection = null;
	    }
	}
	catch (SQLException e)
	{
	    if (jdbcLogger.loggerI18N.isWarnEnabled())
	    {
		jdbcLogger.loggerI18N.warn("com.arjuna.ats.internal.jdbc.drcdest",
					   new Object[] {e});
	    }
	}
    }

    public boolean packInto (OutputObjectState os)
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.packInto ()");
	}

	try
	{
	    os.packString(_dbName);
	    os.packString(_user);
	    os.packString(_passwd);
	    os.packString(_dynamic);

	    return true;
	}
	catch (Exception e)
	{
	    return false;
	}
    }
    
    public boolean unpackFrom (InputObjectState os)
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.unpackFrom ()");
	}

	try
	{
	    _dbName = os.unpackString();
	    _user = os.unpackString();
	    _passwd = os.unpackString();
	    _dynamic = os.unpackString();
	    
	    return true;
	}
	catch (Exception e)
	{
	    return false;
	}
    }

    public boolean setTransaction (javax.transaction.Transaction tx)
    {
	synchronized (this)
	{
	    if (_theTransaction == null)
	    {
		_theTransaction = tx;
	    
		return true;
	    }
	}
	
	/*
	 * In case we have already set it for this transaction.
	 */

	return validTransaction(tx);
    }

    public boolean validTransaction (javax.transaction.Transaction tx)
    {
	boolean valid = true;
	
	if (_theTransaction != null)
	    valid = _theTransaction.equals(tx);

	return valid;
    }

    /**
     * @return a new XAResource for this connection.
     */

    public XAResource getResource () throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.getResource ()");
	}

	try
	{
	    if (_theXAResource == null)
		_theXAResource = getConnection().getXAResource();

	    return _theXAResource;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    
	    throw new SQLException(e.toString());
	}
    }

    /**
     * @message com.arjuna.ats.internal.jdbc.drcclose Caught exception
     */

    public final void close ()
    {
	reset();
    }

    public final void reset ()
    {
	_theXAResource = null;
	_theTransaction = null;
    }
    
    /**
     * If there is a connection then return it. Do not create a
     * new connection otherwise.
     */

    public XAConnection getCurrentConnection () throws SQLException
    {
	return _theConnection;
    }

    public void closeCloseCurrentConnection() throws SQLException
    {
        synchronized (this)
        {
            if (_theConnection != null)
            {
                _theConnection.close();
                _theConnection = null;
            }
        }
    }

    public XAConnection getConnection () throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.getConnection ()");
	}

	try
	{
	    synchronized (this)
	    {
		if (_theConnection == null)
		{
		    createConnection();
		}
	    }

	    return _theConnection;
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    
	    throw new SQLException(e.toString());
	}
    }
    
    public XADataSource getDataSource () throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.getDataSource ()");
	}

	return _theDataSource;
    }

    public boolean inuse ()
    {
	return (boolean) (_theXAResource != null);
    }

    public String user ()
    {
	return _user;
    }
    
    public String password ()
    {
	return _passwd;
    }

    public String url ()
    {
	return _dbName;
    }
    
    public String dynamicClass ()
    {
	return _dynamic;
    }

    public String dataSourceName ()
    {
	return _dbName;
    }

    public Transaction transaction ()
    {
	return _theTransaction;
    }

    public void setModifier (ConnectionModifier cm)
    {
	_theModifier = cm;

	if (_theModifier != null)
	    _dbName = _theModifier.initialise(_dbName);
    }
    
    /**
     * @message com.arjuna.ats.internal.jdbc.dynamicerror No dynamic class specified!
     */

    private final void createConnection () throws SQLException
    {
	if (jdbcLogger.logger.isDebugEnabled())
	{
	    jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PRIVATE,
				    com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC, "DirectRecoverableConnection.createConnection");
	}

	if ((_dynamic == null) || (_dynamic.equals("")))
	{
	    throw new SQLException(jdbcLogger.logMesg.getString("com.arjuna.ats.internal.jdbc.dynamicerror"));
	}
	else
	{
	    try
	    {
		if (_theDataSource == null)
		{
		    Class c = Thread.currentThread().getContextClassLoader().loadClass(_dynamic);

		    _dynamicConnection = (DynamicClass) c.newInstance();
		    _theDataSource = _dynamicConnection.getDataSource(_dbName);
		}

		if ((_user == null) && (_passwd == null))
		{
		    if (jdbcLogger.logger.isDebugEnabled())
		    {
			jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PRIVATE,
						com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC,
						"DirectRecoverableConnection - getting connection with no user");
		    }

		    _theConnection = _theDataSource.getXAConnection();
		}
		else
		{
		    if (jdbcLogger.logger.isDebugEnabled())
		    {
			jdbcLogger.logger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PRIVATE, com.arjuna.ats.jdbc.logging.FacilityCode.FAC_JDBC,
						"DirectRecoverableConnection - getting connection for user " + _user);
		    }

		    _theConnection = _theDataSource.getXAConnection(_user, _passwd);
		}
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
		
		throw new SQLException(e.toString());
	    }
	}
    }

    private String		          _dbName;
    private String		          _user;
    private String		          _passwd;
    private String		          _dynamic;
    private XAConnection                  _theConnection;
    private XADataSource	          _theDataSource;
    private DynamicClass                  _dynamicConnection;
    private XAResource                    _theXAResource;
    private javax.transaction.Transaction _theTransaction;
    private ConnectionImple               _theArjunaConnection;
    private ConnectionModifier            _theModifier;

}

