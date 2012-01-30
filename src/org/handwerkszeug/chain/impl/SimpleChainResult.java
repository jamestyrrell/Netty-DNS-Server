package org.handwerkszeug.chain.impl;

import org.handwerkszeug.chain.ChainResult;

public class SimpleChainResult implements ChainResult {

	public static final ChainResult Continue = new SimpleChainResult(true);

	public static final ChainResult Terminate = new SimpleChainResult();

	protected boolean hasNext;

	public SimpleChainResult() {
		this(false);
	}

	public SimpleChainResult(boolean hasNext) {
		this.hasNext = hasNext;
	}

	@Override
	public boolean hasNext() {
		return this.hasNext;
	}

}
