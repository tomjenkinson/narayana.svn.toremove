/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 * (C) 2009,
 * @author JBoss Inc.
 */
package org.jboss.jbossts.qa.junit.testgroup;

import org.jboss.jbossts.qa.junit.*;
import org.junit.*;

// Automatically generated by XML2JUnit
public class TestGroup_rawresources02_2 extends TestGroupBase
{
	public String getTestGroupName()
	{
		return "rawresources02_2";
	}

	protected Task server0 = null;

	@Before public void setUp()
	{
		super.setUp();
		server0 = createTask("server0", com.arjuna.ats.arjuna.recovery.RecoveryManager.class, Task.TaskType.EXPECT_READY, 480);
		server0.start("-test");
	}

	@After public void tearDown()
	{
		try {
			server0.terminate();
            removeServerIORStore("task0", "$(1)", "$(2)");
		} finally {
			super.tearDown();
		}
	}

    private void runTwoServersOneClient(Class clientClass) {
        Task server1 = createTask("server1", org.jboss.jbossts.qa.RawResources02Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server1.start("$(1)");
		Task server2 = createTask("server2", org.jboss.jbossts.qa.RawResources02Servers.Server01.class, Task.TaskType.EXPECT_READY, 480);
		server2.start("$(2)");
		Task client0 = createTask("client0", clientClass, Task.TaskType.EXPECT_PASS_FAIL, 480);
		client0.start("$(1)", "$(2)");
		client0.waitFor();
		server2.terminate();
		server1.terminate();
    }

	@Test public void RawResources02_2_Test001()
	{
		setTestName("Test001");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client001.class);
	}
    
