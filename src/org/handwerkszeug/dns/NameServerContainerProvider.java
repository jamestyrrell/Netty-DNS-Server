package org.handwerkszeug.dns;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import werkzeugkasten.common.util.Disposable;
import werkzeugkasten.common.util.Initializable;

public class NameServerContainerProvider implements Initializable, Disposable {

	public static final String DEFAULT_NAME = "default";

	protected Map<String, NameServerContainer> containers = new HashMap<String, NameServerContainer>();

	@Override
	public void initialize() {
		initialize(Thread.currentThread().getContextClassLoader());
	}

	public void initialize(ClassLoader classLoader) {
		ServiceLoader<NameServerContainer> loader = ServiceLoader.load(
				NameServerContainer.class, classLoader);
		for (NameServerContainer nc : loader) {
			this.containers.put(nc.name(), nc);
		}
	}

	@Override
	public void dispose() {
		this.containers.clear();
	}

	public NameServerContainer getContainer() {
		String name = System
				.getProperty(Constants.SYSTEM_PROPERTY_ACTIVE_NAMESERVER_CONTAINER);
		return getContainer(name);
	}

	public NameServerContainer getContainer(String name) {
		NameServerContainer result = this.containers.get(name);
		if (result == null) {
			result = this.containers.get(DEFAULT_NAME);
		}
		return result;
	}
}
