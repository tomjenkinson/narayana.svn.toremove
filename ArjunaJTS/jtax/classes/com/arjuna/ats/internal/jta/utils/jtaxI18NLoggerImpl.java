/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
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
 * (C) 2010,
 * @author JBoss, by Red Hat.
 */
package com.arjuna.ats.internal.jta.utils;

import org.jboss.logging.Logger;

import static org.jboss.logging.Logger.Level.*;

/**
 * i18n log messages for the jtax module.
 * This class is autogenerated. Don't mess with it.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
public class jtaxI18NLoggerImpl implements jtaxI18NLogger {

    private final Logger logger;

    jtaxI18NLoggerImpl(Logger logger) {
        this.logger = logger;
    }

    public void info_jtax_recovery_jts_orbspecific_commit(String arg0) {
        logger.logv(INFO, "ARJUNA-24001 XA recovery committing {0}", arg0);
    }

    public void info_jtax_recovery_jts_orbspecific_rollback(String arg0) {
        logger.logv(INFO, "ARJUNA-24002 XA recovery rolling back {0}", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_coperror(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24004 Caught the following error while trying to single phase complete resource", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_createstate() {
        logger.logv(WARN, "ARJUNA-24005 Committing of resource state failed.", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_generror(String arg0, String arg1, String arg2, Throwable arg3) {
        logger.logv(WARN, arg3, "ARJUNA-24006 {0} caused an error from resource {1} in transaction {2}", arg0, arg1, arg2);
    }

    public void warn_jtax_resources_jts_orbspecific_lastResource_disableWarning() {
        logger.logv(WARN, "ARJUNA-24007 You have chosen to disable the Multiple Last Resources warning. You will see it only once.", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_lastResource_disallow(String arg0) {
        logger.logv(WARN, "ARJUNA-24008 Adding multiple last resources is disallowed. Current resource is {0}", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_lastResource_multipleWarning(String arg0) {
        logger.logv(WARN, "ARJUNA-24009 Multiple last resources have been added to the current transaction. This is transactionally unsafe and should not be relied upon. Current resource is {0}", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_lastResource_startupWarning() {
        logger.logv(WARN, "ARJUNA-24010 You have chosen to enable multiple last resources in the transaction manager. This is transactionally unsafe and should not be relied upon.", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_loadstateread(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24011 Reading state caught exception", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_norecoveryxa(String arg0) {
        logger.logv(WARN, "ARJUNA-24012 Could not find new XAResource to use for recovering non-serializable XAResource {0}", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_notprepared(String arg0) {
        logger.logv(WARN, "ARJUNA-24013 {0} caught NotPrepared exception during recovery phase!", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_nulltransaction(String arg0) {
        logger.logv(WARN, "ARJUNA-24014 {0} - null or invalid transaction!", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_preparefailed(String arg0, String arg1, String arg2, Throwable arg3) {
        logger.logv(WARN, arg3, "ARJUNA-24015 XAResource prepare failed on resource {0} for transaction {1} with: {2}", arg0, arg1, arg2);
    }

    public void warn_jtax_resources_jts_orbspecific_recfailed(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-24016 Recovery of resource failed when trying to call {0} got exception", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_remconn(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24017 Attempted shutdown of resource failed with exception", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_restoreerror1(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24018 Exception on attempting to resource XAResource", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_restoreerror2(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24019 Unexpected exception on attempting to resource XAResource", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_saveState() {
        logger.logv(WARN, "ARJUNA-24020 Could not serialize a serializable XAResource!", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_unexpected(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-24021 {0} caught unexpected exception during recovery phase!", arg0);
    }

    public void warn_jtax_resources_jts_orbspecific_updatestate() {
        logger.logv(WARN, "ARJUNA-24022 Updating of resource state failed.", (Object)null);
    }

    public void warn_jtax_resources_jts_orbspecific_xaerror(String arg0, String arg1, String arg2, String arg3, Throwable arg4) {
        logger.logv(WARN, arg4, "ARJUNA-24023 {0} caused an XA error: {1} from resource {2} in transaction {3}", arg0, arg1, arg2, arg3);
    }

    public String get_jtax_transaction_jts_alreadyassociated() {
        return "ARJUNA-24024 thread is already associated with a transaction and subtransaction support is not enabled!";
    }

    public void warn_jtax_transaction_jts_delistfailed(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24025 Delist of resource failed with exception", (Object)null);
    }

    public void warn_jtax_transaction_jts_endsuspendfailed1() {
        logger.logv(WARN, "ARJUNA-24026 Ending suspended RMs failed when rolling back the transaction!", (Object)null);
    }

    public String get_jtax_transaction_jts_endsuspendfailed2() {
        return "ARJUNA-24027 Ending suspended RMs failed when rolling back the transaction, but transaction rolled back.";
    }

    public String get_jtax_transaction_jts_illegalstate() {
        return "ARJUNA-24028 illegal resource state:";
    }

    public String get_jtax_transaction_jts_inactivetx() {
        return "ARJUNA-24029 Transaction is not active.";
    }

    public String get_jtax_transaction_jts_invalidtx2() {
        return "ARJUNA-24031 Invalid transaction.";
    }

    public String get_jtax_transaction_jts_jca_busy() {
        return "ARJUNA-24032 Work already active!";
    }

    public void warn_jtax_transaction_jts_lastResourceOptimisationInterface(String arg0) {
        logger.logv(WARN, "ARJUNA-24033 failed to load Last Resource Optimisation Interface {0}", arg0);
    }

    public String get_jtax_transaction_jts_markedrollback() {
        return "ARJUNA-24034 Could not enlist resource because the transaction is marked for rollback.";
    }

    public String get_jtax_transaction_jts_nosuchtx() {
        return "ARJUNA-24035 No such transaction!";
    }

    public void warn_jtax_transaction_jts_nottximple() {
        logger.logv(WARN, "ARJUNA-24036 Current transaction is not a TransactionImple", (Object)null);
    }

    public String get_jtax_transaction_jts_notx() {
        return "ARJUNA-24037 no transaction!";
    }

    public String get_jtax_transaction_jts_notxe() {
        return "ARJUNA-24038 no transaction! Caught:";
    }

    public String get_jtax_transaction_jts_nullparam() {
        return "ARJUNA-24040 paramater is null!";
    }

    public String get_jtax_transaction_jts_ressusp() {
        return "ARJUNA-24042 is already suspended!";
    }

    public void warn_jtax_transaction_jts_rmerror(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24043 An error occurred while checking if this is a new resource manager:", (Object)null);
    }

    public void warn_jtax_transaction_jts_rollbackerror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-24044 {0} could not mark the transaction as rollback only", arg0);
    }

    public void warn_jtax_transaction_jts_starterror(String arg0, String arg1, String arg2, Throwable arg3) {
        logger.logv(WARN, arg3, "ARJUNA-24046 {0} returned XA error {1} for transaction {2}", arg0, arg1, arg2);
    }

    public String get_jtax_transaction_jts_syncerror() {
        return "ARJUNA-24048 Synchronizations are not allowed!";
    }

    public void warn_jtax_transaction_jts_syncproblem(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24049 cleanup synchronization failed to register:", (Object)null);
    }

    public String get_jtax_transaction_jts_syncrollbackexception() {
        return "ARJUNA-24050 The transaction implementation threw a RollbackException";
    }

    public String get_jtax_transaction_jts_systemexception() {
        return "ARJUNA-24051 The transaction implementation threw a SystemException";
    }

    public void warn_jtax_transaction_jts_threaderror(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-24052 Active thread error:", (Object)null);
    }

    public void warn_jtax_transaction_jts_unknownres(String arg0) {
        logger.logv(WARN, "ARJUNA-24053 {0} attempt to delist unknown resource!", arg0);
    }

    public String get_jtax_transaction_jts_wrongstatetx() {
        return "ARJUNA-24054 The current transaction does not match this transaction!";
    }

    public void warn_jtax_transaction_jts_xaenderror() {
        logger.logv(WARN, "ARJUNA-24055 Could not call end on a suspended resource!", (Object)null);
    }

    public void warn_jtax_transaction_jts_xaerror(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-24056 {0} caught XA exception: {1}", arg0, arg1);
    }

    public void warn_jtax_transaction_jts_timeouterror(String arg0, String arg1, String arg2, Throwable arg3) {
		logger.logv(WARN, arg3, "ARJUNA-24057 {0} setTransactionTimeout on XAResource {2} threw: {1}", arg0, arg1, arg2);
	}
}
