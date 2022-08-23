package com.thekayani;

import com.hk.math.Rand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import static org.junit.Assert.*;

public class MySQLDialectTest
{
	private Connection conn;

	@Before
	public void setUp() throws Exception
	{
		Properties properties = new Properties();
		properties.load(MySQLDialectTest.class.getResourceAsStream("/database.properties"));
		String title = properties.getProperty("database.title");
		String user = properties.getProperty("database.user");
		String pass = properties.getProperty("database.pass");

		Class.forName("com.mysql.cj.jdbc.Driver");

		conn = DriverManager.getConnection("jdbc:mysql://" + user + ":" + pass + "@localhost:3306/" + title);
	}

	/*
	@Test
	public void testStuff()
	{
		Dialect d = new MySQLDialect();

		Dialect.TableMeta points = d.table(Dialect.Owner.LUA, "points");

		Dialect.FieldMeta x = points.field("x");
		Dialect.FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		Dialect.Query q;
		String expected;

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = 1";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.EQUALS, d.value(1)));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` != 1";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.NOT_EQUALS, d.value(1)));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` < 1";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.LESS_THAN, d.value(1)));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE 1 = `lua_points`.`x`";
		q = d.select(x).from(points).where(d.value(1).is(MySQLQueryTest.EQUALS, x));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE 1 >= `lua_points`.`x`";
		q = d.select(x).from(points).where(d.value(1).is(MySQLQueryTest.GRTR_EQ_THAN, x));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`x`";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.EQUALS, x));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`y`";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.EQUALS, y));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE `lua_points`.`y`";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.LIKE, y));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE 'https://%'";
		q = d.select(x).from(points).where(x.is(MySQLQueryTest.LIKE, d.value("https://%")));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`y` = `lua_points`.`x`";
		q = d.select(x).from(points).where(y.is(MySQLQueryTest.EQUALS, x));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + 1 = 2";
		q = d.select(x).from(points).where(x.op(MySQLQueryOperator.ADD, d.value(1)).is(MySQLQueryTest.EQUALS, d.value(2)));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + 1 = 1 + 1";
		q = d.select(x).from(points).where(x.op(MySQLQueryOperator.ADD, d.value(1)).is(MySQLQueryTest.EQUALS, d.value(1).op(MySQLQueryOperator.ADD, d.value(1))));
		assertEquals(expected, Dialect.toString(q));

		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + `lua_points`.`y` = 10 + 10";
		q = d.select(x).from(points).where(x.op(MySQLQueryOperator.ADD, y).is(MySQLQueryTest.EQUALS, d.value(10).op(MySQLQueryOperator.ADD, d.value(10))));
		assertEquals(expected, Dialect.toString(q));
	}
	 */

	@Test
	public void doStuff() throws Exception
	{
		PreparedStatement statement;

		statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS tasks (\n" +
				"    task_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
				"    title VARCHAR(255) NOT NULL" +
				")  ENGINE=INNODB;");
		assertFalse(statement.execute());
		statement.close();

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO tasks (title) VALUES ");
		for (int i = 0; i < 100; i++)
		{
			sb.append("('").append(Rand.nextString(Rand.nextInt(5, 200))).append("'), ");
		}
		sb.setLength(sb.length() - 2);
		sb.append(";");
		statement = conn.prepareStatement(sb.toString());
		assertEquals(100, statement.executeUpdate());
		statement.close();

		statement = conn.prepareStatement("SELECT COUNT(*) FROM tasks;");
		ResultSet set = statement.executeQuery();
		assertTrue(set.next());
		assertEquals(100, set.getInt(1));
		assertFalse(set.next());
		statement.close();
	}

	@After
	public void tearDown() throws Exception
	{
		conn.close();
	}
}