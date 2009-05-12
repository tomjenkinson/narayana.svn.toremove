package com.arjuna.webservices11.wsat.sei;

import org.oasis_open.docs.ws_tx.wsat._2006._06.Notification;
import org.oasis_open.docs.ws_tx.wsat._2006._06.CompletionInitiatorPortType;
import org.jboss.jbossts.xts.soapfault.Fault;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.handler.MessageContext;

import com.arjuna.webservices11.wsarj.ArjunaContext;
import com.arjuna.webservices11.wsat.processors.CompletionInitiatorProcessor;
import com.arjuna.webservices11.SoapFault11;
import com.arjuna.webservices11.wsaddr.map.MAP;
import com.arjuna.webservices11.wsaddr.AddressingHelper;
import com.arjuna.services.framework.task.TaskManager;
import com.arjuna.services.framework.task.Task;
import com.arjuna.webservices.SoapFault;

/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1-b03-
 * Generated source version: 2.0
 *
 */
@WebService(name = "CompletionInitiatorPortType", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06",
        wsdlLocation = "/WEB-INF/wsdl/wsat-completion-initiator-binding.wsdl",
        serviceName = "CompletionInitiatorService",
        portName = "CompletionInitiatorPortType"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@HandlerChain(file="/handlers.xml")
@Addressing(required=true)
public class CompletionInitiatorPortTypeImpl implements CompletionInitiatorPortType
{

    @Resource
     private WebServiceContext webServiceCtx;

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "CommittedOperation", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/Committed")
    @Oneway
    public void committedOperation(
        @WebParam(name = "Committed", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", partName = "parameters")
        Notification parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final Notification committed = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                CompletionInitiatorProcessor.getProcessor().handleCommitted(committed, inboundMap, arjunaContext) ;
            }
        }) ;
    }

    /**
     *
     * @param parameters
     */
    @WebMethod(operationName = "AbortedOperation", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/Aborted")
    @Oneway
    public void abortedOperation(
        @WebParam(name = "Aborted", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", partName = "parameters")
        Notification parameters)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final Notification aborted = parameters;
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                CompletionInitiatorProcessor.getProcessor().handleAborted(aborted, inboundMap, arjunaContext) ;
            }
        }) ;
    }

    @WebMethod(operationName = "fault", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/fault")
    @Oneway
    public void fault(
            @WebParam(name = "Fault", targetNamespace = "http://schemas.xmlsoap.org/soap/envelope/", partName = "parameters")
            Fault fault)
    {
        MessageContext ctx = webServiceCtx.getMessageContext();
        final MAP inboundMap = AddressingHelper.inboundMap(ctx);
        final ArjunaContext arjunaContext = ArjunaContext.getCurrentContext(ctx);
        final SoapFault soapFault = SoapFault11.fromFault(fault);

        TaskManager.getManager().queueTask(new Task() {
            public void executeTask() {
                CompletionInitiatorProcessor.getProcessor().handleSoapFault(soapFault, inboundMap, arjunaContext); ;
            }
        }) ;
    }
}
