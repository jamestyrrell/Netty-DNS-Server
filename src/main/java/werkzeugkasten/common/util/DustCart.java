package werkzeugkasten.common.util;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taichi
 */
public class DustCart {

	static final Logger LOG = Logger.getLogger(DustCart.class.getName());

	static final String FQN_L4J = "org.apache.log4j.LogManager";
	static final String FQN_JCL = "org.apache.commons.logging.LogFactory";
	static final String FQN_T2 = "org.t2framework.commons.Disposer";
	static final String FQN_S2 = "org.seasar.framework.util.DisposableUtil";

	final List<DustBox> dustBoxes = new ArrayList<DustBox>();

	public DustCart() {
		dustBoxes.add(new DustBox(FQN_L4J, "shutdown"));
		dustBoxes.add(new DustBox(FQN_JCL, "releaseAll"));
		dustBoxes.add(new DustBox(FQN_T2, "dispose"));
		dustBoxes.add(new DustBox(FQN_S2, "dispose"));
	}

	public void pickUp(ClassLoader classLoader) {
		Introspector.flushCaches();
		deregisterDrivers();
		if (classLoader != null) {
			for (DustBox db : dustBoxes) {
				db.cleanUp(classLoader);
			}
		}
	}

	protected void deregisterDrivers() {
		try {
			for (Enumeration<Driver> e = DriverManager.getDrivers(); e
					.hasMoreElements();) {
				Driver d = e.nextElement();
				DriverManager.deregisterDriver(d);
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	class DustBox {
		String fqn;
		String path;
		String method;

		public DustBox(String fqn, String method) {
			this.fqn = fqn;
			this.path = fqn.replace('.', '/') + ".class";
			this.method = method;
		}

		public void cleanUp(ClassLoader classLoader) {
			try {
				URL url = classLoader.getResource(this.path);
				if (url != null) {
					Class<?> clazz = classLoader.loadClass(this.fqn);
					Method method = clazz.getMethod(this.method);
					method.invoke(null);
				}
			} catch (SecurityException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
