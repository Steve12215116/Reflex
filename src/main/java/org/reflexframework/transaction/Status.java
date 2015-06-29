package org.reflexframework.transaction;

public enum Status {
	/**
	 * No transaction is currently associated with the target object.
	 */
	STATUS_NO_TRANSACTION ,
	STATUS_ROLLBACK,
	STATUS_COMMITT,
	/**
	 * A transaction is associated with the target object and it is in the active state.
	 */
	STATUS_ACTIVE ,
}
