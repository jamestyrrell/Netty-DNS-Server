package org.handwerkszeug.dns;

import org.handwerkszeug.util.EnumUtil;
import org.handwerkszeug.util.VariableEnum;

/**
 * RFC1035 3.2.4. CLASS values
 * 
 * @author taichi
 */
public enum DNSClass implements VariableEnum {

	/**
	 * the Internet
	 */
	IN(1),

	/**
	 * the CSNET class (Obsolete - used only for examples in some obsolete RFCs)
	 */
	CS(2),

	/**
	 * the CHAOS class
	 */
	CH(3),

	/**
	 * Hesiod [Dyer 87]
	 */
	HS(4),

	/**
	 * any class
	 */
	ANY(255);

	private int value;

	@Override
	public int value() {
		return this.value;
	}

	private DNSClass(int i) {
		this.value = i;
	}

	public static DNSClass valueOf(int value) {
		return EnumUtil.find(DNSClass.values(), value);
	}

	public static DNSClass find(String value) {
		return EnumUtil.find(DNSClass.values(), value, null);
	}
}