    @Test public void RawResources02_2_Test002()
	{
		setTestName("Test002");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client002.class);
	}

	@Test public void RawResources02_2_Test003()
	{
		setTestName("Test003");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client003.class);
	}

	@Test public void RawResources02_2_Test004()
	{
		setTestName("Test004");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client004.class);
	}

	@Test public void RawResources02_2_Test005()
	{
		setTestName("Test005");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client005.class);
	}

	@Test public void RawResources02_2_Test006()
	{
		setTestName("Test006");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client006.class);
	}

	@Test public void RawResources02_2_Test007()
	{
		setTestName("Test007");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client007.class);
	}

	@Test public void RawResources02_2_Test008()
	{
		setTestName("Test008");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client008.class);
	}

	@Test public void RawResources02_2_Test009()
	{
		setTestName("Test009");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client009.class);
	}

	@Test public void RawResources02_2_Test010()
	{
		setTestName("Test010");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client010.class);
	}

	@Test public void RawResources02_2_Test011()
	{
		setTestName("Test011");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client011.class);
	}

	@Test public void RawResources02_2_Test012()
	{
		setTestName("Test012");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client012.class);
	}

	@Test public void RawResources02_2_Test013()
	{
		setTestName("Test013");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client013.class);
	}

	@Test public void RawResources02_2_Test014()
	{
		setTestName("Test014");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client014.class);
	}

	@Test public void RawResources02_2_Test015()
	{
		setTestName("Test015");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client015.class);
	}

	@Test public void RawResources02_2_Test016()
	{
		setTestName("Test016");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client016.class);
	}

	@Test public void RawResources02_2_Test017()
	{
		setTestName("Test017");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client017.class);
	}

	@Test public void RawResources02_2_Test018()
	{
		setTestName("Test018");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client018.class);
	}

	@Test public void RawResources02_2_Test019()
	{
		setTestName("Test019");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client019.class);
	}

	@Test public void RawResources02_2_Test020()
	{
		setTestName("Test020");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client020.class);
	}

	@Test public void RawResources02_2_Test021()
	{
		setTestName("Test021");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client021.class);
	}

	@Test public void RawResources02_2_Test022()
	{
		setTestName("Test022");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client022.class);
	}

	@Test public void RawResources02_2_Test023()
	{
		setTestName("Test023");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client023.class);
	}

	@Test public void RawResources02_2_Test024()
	{
		setTestName("Test024");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client024.class);
	}

	@Test public void RawResources02_2_Test025()
	{
		setTestName("Test025");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client025.class);
	}

	@Test public void RawResources02_2_Test026()
	{
		setTestName("Test026");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client026.class);
	}

	@Test public void RawResources02_2_Test027()
	{
		setTestName("Test027");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client027.class);
	}

	@Test public void RawResources02_2_Test028()
	{
		setTestName("Test028");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client028.class);
	}

	@Test public void RawResources02_2_Test029()
	{
		setTestName("Test029");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client029.class);
	}

	@Test public void RawResources02_2_Test030()
	{
		setTestName("Test030");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client030.class);
	}

	@Test public void RawResources02_2_Test031()
	{
		setTestName("Test031");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client031.class);
	}

	@Test public void RawResources02_2_Test032()
	{
		setTestName("Test032");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client032.class);
	}

	@Test public void RawResources02_2_Test033()
	{
		setTestName("Test033");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client033.class);
	}

	@Test public void RawResources02_2_Test034()
	{
		setTestName("Test034");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client034.class);
	}

	@Test public void RawResources02_2_Test035()
	{
		setTestName("Test035");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client035.class);
	}

	@Test public void RawResources02_2_Test036()
	{
		setTestName("Test036");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client036.class);
	}

	@Test public void RawResources02_2_Test037()
	{
		setTestName("Test037");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client037.class);
	}

	@Test public void RawResources02_2_Test038()
	{
		setTestName("Test038");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client038.class);
	}

	@Test public void RawResources02_2_Test039()
	{
		setTestName("Test039");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client039.class);
	}

	@Test public void RawResources02_2_Test040()
	{
		setTestName("Test040");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client040.class);
	}

	@Test public void RawResources02_2_Test041()
	{
		setTestName("Test041");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client041.class);
	}

	@Test public void RawResources02_2_Test042()
	{
		setTestName("Test042");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client042.class);
	}

	@Test public void RawResources02_2_Test043()
	{
		setTestName("Test043");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client043.class);
	}

	@Test public void RawResources02_2_Test044()
	{
		setTestName("Test044");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client044.class);
	}

	@Test public void RawResources02_2_Test045()
	{
		setTestName("Test045");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client045.class);
	}

	@Test public void RawResources02_2_Test046()
	{
		setTestName("Test046");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client046.class);
	}

	@Test public void RawResources02_2_Test047()
	{
		setTestName("Test047");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client047.class);
	}

	@Test public void RawResources02_2_Test048()
	{
		setTestName("Test048");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client048.class);
	}

	@Test public void RawResources02_2_Test049()
	{
		setTestName("Test049");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client049.class);
	}

	@Test public void RawResources02_2_Test050()
	{
		setTestName("Test050");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client050.class);
	}

	@Test public void RawResources02_2_Test051()
	{
		setTestName("Test051");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client051.class);
	}

	@Test public void RawResources02_2_Test052()
	{
		setTestName("Test052");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client052.class);
	}

	@Test public void RawResources02_2_Test053()
	{
		setTestName("Test053");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client053.class);
	}

	@Test public void RawResources02_2_Test054()
	{
		setTestName("Test054");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client054.class);
	}

	@Test public void RawResources02_2_Test055()
	{
		setTestName("Test055");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client055.class);
	}

	@Test public void RawResources02_2_Test056()
	{
		setTestName("Test056");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client056.class);
	}

	@Test public void RawResources02_2_Test057()
	{
		setTestName("Test057");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client057.class);
	}

	@Test public void RawResources02_2_Test058()
	{
		setTestName("Test058");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client058.class);
	}

	@Test public void RawResources02_2_Test059()
	{
		setTestName("Test059");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client059.class);
	}

	@Test public void RawResources02_2_Test060()
	{
		setTestName("Test060");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client060.class);
	}

	@Test public void RawResources02_2_Test061()
	{
		setTestName("Test061");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client061.class);
	}

	@Test public void RawResources02_2_Test062()
	{
		setTestName("Test062");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client062.class);
	}

	@Test public void RawResources02_2_Test063()
	{
		setTestName("Test063");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client063.class);
	}

	@Test public void RawResources02_2_Test064()
	{
		setTestName("Test064");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client064.class);
	}

	@Test public void RawResources02_2_Test065()
	{
		setTestName("Test065");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client065.class);
	}

	@Test public void RawResources02_2_Test066()
	{
		setTestName("Test066");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client066.class);
	}

	@Test public void RawResources02_2_Test067()
	{
		setTestName("Test067");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client067.class);
	}

	@Test public void RawResources02_2_Test068()
	{
		setTestName("Test068");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client068.class);
	}

	@Test public void RawResources02_2_Test069()
	{
		setTestName("Test069");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client069.class);
	}

	@Test public void RawResources02_2_Test070()
	{
		setTestName("Test070");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client070.class);
	}

	@Test public void RawResources02_2_Test071()
	{
		setTestName("Test071");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client071.class);
	}

	@Test public void RawResources02_2_Test072()
	{
		setTestName("Test072");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client072.class);
	}

	@Test public void RawResources02_2_Test073()
	{
		setTestName("Test073");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client073.class);
	}

	@Test public void RawResources02_2_Test074()
	{
		setTestName("Test074");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client074.class);
	}

	@Test public void RawResources02_2_Test075()
	{
		setTestName("Test075");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client075.class);
	}

	@Test public void RawResources02_2_Test076()
	{
		setTestName("Test076");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client076.class);
	}

	@Test public void RawResources02_2_Test077()
	{
		setTestName("Test077");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client077.class);
	}

	@Test public void RawResources02_2_Test078()
	{
		setTestName("Test078");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client078.class);
	}

	@Test public void RawResources02_2_Test079()
	{
		setTestName("Test079");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client079.class);
	}

	@Test public void RawResources02_2_Test080()
	{
		setTestName("Test080");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client080.class);
	}

	@Test public void RawResources02_2_Test081()
	{
		setTestName("Test081");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client081.class);
	}

	@Test public void RawResources02_2_Test082()
	{
		setTestName("Test082");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client082.class);
	}

	@Test public void RawResources02_2_Test083()
	{
		setTestName("Test083");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client083.class);
	}

	@Test public void RawResources02_2_Test084()
	{
		setTestName("Test084");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client084.class);
	}

	@Test public void RawResources02_2_Test085()
	{
		setTestName("Test085");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client085.class);
	}

	@Test public void RawResources02_2_Test086()
	{
		setTestName("Test086");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client086.class);
	}

	@Test public void RawResources02_2_Test087()
	{
		setTestName("Test087");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client087.class);
	}

	@Test public void RawResources02_2_Test088()
	{
		setTestName("Test088");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client088.class);
	}

	@Test public void RawResources02_2_Test089()
	{
		setTestName("Test089");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client089.class);
	}

	@Test public void RawResources02_2_Test090()
	{
		setTestName("Test090");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client090.class);
	}

	@Test public void RawResources02_2_Test091()
	{
		setTestName("Test091");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client091.class);
	}

	@Test public void RawResources02_2_Test092()
	{
		setTestName("Test092");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client092.class);
	}

	@Test public void RawResources02_2_Test093()
	{
		setTestName("Test093");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client093.class);
	}

	@Test public void RawResources02_2_Test094()
	{
		setTestName("Test094");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client094.class);
	}

	@Test public void RawResources02_2_Test095()
	{
		setTestName("Test095");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client095.class);
	}

	@Test public void RawResources02_2_Test096()
	{
		setTestName("Test096");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client096.class);
	}

	@Test public void RawResources02_2_Test097()
	{
		setTestName("Test097");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client097.class);
	}

	@Test public void RawResources02_2_Test098()
	{
		setTestName("Test098");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client098.class);
	}

	@Test public void RawResources02_2_Test099()
	{
		setTestName("Test099");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client099.class);
	}

	@Test public void RawResources02_2_Test100()
	{
		setTestName("Test100");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client100.class);
	}

	@Test public void RawResources02_2_Test101()
	{
		setTestName("Test101");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client101.class);
	}

	@Test public void RawResources02_2_Test102()
	{
		setTestName("Test102");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client102.class);
	}

	@Test public void RawResources02_2_Test103()
	{
		setTestName("Test103");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client103.class);
	}

	@Test public void RawResources02_2_Test104()
	{
		setTestName("Test104");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client104.class);
	}

	@Test public void RawResources02_2_Test105()
	{
		setTestName("Test105");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client105.class);
	}

	@Test public void RawResources02_2_Test106()
	{
		setTestName("Test106");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client106.class);
	}

	@Test public void RawResources02_2_Test107()
	{
		setTestName("Test107");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client107.class);
	}

	@Test public void RawResources02_2_Test108()
	{
		setTestName("Test108");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client108.class);
	}

	@Test public void RawResources02_2_Test109()
	{
		setTestName("Test109");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client109.class);
	}

	@Test public void RawResources02_2_Test110()
	{
		setTestName("Test110");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client110.class);
	}

	@Test public void RawResources02_2_Test111()
	{
		setTestName("Test111");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client111.class);
	}

	@Test public void RawResources02_2_Test112()
	{
		setTestName("Test112");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client112.class);
	}

	@Test public void RawResources02_2_Test113()
	{
		setTestName("Test113");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client113.class);
	}

	@Test public void RawResources02_2_Test114()
	{
		setTestName("Test114");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client114.class);
	}

	@Test public void RawResources02_2_Test115()
	{
		setTestName("Test115");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client115.class);
	}

	@Test public void RawResources02_2_Test116()
	{
		setTestName("Test116");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client116.class);
	}

	@Test public void RawResources02_2_Test117()
	{
		setTestName("Test117");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client117.class);
	}

	@Test public void RawResources02_2_Test118()
	{
		setTestName("Test118");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client118.class);
	}

	@Test public void RawResources02_2_Test119()
	{
		setTestName("Test119");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client119.class);
	}

	@Test public void RawResources02_2_Test120()
	{
		setTestName("Test120");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client120.class);
	}

	@Test public void RawResources02_2_Test121()
	{
		setTestName("Test121");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client121.class);
	}

	@Test public void RawResources02_2_Test122()
	{
		setTestName("Test122");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client122.class);
	}

	@Test public void RawResources02_2_Test123()
	{
		setTestName("Test123");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client123.class);
	}

	@Test public void RawResources02_2_Test124()
	{
		setTestName("Test124");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client124.class);
	}

	@Test public void RawResources02_2_Test125()
	{
		setTestName("Test125");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client125.class);
	}

	@Test public void RawResources02_2_Test126()
	{
		setTestName("Test126");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client126.class);
	}

	@Test public void RawResources02_2_Test127()
	{
		setTestName("Test127");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client127.class);
	}

	@Test public void RawResources02_2_Test128()
	{
		setTestName("Test128");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client128.class);
	}

	@Test public void RawResources02_2_Test129()
	{
		setTestName("Test129");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client129.class);
	}

	@Test public void RawResources02_2_Test130()
	{
		setTestName("Test130");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client130.class);
	}

	@Test public void RawResources02_2_Test131()
	{
		setTestName("Test131");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client131.class);
	}

	@Test public void RawResources02_2_Test132()
	{
		setTestName("Test132");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client132.class);
	}

	@Test public void RawResources02_2_Test133()
	{
		setTestName("Test133");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client133.class);
	}

	@Test public void RawResources02_2_Test134()
	{
		setTestName("Test134");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client134.class);
	}

	@Test public void RawResources02_2_Test135()
	{
		setTestName("Test135");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client135.class);
	}

	@Test public void RawResources02_2_Test136()
	{
		setTestName("Test136");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client136.class);
	}

	@Test public void RawResources02_2_Test137()
	{
		setTestName("Test137");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client137.class);
	}

	@Test public void RawResources02_2_Test138()
	{
		setTestName("Test138");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client138.class);
	}

	@Test public void RawResources02_2_Test139()
	{
		setTestName("Test139");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client139.class);
	}

	@Test public void RawResources02_2_Test140()
	{
		setTestName("Test140");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client140.class);
	}

	@Test public void RawResources02_2_Test141()
	{
		setTestName("Test141");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client141.class);
	}

	@Test public void RawResources02_2_Test142()
	{
		setTestName("Test142");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client142.class);
	}

	@Test public void RawResources02_2_Test143()
	{
		setTestName("Test143");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client143.class);
	}

	@Test public void RawResources02_2_Test144()
	{
		setTestName("Test144");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client144.class);
	}

	@Test public void RawResources02_2_Test145()
	{
		setTestName("Test145");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client145.class);
	}

	@Test public void RawResources02_2_Test146()
	{
		setTestName("Test146");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client146.class);
	}

	@Test public void RawResources02_2_Test147()
	{
		setTestName("Test147");
        runTwoServersOneClient(org.jboss.jbossts.qa.RawResources02Clients2.Client147.class);
	}
}
