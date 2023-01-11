package com.hk.dialect.mysql;

import com.hk.dialect.Dialect;
import com.hk.dialect.DialectTest;
import com.hk.math.Rand;
import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryOperator.*;
import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryTest.*;
import static org.junit.Assert.*;

public class MySQLDialectTest
{
	private Connection conn;

	@Before
	public void setUp() throws Exception
	{
		conn = DialectTest.getMySQLConnection();
	}

	@Test
	public void testConn() throws Exception
	{
		PreparedStatement statement;

		statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS tasks (\n" +
				"    task_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
				"    title VARCHAR(255) NOT NULL" +
				")  ENGINE=INNODB;");
		assertFalse(statement.execute());
		statement.close();

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO `tasks` (`title`) VALUES ");
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

		statement = conn.prepareStatement("DROP TABLE tasks;");
		assertFalse(statement.execute());
		statement.close();
	}

	@Test
	public void testParameters()
	{
		Dialect d = MySQLDialect.getInstance();

		Dialect.TableMeta points = d.table(Dialect.Owner.LUA, "points");

		Dialect.FieldMeta x = points.field("x");
		Dialect.FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		Dialect.Query q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = ?";
		q = d.select(x).from(points).where(x.is(EQUALS, d.value(1)));
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + ? = ?";
		q = d.select(x).from(points).where(x.op(ADD, d.value(1)).is(EQUALS, d.value(2)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(2, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = ?";
		q = d.select(x).from(points).where(x.is(EQUALS, d.value(10)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` != ?";
		q = d.select(x).from(points).where(x.is(NOT_EQUALS, d.value(1)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` < ?";
		q = d.select(x).from(points).where(x.is(LESS_THAN, d.value(1)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE ? = `lua_points`.`x`";
		q = d.select(x).from(points).where(d.value(1).is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE ? >= `lua_points`.`x`";
		q = d.select(x).from(points).where(d.value(1).is(GRTR_EQ_THAN, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`x`";
		q = d.select(x).from(points).where(x.is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`y`";
		q = d.select(x).from(points).where(x.is(EQUALS, y));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE `lua_points`.`y`";
		q = d.select(x).from(points).where(x.is(LIKE, y));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE ?";
		q = d.select(x).from(points).where(x.is(LIKE, d.value("https://%")));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.VARCHAR, entry.getKey());
		assertEquals("https://%", entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`y` = `lua_points`.`x`";
		q = d.select(x).from(points).where(y.is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + ? = ?";
		q = d.select(x).from(points).where(x.op(ADD, d.value(1)).is(EQUALS, d.value(2)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(2, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + ? = ? + ?";
		q = d.select(x).from(points).where(x.op(ADD, d.value(1)).is(EQUALS, d.value(1).op(ADD, d.value(1))));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(3, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());

		entry = values.get(2);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + `lua_points`.`y` = ? + ?";
		q = d.select(x).from(points).where(x.op(ADD, y).is(EQUALS, d.value(10).op(ADD, d.value(10))));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());
	}

	@After
	public void tearDown() throws Exception
	{
		conn.close();
	}
}