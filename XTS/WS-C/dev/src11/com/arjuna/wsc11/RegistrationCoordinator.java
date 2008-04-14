package com.arjuna.wsc11;

import com.arjuna.webservices.SoapFault;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wscoor.CoordinationConstants;
import com.arjuna.webservices11.wscoor.client.WSCOORClient;
import com.arjuna.wsc.AlreadyRegisteredException;
import com.arjuna.wsc.InvalidProtocolException;
import com.arjuna.wsc.InvalidStateException;
import com.arjuna.wsc.NoActivityException;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponseType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegistrationPortType;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * Wrapper around low level Registration Coordinator messaging.
 * @author kevin
 */
public class RegistrationCoordinator
{
    /**
     * Register the participant in the protocol.
     * @param coordinationContext The current coordination context
     * @param messageID The messageID to use.
     * @param participantProtocolService The participant protocol service.
     * @param protocolIdentifier The protocol identifier.
     * @return The endpoint reference of the coordinator protocol service.
     * @throws com.arjuna.wsc.AlreadyRegisteredException If the participant is already registered.
     * @throws com.arjuna.wsc.InvalidProtocolException If the protocol is unsupported.
     * @throws com.arjuna.wsc.InvalidStateException If the state is invalid
     * @throws com.arjuna.wsc.NoActivityException If there is to activity context active.
     * @throws com.arjuna.webservices.SoapFault for errors during processing.
     */
    public static W3CEndpointReference register(final CoordinationContextType coordinationContext,
        final String messageID, final W3CEndpointReference participantProtocolService,
        final String protocolIdentifier)
        throws AlreadyRegisteredException, InvalidProtocolException,
            InvalidStateException, NoActivityException, SoapFault
    {
        final W3CEndpointReference endpointReference = coordinationContext.getRegistrationService() ;

        try
        {
            RegisterType registerType = new RegisterType();
            RegisterResponseType response;

            registerType.setProtocolIdentifier(protocolIdentifier);
            registerType.setParticipantProtocolService(participantProtocolService);
            RegistrationPortType port = WSCOORClient.getRegistrationPort(endpointReference, CoordinationConstants.WSCOOR_ACTION_REGISTER, messageID);
            response = port.registerOperation(registerType);
            return response.getCoordinatorProtocolService();
        } catch (SOAPFaultException sfe) {
            // TODO -- work out which faults we should really throw. in particular do we need to retain SoapFault
            final SOAPFault soapFault = sfe.getFault() ;
            final QName subcode = soapFault.getFaultCodeAsQName() ;
            if (CoordinationConstants.WSCOOR_ERROR_CODE_ALREADY_REGISTERED_QNAME.equals(subcode))
            {
                throw new AlreadyRegisteredException(soapFault.getFaultString()) ;
            }
            else if (CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_PROTOCOL_QNAME.equals(subcode))
            {
                throw new InvalidProtocolException(soapFault.getFaultString()) ;
            }
            else if (CoordinationConstants.WSCOOR_ERROR_CODE_INVALID_STATE_QNAME.equals(subcode))
            {
                throw new InvalidStateException(soapFault.getFaultString()) ;
            }
            else if (CoordinationConstants.WSCOOR_ERROR_CODE_NO_ACTIVITY_QNAME.equals(subcode))
            {
                throw new NoActivityException(soapFault.getFaultString()) ;
            }
            throw new SoapFault11(sfe);
        }
    }
}
