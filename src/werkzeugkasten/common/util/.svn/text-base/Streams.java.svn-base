package werkzeugkasten.common.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import werkzeugkasten.common.exception.IORuntimeException;

public class Streams {

	static final Logger LOG = Logger.getLogger(Streams.class.getName());

	public static abstract class using<STREAM extends Closeable, T extends Exception> {
		/**
		 * @param t
		 *            is trick parameter. needless to assign.
		 */
		@SuppressWarnings("unchecked")
		public using(T... t) {
			$(this, (Class<T>) t.getClass().getComponentType());
		}

		public abstract STREAM open() throws T;

		public abstract void handle(STREAM stream) throws T;

		public abstract void happen(T exception);
	}

	@SuppressWarnings("unchecked")
	static <STREAM extends Closeable, T extends Exception> void $(
			using<STREAM, T> _, Class<T> clazz) {
		STREAM in = null;
		try {
			in = _.open();
			_.handle(in);
		} catch (Exception e) {
			if (clazz.isAssignableFrom(e.getClass())) {
				_.happen((T) e);
			}
			throw new IllegalStateException(e);
		} finally {
			close(in);
		}
	}

	public static void close(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public static final int BUF_SIZE = 128 * 128;

	/**
	 * @param in
	 * @param out
	 * @see FileUtil#copy(InputStream, java.io.File)
	 */
	public static void copy(InputStream in, OutputStream out)
			throws IORuntimeException {
		byte[] buf = new byte[BUF_SIZE];
		try {
			int len = 0;
			do {
				len = in.read(buf, 0, BUF_SIZE);
				if (0 < len) {
					out.write(buf, 0, len);
				} else {
					break;
				}
			} while (true);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public static String readText(InputStream in) throws IORuntimeException {
		return readText(in, "UTF-8");
	}

	public static String readText(InputStream in, String charset)
			throws IORuntimeException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copy(in, out);
			return out.toString(charset);
		} catch (UnsupportedEncodingException e) {
			throw new IORuntimeException(e);
		}
	}
}
