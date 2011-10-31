package org.jboss.jbossts.xts.crash.test;

import org.jboss.jbossts.xts.crash.test.at.SingleParticipantPrepareAndCommit;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	SingleParticipantPrepareAndCommit.class,
	WatchRecovery.class,
	RenameTestLog.class
})

public class TestATCrashDuringOnePhaseCommitSuite extends BaseCrashTest {
	@BeforeClass
	public static void setUp() throws Exception {
		deleteTestLog();
		copyBytemanScript("ATCrashDuringOnePhaseCommit.txt");
		RenameTestLog.scriptName = "ATCrashDuringOnePhaseCommit";
	}
}
