package werkzeugkasten.common.exception;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7720465648565468997L;

	public IORuntimeException(IOException e) {
		super(e);
	}
}
