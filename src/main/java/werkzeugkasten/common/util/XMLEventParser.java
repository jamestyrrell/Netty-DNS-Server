package werkzeugkasten.common.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import werkzeugkasten.common.exception.XMLStreamRuntimeException;

public class XMLEventParser {

	protected Map<String, Handler> handlers = new HashMap<String, Handler>();
	protected XMLStreamReader reader;

	public XMLEventParser() {
	}

	public XMLEventParser(InputStream in) {
		this();
		this.reader = createStreamParser(in);
	}

	public XMLEventParser(XMLStreamReader reader) {
		this.reader = reader;
	}

	protected XMLStreamReader createStreamParser(InputStream in) {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
					Boolean.FALSE);
			BufferedInputStream stream = new BufferedInputStream(in);
			XMLStreamReader reader = factory.createXMLStreamReader(stream);
			return reader;
		} catch (XMLStreamException e) {
			throw new XMLStreamRuntimeException(e);
		}
	}

	public void add(Handler handler) {
		if (handler != null) {
			this.handlers.put(handler.getTagName(), handler);
		}
	}

	public void parse() {
		parse(null);
	}

	public void parse(String end) {
		try {
			if (reader == null) {
				return;
			}
			for (; reader.hasNext();) {
				int event = reader.next();
				if (XMLStreamConstants.START_ELEMENT == event) {
					String localname = reader.getLocalName();
					Handler handler = handlers.get(localname);
					if (handler == null) {
						skipTo(reader, localname);
					} else {
						handler.handle(reader);
					}
				} else if (XMLStreamConstants.END_ELEMENT == event
						&& reader.getLocalName().equals(end)) {
					return;
				}
			}
		} catch (XMLStreamException e) {
			throw new XMLStreamRuntimeException(e);
		}
	}

	protected void skipTo(XMLStreamReader reader, String end)
			throws XMLStreamException {
		for (; reader.hasNext();) {
			if (XMLStreamConstants.END_ELEMENT == reader.next()) {
				if (end.equals(reader.getLocalName())) {
					break;
				}
			}
		}
	}

	public interface Handler {
		String getTagName();

		void handle(XMLStreamReader reader) throws XMLStreamException;
	}

	public static class DefaultHandler implements Handler {
		protected String tag;

		public DefaultHandler(String tag) {
			this.tag = tag;
		}

		public String getTagName() {
			return tag;
		}

		public void handle(XMLStreamReader reader) throws XMLStreamException {
		}
	}
}
