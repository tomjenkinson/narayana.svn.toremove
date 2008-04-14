
package org.oasis_open.docs.ws_tx.wsba._2006._06;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.Service21;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.2-hudson-182-RC1
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "BusinessAgreementWithParticipantCompletionParticipantService", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsba/2006/06", wsdlLocation = "wsdl/wsba-participant-completion-participant-binding.wsdl")
public class BusinessAgreementWithParticipantCompletionParticipantService
    extends Service21
{

    private final static URL BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONPARTICIPANTSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.oasis_open.docs.ws_tx.wsba._2006._06.BusinessAgreementWithParticipantCompletionParticipantService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.oasis_open.docs.ws_tx.wsba._2006._06.BusinessAgreementWithParticipantCompletionParticipantService.class.getResource("");
            url = new URL(baseUrl, "wsdl/wsba-participant-completion-participant-binding.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'wsdl/wsba-participant-completion-participant-binding.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONPARTICIPANTSERVICE_WSDL_LOCATION = url;
    }

    public BusinessAgreementWithParticipantCompletionParticipantService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BusinessAgreementWithParticipantCompletionParticipantService() {
        super(BUSINESSAGREEMENTWITHPARTICIPANTCOMPLETIONPARTICIPANTSERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/ws-tx/wsba/2006/06", "BusinessAgreementWithParticipantCompletionParticipantService"));
    }

    /**
     * 
     * @return
     *     returns BusinessAgreementWithParticipantCompletionParticipantPortType
     */
    @WebEndpoint(name = "BusinessAgreementWithParticipantCompletionParticipantPortType")
    public BusinessAgreementWithParticipantCompletionParticipantPortType getBusinessAgreementWithParticipantCompletionParticipantPortType() {
        return super.getPort(new QName("http://docs.oasis-open.org/ws-tx/wsba/2006/06", "BusinessAgreementWithParticipantCompletionParticipantPortType"), BusinessAgreementWithParticipantCompletionParticipantPortType.class);
    }

}
