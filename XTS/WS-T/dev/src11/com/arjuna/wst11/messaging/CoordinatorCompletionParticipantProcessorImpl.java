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
package com.arjuna.wst11.messaging;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices.SoapFaultType;
import com.arjuna.webservices.base.processors.ActivatedObjectProcessor;
import com.arjuna.webservices.logging.WSTLogger;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.webservices11.wsaddr.map.MAP;
import com.arjuna.webservices11.wsaddr.client.SoapFaultClient;
import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsarj.InstanceIdentifier;
import com.arjuna.webservices11.wsba.CoordinatorCompletionParticipantInboundEvents;
import com.arjuna.webservices11.wsba.State;
import com.arjuna.webservices11.wsba.client.CoordinatorCompletionCoordinatorClient;
import com.arjuna.webservices11.wsba.processors.CoordinatorCompletionParticipantProcessor;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.wsc11.messaging.MessageId;
import org.oasis_open.docs.ws_tx.wsba._2006._06.NotificationType;
import org.oasis_open.docs.ws_tx.wsba._2006._06.StatusType;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;

import javax.xml.namespace.QName;


/**
 * The Coordinator Completion Participant processor.
 * @author kevin
 */
public class CoordinatorCompletionParticipantProcessorImpl extends CoordinatorCompletionParticipantProcessor
{
    /**
     * The activated object processor.
     */
    private final ActivatedObjectProcessor activatedObjectProcessor = new ActivatedObjectProcessor() ;

    /**
     * Activate the participant.
     * @param participant The participant.
     * @param identifier The identifier.
     */
    public void activateParticipant(final CoordinatorCompletionParticipantInboundEvents participant, final String identifier)
    {
        activatedObjectProcessor.activateObject(participant, identifier) ;
    }

    /**
     * Deactivate the participant.
     * @param participant The participant.
     */
    public void deactivateParticipant(final CoordinatorCompletionParticipantInboundEvents participant)
    {
        activatedObjectProcessor.deactivateObject(participant) ;
    }

    /**
     * Check whether a participant with the given id is currently active
     *
     * @param identifier The identifier.
     */
    public boolean isActive(String identifier) {
        return activatedObjectProcessor.getObject(identifier) != null;
    }

    /**
     * Get the participant with the specified identifier.
     * @param instanceIdentifier The participant identifier.
     * @return The participant or null if not known.
     */
    private CoordinatorCompletionParticipantInboundEvents getParticipant(final InstanceIdentifier instanceIdentifier)
    {
        final String identifier = (instanceIdentifier != null ? instanceIdentifier.getInstanceIdentifier() : null) ;
        return (CoordinatorCompletionParticipantInboundEvents)activatedObjectProcessor.getObject(identifier) ;
    }

    /**
     * Cancel.
     * @param cancel The cancel notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_1] - Unexpected exception thrown from cancel:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_2] - Cancel called on unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_3 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_3] - Cancel request dropped pending WS-BA participant recovery manager initialization for participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_4 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_4] - Cancel request dropped pending WS-BA participant recovery manager scan for unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_5 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_5] - Cancel request dropped pending registration of application-specific recovery module for WS-BA participant: {0}
     */
    public void cancel(final NotificationType cancel, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_3", new Object[] {instanceIdentifier}) ;
            }

