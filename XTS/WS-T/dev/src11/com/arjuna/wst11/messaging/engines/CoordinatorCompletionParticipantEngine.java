/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
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
package com.arjuna.wst11.messaging.engines;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices.util.TransportTimer;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsba.CoordinatorCompletionParticipantInboundEvents;
import com.arjuna.webservices11.wsba.State;
import com.arjuna.webservices11.wsba.BusinessActivityConstants;
import com.arjuna.webservices11.wsba.client.CoordinatorCompletionCoordinatorClient;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionParticipantProcessor;
import com.arjuna.wsc11.messaging.MessageId;
import com.arjuna.wst.BusinessAgreementWithCoordinatorCompletionParticipant;
import com.arjuna.wst.FaultedException;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;
import org.jboss.jbossts.xts11.recovery.participant.ba.BAParticipantRecoveryRecord;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.TimerTask;

/**
 * The coordinator completion participant state engine
 * @author kevin
 */
public class CoordinatorCompletionParticipantEngine implements CoordinatorCompletionParticipantInboundEvents
{
    /**
     * The participant id.
     */
    private final String id ;
    /**
     * The instance identifier.
     */
    private final InstanceIdentifier instanceIdentifier ;
    /**
     * The coordinator endpoint reference.
     */
    private final W3CEndpointReference coordinator ;
    /**
     * The associated participant
     */
    private final BusinessAgreementWithCoordinatorCompletionParticipant participant ;
    /**
     * The current state.
     */
    private State state ;
    /**
     * The associated timer task or null.
     */
    private TimerTask timerTask ;
    /**
     * true if this participant has been recovered otherwise false
     */
    private boolean recovered;

    /**
     * true if this participant's recovery details have been logged to disk otherwise false
     */
    private boolean persisted;


    /**
     * Construct the initial engine for the participant.
     * @param id The participant id.
     * @param coordinator The coordinator endpoint reference.
     * @param participant The participant.
     */
    public CoordinatorCompletionParticipantEngine(final String id, final W3CEndpointReference coordinator,
        final BusinessAgreementWithCoordinatorCompletionParticipant participant)
    {
        this(id, coordinator, participant, State.STATE_ACTIVE, false) ;
    }

    /**
     * Construct the engine for the participant in a specified state.
     * @param id The participant id.
     * @param coordinator The coordinator endpoint reference.
     * @param participant The participant.
     * @param state The initial state.
     * @param recovered true if the engine has been recovered from th elog otherwise false
     */
    public CoordinatorCompletionParticipantEngine(final String id, final W3CEndpointReference coordinator,
        final BusinessAgreementWithCoordinatorCompletionParticipant participant, final State state, final boolean recovered)
    {
        this.id = id ;
        this.instanceIdentifier = new InstanceIdentifier(id) ;
        this.coordinator = coordinator ;
        this.participant = participant ;
        this.state = state ;
        this.recovered = recovered;
        this.persisted = recovered;
    }

