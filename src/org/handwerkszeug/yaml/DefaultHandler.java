package org.handwerkszeug.yaml;

public abstract class DefaultHandler<CTX> implements YamlNodeHandler<CTX> {
	protected String name;

	public DefaultHandler() {
	}

	public DefaultHandler(String name) {
		this.name = name;
	}

	@Override
	public String getNodeName() {
		return this.name;
	}
}