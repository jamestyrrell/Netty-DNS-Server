package werkzeugkasten.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * ConverterUtil is an utility class that converts object.
 * 
 * @author shot
 * @author taichi
 */
public class ConverterUtil {

	static final Logger LOG = Logger.getLogger(ConverterUtil.class.getName());

	public static final Pattern YES_PATTERN = Pattern.compile("(yes|true|y|1)",
			Pattern.CASE_INSENSITIVE);

	private static Map<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>(
			19);

	private static void init() {
		map.put(BigDecimal.class, BIGDECIMAL_CONVERTER);
		map.put(BigInteger.class, BIGINTEGER_CONVERTER);
		map.put(Byte.class, BYTE_CONVERTER);
		map.put(byte[].class, BINARY_CONVERTER);
		map.put(Boolean.class, BOOLEAN_CONVERTER);
		map.put(Calendar.class, CALENDAR_CONVERTER);
		map.put(java.util.Date.class, DATE_CONVERTER);
		map.put(Double.class, DOUBLE_CONVERTER);
		map.put(Float.class, FLOAT_CONVERTER);
		map.put(Integer.class, INTEGER_CONVERTER);
		map.put(Long.class, LONG_CONVERTER);
		map.put(Short.class, SHORT_CONVERTER);
		map.put(java.sql.Date.class, SQLDATE_CONVERTER);
		map.put(String.class, STRING_CONVERTER);
		map.put(Time.class, TIME_CONVERTER);
		map.put(Timestamp.class, TIMESTAMP_CONVERTER);
		map.put(URL.class, URL_CONVERTER);
		map.put(InputStream.class, INPUTSTREAM_CONVERTER);
		map.put(Reader.class, READER_CONVERTER);
	}

