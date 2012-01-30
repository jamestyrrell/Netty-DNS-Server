package org.handwerkszeug.chain.impl;

import java.util.ArrayList;
import java.util.List;

import org.handwerkszeug.chain.Chain;
import org.handwerkszeug.chain.ChainResult;

public class DefaultChainExecutor<CTX, R extends ChainResult> implements
		Chain<CTX, R> {

	protected List<Chain<CTX, R>> chains = new ArrayList<Chain<CTX, R>>();

	@Override
	public R execute(CTX context) {
		R r = null;
		for (Chain<CTX, R> c : this.chains) {
			r = c.execute(context);
			if (r.hasNext() == false) {
				break;
			}
		}
		return r;
	}

	public void add(Chain<CTX, R> c) {
		this.chains.add(c);
	}
}
