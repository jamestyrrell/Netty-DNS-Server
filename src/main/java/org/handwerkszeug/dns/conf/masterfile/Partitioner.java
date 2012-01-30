package org.handwerkszeug.dns.conf.masterfile;

import java.io.IOException;
import java.io.InputStream;

import org.handwerkszeug.dns.conf.masterfile.Partition.PartitionType;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Partitioner {

	static final Logger LOG = LoggerFactory.getLogger(Partitioner.class);

	final InputStream source;

	static final int DEFAULT_BUFFER_SIZE = 2000;
	protected ChannelBuffer working;
	protected Partition next;

	public Partitioner(InputStream in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	public Partitioner(InputStream in, int size) {
		this.source = in;
		this.working = new DynamicChannelBuffer(size);
	}

	public Partition partition() {
		Partition result = this.next;
		if (result != null) {
			this.next = null;
			return result;
		}
		while (true) {
			byte ch = readByte();
			if (ch == -1) {
				if (0 < this.working.readerIndex()) {
					result = makePartition(PartitionType.Default, 0);
					discardBefore(0);
				} else {
					break;
				}
			}
			if (ch == '\r') {
				byte n = readByte();
				if (n == '\n') {
					if (this.working.readerIndex() < 3) {
						this.working.discardReadBytes();
						return Partition.EOL;
					}
					this.next = Partition.EOL;
					result = makePartition(PartitionType.Default, 2);
					discardBefore(0);
				} else {
					this.working.readerIndex(this.working.readerIndex() - 1);
				}
			}
			if (ch == '\n') {
				if (this.working.readerIndex() < 2) {
					this.working.discardReadBytes();
					return Partition.EOL;
				}
				this.next = Partition.EOL;
				result = makePartition(PartitionType.Default, 1);
				discardBefore(0);
			}

			if (ch == ';') {
				result = readTo(partitionBefore(), PartitionType.Comment, '\n');
			}

			if (ch == '(') {
				result = currentOrNext(partitionBefore(), Partition.LP);
			}
			if (ch == ')') {
				result = currentOrNext(partitionBefore(), Partition.RP);
			}

			if ((ch == '"')) {
				result = readTo(partitionBefore(), PartitionType.Quoted, '"');
			}

			if (((ch == ' ') || (ch == '\t'))) {
				result = partitionBefore();
				int begin = this.working.readerIndex() - 1;
				while (true) {
					byte c = readByte();
					if ((c != ' ') && (c != '\t')) {
						int end = this.working.readerIndex() - 1;
						Partition ws = makePartition(PartitionType.Whitespace,
								begin, end);
						result = currentOrNext(result, ws, 1);
						break;
					}
				}
			}
			if (result != null) {
				return result;
			}
		}
		return Partition.EOF;
	}

	protected Partition currentOrNext(Partition before, Partition p) {
		return currentOrNext(before, p, 0);
	}

	protected Partition currentOrNext(Partition before, Partition p, int discard) {
		Partition result = before;
		if (before == null) {
			result = p;
		} else {
			this.next = p;
		}
		discardBefore(discard);
		return result;
	}

	protected Partition readTo(Partition back, PartitionType type, char stop) {
		Partition result = back;
		int begin = this.working.readerIndex() - 1;
		while (true) {
			byte c = readByte();
			if ((c == stop) || (c == -1)) {
				int end = this.working.readerIndex();
				Partition p = makePartition(type, begin, end);
				result = currentOrNext(back, p);
				break;
			}
		}
		return result;
	}

	protected byte readByte() {
		try {
			if (this.working.readable() == false) {
				if (0 < this.source.available()) {
					this.working.writeBytes(this.source, DEFAULT_BUFFER_SIZE);
				}
			}
			if (this.working.readable()) {
				return this.working.readByte();
			}
			return -1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Partition makePartition(PartitionType type, int begin, int end) {
		byte[] newone = new byte[end - begin];
		this.working.getBytes(begin, newone);
		return new Partition(type, newone);
	}

	protected Partition makePartition(PartitionType type, int stripSize) {
		int newsize = this.working.readerIndex() - stripSize;
		byte[] newone = new byte[newsize];
		this.working.getBytes(0, newone);
		return new Partition(type, newone);
	}

	protected Partition partitionBefore() {
		if (1 < this.working.readerIndex()) {
			return makePartition(PartitionType.Default, 1);
		}
		return null;
	}

	protected void discardBefore(int backSize) {
		this.working.readerIndex(this.working.readerIndex() - backSize);
		this.working.discardReadBytes();
	}

	public void close() {
		try {
			this.source.close();
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
