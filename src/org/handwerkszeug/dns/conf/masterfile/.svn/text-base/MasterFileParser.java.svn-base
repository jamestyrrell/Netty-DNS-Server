package org.handwerkszeug.dns.conf.masterfile;

import static org.handwerkszeug.util.Validation.notNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.handwerkszeug.dns.DNSClass;
import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.RRType;
import org.handwerkszeug.dns.conf.MasterDataHandler;
import org.handwerkszeug.dns.conf.MasterDataResource;
import org.handwerkszeug.dns.conf.ServerConfiguration;
import org.handwerkszeug.dns.conf.masterfile.Partition.PartitionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import werkzeugkasten.common.util.FileUtil;

/**
 * RFC1035 5. MASTER FILES
 * 
 * @author taichi
 */
public class MasterFileParser implements MasterDataResource {

	static final Logger LOG = LoggerFactory.getLogger(MasterFileParser.class);

	static final int MAX_INCLUDE_DEPTH = 10; // TODO from configuration ?

	final Partitioner partitioner;

	ServerConfiguration conf;

	Name origin;
	long ttl;

	IncludeContext includeContext;

	int currentLine = 1; // TODO for error messages.

	class IncludeContext {
		int includeDepth = 0;
		Set<String> includedPath = new HashSet<String>();
	}

	public MasterFileParser(String origin, File master) {
		this(origin, FileUtil.open(master));
		this.includeContext.includedPath.add(master.getAbsolutePath());
	}

	public MasterFileParser(String origin, InputStream in) {
		this.includeContext = new IncludeContext();
		this.partitioner = new Partitioner(in);
		this.origin = new Name(origin);
	}

	protected MasterFileParser(Name origin, File file, IncludeContext context) {
		this.includeContext = context;
		this.partitioner = new Partitioner(FileUtil.open(file));
		this.origin = origin;
	}

	@Override
	public void initialize(ServerConfiguration conf) {
		notNull(conf, "conf");
		if (LOG.isInfoEnabled()) {
			LOG.info("initialize");
		}
		this.conf = conf;
	}

	@Override
	public void dispose() {
		if (LOG.isInfoEnabled()) {
			LOG.info("dispose");
		}
		this.partitioner.close();
	}

	@Override
	public void process(MasterDataHandler handler) {
		notNull(handler, "processor");
		try {
			handler.initialize(this.conf);
		} catch (RuntimeException e) {
			handler.rollback();
			throw e;
		} finally {
			handler.dispose();
		}
	}

	protected void internalProcess(MasterDataHandler handler) {
		Name currentName = null;
		long currentTTL = 0L;
		DNSClass currentClass = DNSClass.IN;

		while (true) {
			Iterator<Partition> line = readLine();
			if (line.hasNext()) {
				break;
			}
			Partition first = line.next();

			if (isDirective(first)) {
				String directive = first.getString().toUpperCase();
				if ("$INCLUDE".equals(directive)) {
					String path = null;
					Name newOrigin = this.origin;
					if (line.hasNext()) {
						path = line.next().getString();
					} else {
						// TODO parser error
						throw new IllegalStateException();
					}
					if (line.hasNext()) {
						String s = line.next().getString();
						newOrigin = new Name(s);
					}
					processInclude(path, newOrigin, handler);
				} else if ("$ORIGIN".equals(directive)) {
					if (line.hasNext()) {
						String origin = line.next().getString();
						this.origin = new Name(origin);
					}
				} else if ("$TTL".equals(directive)) {
					if (line.hasNext()) {
						String num = line.next().getString();
						if (isTTL(num)) {
							this.ttl = Long.parseLong(num);
						}
					}
				} else {
					LOG.warn("unknown directive {}", directive);
				}
				continue;
			}
			if (first.type().equals(PartitionType.Default)) {
				currentName = new Name(first.getString());
			}
			if (line.hasNext()) {
				String second = line.next().getString();
				// ttl class type
				// ttl type
				// class ttl type
				// class type
				// type
				if (isTTL(second)) {
					currentTTL = Long.parseLong(second);
					if (line.hasNext()) {
						String third = line.next().getString();
						if (isDNSClass(third)) {

						} else if (isRRType(third)) {

						} else {
							// TODO parser error
						}
					} else {
						// TODO parser error
					}
				} else if (isDNSClass(second)) {
					currentClass = DNSClass.valueOf(second.toUpperCase());
				} else if (isRRType(second)) {
					RRType type = RRType.valueOf(second.toUpperCase());
				} else {
					// TODO parser error.
				}
			} else {
				// TODO parser error
			}
		}
	}

	protected void processInclude(String path, Name origin,
			MasterDataHandler handler) {
		if (MAX_INCLUDE_DEPTH < ++this.includeContext.includeDepth) {
			// TODO error message.
			throw new IllegalStateException();
		}
		File file = new File(path);
		if (this.includeContext.includedPath.add(file.getAbsolutePath()) == false) {
			// TODO error message. cyclic include.
			throw new IllegalStateException();
		}

		MasterFileParser newone = new MasterFileParser(origin, file,
				this.includeContext);
		try {
			newone.initialize(this.conf);
			newone.process(handler);
		} finally {
			newone.dispose();
		}
	}

	protected Iterator<Partition> readLine() {
		// 改行のみ 空白のみ コメントのみ は読み飛ばす。
		// 先頭のWhitespaceは読み飛ばさないが、他のWhitespaceは読み飛ばす。
		return null;
	}

	protected boolean isDirective(Partition p) {
		byte[] b = p.division();
		return b != null && 0 < b.length && b[0] == '$';
	}

	protected boolean isWhitespace(Partition p) {
		return PartitionType.Whitespace.equals(p.type());
	}

	protected boolean isTTL(String p) {
		return false;
	}

	protected boolean isDNSClass(String p) {
		DNSClass dc = DNSClass.find(p);
		return dc != null;
	}

	protected boolean isRRType(String p) {
		return false;
	}

}
