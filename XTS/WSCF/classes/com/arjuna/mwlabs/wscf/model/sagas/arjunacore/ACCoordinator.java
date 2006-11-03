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
 * Copyright (C) 2002,
 *
 * Arjuna Technologies Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: ACCoordinator.java,v 1.5 2005/05/19 12:13:37 nmcl Exp $
 */

package com.arjuna.mwlabs.wscf.model.sagas.arjunacore;

import com.arjuna.mw.wscf.logging.wscfLogger;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.*;

import com.arjuna.mw.wscf.model.sagas.participants.*;

import com.arjuna.mw.wscf.common.Qualifier;
import com.arjuna.mw.wscf.common.CoordinatorId;

import com.arjuna.mw.wsas.activity.Outcome;

import com.arjuna.mw.wsas.completionstatus.CompletionStatus;

import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wsas.exceptions.WrongStateException;
import com.arjuna.mw.wsas.exceptions.ProtocolViolationException;

import com.arjuna.mw.wscf.exceptions.*;

/**
 * This class represents a specific coordination instance. It is essentially an
 * ArjunaCore TwoPhaseCoordinator, which gives us access to two-phase with
 * synchronization support but without thread management.
 * 
 * @author Mark Little (mark.little@arjuna.com)
 * @version $Id: ACCoordinator.java,v 1.5 2005/05/19 12:13:37 nmcl Exp $
 * @since 1.0.
 * 
 * @message com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_1
 *          [com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_1] -
 *          Complete call failed!
 * @message com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_2
 *          [com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_2] -
 *          Null is an invalid parameter.
 * @message com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_3
 *          [com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_3] -
 *          Wrong state for operation!
 */

public class ACCoordinator extends TwoPhaseCoordinator
{

	private final static int DELISTED = 0;

	private final static int COMPLETED = 1;

	public ACCoordinator ()
	{
		super();

		_theId = new CoordinatorIdImple(get_uid());
	}

	public ACCoordinator (Uid recovery)
	{
		super(recovery);

		_theId = new CoordinatorIdImple(get_uid());
	}

	/**
	 * If the application requires and if the coordination protocol supports it,
	 * then this method can be used to execute a coordination protocol on the
	 * currently enlisted participants at any time prior to the termination of
	 * the coordination scope.
	 * 
	 * This implementation only supports coordination at the end of the
	 * activity.
	 * 
	 * @param CompletionStatus
	 *            cs The completion status to use when determining how to
	 *            execute the protocol.
	 * 
	 * @exception WrongStateException
	 *                Thrown if the coordinator is in a state the does not allow
	 *                coordination to occur.
	 * @exception ProtocolViolationException
	 *                Thrown if the protocol is violated in some manner during
	 *                execution.
	 * @exception SystemException
	 *                Thrown if any other error occurs.
	 * 
	 * @return The result of executing the protocol, or null.
	 */

	public Outcome coordinate (CompletionStatus cs) throws WrongStateException,
			ProtocolViolationException, SystemException
	{
		return null;
	}

	/**
	 */

	public synchronized void complete () throws WrongStateException,
			SystemException
	{
		if (status() == ActionStatus.RUNNING)
		{
			/*
			 * Transaction is active, so we can look at the pendingList only.
			 */

			if (pendingList != null)
			{
				RecordListIterator iter = new RecordListIterator(pendingList);
				AbstractRecord absRec = iter.iterate();

				try
				{
					while (absRec != null)
					{
						if (absRec instanceof ParticipantRecord)
						{
							ParticipantRecord pr = (ParticipantRecord) absRec;

							if (!pr.complete())
							{
								preventCommit();

								throw new SystemException(
										wscfLogger.log_mesg
												.getString("com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_1"));
							}
						}

						absRec = iter.iterate();
					}
				}
				catch (SystemException ex)
				{
					throw ex;
				}
				catch (Exception ex)
				{
					preventCommit();

					throw new SystemException(ex.toString());
				}
			}
		}
		else
			throw new WrongStateException();
	}

