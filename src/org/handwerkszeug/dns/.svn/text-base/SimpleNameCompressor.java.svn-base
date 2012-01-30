package org.handwerkszeug.dns;

import java.util.HashMap;
import java.util.Map;

public class SimpleNameCompressor implements NameCompressor {

	protected Map<Name, Integer> map = new HashMap<Name, Integer>();

	public void put(Name name, int offset) {
		this.map.put(name, offset);
	}

	public int get(Name name) {
		Integer i = this.map.get(name);
		if (i == null) {
			return -1;
		}
		return i.intValue();
	}
}
