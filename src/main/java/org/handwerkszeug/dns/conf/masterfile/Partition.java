package org.handwerkszeug.dns.conf.masterfile;

import java.util.Arrays;

public class Partition {
	enum PartitionType {
		Default, Quoted, LP, RP, Comment, Whitespace, EOL, EOF;
	}

	public static final Partition EOF = new Partition(PartitionType.EOF);

	public static final Partition EOL = new Partition(PartitionType.EOL);

	public static final Partition LP = new Partition(PartitionType.LP);

	public static final Partition RP = new Partition(PartitionType.RP);

	final PartitionType type;
	final byte[] division;

	public Partition(PartitionType type) {
		this(type, null);
	}

	public Partition(PartitionType type, byte[] buffer) {
		this.type = type;
		this.division = buffer;
	}

	public PartitionType type() {
		return this.type;
	}

	public byte[] division() {
		return this.division;
	}

	public String getString() {
		return new String(this.division);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		result = prime * result + Arrays.hashCode(this.division);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Partition other = (Partition) obj;
		if (this.type != other.type) {
			return false;
		}
		if (!Arrays.equals(this.division, other.division)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append("[");
		stb.append(this.type);
		stb.append("]");
		if (this.division != null) {
			stb.append("<");
			stb.append(new String(this.division));
			stb.append(">");
		}
		return stb.toString();
	}
}