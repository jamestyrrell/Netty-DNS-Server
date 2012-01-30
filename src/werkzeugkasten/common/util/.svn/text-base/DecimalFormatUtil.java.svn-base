package werkzeugkasten.common.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * {@link DecimalFormatSymbols} utility.
 * 
 * @author shot
 * 
 */
public class DecimalFormatUtil {

	/**
	 * Special character for initialization.
	 */
	protected static Character[] SPECIAL_CURRENCY_SYMBOLS = new Character[] {
			'\\', '$' };

	protected static boolean initialized = false;

	static {
		if (!initialized) {
			synchronized (DecimalFormatUtil.class) {
				init();
			}
		}
		initialized = true;
	}

	private static void init() {
		for (Locale locale : DecimalFormatSymbols.getAvailableLocales()) {
			final DecimalFormatSymbols localeSymbols = DecimalFormatSymbols
					.getInstance(locale);
			final String symbol = localeSymbols.getCurrencySymbol();
			Character c = Character.valueOf(symbol.toCharArray()[0]);
			SPECIAL_CURRENCY_SYMBOLS = ArrayUtil.add(SPECIAL_CURRENCY_SYMBOLS,
					c);
		}
	}

	/**
	 * Normalize as decimal format.
	 * 
	 * @param s
	 * @return
	 */
	public static String normalize(String s) {
		return normalize(s, Locale.getDefault());
	}

	/**
	 * Normalize as decimal format with locale.
	 * 
	 * @param s
	 * @param locale
	 * @return
	 */
	public static String normalize(String s, Locale locale) {
		if (StringUtil.isEmpty(s)) {
			return null;
		}
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		char groupingSeparator = symbols.getGroupingSeparator();
		char decimalSeparator = symbols.getDecimalSeparator();
		final StringBuilder builder = new StringBuilder(20);
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == groupingSeparator) {
				continue;
			} else if (c == decimalSeparator) {
				c = '.';
			} else if (ArrayUtil.contains(SPECIAL_CURRENCY_SYMBOLS, Character
					.valueOf(c))) {
				continue;
			}
			builder.append(c);
		}
		return builder.toString();
	}

}
