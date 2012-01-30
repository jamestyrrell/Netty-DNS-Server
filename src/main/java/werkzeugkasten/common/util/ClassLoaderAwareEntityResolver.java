package werkzeugkasten.common.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClassLoaderAwareEntityResolver implements EntityResolver {

	protected Map<String, String> paths = new HashMap<String, String>();
	protected ClassLoader classLoader;

	public ClassLoaderAwareEntityResolver() {
		this.classLoader = getClass().getClassLoader();
	}

	public ClassLoaderAwareEntityResolver(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		String path = this.paths.get(publicId);
		if (StringUtil.isEmpty(path) == false) {
			return new InputSource(new BufferedInputStream(this.classLoader
					.getResourceAsStream(path)));
		}
		return null;
	}

	public void add(String publicId, String path) {
		this.paths.put(publicId, path);
	}

}
