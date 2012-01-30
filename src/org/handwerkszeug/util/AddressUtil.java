package org.handwerkszeug.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see <a href="http://tools.ietf.org/html/rfc4291">[RFC4291] IP Version 6
 *      Addressing Architecture</a>
 * @see <a href="http://tools.ietf.org/html/rfc5952">[RFC5952] A Recommendation
 *      for IPv6 Address Text Representation</a>
 * @see http://www.intermapper.com/ipv6validator
 * @see http://download.dartware.com/thirdparty/test-ipv6-regex.pl
 * @author taichi
 */
public class AddressUtil {

	static final Logger LOG = LoggerFactory.getLogger(AddressUtil.class);

	/**
	 * @see <a href="http://tools.ietf.org/html/rfc952">[RFC952] DOD INTERNET
	 *      HOST TABLE SPECIFICATION</a>
	 */
	public static final Pattern hostname = Pattern
			.compile("([a-zA-Z][\\w-]*[\\w]*(\\.[a-zA-Z][\\w-]*[\\w]*)*)");

	public static final Pattern hostWithPort = withPortNumber(hostname);

	public static final String under65536 = "(6553[0-5]|6(55[012]|(5[0-4]|[0-4]\\d)\\d)\\d|[1-5]?\\d{1,4})";
	public static final Pattern v4Address = Pattern
			.compile("((25[0-5]|(2[0-4]|1\\d|[1-9]?)\\d)(\\.|\\b)){4}(?<!\\.)");
	public static final Pattern v4withPort = withPortNumber(v4Address);

	protected static Pattern withPortNumber(Pattern p) {
		return Pattern.compile("(" + p.pattern() + ")(:" + under65536 + ")?");
	}

	protected static final String internal_v6address = "((((?=(?>.*?::)(?!.*::)))(::)?([0-9a-f]{1,4}::?){0,5}|([0-9a-f]{1,4}:){6})(((25[0-5]|(2[0-4]|1[0-9]|[1-9])?[0-9])(\\.|\\b)){4}|\\3([0-9a-f]{1,4}(::?|\\b)){0,2}|[0-9a-f]{1,4}:[0-9a-f]{1,4})(?<![^:]:)(?<!\\.))";
	public static final Pattern v6Address = Pattern.compile(internal_v6address
			+ "$");
	public static final Pattern v6withSuffixPort = Pattern
			.compile(internal_v6address + "(?:(#|\\.)" + under65536 + ")?$");

	public static final Pattern v6withBracketPort = Pattern.compile("\\["
			+ internal_v6address + "\\](:" + under65536 + ")?$"); // 1 15

	protected static final List<SocketAddressConverter> CONVERTERS = new ArrayList<AddressUtil.SocketAddressConverter>();
	static {
		CONVERTERS.add(new FromV4Address());
		CONVERTERS.add(new FromBracketV6Address());
		CONVERTERS.add(new FromV6Address());
		CONVERTERS.add(new FromHostname());
	}

	public static InetSocketAddress convertTo(String addressWithPort,
			int defaultPort) {
		InetSocketAddress result = null;
		for (SocketAddressConverter sac : CONVERTERS) {
			result = sac.to(addressWithPort, defaultPort);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	public interface SocketAddressConverter {
		InetSocketAddress to(String addr, int defaultPort);
	}

	public static class FromHostname implements SocketAddressConverter {
		@Override
		public InetSocketAddress to(String addr, int defaultPort) {
			return toSocketAddress(addr, defaultPort, hostWithPort, 1, 5);
		}
	}

	public static class FromV4Address implements SocketAddressConverter {
		@Override
		public InetSocketAddress to(String addr, int defaultPort) {
			return toSocketAddress(addr, defaultPort, v4withPort, 1, 7);
		}
	}

	public static class FromBracketV6Address implements SocketAddressConverter {
		@Override
		public InetSocketAddress to(String addr, int defaultPort) {
			return toSocketAddress(addr, defaultPort, v6withBracketPort, 1, 15);
		}
	}

	public static class FromV6Address implements SocketAddressConverter {
		@Override
		public InetSocketAddress to(String addr, int defaultPort) {
			return toSocketAddress(addr, defaultPort, v6withSuffixPort, 1, 15);
		}
	}

	protected static InetSocketAddress toSocketAddress(String addressWithPort,
			int defaultPort, Pattern p, int HOST_INDEX, int PORT_INDEX) {
		Matcher m = p.matcher(addressWithPort);
		if (m.matches() && m.reset().find()) {
			int port = toInt(m.group(PORT_INDEX), defaultPort);
			return new InetSocketAddress(m.group(HOST_INDEX), port);
		}
		return null;
	}

	public static int toInt(String s, int defaultValue) {
		int result = defaultValue;
		try {
			if ((s != null) && (s.isEmpty() == false)) {
				result = Integer.parseInt(s);
			}
		} catch (NumberFormatException e) {
		}
		return result;
	}

	public static InetAddress getByAddress(long v4address) {
		byte[] a = new byte[4];
		for (int i = 0; i < 4; i++) {
			a[i] = (byte) ((v4address >>> ((3 - i) * 8)) & 0xFF);
		}
		try {
			return InetAddress.getByAddress(a);
		} catch (UnknownHostException e) {
			LOG.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	public static long toLong(InetAddress v4address) {
		byte[] a = v4address.getAddress();
		long result = 0;
		for (int i = 0; i < 4; i++) {
			result |= (long) ((a[i] & 0xFF)) << ((3 - i) * 8);
		}
		return result;
	}

	public static InetAddress getByName(String host) {
		try {
			return InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			LOG.error(host, e);
			throw new IllegalArgumentException(e);
		}
	}
}
