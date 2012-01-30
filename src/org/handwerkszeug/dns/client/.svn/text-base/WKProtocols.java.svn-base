package org.handwerkszeug.dns.client;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.handwerkszeug.util.ClassUtil;

import werkzeugkasten.common.util.Streams;
import werkzeugkasten.common.util.StringUtil;
import werkzeugkasten.common.util.XMLEventParser;
import werkzeugkasten.common.util.XMLEventParser.DefaultHandler;

/**
 * 
 * <a href="http://www.iana.org/assignments/protocol-numbers/">Protocol
 * Numbers</a>
 * 
 * @author taichi
 * 
 */
public class WKProtocols {

	public static final String UNKNOWN_PROTOCOL = "UNKNOWN";
	public static final String PATH = ClassUtil
			.toPackagePath(WKProtocols.class) + "/ProtocolNumbers.xml";

	protected Map<Short, String> protocols = new HashMap<Short, String>();

	public WKProtocols() {
	}

	public void load() {
		load(PATH);
	}

	public void load(String path) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		load(cl.getResourceAsStream(path));
	}

	public void load(final InputStream in) {
		new Streams.using<BufferedInputStream, Exception>() {

			@Override
			public BufferedInputStream open() throws Exception {
				return new BufferedInputStream(in);
			}

			@Override
			public void handle(BufferedInputStream stream) throws Exception {
				XMLEventParser parser = new XMLEventParser(stream);
				parser.add(new DefaultHandler("registry"));
				parser.add(new RecordHandler());
				parser.parse();
			}

			@Override
			public void happen(Exception exception) {
				throw new IllegalStateException(exception);
			}
		};
	}

	class Record {
		String value;
		String name;
	}

	public class RecordHandler extends DefaultHandler {
		Pattern isDigit = Pattern.compile("\\p{Digit}+");

		public RecordHandler() {
			super("record");
		}

		@Override
		public void handle(XMLStreamReader reader) throws XMLStreamException {
			XMLEventParser parser = new XMLEventParser(reader);
			Record r = new Record();
			parser.add(new ValueHandler(r));
			parser.add(new NameHandler(r));
			parser.parse(getTagName());
			if (this.isDigit.matcher(r.value).matches()) {
				WKProtocols.this.add(Short.valueOf(r.value), r.name);
			}
		}
	}

	public class ValueHandler extends DefaultHandler {
		Record r;

		public ValueHandler(Record r) {
			super("value");
			this.r = r;
		}

		@Override
		public void handle(XMLStreamReader reader) throws XMLStreamException {
			this.r.value = reader.getElementText();
		}
	}

	public class NameHandler extends DefaultHandler {
		Record r;

		public NameHandler(Record r) {
			super("name");
			this.r = r;
		}

		@Override
		public void handle(XMLStreamReader reader) throws XMLStreamException {
			this.r.name = reader.getElementText();
		}
	}

	protected void add(Short value, String name) {
		this.protocols.put(value, name);
	}

	public String find(short ubyte) {
		String s = this.protocols.get(ubyte);
		if (StringUtil.isEmpty(s)) {
			return UNKNOWN_PROTOCOL;
		}
		return s;
	}
}
