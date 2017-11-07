package com.tianshouzhi.dragon.transaction;

import javax.transaction.xa.Xid;

/**
 * Created by tianshouzhi on 2017/9/30.
 */
public class MyXid implements Xid {
	protected int formatId;

	protected byte gtrid[];

	protected byte bqual[];

	public MyXid(int formatId, byte[] gtrid, byte[] bqual) {
		this.formatId = formatId;
		this.gtrid = gtrid;
		this.bqual = bqual;
	}

	public int getFormatId() {
		return formatId;
	}

	public byte[] getGlobalTransactionId() {
		return gtrid;
	}

	public byte[] getBranchQualifier() {
		return bqual;
	}
}
