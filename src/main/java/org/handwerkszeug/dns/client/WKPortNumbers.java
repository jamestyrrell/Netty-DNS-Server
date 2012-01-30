package org.handwerkszeug.dns.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.handwerkszeug.dns.record.WKSRecord;
import org.handwerkszeug.util.ClassUtil;

import werkzeugkasten.common.util.Streams;
import werkzeugkasten.common.util.StringUtil;

/**
 * <a href="http://www.iana.org/assignments/port-numbers">PORT NUMBERS</a>
 * 
 * @author taichi
 */
public class WKPortNumbers {

	public static final String UNKNOWN_PORT = "unknown";
	public static final String PATH = ClassUtil
			.toPackagePath(WKPortNumbers.class) + "/PortNumbers.txt";

	protected static final Set<String> skipWords = new HashSet<String>();
	static {
		skipWords.add("Reserved");
		skipWords.add("Unassigned");
		skipWords.add("Discard");
	}

	protected Map<Integer, String> ports = new HashMap<Integer, String>();
	protected Map<String, Integer> keywords = new HashMap<String, Integer>();

	public WKPortNumbers() {}

	public void load() {
		load(PATH);
	}

	public void load(String path) {
        try {
            InputStream fin = getClass().getResource("/" + path).openStream();
            load(fin);

        } catch (IOException e) {
            throw new IllegalStateException("Resource '" + path + "' was not found");
        }
	}

	public void load(final InputStream in) {
		new Streams.using<BufferedReader, Exception>() {

			@Override
			public BufferedReader open() throws Exception {
				return new BufferedReader(new InputStreamReader(in));
			}

			@Override
			public void handle(BufferedReader stream) throws Exception {
				WKPortNumbers.this.parse(stream);

			}

			@Override
			public void happen(Exception exception) {
				throw new IllegalStateException(exception);
			}
		};
	}

	protected void parse(BufferedReader br) throws IOException {
		while (br.ready()) {
			parse(br.readLine());
		}
	}

	protected void parse(String line) {
		if (line.startsWith("#")) {
			return;
		}
		String[] ary = line.split("\\p{Space}+");
		if ((ary.length < 3) || skipWords.contains(ary[2])) {
			return;
		}
		int index = ary[1].indexOf('/');
		String port = ary[1].substring(0, index);
		add(Integer.valueOf(port), ary[0]);
	}

	public void add(Integer port, String keyword) {
		this.ports.put(port, keyword);
		this.keywords.put(keyword, port);
	}

	public String find(Integer port) {
		if (port == null) {
			return UNKNOWN_PORT;
		}
		String keyword = this.ports.get(port);
		if (StringUtil.isEmpty(keyword)) {
			return UNKNOWN_PORT;
		}
		return keyword;
	}

	public Integer find(String keyword) {
		if (StringUtil.isEmpty(keyword)) {
			return null;
		}
		return this.keywords.get(keyword.toLowerCase());
	}

	static final Pattern isDigit = Pattern.compile("\\d+");

	public void setServices(WKSRecord record, String[] services) {
		List<Integer> list = new ArrayList<Integer>();
		for (String s : services) {
			if (isDigit.matcher(s).matches()) {
				list.add(Integer.valueOf(s));
			} else {
				Integer i = find(s);
				if (i != null) {
					list.add(i);
				}
			}
		}
		int[] ary = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ary[i] = list.get(i);
		}
		WKPortNumbers.setServices(record, ary);
	}

	/**
	 * 3.4.2. WKS RDATA format
	 * 
	 * @param record
	 * @param services
	 */
	public static void setServices(WKSRecord record, int[] services) {
		Arrays.sort(services);
		int last = services[services.length - 1];
		byte[] bitmap = new byte[last / 8 + 1];
		for (int i : services) {
			bitmap[i / 8] |= (1 << (7 - i % 8));
		}
		record.bitmap(bitmap);
	}

	protected List<Integer> getServices(WKSRecord record) {
		byte[] bitmap = record.bitmap();
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0, length = bitmap.length; i < length; i++) {
			int octets = bitmap[i] & 0xFF;
			for (int j = 0; j < 8; j++) {
				if ((octets & (1 << (7 - j))) != 0) {
					result.add(Integer.valueOf(i * 8 + j));
				}
			}
		}
		return result;
	}

	public void appendServices(WKSRecord record, StringBuilder stb) {
		for (Integer i : getServices(record)) {
			stb.append(find(i));
			stb.append(' ');
		}
	}
}
