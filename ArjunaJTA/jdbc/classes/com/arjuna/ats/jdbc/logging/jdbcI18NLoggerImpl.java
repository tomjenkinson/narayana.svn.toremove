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
package com.arjuna.ats.jdbc.logging;

import com.arjuna.common.util.logging.Logi18n;

/**
 * i18n log messages for the jdbc module.
 * This class is autogenerated. Don't mess with it.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
public class jdbcI18NLoggerImpl implements jdbcI18NLogger {

	private final Logi18n logi18n;

	jdbcI18NLoggerImpl(Logi18n logi18n) {
		this.logi18n = logi18n;
	}

	public void log_aborterror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.aborterror");
		}
	}

	public void log_alreadyassociated() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.alreadyassociated");
		}
	}

	public void log_alreadyassociatedcheck() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.alreadyassociatedcheck");
		}
	}

	public void log_autocommit() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.autocommit");
		}
	}

	public void log_closeerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.closeerror");
		}
	}

	public void log_closeerrorinvalidtx(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.closeerrorinvalidtx", new Object[] {arg0});
		}
	}

	public void log_closingconnection(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.closingconnection", new Object[] {arg0});
		}
	}

	public void log_closingconnectionnull(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.closingconnectionnull", new Object[] {arg0});
		}
	}

	public void log_commiterror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.commiterror");
		}
	}

	public void log_conniniterror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.conniniterror");
		}
	}

	public void log_delisterror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.delisterror");
		}
	}

	public void log_drcclose() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.drcclose");
		}
	}

	public void log_drcdest() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.drcdest");
		}
	}

	public void log_drivers_exception() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.drivers.exception");
		}
	}

	public void log_drivers_invaliddb() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.drivers.invaliddb");
		}
	}

	public void log_dynamicerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.dynamicerror");
		}
	}

	public void log_enlistfailed() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.enlistfailed");
		}
	}

	public void log_getmoderror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.getmoderror");
		}
	}

	public void log_idrcclose() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.idrcclose");
		}
	}

	public void log_inactivetransaction() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.inactivetransaction");
		}
	}

	public void log_infoerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.infoerror");
		}
	}

	public void log_ircdest() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.ircdest");
		}
	}

	public void log_isolationlevelfailget(String arg0, String arg1) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.isolationlevelfailget", new Object[] {arg0, arg1});
		}
	}

	public void log_isolationlevelfailset(String arg0, String arg1) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.isolationlevelfailset", new Object[] {arg0, arg1});
		}
	}

	public void log_jndierror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.jndierror");
		}
	}

	public void log_nojdbcimple(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.nojdbcimple", new Object[] {arg0});
		}
	}

	public void log_recovery_basic_initexp() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.recovery.basic.initexp");
		}
	}

	public void log_recovery_basic_xarec(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.recovery.basic.xarec", new Object[] {arg0});
		}
	}

	public void log_recovery_xa_initexp() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.recovery.xa.initexp");
		}
	}

	public void log_recovery_xa_xarec(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.recovery.xa.xarec", new Object[] {arg0});
		}
	}

	public void log_releasesavepointerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.releasesavepointerror");
		}
	}

	public void log_rollbackerror(String arg0) {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.rollbackerror", new Object[] {arg0});
		}
	}

	public void log_rollbacksavepointerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.rollbacksavepointerror");
		}
	}

	public void log_setreadonly() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.setreadonly");
		}
	}

	public void log_setsavepointerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.setsavepointerror");
		}
	}

	public void log_stateerror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.stateerror");
		}
	}

	public void log_xa_recjndierror() {
		if(logi18n.isWarnEnabled()) {
			logi18n.warn("com.arjuna.ats.internal.jdbc.xa.recjndierror");
		}
	}

}
