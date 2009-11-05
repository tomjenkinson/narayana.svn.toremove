package org.jboss.jbossts.xts.recovery.coordinator.ba;

import org.jboss.jbossts.xts.logging.XTSLogger;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.logging.FacilityCode;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;

import com.arjuna.mwlabs.wscf.model.sagas.arjunacore.subordinate.SubordinateBACoordinator;
import com.arjuna.common.util.logging.DebugLevel;
import com.arjuna.common.util.logging.VisibilityLevel;

/**
 * This class is a plug-in module for the recovery manager.
 * It is responsible for recovering failed WSBA ACCoordinator transactions.
 *
 * @message org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverySubordinateBACoordinator_1 [org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_1] - RecoverBASubordinateCoordinator.replayPhase2 recovering {0} ActionStatus is {1}
 * @message org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_2 [org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_2] - RecoverBASubordinateCoordinator.replayPhase2: Unexpected status: {0}
 * @message org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_3 [org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_3] - RecoverBASubordinateCoordinator.replayPhase2( {0} )  finished
 * @message org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_4 [org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_4] - RecoverBASubordinateCoordinator.replayPhase2 transaction {0} not activated, unable to replay phase 2 commit
*/
public class RecoverySubordinateBACoordinator extends SubordinateBACoordinator {

   /**
    * Re-creates/activates an AtomicAction for the specified
    * transaction Uid.
    */
   public RecoverySubordinateBACoordinator( Uid rcvUid )
   {
      super( rcvUid ) ;
      _activated = activate() ;
   }

    /**
     * run parent activate and also make this coordinator visible if there might be a durable participant waiting
     * for it to commit.
     * @return
     */
    public boolean activate()
    {
        boolean result = super.activate();

        // if we cannot activate we want the participant which was registered on behalf of this
        // coordinator to produce a heuristic result for the transaction. it will do this if it
        // finds no entry for the coordinate in the subordinate coordinators list. in this case
        // the subordinate transaction record needs to left as is awaiting manual intervention.

        if (result) {
            // record that the activation worked
            setActivated();

            int status = status();

            if (status == ActionStatus.PREPARED || status == ActionStatus.COMMITTING) {
                // we need to install this coordinator in a global table so that the participant which
                // was driving it will know that it has been recovered but not yet committed

                SubordinateBACoordinator.addRecoveredCoordinator(this);
            }
        }
        return result;
    }

   /**
    * Replays phase 2 of the commit protocol.
    */
   public void replayPhase2()
   {
       final int status = status();

       if (XTSLogger.arjLoggerI18N.isDebugEnabled())
       {
	   XTSLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
					FacilityCode.FAC_CRASH_RECOVERY,
					"org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_1",
					new Object[]{get_uid(), ActionStatus.stringForm(status)});
       }

       if ( _activated )
       {
           // we don't run phase 2 again if status is PREPARED or COMMITTING because we need to wait
           // for the parent coordinator to tell us what to do. this is true if the coordinator has
           // been recreated by the first recovery operation after a crash or by a reload from the
           // log after it was saved in response to a comms tiemout from one of the participants.
           // In either case the parent transaction should get back to us.

       if ((status == ActionStatus.PREPARED) ||
               (status == ActionStatus.COMMITTING)||
               (status == ActionStatus.COMMITTED) ||
               (status == ActionStatus.H_COMMIT) ||
               (status == ActionStatus.H_MIXED) ||
               (status == ActionStatus.H_HAZARD))
	   {
	       // ok, we are ready to commit but we wait
           // for the parent transaction to drive phase2Commit
           // so do nothing just now
	   } else if ((status ==  ActionStatus.ABORTED) ||
               (status == ActionStatus.H_ROLLBACK) ||
               (status == ActionStatus.ABORTING) ||
               (status == ActionStatus.ABORT_ONLY))
       {
           // TODO -- do we need to do this?
           super.phase2Abort( _reportHeuristics ) ;
           SubordinateBACoordinator.removeRecoveredCoordinator(this);
       }

       if (XTSLogger.arjLoggerI18N.isDebugEnabled())
	   {
	       XTSLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC,
					    FacilityCode.FAC_CRASH_RECOVERY,
					    "org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_3",
					    new Object[]{get_uid()});
	   }
       }
       else
       {
	   XTSLogger.arjLoggerI18N.warn("org.jboss.jbossts.xts.recovery.coordinator.ba.RecoverBASubordinateCoordinator_4", new Object[]{get_uid()});
       }
   }

   // Flag to indicate that this transaction has been re-activated
   // successfully.
   private boolean _activated = false ;

   // whether heuristic reporting on phase 2 commit is enabled.
   private boolean _reportHeuristics = true ;
}