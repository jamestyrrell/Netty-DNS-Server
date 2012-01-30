package org.handwerkszeug.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.handwerkszeug.dns.record.AbstractRecord;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * RFC1035 4. MESSAGES
 * 
 * <pre>
 *     +---------------------+
 *     |        Header       |
 *     +---------------------+
 *     |       Question      | the question for the name server
 *     +---------------------+
 *     |        Answer       | RRs answering the question
 *     +---------------------+
 *     |      Authority      | RRs pointing toward an authority
 *     +---------------------+
 *     |      Additional     | RRs holding additional information
 *     +---------------------+
 * </pre>
 * 
 * @author taichi
 */
public class DNSMessage {

	protected Header header;
	protected List<ResourceRecord> question;
	protected List<ResourceRecord> answer;
	protected List<ResourceRecord> authority;
	protected List<ResourceRecord> additional;

	protected int messageSize;

	public DNSMessage(Header header) {
		this.header(header);
		this.question = new ArrayList<ResourceRecord>();
		this.answer = new ArrayList<ResourceRecord>();
		this.authority = new ArrayList<ResourceRecord>();
		this.additional = new ArrayList<ResourceRecord>();
	}

	public DNSMessage() {
		this(new Header());
	}

	public DNSMessage(DNSMessage from) {
		this(new Header(from.header()));

		this.question().addAll(from.question());
		this.answer().addAll(from.answer());
		this.authority().addAll(from.authority());
		this.additional().addAll(from.additional());

		this.messageSize(from.messageSize());
	}

	public DNSMessage(ChannelBuffer buffer) {
		this.header = new Header(buffer);
		if (this.header.rcode().equals(RCode.FormErr)) {
			this.question = Collections.emptyList();
			this.answer = Collections.emptyList();
			this.authority = Collections.emptyList();
			this.additional = Collections.emptyList();
		} else {
			this.parse(buffer);
		}
	}

	protected void parse(ChannelBuffer buffer) {
		int q = this.header().qdcount();
		if (q < 1) {
			this.question = Collections.emptyList();
		} else {
			this.question(new ArrayList<ResourceRecord>(q));
			for (int i = 0; i < q; i++) {
				this.question().add(AbstractRecord.parseSection(buffer));
			}
		}
		this.answer(parse(buffer, this.header().ancount()));
		this.authority(parse(buffer, this.header().nscount()));
		this.additional(parse(buffer, this.header().arcount()));

		this.messageSize(buffer.readerIndex());
	}

	protected List<ResourceRecord> parse(ChannelBuffer buffer, int size) {
		if (size < 1) {
			return Collections.emptyList();
		}
		List<ResourceRecord> result = new ArrayList<ResourceRecord>(size);
		for (int i = 0; i < size; i++) {
			ResourceRecord rr = AbstractRecord.parseSection(buffer);
			rr.parse(buffer);
			result.add(rr);
		}
		return result;
	}

	public void write(ChannelBuffer buffer) {
		header().qdcount(this.question().size());
		header().ancount(this.answer().size());
		header().nscount(this.authority().size());
		header().arcount(this.additional().size());

		header().write(buffer);
		NameCompressor nc = new SimpleNameCompressor();
		for (ResourceRecord rr : this.question()) {
			AbstractRecord.writeSection(buffer, nc, rr);
		}
		write(buffer, nc, answer());
		write(buffer, nc, authority());
		write(buffer, nc, additional());
	}

	protected void write(ChannelBuffer buffer, NameCompressor compressor,
			List<ResourceRecord> list) {
		for (ResourceRecord rr : list) {
			AbstractRecord.writeSection(buffer, compressor, rr);
			rr.write(buffer, compressor);
		}
	}

	public Header header() {
		return this.header;
	}

	public void header(Header header) {
		this.header = header;
	}

	/**
	 * 4.1.2. Question section format
	 */
	public List<ResourceRecord> question() {
		return this.question;
	}

	public void question(List<ResourceRecord> list) {
		this.question = list;
	}

	public List<ResourceRecord> answer() {
		return this.answer;
	}

	public void answer(List<ResourceRecord> list) {
		this.answer = list;
	}

	public List<ResourceRecord> authority() {
		return this.authority;
	}

	public void authority(List<ResourceRecord> list) {
		this.authority = list;
	}

	public List<ResourceRecord> additional() {
		return this.additional;
	}

	public void additional(List<ResourceRecord> list) {
		this.additional = list;
	}

	public int messageSize() {
		return this.messageSize;
	}

	public void messageSize(int size) {
		this.messageSize = size;
	}
}
