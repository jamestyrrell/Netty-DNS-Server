package org.handwerkszeug.dns.conf;

import java.lang.reflect.Method;
import java.util.List;

import org.handwerkszeug.chain.Chain;
import org.handwerkszeug.chain.ChainResult;
import org.handwerkszeug.chain.impl.SimpleChainResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SunJRE implements Chain<List<String>, ChainResult> {

	static final Logger LOG = LoggerFactory.getLogger(SunJRE.class);

	@Override
	public ChainResult execute(List<String> context) {
		try {
			Class<?> clazz = Class.forName("sun.net.dns.ResolverConfiguration");
			Method open = clazz.getDeclaredMethod("open");
			Method nameservers = clazz.getDeclaredMethod("nameservers");
			Object conf = open.invoke(null);
			Object maybelist = nameservers.invoke(conf);
			for (Object o : List.class.cast(maybelist)) {
				context.add(o.toString());
			}
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return SimpleChainResult.Continue;
	}
}
