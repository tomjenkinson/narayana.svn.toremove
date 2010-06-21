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
 * $Id: PersistenceRecord.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.internal.arjuna.abstractrecords;

import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.logging.tsLogger;

import com.arjuna.ats.arjuna.coordinator.*;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.objectstore.ObjectStore;
import com.arjuna.ats.arjuna.common.*;
import com.arjuna.ats.arjuna.state.*;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreType;
import java.io.PrintWriter;

import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import java.io.IOException;

/**
 * A PersistenceRecord is created whenever a persistent object is
 * created/read/modified within the scope of a transaction. It is responsible
 * for ensuring that state changes are committed or rolled back on behalf of the
 * object depending upon the outcome of the transaction.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: PersistenceRecord.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 *
 *
 * @message com.arjuna.ats.arjuna.PersistenceRecord_2
 *          [com.arjuna.ats.arjuna.PersistenceRecord_2]
 *          PersistenceRecord::topLevelCommit - commit_state call failed for {0}
 * @message com.arjuna.ats.arjuna.PersistenceRecord_3
 *          [com.arjuna.ats.arjuna.PersistenceRecord_3]
 *          PersistenceRecord::topLevelCommit - no state to commit!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_4
 *          [com.arjuna.ats.arjuna.PersistenceRecord_4]
 *          PersistenceRecord::topLevelCommit - caught exception: {0}
 * @message com.arjuna.ats.arjuna.PersistenceRecord_5
 *          [com.arjuna.ats.arjuna.PersistenceRecord_5]
 *          PersistenceRecord::topLevelCommit - no object store specified!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_6
 *          [com.arjuna.ats.arjuna.PersistenceRecord_6]
 *          PersistenceRecord::topLevelCommit - commit_state error
 * @message com.arjuna.ats.arjuna.PersistenceRecord_7
 *          [com.arjuna.ats.arjuna.PersistenceRecord_7] PersistenceRecord
 *          deactivate error, object probably already deactivated!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_8
 *          [com.arjuna.ats.arjuna.PersistenceRecord_8]
 *          PersistenceRecord.topLevelPrepare - setup error!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_10
 *          [com.arjuna.ats.arjuna.PersistenceRecord_10]
 *          PersistenceRecord::restore_state: Failed to unpack object store type
 * @message com.arjuna.ats.arjuna.PersistenceRecord_11
 *          [com.arjuna.ats.arjuna.PersistenceRecord_11]
 *          PersistenceRecord::save_state - type of store is unknown
 * @message com.arjuna.ats.arjuna.PersistenceRecord_14
 *          [com.arjuna.ats.arjuna.PersistenceRecord_14]
 *          PersistenceRecord::save_state - packing top level state failed
 * @message com.arjuna.ats.arjuna.PersistenceRecord_15
 *          [com.arjuna.ats.arjuna.PersistenceRecord_15]
 *          PersistenceRecord::save_state - failed
 * @message com.arjuna.ats.arjuna.PersistenceRecord_16
 *          [com.arjuna.ats.arjuna.PersistenceRecord_16]
 *          PersistenceRecord::save_state - no object store defined for object
 * @message com.arjuna.ats.arjuna.PersistenceRecord_18
 *          [com.arjuna.ats.arjuna.PersistenceRecord_18]
 *          PersistenceRecord::topLevelAbort() - Expecting state but found none!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_19
 *          [com.arjuna.ats.arjuna.PersistenceRecord_19]
 *          PersistenceRecord::topLevelAbort() - Could not remove state from object store!
 * @message com.arjuna.ats.arjuna.PersistenceRecord_20
 *          [com.arjuna.ats.arjuna.PersistenceRecord_20]
 *          PersistenceRecord::topLevelAbort() - Received ObjectStoreException
 *          {0}
 * @message com.arjuna.ats.arjuna.PersistenceRecord_21
 *          [com.arjuna.ats.arjuna.PersistenceRecord_21]
 *          PersistenceRecord.topLevelPrepare - write_uncommitted error
 */