	/**
	 * Enrol the specified participant with the coordinator associated with the
	 * current thread.
	 * 
	 * @param Participant
	 *            act The participant.
	 * 
	 * @exception WrongStateException
	 *                Thrown if the coordinator is not in a state that allows
	 *                participants to be enrolled.
	 * @exception DuplicateParticipantException
	 *                Thrown if the participant has already been enrolled and
	 *                the coordination protocol does not support multiple
	 *                entries.
	 * @exception InvalidParticipantException
	 *                Thrown if the participant is invalid.
	 * @exception SystemException
	 *                Thrown if any other error occurs.
	 */

	public void enlistParticipant (Participant act) throws WrongStateException,
			DuplicateParticipantException, InvalidParticipantException,
			SystemException
	{
		if (act == null) throw new InvalidParticipantException();

		AbstractRecord rec = new ParticipantRecord(act, new Uid());

		if (add(rec) != AddOutcome.AR_ADDED) throw new WrongStateException();
		else
		{
			/*
			 * Presume nothing protocol, so we need to write the intentions list
			 * every time a participant is added.
			 */
		}
	}

	/**
	 * Remove the specified participant from the coordinator's list.
	 * 
	 * @exception InvalidParticipantException
	 *                Thrown if the participant is not known of by the
	 *                coordinator.
	 * @exception WrongStateException
	 *                Thrown if the state of the coordinator does not allow the
	 *                participant to be removed (e.g., in a two-phase protocol
	 *                the coordinator is committing.)
	 * @exception SystemException
	 *                Thrown if any other error occurs.
	 */

	public synchronized void delistParticipant (String participantId)
			throws InvalidParticipantException, WrongStateException,
			SystemException
	{
		if (participantId == null)
			throw new SystemException(
					wscfLogger.log_mesg
							.getString("com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_2"));

		if (status() == ActionStatus.RUNNING) changeParticipantStatus(
				participantId, DELISTED);
		else
			throw new WrongStateException();
	}

	public synchronized void participantCompleted (String participantId)
			throws InvalidParticipantException, WrongStateException,
			SystemException
	{
		if (participantId == null)
			throw new SystemException(
					wscfLogger.log_mesg
							.getString("com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_2"));

		if (status() == ActionStatus.RUNNING) changeParticipantStatus(
				participantId, COMPLETED);
		else
			throw new WrongStateException();
	}

	public synchronized void participantFaulted (String participantId)
			throws InvalidParticipantException, SystemException
	{
		if (participantId == null)
			throw new SystemException(
					wscfLogger.log_mesg
							.getString("com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_2"));

		if (status() == ActionStatus.RUNNING)
		{
			// participant faulted means it has compensated and gone away

			changeParticipantStatus(participantId, DELISTED);
		}
		else
			throw new SystemException(
					wscfLogger.log_mesg
							.getString("com.arjuna.mwlabs.wscf.model.sagas.arjunacore.ACCoordinator_3"));
	}

	/**
	 * @exception SystemException
	 *                Thrown if any error occurs.
	 * 
	 * @return the complete list of qualifiers that have been registered with
	 *         the current coordinator.
	 */

	public Qualifier[] qualifiers () throws SystemException
	{
		return null;
	}

	/**
	 * @exception SystemException
	 *                Thrown if any error occurs.
	 * 
	 * @return The unique identity of the current coordinator.
	 */

	public CoordinatorId identifier () throws SystemException
	{
		return _theId;
	}

	private final void changeParticipantStatus (String participantId, int status)
			throws InvalidParticipantException, SystemException
	{
		/*
		 * Transaction is active, so we can look at the pendingList only.
		 */

		// TODO allow transaction status to be changed during commit - exit
		// could come in late
		boolean found = false;

		if (pendingList != null)
		{
			RecordListIterator iter = new RecordListIterator(pendingList);
			AbstractRecord absRec = iter.iterate();

			try
			{
				while ((absRec != null) && !found)
				{
					if (absRec instanceof ParticipantRecord)
					{
						ParticipantRecord pr = (ParticipantRecord) absRec;
						Participant participant = (Participant) pr.value();

						if (participantId.equals(participant.id()))
						{
							found = true;

							if (status == DELISTED) pr.delist();
							else
								pr.completed();
						}
					}

					absRec = iter.iterate();
				}
			}
			catch (Exception ex)
			{
				throw new SystemException(ex.toString());
			}
		}

		if (!found) throw new InvalidParticipantException();
	}

	private CoordinatorIdImple _theId;

}
