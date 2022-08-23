package com.thekayani;

import com.hk.dialect.Dialect;
import com.hk.dialect.mysql.MySQLDialect;
import com.hk.dialect.mysql.MySQLDialect.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class MySQLDialectTest
{
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
}