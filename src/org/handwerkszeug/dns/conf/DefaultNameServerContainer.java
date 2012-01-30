package org.handwerkszeug.dns.conf;

import java.util.ArrayList;
import java.util.List;

import org.handwerkszeug.chain.Chain;
import org.handwerkszeug.chain.ChainResult;
import org.handwerkszeug.chain.impl.DefaultChainExecutor;
import org.handwerkszeug.dns.NameServerContainer;
import org.handwerkszeug.dns.NameServerContainerProvider;

public class DefaultNameServerContainer implements NameServerContainer {

	protected Chain<List<String>, ChainResult> executor;

	@Override
	public String name() {
		return NameServerContainerProvider.DEFAULT_NAME;
	}

	@Override
	public List<String> nameservers() {
		List<String> result = new ArrayList<String>();
		this.executor.execute(result);
		return result;
	}

	@Override
	public void initialize() {
		// FIXME this code run only sun JRE.
		// find from IBM JDK. JRockit.
		DefaultChainExecutor<List<String>, ChainResult> dce = new DefaultChainExecutor<List<String>, ChainResult>();
		dce.add(new SystemProperties());
		dce.add(new SunJRE());
		dce.add(new ResolvConf());
		this.executor = dce;
	}

	@Override
	public void dispose() {
		// do nothing.
	}

}
