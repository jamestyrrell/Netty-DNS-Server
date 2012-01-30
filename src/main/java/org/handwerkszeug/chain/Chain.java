package org.handwerkszeug.chain;

public interface Chain<CTX, R extends ChainResult> {

	R execute(CTX context);
}
