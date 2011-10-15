package com.arjuna.ats.jta.distributed;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.junit.BeforeClass;
import org.junit.Test;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.jta.distributed.server.IsolatableServersClassLoader;
import com.arjuna.ats.jta.distributed.server.LocalServer;
import com.arjuna.ats.jta.distributed.server.RemoteServer;

public class SimpleIsolatedServers {
	private static LocalServer[] localServers = new LocalServer[3];
	private static RemoteServer[] remoteServers = new RemoteServer[3];

	@BeforeClass
	public static void setup() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			CoreEnvironmentBeanException, IOException {

		// Get the Server interface loaded, only way I found to do this was
		// instantiate one
		java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { LocalServer.class, RemoteServer.class },
				new InvocationHandler() {

					@Override
					public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
						// TODO Auto-generated method stub
						return null;
					}
				});

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		for (int i = 0; i < localServers.length; i++) {
			IsolatableServersClassLoader classLoader = new IsolatableServersClassLoader(contextClassLoader);
			localServers[i] = (LocalServer) classLoader.loadClass("com.arjuna.ats.jta.distributed.server.impl.ServerImpl").newInstance();
			localServers[i].initialise((i + 1) * 1000);
			remoteServers[i] = localServers[i].connectTo();
		}
	}

	@Test
	public void testRecovery() {
		localServers[0].doRecoveryManagerScan();
	}

	@Test
	public void testMigrateTransaction() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, InvalidTransactionException,
			XAException, SecurityException, HeuristicMixedException, HeuristicRollbackException {

		File file = new File(System.getProperty("user.dir") + "/tmp/");
		if (file.exists()) {
			file.delete();
		}
		int startingTimeout = 0;

		// Start out at the first server
		LocalServer originalServer = getLocalServer(1000);
		TransactionManager transactionManager = originalServer.getTransactionManager();
		transactionManager.setTransactionTimeout(startingTimeout);
		transactionManager.begin();
		Transaction originalTransaction = transactionManager.getTransaction();
		originalTransaction.registerSynchronization(new TestSynchronization(originalServer.getNodeName()));
		originalTransaction.enlistResource(new TestResource(originalServer.getNodeName(), false));
		Xid toMigrate = originalServer.getCurrentXid();

		// Loop through the rest of the servers passing the transaction up and
		// down
		Transaction suspendedTransaction = originalServer.getTransactionManager().suspend();
		long timeLeftBeforeTransactionTimeout = originalServer.getTimeLeftBeforeTransactionTimeout();
		List<Integer> nodesToFlowTo = new LinkedList<Integer>(Arrays.asList(new Integer[] { 2000, 3000, 2000, 1000, 2000, 3000, 1000, 3000 }));
		boolean proxyRequired = recursivelyFlowTransaction(nodesToFlowTo, timeLeftBeforeTransactionTimeout, toMigrate);
		originalServer.getTransactionManager().resume(suspendedTransaction);
		if (proxyRequired) {
			XAResource proxyXAResource = originalServer.generateProxyXAResource(originalServer.getNodeName(), 2000);
			originalTransaction.enlistResource(proxyXAResource);
			originalTransaction.registerSynchronization(originalServer.generateProxySynchronization(originalServer.getNodeName(), 2000, toMigrate));
		}

		Transaction transaction = transactionManager.getTransaction();
		transaction.commit();
	}

	private boolean recursivelyFlowTransaction(List<Integer> nodesToFlowTo, long timeLeftBeforeTransactionTimeout, Xid toMigrate) throws RollbackException,
			InvalidTransactionException, IllegalStateException, XAException, SystemException, NotSupportedException {

		Integer currentServerName = nodesToFlowTo.remove(0);
		LocalServer currentServer = getLocalServer(currentServerName);

		// Migrate the transaction to the next server
		int remainingTimeout = (int) (timeLeftBeforeTransactionTimeout / 1000);

		boolean requiresProxyAtPreviousServer = !currentServer.importTransaction(remainingTimeout, toMigrate);
		// Perform work on the migrated transaction
		TransactionManager transactionManager = currentServer.getTransactionManager();
		Transaction transaction = transactionManager.getTransaction();
		transaction.registerSynchronization(new TestSynchronization(currentServer.getNodeName()));
		transaction.enlistResource(new TestResource(currentServer.getNodeName(), false));

		if (!nodesToFlowTo.isEmpty()) {
			Integer nextServerNodeName = nodesToFlowTo.get(0);

			// SUSPEND THE TRANSACTION
			Transaction suspendedTransaction = currentServer.getTransactionManager().suspend();
			// FLOW THE TRANSACTION
			timeLeftBeforeTransactionTimeout = currentServer.getTimeLeftBeforeTransactionTimeout();
			boolean proxyRequired = recursivelyFlowTransaction(nodesToFlowTo, timeLeftBeforeTransactionTimeout, toMigrate);
			// Create a proxy for the new server if necessary, this can orphan
			// the
			// remote server but XA recovery will handle that on the remote
			// server
			// The alternative is to always create a proxy but this is a
			// performance
			// drain and will result in multiple subordinate transactions and
			// performance
			// issues
			// RESUME THE TRANSACTION IN CASE THERE IS MORE WORK
			currentServer.getTransactionManager().resume(suspendedTransaction);
			if (proxyRequired) {
				XAResource proxyXAResource = currentServer.generateProxyXAResource(currentServer.getNodeName(), nextServerNodeName);
				suspendedTransaction.enlistResource(proxyXAResource);
				suspendedTransaction.registerSynchronization(currentServer.generateProxySynchronization(currentServer.getNodeName(), nextServerNodeName,
						toMigrate));
			}
		}
		// SUSPEND THE TRANSACTION WHEN YOU ARE READY TO RETURN TO YOUR CALLER
		currentServer.getTransactionManager().suspend();
		return requiresProxyAtPreviousServer;
	}

	public static RemoteServer lookup(Integer jndiName) {
		int index = (jndiName / 1000) - 1;
		return remoteServers[index];
	}

	private static LocalServer getLocalServer(Integer jndiName) {
		int index = (jndiName / 1000) - 1;
		return localServers[index];
	}
}