            return;
        }

        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.cancel(cancel, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_1", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted())
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_4", new Object[] {instanceIdentifier}) ;
            }
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null)
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_5", new Object[] {instanceIdentifier}) ;
            }
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.cancel_2", new Object[] {instanceIdentifier}) ;
            }
            sendCancelled(map, arjunaContext) ;
        }
    }

    /**
     * Close.
     * @param close The close notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_1] - Unexpected exception thrown from close:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_2] - Close called on unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_3 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_3] - Close request dropped pending WS-BA participant recovery manager initialization for participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_4 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_4] - Close request dropped pending WS-BA participant recovery manager scan for unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_5 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_5] - Close request dropped pending registration of application-specific recovery module for WS-BA participant: {0}
     */
    public void close(final NotificationType close, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_3", new Object[] {instanceIdentifier}) ;
            }

            return;
        }

        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.close(close, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_1", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted())
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_4", new Object[] {instanceIdentifier}) ;
            }
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null)
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_5", new Object[] {instanceIdentifier}) ;
            }
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.close_2", new Object[] {instanceIdentifier}) ;
            }
            sendClosed(map, arjunaContext) ;
        }
    }

    /**
     * Compensate.
     * @param compensate The compensate notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_1] - Unexpected exception thrown from compensate:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_2] - Compensate called on unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_3 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_3] - Compensate request dropped pending WS-BA participant recovery manager initialization for participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_4 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_4] - Compensate request dropped pending WS-BA participant recovery manager scan for unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_5 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_5] - Compensate request dropped pending registration of application-specific recovery module for WS-BA participant: {0}
     */
    public void compensate(final NotificationType compensate, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;

        /**
         * ensure the BA participant recovery manager is running
         */

        XTSBARecoveryManager recoveryManager = XTSBARecoveryManager.getRecoveryManager();

        if (recoveryManager == null) {
            // log warning and drop this message -- it will be resent
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_3", new Object[] {instanceIdentifier}) ;
            }

            return;
        }

        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.compensate(compensate, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_1", th) ;
                }
            }
        }
        else if (!recoveryManager.isParticipantRecoveryStarted())
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_4", new Object[] {instanceIdentifier}) ;
            }
        }
        else if (recoveryManager.findParticipantRecoveryRecord(instanceIdentifier.getInstanceIdentifier()) != null)
        {
            if (WSTLogger.arjLoggerI18N.isWarnEnabled())
            {
                WSTLogger.arjLoggerI18N.warn("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_5", new Object[] {instanceIdentifier}) ;
            }
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.compensate_2", new Object[] {instanceIdentifier}) ;
            }
            sendCompensated(map, arjunaContext) ;
        }
    }

    /**
     * Complete.
     * @param complete The complete notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_1] - Unexpected exception thrown from complete:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_2] - Complete called on unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_3 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_3] - Complete called on unknown participant
     */
    public void complete(final NotificationType complete, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.complete(complete, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_1", th) ;
                }
            }
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.complete_2", new Object[] {instanceIdentifier}) ;
            }
            sendFail(map, arjunaContext, State.STATE_ENDED.getValue()) ;
        }
    }

    /**
     * Exited.
     * @param exited The exited notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_1] - Unexpected exception thrown from exited:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_2] - Exited called on unknown participant: {0}
     */
    public void exited(final NotificationType exited, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.exited(exited, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_1", th) ;
                }
            }
        }
        else if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.exited_2", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Not Completed.
     * @param notCompleted The not completed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_1] - Unexpected exception thrown from notCompleted:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_2] - NotCompleted called on unknown participant: {0}
     */
    public void notCompleted(final NotificationType notCompleted, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.notCompleted(notCompleted, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_1", th) ;
                }
            }
        }
        else if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.notCompleted_2", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Failed.
     * @param failed The failed notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.failed_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.failed_1] - Unexpected exception thrown from failed:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.failed_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.failed_2] - Failed called on unknown participant: {0}
     */
    public void failed(final NotificationType failed, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.failed(failed, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.faulted_1", th) ;
                }
            }
        }
        else if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.faulted_2", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Get Status.
     * @param getStatus The get status notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_1] - Unexpected exception thrown from getStatus:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_2] - GetStatus called on unknown participant: {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_3 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_3] - Unexpected exception while sending InvalidStateFault to coordinator for participant {0}
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_4 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_4] - GetStatus requested for unknown coordinator completion participant
     */
    public void getStatus(final NotificationType getStatus, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.getStatus(getStatus, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_1", th) ;
                }
            }
        }
        else
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_2", new Object[] {instanceIdentifier}) ;
            }
            // send an invalid state fault

            final String messageId = MessageId.getMessageId();
            final MAP faultMAP = AddressingHelper.createFaultContext(map, messageId) ;
            try
            {
                final SoapFault11 soapFault = new SoapFault11(SoapFaultType.FAULT_SENDER, CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME,
                        WSTLogger.log_mesg.getString("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_4")) ;
                AddressingHelper.installNoneReplyTo(faultMAP);
                SoapFaultClient.sendSoapFault(soapFault, faultMAP, getFaultAction()) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isInfoEnabled())
                {
                    WSTLogger.arjLoggerI18N.info("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.getStatus_3", new Object[] {instanceIdentifier},  th) ;
                }
            }
        }
    }

    private static String getFaultAction()
    {
        return CoordinationConstants.WSCOOR_ACTION_FAULT;
    }

    /**
     * Status.
     * @param status The status.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_1] - Unexpected exception thrown from status:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_2] - Status called on unknown participant: {0}
     */
    public void status(final StatusType status, final MAP map, final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.status(status, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_1", th) ;
                }
            }
        }
        else if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.status_2", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * SOAP Fault.
     * @param fault The SOAP fault notification.
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_1 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_1] - Unexpected exception thrown from soapFault:
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_2 [com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_2] - SoapFault called on unknown participant: {0}
     */
    public void soapFault(final SoapFault fault, final MAP map,
        final ArjunaContext arjunaContext)
    {
        final InstanceIdentifier instanceIdentifier = arjunaContext.getInstanceIdentifier() ;
        final CoordinatorCompletionParticipantInboundEvents participant = getParticipant(instanceIdentifier) ;

        if (participant != null)
        {
            try
            {
                participant.soapFault(fault, map, arjunaContext) ;
            }
            catch (final Throwable th)
            {
                if (WSTLogger.arjLoggerI18N.isDebugEnabled())
                {
                    WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_1", th) ;
                }
            }
        }
        else if (WSTLogger.arjLoggerI18N.isDebugEnabled())
        {
            WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionParticipantProcessorImpl.soapFault_2", new Object[] {instanceIdentifier}) ;
        }
    }

    /**
     * Send a cancelled message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCancelled_1 [com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCancelled_1] - Unexpected exception while sending Cancelled
     */
    private void sendCancelled(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply null endpoint to indicate addressing properties should be used to route message
            CoordinatorCompletionCoordinatorClient.getClient().sendCancelled(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCancelled_1", th) ;
            }
        }
    }

    /**
     * Send a closed message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendClosed_1 [com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendClosed_1] - Unexpected exception while sending Closed
     */
    private void sendClosed(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply null endpoint to indicate addressing properties should be used to route message
            CoordinatorCompletionCoordinatorClient.getClient().sendClosed(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendClosed_1", th) ;
            }
        }
    }

    /**
     * Send a compensated message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCompensated_1 [com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCompensated_1] - Unexpected exception while sending Compensated
     */
    private void sendCompensated(final MAP map, final ArjunaContext arjunaContext)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createOneWayResponseContext(map, messageId) ;

        try
        {
            // supply null endpoint to indicate addressing properties should be used to route message
            CoordinatorCompletionCoordinatorClient.getClient().sendCompensated(null, responseMAP, arjunaContext.getInstanceIdentifier()) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendCompensated_1", th) ;
            }
        }
    }

    /**
     * Send a fail message.
     *
     * @param map The addressing context.
     * @param arjunaContext The arjuna context.
     * @param exceptionIdentifier The exception identifier.
     *
     * @message com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendFail_1 [com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendFail_1] - Unexpected exception while sending Fail
     */
    private void sendFail(final MAP map, final ArjunaContext arjunaContext, final QName exceptionIdentifier)
    {
        // KEV add check for recovery
        final String messageId = MessageId.getMessageId() ;
        final MAP responseMAP = AddressingHelper.createFaultContext(map, messageId) ;

        try
        {
            // supply null endpoint to indicate addressing properties should be used to route message
            CoordinatorCompletionCoordinatorClient.getClient().sendFail(null, responseMAP, arjunaContext.getInstanceIdentifier(), exceptionIdentifier) ;
        }
        catch (final Throwable th)
        {
            if (WSTLogger.arjLoggerI18N.isDebugEnabled())
            {
                WSTLogger.arjLoggerI18N.debug("com.arjuna.wst11.messaging.CoordinatorCompletionCoordinatorProcessorImpl.sendFail_1", th) ;
            }
        }
    }
}