    /**
     * Handle the cancel event.
     * @param cancel The cancel notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Canceling
     * Canceling -> Canceling
     * Completing -> Canceling
     * Completed -> Completed (resend Completed)
     * Closing -> Closing
     * Compensating -> Compensating
     * Failing-Active -> Failing-Active (resend Fail)
     * Failing-Canceling -> Failing-Canceling (resend Fail)
     * Failing-Completing -> Failing-Completing (resend Fail)
     * Failing-Compensating -> Failing-Compensating
     * NotCompleting -> NotCompleting (resend CannotComplete)
     * Exiting -> Exiting (resend Exit)
     * Ended -> Ended (send Canceled)
     */
    public void cancel(final NotificationType cancel, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETING))
            {
                changeState(State.STATE_CANCELING) ;
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            executeCancel(false) ;
        }
        else if (current == State.STATE_COMPLETING)
        {
            executeCancel(true) ;
        }
        else if (current == State.STATE_COMPLETED)
        {
            sendCompleted() ;
        }
        else if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING) ||
        	 (current == State.STATE_FAILING_COMPLETING))
        {
            sendFail(current.getValue()) ;
        }
        else if (current == State.STATE_NOT_COMPLETING)
        {
            sendCannotComplete() ;
        }
        else if (current == State.STATE_EXITING)
        {
            sendExit() ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendCancelled() ;
        }
    }

    /**
     * Handle the close event.
     * @param close The close notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completing (invalid state)
     * Completed -> Closing
     * Closing -> Closing
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * NotCompleting -> NotCompleting (invalid state)
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended (send Closed)
     */
    public void close(final NotificationType close, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_CLOSING) ;
            }
        }

        if (current == State.STATE_COMPLETED)
        {
            if (timerTask != null)
            {
                timerTask.cancel() ;
            }
            executeClose() ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendClosed() ;
        }
    }

    /**
     * Handle the compensate event.
     * @param compensate The compensate notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completing (invalid state)
     * Completed -> Compensating
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (resend Fail)
     * NotCompleting -> NotCompleting (invalid state)
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended (send Compensated)
     */
    public void compensate(final NotificationType compensate, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED)
            {
                changeState(State.STATE_COMPENSATING) ;
            }
        }

        if (current == State.STATE_COMPLETED)
        {
            if (timerTask != null)
            {
                timerTask.cancel() ;
            }
            executeCompensate() ;
        }
        else if (current == State.STATE_FAILING_COMPENSATING)
        {
            sendFail(current.getValue()) ;
        }
        else if (current == State.STATE_ENDED)
        {
            sendCompensated() ;
        }
    }

    /**
     * Handle the complete event.
     * @param complete The complete notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Completing
     * Canceling -> Canceling
     * Completing -> Completing
     * Completed -> Completed (resend Completed)
     * Closing -> Closing
     * Compensating -> Compensating
     * Failing-Active -> Failing-Active (resend Fail)
     * Failing-Canceling -> Failing-Canceling (resend Fail)
     * Failing-Completing -> Failing-Completing (resend Fail)
     * Failing-Compensating -> Failing-Compensating
     * NotCompleting -> NotCompleting (resend CannotComplete)
     * Exiting -> Exiting (resend Exit)
     * Ended -> Ended (send Fail)
     */
    public void complete(final NotificationType complete, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_COMPLETING) ;
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            executeComplete() ;
        }
        else if (current == State.STATE_COMPLETED)
        {
            sendCompleted() ;
        }
        else if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING) ||
        	 (current == State.STATE_FAILING_COMPLETING) || (current == State.STATE_ENDED))
        {
            sendFail(current.getValue()) ;
        }
        else if (current == State.STATE_NOT_COMPLETING)
        {
            sendCannotComplete() ;
        }
        else if (current == State.STATE_EXITING)
        {
            sendExit() ;
        }
    }

    /**
     * Handle the exited event.
     * @param exited The exited notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completing (invalid state)
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * NotCompleting -> NotCompleting (invalid state)
     * Exiting -> Ended
     * Ended -> Ended
     */
    public void exited(final NotificationType exited, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_EXITING)
            {
                ended() ;
            }
        }
    }

    /**
     * Handle the failed event.
     * @param failed The failed notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completing (invalid state)
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Ended
     * Failing-Canceling -> Ended
     * Failing-Completing -> Ended
     * Failing-Compensating -> Ended
     * NotCompleting -> NotCompleting (invalid state)
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.failed_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.failed_1] - Unable to delete recovery record during failed for WS-BA participant {0}
     */
    public void failed(final NotificationType failed, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        boolean deleteRequired = false;
        synchronized(this)
        {
            current = state ;
            if ((current == State.STATE_FAILING_ACTIVE) || (current == State.STATE_FAILING_CANCELING) ||
                (current == State.STATE_FAILING_COMPLETING) || (current == State.STATE_FAILING_COMPENSATING))
            {
                deleteRequired = persisted;
                ended() ;
            }
        }
        // if we just ended the participant ensure any log record gets deleted

        if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- nothing more we can do than log a message
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.failed_1", new Object[] {id}) ;
                }
            }
        }
    }

    /**
     * Handle the not completed event.
     * @param notCompleted The not completed notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completing (invalid state)
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * NotCompleting -> Ended
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended
     */
    public void notCompleted(final NotificationType notCompleted, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_NOT_COMPLETING)
            {
        	ended() ;
            }
        }
    }

    /**
     * Handle the getStatus event.
     * @param getStatus The getStatus notification.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.getStatus_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.getStatus_1] - Unknown error: {0}
     */
    public void getStatus(final NotificationType getStatus, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
	final State current ;
	synchronized(this)
	{
	    current = state ;
	}
	sendStatus(current) ;
    }

    /**
     * Handle the status event.
     * @param status The status type.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     */
    public void status(final StatusType status, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        // KEV - implement
    }

    /**
     * Handle the soap fault event.
     * @param soapFault The soap fault.
     * @param addressingProperties The addressing context.
     * @param arjunaContext The arjuna context.
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.soapFault_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.soapFault_1] - Unable to delete recovery record during soapFault processing for WS-BA participant {0}
     */
    public void soapFault(final SoapFault soapFault, final AddressingProperties addressingProperties, final ArjunaContext arjunaContext)
    {
        boolean deleteRequired;
        synchronized(this) {
            deleteRequired = persisted;
            ended() ;
        }
        // TODO -- clarify when and why this gets called and update doc in interface. also check unknown()
        try
        {
            participant.error() ;
        }
        catch (final Throwable th) {} // ignore
        // if we just ended the participant ensure any log record gets deleted
        if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- nothing more we can do than log a message
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.soapFault_1", new Object[] {id}) ;
                }
            }
        }
    }

    /**
     * Handle the completed event.
     *
     * Active -> Active (invalid state)
     * Canceling -> Canceling (invalid state)
     * Completing -> Completed
     * Completed -> Completed
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * Exiting -> Exiting (invalid state)
     * NotCompleting -> NotCompleting (invalid state)
     * Ended -> Ended (invalid state)
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.completed_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.completed_1] - Unable to write recovery record during completed for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.completed_2 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.completed_2] - Unable to delete recovery record during completed for WS-BA participant {0}
     */
    public State completed()
    {
        State current ;
        boolean failRequired  = false;
        boolean deleteRequired  = false;
        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETING)
            {
                changeState(State.STATE_COMPLETED) ;
            }
        }

        if (current == State.STATE_COMPLETING) {
            // ok we need to write the participant details to disk because it has just completed
            BAParticipantRecoveryRecord recoveryRecord = new BAParticipantRecoveryRecord(id, participant, true, coordinator);

            if (!XTSBARecoveryManager.getRecoveryManager().writeParticipantRecoveryRecord(recoveryRecord)) {
                // hmm, could not write entry log warning
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.completed_1", new Object[] {id}) ;
                }
                // we need to fail this transaction
                failRequired = true;
            }
        }
        // recheck state before we decide whether we need to fail -- we might have been sent a cancel while
        // writing the log

        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED) {
                if (!failRequired) {
                    // record the fact that we have persisted this object so later operations will delete
                    // the log record
                    persisted = true;
                } else {
                    // we must force a fail but we don't have a log record to delete
                    changeState(State.STATE_FAILING_COMPLETING);
                }
            } else {
                // we need to delete the log record here as the cancel would not have known it was persisted
                deleteRequired = true;
            }
        }


        // check to see if we need to send a fail or delete the log record before going ahead to complete

        if (failRequired) {
            current = fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        } else if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry log warning
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.completed_2", new Object[] {id}) ;
                }
            }
        } else if ((current == State.STATE_COMPLETING) || (current == State.STATE_COMPLETED)) {
            sendCompleted() ;
        }

        return current ;
    }

    /**
     * Handle the exit event.
     *
     * Active -> Exiting
     * Canceling -> Canceling (invalid state)
     * Completing -> Exiting
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * Exiting -> Exiting
     * NotCompleting -> NotCompleting (invalid state)
     * Ended -> Ended (invalid state)
     */
    public State exit()
    {
        final State current ;
        synchronized (this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETING))
            {
                changeState(State.STATE_EXITING) ;
            }
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETING) ||
            (current == State.STATE_EXITING))
        {
            sendExit() ;
            return waitForState(State.STATE_EXITING, TransportTimer.getTransportTimeout()) ;
        }
        return current ;
    }

    /**
     * Handle the fail event.
     *
     * Active -> Failing-Active
     * Canceling -> Failing-Canceling
     * Completing -> Failing-Completing
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Failing-Compensating
     * Failing-Active -> Failing-Active
     * Failing-Canceling -> Failing-Canceling
     * Failing-Completing -> Failing-Completing
     * Failing-Compensating -> Failing-Compensating
     * NotCompleting -> NotCompleting (invalid state)
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended (invalid state)
     */
    public State fail(final QName exceptionIdentifier)
    {
        final State current ;
        synchronized (this)
        {
            current = state ;
            if (current == State.STATE_ACTIVE)
            {
                changeState(State.STATE_FAILING_ACTIVE) ;
            }
            else if (current == State.STATE_CANCELING)
            {
        	changeState(State.STATE_FAILING_CANCELING) ;
            }
            else if (current == State.STATE_COMPLETING)
            {
        	changeState(State.STATE_FAILING_COMPLETING) ;
            }
            else if (current == State.STATE_COMPENSATING)
            {
                changeState(State.STATE_FAILING_COMPENSATING) ;
            }
        }

        if (current == State.STATE_ACTIVE)
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_ACTIVE, TransportTimer.getTransportTimeout()) ;
        }
        else if (current == State.STATE_CANCELING)
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_CANCELING, TransportTimer.getTransportTimeout()) ;
        }
        else if (current == State.STATE_COMPLETING)
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_COMPLETING, TransportTimer.getTransportTimeout()) ;
        }
        else if (current == State.STATE_COMPENSATING)
        {
            sendFail(exceptionIdentifier) ;
            return waitForState(State.STATE_FAILING_COMPENSATING, TransportTimer.getTransportTimeout()) ;
        }

        return current ;
    }

    /**
     * Handle the cannot complete event.
     *
     * Active -> NotCompleting
     * Canceling -> Canceling (invalid state)
     * Completing -> NotCompleting
     * Completed -> Completed (invalid state)
     * Closing -> Closing (invalid state)
     * Compensating -> Compensating (invalid state)
     * Failing-Active -> Failing-Active (invalid state)
     * Failing-Canceling -> Failing-Canceling (invalid state)
     * Failing-Completing -> Failing-Completing (invalid state)
     * Failing-Compensating -> Failing-Compensating (invalid state)
     * NotCompleting -> NotCompleting
     * Exiting -> Exiting (invalid state)
     * Ended -> Ended (invalid state)
     */
    public State cannotComplete()
    {
        final State current ;
        synchronized (this)
        {
            current = state ;
            if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETING))
            {
                changeState(State.STATE_NOT_COMPLETING) ;
            }
        }

        if ((current == State.STATE_ACTIVE) || (current == State.STATE_COMPLETING) ||
            (current == State.STATE_NOT_COMPLETING))
        {
            sendCannotComplete() ;
            return waitForState(State.STATE_NOT_COMPLETING, TransportTimer.getTransportTimeout()) ;
        }
        return current ;
    }

    /**
     * Handle the comms timeout event.
     *
     * Completed -> Completed (resend Completed)
     */
    private void commsTimeout()
    {
        final State current ;
        synchronized(this)
        {
            current = state ;
        }

        if (current == State.STATE_COMPLETED)
        {
            sendCompleted() ;
        }
    }

    /**
     * Send the exit message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendExit_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendExit_1] - Unexpected exception while sending Exit
     */
    private void sendExit()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendExit(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendExit_1", th) ;
            }
        }
    }

    /**
     * Send the completed message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompleted_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompleted_1] - Unexpected exception while sending Completed
     */
    private void sendCompleted()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendCompleted(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompleted_1", th) ;
            }
        }

        initiateTimer() ;
    }

    /**
     * Send the fail message.
     * @param message The fail message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendFail_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendFail_1] - Unexpected exception while sending Fail
     */
    private void sendFail(final QName message)
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendFail(coordinator, addressingProperties, instanceIdentifier, message) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendFail_1", th) ;
            }
        }
    }

    /**
     * Send the cancelled message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCancelled_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCancelled_1] - Unexpected exception while sending Cancelled
     */
    private void sendCancelled()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendCancelled(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCancelled_1", th) ;
            }
        }
    }

    /**
     * Send the closed message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendClosed_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendClosed_1] - Unexpected exception while sending Closed
     */
    private void sendClosed()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendClosed(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendClosed_1", th) ;
            }
        }
    }

    /**
     * Send the compensated message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompensated_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompensated_1] - Unexpected exception while sending Compensated
     */
    private void sendCompensated()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendCompensated(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCompensated_1", th) ;
            }
        }
    }

    /**
     * Send the status message.
     * @param state The state.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendStatus_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendStatus_1] - Unexpected exception while sending Status
     */
    private void sendStatus(final State state)
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendStatus(coordinator, addressingProperties, instanceIdentifier, state.getValue()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendStatus_1", th) ;
            }
        }
    }

    /**
     * Send the cannot complete message.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCannotComplete_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCannotComplete_1] - Unexpected exception while sending CannotComplete
     */
    private void sendCannotComplete()
    {
        final AddressingProperties addressingProperties = createContext() ;
        try
        {
            CoordinatorCompletionCoordinatorClient.getClient().sendCannotComplete(coordinator, addressingProperties, instanceIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.sendCannotComplete_1", th) ;
            }
        }
    }

    /**
     * Get the coordinator id.
     * @return The coordinator id.
     */
    public String getId()
    {
        return id ;
    }

    /**
     * Get the coordinator endpoint reference
     * @return The coordinator endpoint reference
     */
    public W3CEndpointReference getCoordinator()
    {
        return coordinator ;
    }

    /**
     * Get the associated participant.
     * @return The associated participant.
     */
    public BusinessAgreementWithCoordinatorCompletionParticipant getParticipant()
    {
        return participant ;
    }

    /**
     * Change the state and notify any listeners.
     * @param state The new state.
     */
    private synchronized void changeState(final State state)
    {
        if (this.state != state)
        {
            this.state = state ;
            notifyAll() ;
        }
    }

    /**
     * Wait for the state to change from the specified state.
     * @param origState The original state.
     * @param delay The maximum time to wait for (in milliseconds).
     * @return The current state.
     */
    private State waitForState(final State origState, final long delay)
    {
        final long end = System.currentTimeMillis() + delay ;
        synchronized(this)
        {
            while(state == origState)
            {
                final long remaining = end - System.currentTimeMillis() ;
                if (remaining <= 0)
                {
                    break ;
                }
                try
                {
                    wait(remaining) ;
                }
                catch (final InterruptedException ie) {} // ignore
            }
            return state ;
        }
    }

    /**
     * Execute the cancel transition.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_1] - Faulted exception from participant cancel for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_2 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_2] - Unexpected exception from participant cancel for WS-BA participant {0}
     */
    private void executeCancel(boolean duringComplete)
    {
        boolean failRequired = false;

        // TODO -- there is a potential race here with a completing thread
        // the state diagrams in the spec say that if a cancel comes in while completing we have to cancel
        // but the participant may be part way through executing a complete. strictly, that's something
        // the participant has to deal with not us
        try
        {
            participant.cancel() ;
        }
        catch (final FaultedException fe)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_1", new Object[] { id}, fe) ;
            }
            // fail here because the participant doesn't want to retry the cancel
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCancel_2", new Object[] { id}, th) ;
            }
            /*
             * we can get here from state ACTIVE or CONPLETING. we could roll back the state as though the cancel
             * never happened. the only problem is that coming from state COMPLETING we don't know whether the
             * completing thread has logged its state or logged it and then deleted it because it saw the transition
             * to CANCELING. so we roll back to ACTIVE but fail if we have come from COMPLETING
             */
            synchronized (this) {
                if (state == State.STATE_CANCELING) {
                    if (duringComplete) {
                        failRequired = true;
                        changeState(State.STATE_FAILING_CANCELING);
                    } else {
                        changeState(State.STATE_ACTIVE);
                        return;
                    }
                }
            }
        }
        if (failRequired) {
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        } else {
            sendCancelled() ;
            ended() ;
        }
    }

    /**
     * Execute the close transition.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeClose_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeClose_1] - Unexpected exception from participant close for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeClose_2 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeClose_2] - Unable to delete recovery record during close for WS-BA participant {0}
     */
    private void executeClose()
    {
        try
        {
            participant.close() ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeClose_1", th) ;
            }
            // restore previous state so we can retry the close otherwise we get stuck in state closing forever
            changeState(State.STATE_COMPLETED);

            sendCompleted();
            return ;
        }
        // delete any log record for the participant
        if (persisted) {
            // if we cannot delete the participant record we effectively drop the close message
            // here in the hope that we have better luck next time..
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry -- leave it so we can maybe retry later
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.executeClose_2", new Object[] {id}) ;
                }
                // restore previous state so we can retry the close otherwise we get stuck in state closing forever

                changeState(State.STATE_COMPLETED);

                sendCompleted();

                return;
            }
        }

        sendClosed() ;
        ended() ;
    }

    /**
     * Execute the compensate transition.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_1] - Faulted exception from participant compensate for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_2 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_2] - Unexpected exception from participant compensate for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_3 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_3] - Unable to delete recovery record during compensate for WS-BA participant {0}
     */
    private void executeCompensate()
    {
        try
        {
            participant.compensate() ;
        }
        catch (final FaultedException fe)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_1", new Object[] { id}, fe) ;
            }
            // fail here because the participant doesn't want to retry the compensate
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME) ;
        }
        catch (final Throwable th)
        {
            final State current ;
            synchronized (this)
            {
                current = state ;
                if (current == State.STATE_COMPENSATING)
                {
                    changeState(State.STATE_COMPLETED) ;
                }
            }
            if (current == State.STATE_COMPENSATING)
            {
                initiateTimer() ;
            }

            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeCompensate_2", new Object[] { id }, th) ;
            }
            return ;
        }

        final State current ;
        boolean failRequired = false;
        synchronized (this)
        {
            current = state ;
            // need to do this while synchronized so no fail calls can get in on between

            if (current == State.STATE_COMPENSATING)
            {
                if (persisted) {
                    if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                        // we have to fail since we don't want to run the compensate method again
                        if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                        {
                            WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.executeCompensate_3", new Object[] {id}) ;
                        }
                        failRequired = true;
                        changeState(State.STATE_FAILING_COMPENSATING);
                    }
                }
                // if we did not fail then we can decommission the participant now avoiding any further races
                // we will send the compensate after we exit the synchronized block
                if (!failRequired) {
                    ended();
                }
            }
        }
        if (failRequired) {
            fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        } else if (current == State.STATE_COMPENSATING)
        {
            sendCompensated() ;
        }
    }

    /**
     * Execute the complete transition.
     *
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeComplete_1 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeComplete_1] - Unexpected exception from participant complete for WS-BA participant {0}
     * @message com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeComplete_2 [com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeComplete_2] - Unable to write log record during participant complete for WS-BA participant {0}
     */
    private void executeComplete()
    {
        try
        {
            participant.complete() ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.engines.CoordinatorCompletionParticipantEngine.executeComplete_1", new Object[] {id}, th) ;
            }
            return ;
        }

        State current ;
        boolean failRequired  = false;
        boolean deleteRequired  = false;
        synchronized (this)
        {
            current = state ;
            if (current == State.STATE_COMPLETING)
            {
                changeState(State.STATE_COMPLETED) ;
            }
        }
        if (current == State.STATE_COMPLETING)
        {
            // ok we need to write the participant details to disk because it has just completed
            BAParticipantRecoveryRecord recoveryRecord = new BAParticipantRecoveryRecord(id, participant, true, coordinator);

            if (!XTSBARecoveryManager.getRecoveryManager().writeParticipantRecoveryRecord(recoveryRecord)) {
                // hmm, could not write entry log warning
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.executeComplete_2", new Object[] {id}) ;
                }
                // we need to fail this transaction
                failRequired = true;
            }
        }

        // recheck state before we decide whether we need to fail -- we might have been sent a cancel while
        // writing the log

        synchronized(this)
        {
            current = state ;
            if (current == State.STATE_COMPLETED) {
                if (!failRequired) {
                    // record the fact that we have persisted this object so later operations will delete
                    // the log record
                    persisted = true;
                } else {
                    // we must force a fail but we don't have a log record to delete
                    changeState(State.STATE_FAILING_COMPLETING);
                }
            } else {
                // we cannot force a fail now so just delete
                failRequired = false;
                // we need to delete the log record here as the cancel would not have known it was persisted
                deleteRequired = true;
            }
        }

        // check to see if we need to send a fail or delete the log record before going ahead to complete

        if (failRequired) {
            current = fail(BusinessActivityConstants.WSBA_ELEMENT_FAIL_QNAME);
        } else if (deleteRequired) {
            if (!XTSBARecoveryManager.getRecoveryManager().deleteParticipantRecoveryRecord(id)) {
                // hmm, could not delete entry log warning
                if (WSTLogger.arjLoggerI18N.isWarnEnabled())
                {
                    WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.engines.ParticipantCompletionParticipantEngine.completed_2", new Object[] {id}) ;
                }
            }
        } else if ((current == State.STATE_COMPLETING) || (current == State.STATE_COMPLETED)) {
            sendCompleted() ;
        }
    }

    /**
     * End the current participant.
     */
    private void ended()
    {
	changeState(State.STATE_ENDED) ;
        CoordinatorCompletionParticipantProcessor.getProcessor().deactivateParticipant(this) ;
    }
    
    /**
     * Initiate the timer.
     */
    private synchronized void initiateTimer()
    {
        if (timerTask != null)
        {
            timerTask.cancel() ;
        }

        if (state == State.STATE_COMPLETED)
        {
            timerTask = new TimerTask() {
                public void run() {
                    commsTimeout() ;
                }
            } ;
            TransportTimer.getTimer().schedule(timerTask, TransportTimer.getTransportPeriod()) ;
        }
        else
        {
            timerTask = null ;
        }
    }

    /**
     * Create a context for the outgoing message.
     * @return The addressing context.
     */
    private AddressingProperties createContext()
    {
        final String messageId = MessageId.getMessageId() ;

        return AddressingHelper.createNotificationContext(messageId) ;
    }
}