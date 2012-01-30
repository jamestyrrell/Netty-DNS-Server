package org.handwerkszeug.dns.conf;

import java.util.List;

import org.handwerkszeug.chain.Chain;
import org.handwerkszeug.chain.ChainResult;
import org.handwerkszeug.chain.impl.SimpleChainResult;
import org.handwerkszeug.dns.Constants;

import werkzeugkasten.common.util.StringUtil;

public class SystemProperties implements Chain<List<String>, ChainResult> {

	@Override
	public ChainResult execute(List<String> context) {
		String servers = System.getProperty(Constants.SYSTEM_PROPERTY_NAMESERVERS);
		if (StringUtil.isEmpty(servers) == false) {
			for (String s : servers.split(",")) {
				context.add(s);
			}
		}
		return SimpleChainResult.Continue;
	}
}
