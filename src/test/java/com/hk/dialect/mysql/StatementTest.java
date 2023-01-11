package com.hk.dialect.mysql;

import com.hk.dialect.Dialect;
import com.hk.dialect.DialectTest;
import com.hk.math.MathUtil;
import com.hk.str.HTMLText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryTest.*;
import static org.junit.Assert.*;

public class StatementTest
{
	private Connection conn;

	@Before
	public void setUp() throws Exception
	{
		conn = DialectTest.getMySQLConnection();
	}

	@Test
	public void test() throws SQLException
	{
		Dialect d = MySQLDialect.getInstance();
		PreparedStatement statement;
		ResultSet set;

		Dialect.TableMeta names = d.table(Dialect.Owner.SYSTEM, "names");

		statement = conn.prepareStatement("CREATE TABLE `sys_names` (\n" +
				"  `fname` VARCHAR(256),\n" +
				"  `lname` VARCHAR(256),\n" +
				"  `frequency` DOUBLE,\n" +
				"  PRIMARY KEY (`fname`, `lname`)\n" +
				");");
		assertFalse(statement.execute());
		statement.close();

		Dialect.FieldMeta fname = names.field("fname");
		Dialect.FieldMeta lname = names.field("lname");
		Dialect.FieldMeta frequency = names.field("frequency");
		String[][] arrs = {
				{ "John", "Doe" },
				{ "Jane", "Doe" },
				{ "Arnold", "Smith" },
				{ "Bob", "Dylan" },
				{ "Ezekiel", "Nosferatu" },
				{ "Hope", "Simmons" },
				{ "Crab", "Man" },
		};
		double[] freqs = {
				0.2,
				0.15,
				0.1,
				0.25,
				0.09,
				0.2,
				0.01
		};

		assertEquals(arrs.length, freqs.length);

		for (int i = 0; i < arrs.length; i++)
		{
			statement = conn.prepareStatement(String.format("INSERT INTO `sys_names` (`fname`, `lname`, `frequency`) VALUES (?, ?, %s);", freqs[i]));
			statement.setString(1, arrs[i][0]);
			statement.setString(2, arrs[i][1]);
			assertEquals(1, statement.executeUpdate());
			statement.close();
		}

		Dialect.Query q = d.select(fname, lname, frequency)
				.from(names)
				.where(lname.is(EQUALS, d.value("Doe")));
		statement = Dialect.prepareStatement(conn, q);
		assertTrue(statement.execute());
		set = statement.getResultSet();
		assertTrue(set.next());
		assertEquals("Jane", set.getString(1));
		assertEquals("Doe", set.getString(2));
		assertEquals(0.15, set.getDouble(3), 0.001);
		assertTrue(set.next());
		assertEquals("John", set.getString(1));
		assertEquals("Doe", set.getString(2));
		assertEquals(0.2, set.getDouble(3), 0.001);
		assertFalse(set.next());
		set.close();
		statement.close();

		statement = conn.prepareStatement("DROP TABLE `sys_names`;");
		assertFalse(statement.execute());
		statement.close();
	}

	@After
	public void tearDown() throws Exception
	{
		conn.close();
	}
}
