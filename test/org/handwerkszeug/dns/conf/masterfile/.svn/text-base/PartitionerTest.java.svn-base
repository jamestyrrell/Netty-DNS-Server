package org.handwerkszeug.dns.conf.masterfile;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.handwerkszeug.dns.conf.masterfile.Partition.PartitionType;
import org.junit.Test;

public class PartitionerTest {

	@Test
	public void testPartition() throws Exception {
		String s = "A \t\tB C;hogehoge fugafuga\r\n\n\r\nmogemoge\t\t\tABC\"aaaa \n bbbbb\"";
		// String s = "ZYX\t\t\tABC";
		// String s = "\nmogemoge";

		List<Partition> list = new ArrayList<Partition>();
		list.add(new Partition(PartitionType.Default, "A".getBytes()));
		list.add(new Partition(PartitionType.Whitespace, " \t\t".getBytes()));
		list.add(new Partition(PartitionType.Default, "B".getBytes()));
		list.add(new Partition(PartitionType.Whitespace, " ".getBytes()));
		list.add(new Partition(PartitionType.Default, "C".getBytes()));
		list.add(new Partition(PartitionType.Comment, ";hogehoge fugafuga\r\n"
				.getBytes()));
		list.add(Partition.EOL);
		list.add(Partition.EOL);
		list.add(new Partition(PartitionType.Default, "mogemoge".getBytes()));
		list.add(new Partition(PartitionType.Whitespace, "\t\t\t".getBytes()));
		list.add(new Partition(PartitionType.Default, "ABC".getBytes()));
		list.add(new Partition(PartitionType.Quoted, "\"aaaa \n bbbbb\""
				.getBytes()));
		list.add(Partition.EOF);

		assertPartitions(list, s);
		// while (true) {
		// Partition pp = p.partition();
		// if (pp == Partition.EOF) {
		// break;
		// }
		// System.out.println(pp);
		// }
	}

	@Test
	public void testLPRP() throws Exception {
		String s = "\t\t\t(aaaa)";

		List<Partition> list = new ArrayList<Partition>();
		list.add(new Partition(PartitionType.Whitespace, "\t\t\t".getBytes()));
		list.add(Partition.LP);
		list.add(new Partition(PartitionType.Default, "aaaa".getBytes()));
		list.add(Partition.RP);
		assertPartitions(list, s);
	}

	@Test
	public void testQuoted() throws Exception {
		String s = "$INCLUDE \"c:\\program \nfiles\\named.conf\"";
		List<Partition> list = new ArrayList<Partition>();
		list.add(new Partition(PartitionType.Default, "$INCLUDE".getBytes()));
		list.add(new Partition(PartitionType.Whitespace, " ".getBytes()));
		list.add(new Partition(PartitionType.Quoted,
				"\"c:\\program \nfiles\\named.conf\"".getBytes()));

		assertPartitions(list, s);
	}

	@Test
	public void testDirective() throws Exception {
		String s = "$TTL 30";
		Partitioner p = create(s);

		List<Partition> list = new ArrayList<Partition>();
		list.add(new Partition(PartitionType.Default, "$TTL".getBytes()));
		list.add(new Partition(PartitionType.Whitespace, " ".getBytes()));
		list.add(new Partition(PartitionType.Default, "30".getBytes()));

		for (Partition exp : list) {
			Partition act = p.partition();
			System.out.println(new String(act.division()));
			assertEquals(exp, act);
		}
	}

	protected void assertPartitions(List<Partition> expected, String data)
			throws Exception {
		Partitioner p = create(data);

		for (Partition exp : expected) {
			assertEquals(exp, p.partition());
		}
	}

	Partitioner create(String testdata) throws Exception {
		ByteArrayInputStream bai = new ByteArrayInputStream(testdata.getBytes());
		return new Partitioner(bai);
	}
}
