package werkzeugkasten.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import werkzeugkasten.common.exception.IORuntimeException;

public class UrlUtil {

	public static InputStream open(URL url) {
		try {
			return url.openConnection().getInputStream();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public static URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IORuntimeException(e);
		}
	}

	public static URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IORuntimeException(e);
		}
	}

	public static void setUpProxy(String proxyUrl) {
		if (StringUtil.isEmpty(proxyUrl) == false) {
			URL url = UrlUtil.toURL(proxyUrl);
			if ("http".equalsIgnoreCase(url.getProtocol())) {
				System.setProperty("http.proxyHost", url.getHost());
				int port = url.getPort();
				if (0 < port) {
					System.setProperty("http.proxyPort", String.valueOf(port));
				}
			} else if ("socks".equalsIgnoreCase(url.getProtocol())) {
				System.setProperty("socksProxyHost", url.getHost());
				int port = url.getPort();
				if (0 < port) {
					System.setProperty("socksProxyPort", String.valueOf(port));
				}
			}
		}
	}

	public static void setDefaultUseCaches() {
		try {
			new URL("http://example.com").openConnection().setDefaultUseCaches(
					false);
		} catch (Exception e) {
		}
	}
}
