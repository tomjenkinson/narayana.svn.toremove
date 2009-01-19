package org.jboss.jbossts.xts.servicetests.service.recovery;

import org.jboss.jbossts.xts.recovery.participant.at.XTSATRecoveryModule;
import org.jboss.jbossts.xts.recovery.participant.at.XTSATRecoveryManager;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryModule;
import org.jboss.jbossts.xts.recovery.participant.ba.XTSBARecoveryManager;
import org.jboss.jbossts.xts.servicetests.service.Constants;
import org.jboss.jbossts.xts.servicetests.service.participant.DurableTestParticipant;
import com.arjuna.wst.Durable2PCParticipant;
import com.arjuna.wst.BusinessAgreementWithParticipantCompletionParticipant;
import com.arjuna.wst.BusinessAgreementWithCoordinatorCompletionParticipant;

import java.io.ObjectInputStream;

/**
 * Application-specific WS-AT participant recovery manager for demo application, This class
 * is responsible for recreating application-specific durable participants from records
 * logged at prepare time.
 */
public class TestBARecoveryModule implements XTSBARecoveryModule
{
    /**
     * the singleton recovery module
     */
    private static TestBARecoveryModule theRecoveryModule = null;

    /**
     * a count of how many xts demo services are currently installed
     */
    private static int serviceCount = 0;

    /**
     * called during deployment of an xts-demo web service to ensure the recovery module for the
     * demo is installed whenever any of the services is active
     */
    public static void register()
    {
        if (theRecoveryModule == null) {
            theRecoveryModule = new TestBARecoveryModule();
        }
        if (serviceCount == 0) {
            XTSBARecoveryManager.getRecoveryManager().registerRecoveryModule(theRecoveryModule);
        }
        serviceCount++;
    }

    /**
     * called during undeployment of an xts-demo web service to ensure the recovery module for
     * the demo is deinstalled once none of the services is active
     */
    public static void unregister()
    {
        if (serviceCount > 0) {
            serviceCount--;
            if (serviceCount == 0) {
                XTSBARecoveryManager.getRecoveryManager().unregisterRecoveryModule(theRecoveryModule);
            }
        }
    }

    /**
     * called during recovery processing to allow an application to identify a participant id
     * belonging to one of its participants and recreate the participant by deserializing
     * it from the supplied object input stream. n.b. this is only appropriate in case the
     * participant was originally saved using serialization.
     *
     * @param id     the id used when the participant was created
     * @param stream a stream from which the application should deserialise the participant
     *               if it recognises that the id belongs to the module's application
     * @return
     * @throws Exception if an error occurs deserializing the durable participant
     */
    public Durable2PCParticipant deserialize(String id, ObjectInputStream stream) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX + "DurableTestParticipant")) {
            System.out.println("xts service test : attempting to deserialize WS-AT participant " + id);
            DurableTestParticipant participant = (DurableTestParticipant)stream.readObject();
            System.out.println("xts service test : deserialized WS-AT participant " + id);
            return participant;
        }

        return null;
    }

    /**
     * called during recovery processing to allow an application to identify a participant id
     * belonging to one of its participants and use the saved recovery state to recreate the
     * participant. n.b. this is only appropriate in case the participant was originally saved
     * after being converted to a byte array using the PersistibleATParticipant interface.
     *
     * @param id            the id used when the participant was created
     * @param recoveryState a byte array returned form the original participant via a call to
     *                      method getRecoveryState of interface PersistableATParticipant
     * @return
     * @throws Exception if an error occurs converting the recoveryState back to a
     *                   durable participant
     */
    public Durable2PCParticipant recreate(String id, byte[] recoveryState) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX)) {
            // this should not get called -- test WS-AT participants are saved and restored
            // using serialization
            throw new Exception("xts service test : invalid request to recreate() WS-AT participant " + id);
        }
        return null;
    }

    public BusinessAgreementWithParticipantCompletionParticipant deserializeParticipantCompletionParticipant(String id, ObjectInputStream stream) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX + "ParticipantCompletionParticipant")) {
            System.out.println("xts service test : attempting to deserialize WS-BA participant completion participant " + id);
            BusinessAgreementWithParticipantCompletionParticipant participant
                    = (BusinessAgreementWithParticipantCompletionParticipant)stream.readObject();
            System.out.println("xts service test : deserialized WS-BA participant completion participant " + id);
            return participant;
        }

        return null;
    }

    public BusinessAgreementWithParticipantCompletionParticipant recreateParticipantCompletionParticipant(String id, byte[] recoveryState) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX + "ParticipantCompletionParticipant")) {
            // this should not get called -- test WS-BA participants are saved and restored
            // using serialization
            throw new Exception("xts service test : invalid request to recreate() WS-BA participant completion participant " + id);
        }

        return null;
    }

    public BusinessAgreementWithCoordinatorCompletionParticipant deserializeCoordinatorCompletionParticipant(String id, ObjectInputStream stream) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX + "CoordinatorCompletionParticipant")) {
            System.out.println("xts service test : attempting to deserialize WS-BA participant completion participant " + id);
            BusinessAgreementWithCoordinatorCompletionParticipant participant
                    = (BusinessAgreementWithCoordinatorCompletionParticipant)stream.readObject();
            System.out.println("xts service test : deserialized WS-BA coordinator completion participant " + id);
            return participant;
        }

        return null;
    }

    public BusinessAgreementWithCoordinatorCompletionParticipant recreateCoordinatorCompletionParticipant(String id, byte[] recoveryState) throws Exception {
        if (id.startsWith(Constants.PARTICIPANT_ID_PREFIX + "ParticipantCompletionParticipant")) {
            // this should not get called -- test WS-BA participants are saved and restored
            // using serialization
            throw new Exception("xts service test : invalid request to recreate() WS-BA coordinator completion participant " + id);
        }
        
        return null;
    }
}