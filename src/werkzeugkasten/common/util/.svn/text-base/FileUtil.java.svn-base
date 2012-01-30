package werkzeugkasten.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import werkzeugkasten.common.exception.IORuntimeException;

public class FileUtil {

	public interface NameFilter {
		boolean accept(String name);
	}

	public interface FileHandler {
		void handle(File file);
	}

	public static final NameFilter NULL_FILTER = new NameFilter() {
		@Override
		public boolean accept(String path) {
			return true;
		}
	};

	public static class PatternFilter implements NameFilter {
		protected Pattern pattern;

		public PatternFilter(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		public PatternFilter(Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean accept(String path) {
			return this.pattern.matcher(path).matches();
		}
	}

	public static class ReverseFilter implements NameFilter {
		protected NameFilter delegate;

		public ReverseFilter(NameFilter filter) {
			this.delegate = filter;
		}

		@Override
		public boolean accept(String path) {
			return this.delegate.accept(path) == false;
		}
	}

	public static void walk(String path, NameFilter filter, FileHandler handler) {
		File f = new File(path);
		if (filter.accept(f.getName())) {
			if (f.isDirectory()) {
				for (String s : f.list()) {
					walk(new File(f, s).getPath(), filter, handler);
				}
			}
			if (f.exists()) {
				handler.handle(f);
			}
		}
	}

	public static List<File> list(String path) {
		return list(path, NULL_FILTER);
	}

	public static List<File> list(String path, NameFilter filter) {
		final List<File> list = new ArrayList<File>();
		walk(path, filter, new FileHandler() {
			@Override
			public void handle(File file) {
				if (file.isFile()) {
					list.add(file);
				}
			}
		});
		return list;
	}

	public static void delete(String path) {
		delete(path, NULL_FILTER);
	}

	public static void delete(String path, NameFilter filter) {
		walk(path, filter, new FileHandler() {
			@Override
			public void handle(File file) {
				file.delete();
			}
		});
	}

	public static InputStream open(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IORuntimeException(e);
		}
	}

	public static void copy(final InputStream in, final File dest) {
		File dir = dest.getParentFile();
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		new Streams.using<FileOutputStream, IOException>() {
			@Override
			public FileOutputStream open() throws IOException {
				return new FileOutputStream(dest);
			}

			@Override
			public void handle(FileOutputStream stream) throws IOException {
				Streams.copy(in, stream);
			}

			@Override
			public void happen(IOException exception) {
				throw new IORuntimeException(exception);
			}
		};
	}

	public static void copy(String from, String to) {
		copy(from, to, NULL_FILTER);
	}

	public static void copy(String from, final String to, NameFilter filter) {
		File fromFile = new File(from);
		if (fromFile.exists()) {
			File t = new File(to);
			if (fromFile.isFile()) {
				File dest = null;
				if (t.exists()) {
					if (t.isDirectory()) {
						dest = new File(t, fromFile.getName());
					} else {
						delete(t.getPath());
						dest = t;
					}
				} else {
					dest = t;
				}
				copy(open(fromFile), dest);
			} else if (fromFile.isDirectory()) {
				final String base = fromFile.getAbsolutePath();
				walk(from, filter, new FileHandler() {
					@Override
					public void handle(File file) {
						String path = file.getAbsolutePath().replace(base, "");
						File t = new File(to, path);
						if (file.isDirectory() && t.exists() == false) {
							t.mkdirs();
						}
						if (file.isFile()) {
							copy(open(file), t.getAbsoluteFile());
						}
					}
				});
			}
		}
	}
}
