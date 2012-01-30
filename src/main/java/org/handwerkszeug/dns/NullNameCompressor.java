package org.handwerkszeug.dns;

public class NullNameCompressor implements NameCompressor {

	public static final NullNameCompressor INSTANCE = new NullNameCompressor();

	private NullNameCompressor() {

	}

	@Override
	public void put(Name name, int offset) {
	}

	@Override
	public int get(Name name) {
		return -1;
	}

}