	public static <T> T convert(Object target, Class<T> convertClass) {
		return convert(target, convertClass, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object target, Class<T> convertClass,
			String pattern) {
		Converter<T> converter = (Converter<T>) map.get(convertClass);
		if (converter == null) {
			if (convertClass.isInstance(target)) {
				return (T) target;
			}
			return null;
		}
		return converter.convert(target, pattern);
	}

	public static interface Converter<T> {

		/**
		 * convert to T from o. if conversion is failed, return null.
		 * 
		 * @param o
		 * @return
		 * */
		T convert(Object o);

		/**
		 * convert to T from o. if conversion is failed, return null.
		 * 
		 * @param o
		 * @param pattern
		 *            some of type needly conversion format.
		 * @return converted object, or null.
		 */
		T convert(Object o, String pattern);

	}

	public static final Converter<BigDecimal> BIGDECIMAL_CONVERTER = new Converter<BigDecimal>() {

		@Override
		public BigDecimal convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (BigDecimal.class.isInstance(o)) {
				return (BigDecimal) o;
			} else if (o instanceof java.util.Date) {
				return new BigDecimal(((java.util.Date) o).getTime());
			} else if (Integer.class.isInstance(o)) {
				int i = Integer.class.cast(o).intValue();
				return new BigDecimal(i);
			} else if (Double.class.isInstance(o)) {
				double d = Double.class.cast(o).doubleValue();
				return new BigDecimal(d);
			} else if (Long.class.isInstance(o)) {
				long l = Long.class.cast(o).longValue();
				return new BigDecimal(l);
			} else if (Float.class.isInstance(o)) {
				float f = Float.class.cast(o).floatValue();
				return new BigDecimal(f);
			} else if (Byte.class.isInstance(o)) {
				byte b = Byte.class.cast(o).byteValue();
				return new BigDecimal(b);
			} else if (BigInteger.class.isInstance(o)) {
				BigInteger bi = BigInteger.class.cast(o);
				return new BigDecimal(bi);
			} else if (String.class.isInstance(o)) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return new BigDecimal(s);
				}
				return null;
			} else {
				return null;
			}

		}

		@Override
		public BigDecimal convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<BigInteger> BIGINTEGER_CONVERTER = new Converter<BigInteger>() {

		@Override
		public BigInteger convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof BigInteger) {
				return (BigInteger) o;
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return new BigInteger(s);
				}
			}
			return null;
		}

		@Override
		public BigInteger convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<Byte> BYTE_CONVERTER = new Converter<Byte>() {

		@Override
		public Byte convert(Object o) {
			return convert(o, null);
		}

		@Override
		public Byte convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Byte) {
				return (Byte) o;
			} else if (o instanceof Number) {
				return new Byte(((Number) o).byteValue());
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Byte.valueOf(s);
				}
				return null;
			}
			return null;
		}
	};

	public static final Converter<byte[]> BINARY_CONVERTER = new Converter<byte[]>() {

		@Override
		public byte[] convert(Object o) {
			if (o == null) {
				return null;
			} else if (o instanceof byte[]) {
				return (byte[]) o;
			} else if (o instanceof String) {
				return Base64Util.decode(String.class.cast(o));
			}
			return null;
		}

		@Override
		public byte[] convert(Object o, String pattern) {
			return convert(o);
		}

	};

	public static final Converter<Boolean> BOOLEAN_CONVERTER = new Converter<Boolean>() {

		@Override
		public Boolean convert(Object o) {
			if (o == null) {
				return null;
			} else if (Boolean.class.isInstance(o)) {
				return Boolean.class.cast(o);
			} else if (String.class.isInstance(o)) {
				String s = String.class.cast(o);
				return Boolean.valueOf(YES_PATTERN.matcher(s).matches());
			} else if (Number.class.isInstance(o)) {
				Number n = Number.class.cast(o);
				return Boolean.valueOf(n.intValue() != 0);
			}
			return null;
		}

		@Override
		public Boolean convert(Object o, String pattern) {
			return convert(o);
		}

	};

	public static final Converter<Calendar> CALENDAR_CONVERTER = new Converter<Calendar>() {

		@Override
		public Calendar convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Calendar) {
				return (Calendar) o;
			}
			java.util.Date date = DATE_CONVERTER.convert(o, pattern);
			if (date != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				return cal;
			}
			return null;
		}

		@Override
		public Calendar convert(Object o) {
			return convert(o, null);
		}

	};

	public abstract static class DateConverter implements
			Converter<java.util.Date> {

		public java.util.Date toDate(String s, String pattern) {
			return toDate(s, pattern, Locale.getDefault());
		}

		public java.util.Date toDate(String s, String pattern, Locale locale) {
			SimpleDateFormat sdf = getDateFormat(s, pattern, locale);
			try {
				return sdf.parse(s);
			} catch (ParseException ex) {
				LOG.log(Level.CONFIG, ex.getMessage(), ex);
				return null;
			}
		}

		public SimpleDateFormat getDateFormat(String s, String pattern,
				Locale locale) {
			if (pattern != null) {
				return createSimpleDateFormat(pattern);
			}
			return getDateFormat(s, locale);
		}

		public SimpleDateFormat getDateFormat(String s, Locale locale) {
			String pattern = getPattern(locale);
			String shortPattern = removeDelimiter(pattern);
			String delimitor = findDelimiter(s);
			if (delimitor == null) {
				if (s.length() == shortPattern.length()) {
					return createSimpleDateFormat(shortPattern);
				}
				if (s.length() == shortPattern.length() + 2) {
					return createSimpleDateFormat(StringUtil.replace(
							shortPattern, "yy", "yyyy"));
				}
			} else {
				String[] array = s.split(delimitor);
				for (int i = 0; i < array.length; ++i) {
					if (array[i].length() == 4) {
						pattern = StringUtil.replace(pattern, "yy", "yyyy");
						break;
					}
				}
				return createSimpleDateFormat(pattern);
			}
			return createSimpleDateFormat();
		}

		public SimpleDateFormat getDateFormat(Locale locale) {
			return createSimpleDateFormat(getPattern(locale));
		}

		public SimpleDateFormat getY4DateFormat(Locale locale) {
			return createSimpleDateFormat(getY4Pattern(locale));
		}

		public String getY4Pattern(Locale locale) {
			String pattern = getPattern(locale);
			if (pattern.indexOf("yyyy") < 0) {
				pattern = StringUtil.replace(pattern, "yy", "yyyy");
			}
			return pattern;
		}

		public String getPattern(Locale locale) {
			SimpleDateFormat df = (SimpleDateFormat) DateFormat
					.getDateInstance(DateFormat.SHORT, locale);
			String pattern = df.toPattern();
			int index = pattern.indexOf(' ');
			if (index > 0) {
				pattern = pattern.substring(0, index);
			}
			if (pattern.indexOf("MM") < 0) {
				pattern = StringUtil.replace(pattern, "M", "MM");
			}
			if (pattern.indexOf("dd") < 0) {
				pattern = StringUtil.replace(pattern, "d", "dd");
			}
			return pattern;
		}

		public String findDelimiter(String value) {
			for (int i = 0; i < value.length(); ++i) {
				char c = value.charAt(i);
				if (Character.isDigit(c)) {
					continue;
				}
				return Character.toString(c);
			}
			return null;
		}

		public String removeDelimiter(String pattern) {
			StringBuilder builder = new StringBuilder(pattern.length());
			for (int i = 0; i < pattern.length(); ++i) {
				char c = pattern.charAt(i);
				if (c == 'y' || c == 'M' || c == 'd') {
					builder.append(c);
				}
			}
			return builder.toString();
		}

		protected SimpleDateFormat createSimpleDateFormat(String pattern) {
			return new SimpleDateFormat(pattern);
		}

		protected SimpleDateFormat createSimpleDateFormat() {
			return new SimpleDateFormat();
		}

	}

	public static final DateConverter DATE_CONVERTER = new DateConverter() {

		@Override
		public java.util.Date convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof String) {
				return toDate((String) o, pattern);
			} else if (o instanceof java.util.Date) {
				return (java.util.Date) o;
			} else if (o instanceof Calendar) {
				return ((Calendar) o).getTime();
			} else if (o instanceof Number) {
				return new java.util.Date(((Number) o).longValue());
			}
			return null;
		}

		@Override
		public java.util.Date convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<Double> DOUBLE_CONVERTER = new Converter<Double>() {

		@Override
		public Double convert(Object o) {
			return convert(o, null);
		}

		@Override
		public Double convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Double) {
				return (Double) o;
			} else if (o instanceof Number) {
				return ((Number) o).doubleValue();
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Double.valueOf(s);
				}
				return null;
			} else if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue() ? 1.0 : 0.0;
			}
			return null;
		}

	};

	public static final Converter<Float> FLOAT_CONVERTER = new Converter<Float>() {

		@Override
		public Float convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Float) {
				return (Float) o;
			} else if (o instanceof Number) {
				return new Float(((Number) o).floatValue());
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Float.valueOf(s);
				}
				return null;
			} else if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue() ? 1.0f : 0.0f;
			}
			return null;
		}

		@Override
		public Float convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {

		@Override
		public Integer convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Integer) {
				return (Integer) o;
			} else if (o instanceof Number) {
				return ((Number) o).intValue();
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Integer.valueOf(s);
				}
				return null;
			} else if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue() ? 1 : 0;
			}
			return null;
		}

		@Override
		public Integer convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<Long> LONG_CONVERTER = new Converter<Long>() {

		@Override
		public Long convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Long) {
				return (Long) o;
			} else if (o instanceof Number) {
				return ((Number) o).longValue();
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Long.valueOf(s);
				}
				return null;
			} else if (o instanceof java.util.Date) {
				return ((java.util.Date) o).getTime();
			} else if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue() ? 1L : 0L;
			}
			return null;
		}

		@Override
		public Long convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<Short> SHORT_CONVERTER = new Converter<Short>() {

		@Override
		public Short convert(Object o, String pattern) {
			if (o == null) {
				return null;
			} else if (o instanceof Short) {
				return (Short) o;
			} else if (o instanceof Number) {
				return new Short(((Number) o).shortValue());
			} else if (o instanceof String) {
				String s = DecimalFormatUtil.normalize(String.class.cast(o));
				if (StringUtil.isEmpty(s) == false) {
					return Short.valueOf(s);
				}
				return null;
			} else if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue() ? (short) 1 : (short) 0;
			}
			return null;
		}

		@Override
		public Short convert(Object o) {
			return convert(o, null);
		}

	};

	public static final Converter<java.sql.Date> SQLDATE_CONVERTER = new Converter<java.sql.Date>() {

		@Override
		public java.sql.Date convert(Object o, String pattern) {
			if (o instanceof java.sql.Date) {
				return (java.sql.Date) o;
			}
			java.util.Date date = DATE_CONVERTER.convert(o, pattern);
			if (date != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.HOUR, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				return new java.sql.Date(c.getTimeInMillis());
			}
			return null;
		}

		@Override
		public java.sql.Date convert(Object o) {
			return convert(o, null);
		}
	};

	public static final Converter<String> STRING_CONVERTER = new Converter<String>() {

		@Override
		public String convert(Object value, String pattern) {
			if (value == null) {
				return null;
			} else if (value instanceof String) {
				return (String) value;
			} else if (value instanceof java.util.Date) {
				return toString((java.util.Date) value, pattern);
			} else if (value instanceof Number) {
				return toString((Number) value, pattern);
			} else if (value instanceof byte[]) {
				return Base64Util.encode((byte[]) value);
			} else if (value instanceof URL) {
				return ((URL) value).toExternalForm();
			} else {
				return value.toString();
			}
		}

		protected String toString(Number value, String pattern) {
			if (value != null) {
				if (pattern != null) {
					return new DecimalFormat(pattern).format(value);
				}
				return value.toString();
			}
			return null;
		}

		protected String toString(java.util.Date value, String pattern) {
			if (pattern != null) {
				return new SimpleDateFormat(pattern).format(value);
			}
			return value.toString();
		}

		@Override
		public String convert(Object o) {
			return convert(o, null);
		}
	};

	public static final Converter<Time> TIME_CONVERTER = new Converter<Time>() {

		@Override
		public Time convert(Object o) {
			return convert(o, null);
		}

		@Override
		public Time convert(Object o, String pattern) {
			if (o instanceof Time) {
				return (Time) o;
			}
			java.util.Date date = DATE_CONVERTER.convert(o, pattern);
			if (date != null) {
				return new Time(date.getTime());
			}
			return null;
		}
	};

	public static final Converter<Timestamp> TIMESTAMP_CONVERTER = new Converter<Timestamp>() {

		@Override
		public Timestamp convert(Object o) {
			return convert(o, null);
		}

		@Override
		public Timestamp convert(Object o, String pattern) {
			if (o instanceof Timestamp) {
				return (Timestamp) o;
			}
			java.util.Date date = DATE_CONVERTER.convert(o, pattern);
			if (date != null) {
				return new Timestamp(date.getTime());
			}
			return null;
		}
	};

	public static final Converter<URL> URL_CONVERTER = new Converter<URL>() {
		@Override
		public URL convert(Object o) {
			if (o instanceof URL) {
				return (URL) o;
			}
			try {
				if (o instanceof File) {
					return ((File) o).toURI().toURL();
				}
				String url = STRING_CONVERTER.convert(o);
				if (url != null) {
					return new URL(url);
				}
				return null;
			} catch (MalformedURLException ex) {
				LOG.log(Level.CONFIG, ex.getMessage(), ex);
				return null;
			}
		}

		@Override
		public URL convert(Object o, String pattern) {
			return convert(o);
		}
	};

	public static final Converter<InputStream> INPUTSTREAM_CONVERTER = new Converter<InputStream>() {

		@Override
		public InputStream convert(Object o) {
			if (o == null) {
				return null;
			}
			if (o instanceof InputStream) {
				return (InputStream) o;
			}
			try {
				if (o instanceof File) {
					return new FileInputStream((File) o);
				}
				URL url = URL_CONVERTER.convert(o);
				if (url != null) {
					return url.openStream();
				}
			} catch (IOException ex) {
				LOG.log(Level.CONFIG, ex.getMessage(), ex);
			}
			return null;
		}

		@Override
		public InputStream convert(Object o, String pattern) {
			return convert(o);
		}
	};

	public static final Converter<Reader> READER_CONVERTER = new Converter<Reader>() {
		@Override
		public Reader convert(Object o) {
			if (o == null) {
				return null;
			}
			if (o instanceof Reader) {
				return (Reader) o;
			}
			InputStream in = INPUTSTREAM_CONVERTER.convert(o);
			if (in != null) {
				return new InputStreamReader(in);
			}
			return null;
		}

		@Override
		public Reader convert(Object o, String pattern) {
			return convert(o);
		}
	};

	static {
		init();
	}

}