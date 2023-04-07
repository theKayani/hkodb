package com.hk.dialect.mysql;

import com.hk.dialect.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryTest.EQUALS;
import static com.hk.dialect.mysql.MySQLColumns.*;
import static org.junit.Assert.*;

// Testing the MySQL CRUD model
// Create // INSERT
// Read // SELECT
// Update // UPDATE
// Delete // DELETE
public class StatementTest
{
	private Connection conn;

	@Before
	public void setUp() throws Exception
	{
		conn = DialectTest.getMySQLConnection();
	}

	// Create // INSERT
	@Test
	public void testInsert() throws SQLException
	{
		Dialect mysql = MySQLDialect.getInstance();
		PreparedStatement statement;

		TableMeta names = mysql.table(Owner.SYSTEM, "ins_names");

		statement = conn.prepareStatement("CREATE TABLE `sys_ins_names` (\n" +
				"  `fname` VARCHAR(256),\n" +
				"  `lname` VARCHAR(256),\n" +
				"  `frequency` DOUBLE,\n" +
				"  PRIMARY KEY (`fname`, `lname`)\n" +
				");");
		assertFalse(statement.execute());
		statement.close();

		FieldMeta fname = names.field("fname");
		FieldMeta lname = names.field("lname");
		FieldMeta frequency = names.field("frequency");
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

		Runnable checkWithSelect = () -> {
			Map<String, Double> fullNames = new HashMap<>();
			for (int i = 0; i < arrs.length; i++)
				fullNames.put(arrs[i][0] + "-" + arrs[i][1], freqs[i]);

			try
			{
				PreparedStatement st = conn.prepareStatement("SELECT `sys_ins_names`.`fname`, `sys_ins_names`.`lname`, `sys_ins_names`.`frequency` FROM `sys_ins_names`;");
				assertTrue(st.execute());
				ResultSet set = st.getResultSet();
				while(set.next())
				{
					String fullName = set.getString(1) + "-" + set.getString(2);
					assertTrue(fullNames.containsKey(fullName));
					assertEquals(fullNames.remove(fullName), set.getDouble(3), 0.00001);
				}
				set.close();
				st.close();
			}
			catch (SQLException e)
			{
				throw new RuntimeException(e);
			}
			assertTrue(fullNames.isEmpty());
		};

		assertEquals(arrs.length, freqs.length);
		InsertQuery query;

		query = mysql.insert(names);
		for (int i = 0; i < arrs.length; i++)
		{
			InsertRow row = query.row();
			row.set(fname, mysql.value(arrs[i][0]));
			row.set(lname, mysql.value(arrs[i][1]));
			row.set(frequency, mysql.value(freqs[i]));
			row.insert();
		}
		statement = Dialect.prepareStatement(conn, query);
		assertEquals(arrs.length, statement.executeUpdate());
		statement.close();

		checkWithSelect.run();
		statement = conn.prepareStatement("TRUNCATE TABLE `sys_ins_names`;");
		assertFalse(statement.execute());
		statement.close();

		for (int i = 0; i < arrs.length; i++)
		{
			query = mysql.insert(names);
			query.cols(fname, lname, frequency);
			Query.QueryValue[][] values = new Query.QueryValue[1][3];
			values[0][0] = mysql.value(arrs[i][0]);
			values[0][1] = mysql.value(arrs[i][1]);
			values[0][2] = mysql.value(freqs[i]);
			query = query.values(values);
			statement = Dialect.prepareStatement(conn, query);
			assertEquals(1, statement.executeUpdate());
			statement.close();
		}

		checkWithSelect.run();
		statement = conn.prepareStatement("TRUNCATE TABLE `sys_ins_names`;");
		assertFalse(statement.execute());
		statement.close();

		for (int i = 0; i < arrs.length; i++)
		{
			query = mysql.insert(names);
			query.row()
					.set(fname, mysql.value(arrs[i][0]))
					.set(lname, mysql.value(arrs[i][1]))
					.set(frequency, mysql.value(freqs[i]))
					.insert();
			statement = Dialect.prepareStatement(conn, query);
			assertEquals(1, statement.executeUpdate());
			statement.close();
		}

		checkWithSelect.run();
		statement = conn.prepareStatement("TRUNCATE TABLE `sys_ins_names`;");
		assertFalse(statement.execute());
		statement.close();

		query = mysql.insert(names);
		query.cols(fname, lname, frequency);
		Query.QueryValue[][] values = new Query.QueryValue[arrs.length][3];
		for (int i = 0; i < arrs.length; i++)
		{
			values[i][0] = mysql.value(arrs[i][0]);
			values[i][1] = mysql.value(arrs[i][1]);
			values[i][2] = mysql.value(freqs[i]);
		}
		query = query.values(values);
		statement = Dialect.prepareStatement(conn, query);
		assertEquals(arrs.length, statement.executeUpdate());
		statement.close();

		checkWithSelect.run();
		statement = conn.prepareStatement("DROP TABLE `sys_ins_names`;");
		assertFalse(statement.execute());
		statement.close();
	}

	@Test
	public void testSelect() throws SQLException
	{
		Dialect mysql = MySQLDialect.getInstance();
		PreparedStatement statement;
		ResultSet set;

		TableMeta names = mysql.table(Owner.SYSTEM, "sel_names");

		statement = conn.prepareStatement("CREATE TABLE `sys_sel_names` (\n" +
				"  `fname` VARCHAR(256),\n" +
				"  `lname` VARCHAR(256),\n" +
				"  `frequency` DOUBLE,\n" +
				"  PRIMARY KEY (`fname`, `lname`)\n" +
				");");
		assertFalse(statement.execute());
		statement.close();

		FieldMeta fname = names.field("fname");
		FieldMeta lname = names.field("lname");
		FieldMeta frequency = names.field("frequency");
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
			statement = conn.prepareStatement("INSERT INTO `sys_sel_names` (`fname`, `lname`, `frequency`) VALUES (?, ?, ?);");
			statement.setString(1, arrs[i][0]);
			statement.setString(2, arrs[i][1]);
			statement.setDouble(3, freqs[i]);
			assertEquals(1, statement.executeUpdate());
			statement.close();
		}

		SelectQuery q = mysql.select(fname, lname, frequency)
				.from(names)
				.where(lname.is(EQUALS, mysql.value("Doe")));
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

		statement = conn.prepareStatement("DROP TABLE `sys_sel_names`;");
		assertFalse(statement.execute());
		statement.close();
	}

	@Test
	public void testCreateTable() throws Exception
	{
		Dialect mysql = MySQLDialect.getInstance();
		PreparedStatement statement;

		TableMeta names = mysql.table(Owner.SYSTEM, "cre_tbl_names");
		FieldMeta fname = names.field("fname");
		FieldMeta lname = names.field("lname");
		FieldMeta frequency = names.field("frequency");

		CreateTableQuery q = mysql.createTable(names)
				.column(fname, VARCHAR(256))
				.column(lname, VARCHAR(256))
				.column(frequency, DOUBLE())
				.extra("primary key", fname, lname);
		statement = Dialect.prepareStatement(conn, q);
		assertFalse(statement.execute());

		statement = conn.prepareStatement("DROP TABLE `sys_cre_tbl_names`;");
		assertFalse(statement.execute());
		statement.close();
	}

	@After
	public void tearDown() throws SQLException
	{
		conn.close();
	}
}