public class PersistenceRecord extends RecoveryRecord
{

	/**
	 * This constructor is used to create a new instance of PersistenceRecord.
	 */

	public PersistenceRecord (OutputObjectState os, ObjectStore objStore, StateManager sm)
	{
		super(os, sm);

		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::PersistenceRecord("
                    + os + ", " + sm.get_uid() + ")");
        }

		shadowMade = false;
		store = objStore;
		topLevelState = null;
	}

	/**
	 * Redefintions of abstract functions inherited from RecoveryRecord.
	 */

	public int typeIs ()
	{
		return RecordType.PERSISTENCE;
	}

	/**
	 * topLevelAbort may have to remove the persistent state that was written
	 * into the object store during the processing of topLevelPrepare. It then
	 * does the standard abort processing.
	 */

	public int topLevelAbort ()
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::topLevelAbort() for "
                    + order());
        }

		Uid uid = null;
		String type = null;

		if (shadowMade) // state written by StateManager instance
		{
			uid = order();
			type = getTypeOfObject();
		}
		else
		{
			if (topLevelState == null) // hasn't been prepared, so no state
			{
				return nestedAbort();
			}
			else
			{
				uid = topLevelState.stateUid();
				type = topLevelState.type();
			}
		}

		try
		{
			if (!store.remove_uncommitted(uid, type))
			{
				if (tsLogger.arjLoggerI18N.isWarnEnabled()) {
                    tsLogger.i18NLogger.warn_PersistenceRecord_19();
                }

				return TwoPhaseOutcome.FINISH_ERROR;
			}
		}
		catch (ObjectStoreException e)
		{
			if (tsLogger.arjLoggerI18N.isWarnEnabled()) {
                tsLogger.i18NLogger.warn_PersistenceRecord_20(e);
            }

			return TwoPhaseOutcome.FINISH_ERROR;
		}

		return nestedAbort();
	}

	/**
	 * commit the state saved during the prepare phase.
	 */

	public int topLevelCommit ()
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::topLevelCommit() : About to commit state, "+
                    "uid = "+order()+", ObjType = "+getTypeOfObject());
        }

		if (tsLogger.arjLogger.isDebugEnabled())
		{
			if (store != null) {
                tsLogger.arjLogger.debug(", store = "
                        + store + "(" + store.typeIs() + ")");
            }
			else {
                tsLogger.arjLogger.debug("");
            }
		}

		boolean result = false;

		if (store != null)
		{
			try
			{
				if (shadowMade)
				{
					result = store.commit_state(order(), super.getTypeOfObject());

					if (!result)
					{
						if (tsLogger.arjLoggerI18N.isWarnEnabled()) {
                            tsLogger.i18NLogger.warn_PersistenceRecord_2(order());
                        }
					}
				}
				else
				{
					if (topLevelState != null)
					{
						result = store.write_committed(order(), super.getTypeOfObject(), topLevelState);
					}
					else
					{
                        if (tsLogger.arjLoggerI18N.isWarnEnabled())
                            tsLogger.i18NLogger.warn_PersistenceRecord_3();
					}
				}
			}
			catch (ObjectStoreException e)
			{
                if (tsLogger.arjLoggerI18N.isWarnEnabled())
                    tsLogger.i18NLogger.warn_PersistenceRecord_4(e);

				result = false;
			}
		}
		else
		{
            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                tsLogger.i18NLogger.warn_PersistenceRecord_5();
		}

		if (!result)
		{
            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                tsLogger.i18NLogger.warn_PersistenceRecord_6();
		}

		super.forgetAction(true);

		return ((result) ? TwoPhaseOutcome.FINISH_OK
				: TwoPhaseOutcome.FINISH_ERROR);
	}

	/**
	 * topLevelPrepare attempts to save the object. It will either do this in
	 * the action intention list or directly in the object store by using the
	 * 'deactivate' function of the object depending upon the size of the state.
	 * To ensure that objects are correctly hidden while they are in an
	 * uncommitted state if we use the abbreviated protocol then we write an
	 * EMPTY object state as the shadow state - THIS MUST NOT BE COMMITTED.
	 * Instead we write_committed the one saved in the intention list. If the
	 * store cannot cope with being given an empty state we revert to the old
	 * protocol.
	 */

	public int topLevelPrepare ()
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::topLevelPrepare() for "
                    + order());
        }

		int result = TwoPhaseOutcome.PREPARE_NOTOK;
		StateManager sm = super.objectAddr;

		if ((sm != null) && (store != null))
		{
		    /*
		     * Get ready to create our state to be saved. At this stage we're not
		     * sure if the state will go into its own log or be written into the
		     * transaction log for improved performance.
		     */
		    
			topLevelState = new OutputObjectState(sm.get_uid(), sm.type());

			if (writeOptimisation
					&& (!store.fullCommitNeeded()
							&& (sm.save_state(topLevelState, ObjectType.ANDPERSISTENT)) && (topLevelState.size() <= PersistenceRecord.MAX_OBJECT_SIZE)))
			{
			    /*
			     * We assume that crash recovery will always run before
			     * the object can be reactivated!
			     */

				if (PersistenceRecord.classicPrepare)
				{
					OutputObjectState dummy = new OutputObjectState(
							Uid.nullUid(), null);

					/*
					 * Write an empty shadow state to the store to indicate one
					 * exists, and to prevent bogus activation in the case where
					 * crash recovery hasn't run yet.
					 */

					try
					{
						store.write_uncommitted(sm.get_uid(), sm.type(), dummy);
						result = TwoPhaseOutcome.PREPARE_OK;
					}
					catch (ObjectStoreException e) {
                        tsLogger.i18NLogger.warn_PersistenceRecord_21(e);
                    }

					dummy = null;
				}
				else
				{
				    /*
				     * Don't write anything as our state will go into the log.
				     */
				    
					result = TwoPhaseOutcome.PREPARE_OK;
				}
			}
			else
			{
				if (sm.deactivate(store.getStoreName(), false))
				{
					shadowMade = true;

					result = TwoPhaseOutcome.PREPARE_OK;
				}
				else
				{
                    if (tsLogger.arjLoggerI18N.isWarnEnabled())
                        tsLogger.i18NLogger.warn_PersistenceRecord_7();
				}
			}
		}
		else
		{
            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                tsLogger.i18NLogger.warn_PersistenceRecord_8();
		}

		return result;
	}

	/**
	 * topLevelCleanup must leave the persistent state that was written in the
	 * object store during the processing of topLevelPrepare intact. Crash
	 * recovery will take care of its resolution
	 */

	public int topLevelCleanup ()
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::topLevelCleanup() for "
                    + order());
        }

		return TwoPhaseOutcome.FINISH_OK;
	}

	/**
	 * @return <code>true</code>
	 */

	public boolean doSave ()
	{
		return true;
	}

    public boolean restore_state (InputObjectState os, int ot)
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::restore_state() for "
                    + order());
        }

		boolean res = false;
		int objStoreType = 0;

		topLevelState = null;
		
		try
		{
			objStoreType = os.unpackInt();

			if (tsLogger.arjLogger.isDebugEnabled()) {
                tsLogger.arjLogger.debug(" PersistenceRecord::restore_state: Just unpacked object store type = "+Integer.toString(objStoreType));
            }

			if (ObjectStoreType.valid(objStoreType))
			{
				/* discard old store before creating new */

				if (store == null)
				{
				    Class<? extends ObjectStore> oc = ObjectStoreType.typeToClass(objStoreType);
				    
				    store = oc.newInstance();
				}

				store.unpack(os);
				shadowMade = os.unpackBoolean();

				// topLevelState = null;

				if (!shadowMade)
				{
					topLevelState = new OutputObjectState(os);
					res = topLevelState.valid();
				}
				else
					res = true;

				return (res && super.restore_state(os, ot));
			}
		}
		catch (final Exception e)
		{
            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                tsLogger.i18NLogger.warn_PersistenceRecord_10();
		}

		return res;
	}

	public boolean save_state (OutputObjectState os, int ot)
	{
		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::save_state() for "
                    + order());
        }

		boolean res = true;

		if (store != null)
		{
			if (!ObjectStoreType.valid(store.typeIs()))
			{
				if (tsLogger.arjLoggerI18N.isWarnEnabled()) {
                    tsLogger.i18NLogger.warn_PersistenceRecord_11();
                }

				res = false;
			}
			else
			{
				try
				{
					os.packInt(store.typeIs());

					if (tsLogger.arjLogger.isDebugEnabled()) {
                        tsLogger.arjLogger.debug("PersistenceRecord::save_state: Packed object store type = "+Integer.toString(store.typeIs()));
                    }

					store.pack(os);

					if (tsLogger.arjLogger.isDebugEnabled()) {
                        tsLogger.arjLogger.debug("PersistenceRecord::save_state: Packed object store root");
                    }

					os.packBoolean(shadowMade);

					/*
					 * If we haven't written a shadow state, then pack the state
					 * into the transaction log. There MUST be a state at this
					 * point.
					 */

					if (!shadowMade)
					{
						res = (topLevelState != null);

						if (res)
							topLevelState.packInto(os);
						else
						{
                            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                                tsLogger.i18NLogger.warn_PersistenceRecord_14();
						}
					}
				}
				catch (IOException e)
				{
					res = false;

                    if (tsLogger.arjLoggerI18N.isWarnEnabled())
                        tsLogger.i18NLogger.warn_PersistenceRecord_15();
				}
			}
		}
		else
		{
            if (tsLogger.arjLoggerI18N.isWarnEnabled())
                tsLogger.i18NLogger.warn_PersistenceRecord_16();

			try
			{
				os.packString(null);
			}
			catch (IOException e)
			{
				res = false;
			}
		}

		return res && super.save_state(os, ot);
	}

	public void print (PrintWriter strm)
	{
		super.print(strm); /* bypass RecoveryRecord */

		strm.println("PersistenceRecord with state:\n" + super.state);
	}

	public String type ()
	{
		return "/StateManager/AbstractRecord/RecoveryRecord/PersistenceRecord";
	}

	/**
	 * Creates a 'blank' persistence record. This is used during crash recovery
	 * when recreating the prepared list of a server atomic action.
	 */

	public PersistenceRecord ()
	{
		super();

		if (tsLogger.arjLogger.isDebugEnabled()) {
            tsLogger.arjLogger.debug("PersistenceRecord::PersistenceRecord() - crash recovery constructor");
        }

		shadowMade = false;
		store = null;
		topLevelState = null;
	}

	/**
	 * Cadaver records force write shadows. This operation supresses to
	 * abbreviated commit This should never return false
	 */

	protected boolean shadowForced ()
	{
		if (topLevelState == null)
		{
			shadowMade = true;

			return true;
		}

		/* I've already done the abbreviated protocol so its too late */

		return false;
	}

	// this value should really come from the object store implementation!

	public static final int MAX_OBJECT_SIZE = 4096; // block size

	protected boolean shadowMade;
	protected ObjectStore store;
	protected OutputObjectState topLevelState;
	protected static boolean classicPrepare = false;

	private static boolean writeOptimisation = false;

	static
	{
        classicPrepare = arjPropertyManager.getCoordinatorEnvironmentBean().isClassicPrepare();

        writeOptimisation = arjPropertyManager.getCoordinatorEnvironmentBean().isWriteOptimisation();
	}
}
