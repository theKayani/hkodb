package com.hk.dialect.mysql;

import com.hk.dialect.*;
import com.hk.math.Rand;
import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hk.dialect.mysql.MySQLColumns.*;
import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryOperator.ADD;
import static com.hk.dialect.mysql.MySQLDialect.MySQLQueryTest.*;
import static org.junit.Assert.*;

public class SyntaxTest
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

	// CREATE
	@Test
	public void testInsertSyntax()
	{
		Dialect mysql = MySQLDialect.getInstance();

		Owner lua = mysql.owner("lua");
		TableMeta points = mysql.table(lua, "points");

		FieldMeta x = points.field("x");
		FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		InsertQuery q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`, `lua_points`.`y`) VALUES (?, ?)";
		q = mysql.insert(points).cols(x, y).values(new Query.QueryValue[] { mysql.value(12), mysql.value(24) });
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(12, entry.getValue());
		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(24, entry.getValue());
		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`, `lua_points`.`y`) VALUES (?, ?)";
		q = mysql.insert(points).row().set(x, mysql.value(36)).set(y, mysql.value(48)).insert();
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(36, entry.getValue());
		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(48, entry.getValue());
		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`) VALUES (?)";
		q = mysql.insert(points).row().set(x, mysql.value(1)).insert();
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`, `lua_points`.`y`) VALUES (?, DEFAULT(`lua_points`.`y`)), (DEFAULT(`lua_points`.`x`), ?)";
		q = mysql.insert(points)
				.row().set(x, mysql.value(3)).insert()
				.row().set(y, mysql.value(6)).insert();
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(3, entry.getValue());
		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(6, entry.getValue());
		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`, `lua_points`.`y`) VALUES (?, DEFAULT(`lua_points`.`y`)), (DEFAULT(`lua_points`.`x`), ?)";
		q = mysql.insert(points).cols(x, y).values(new Query.QueryValue[] { mysql.value(4), null }, new Query.QueryValue[] { null, mysql.value(8) });
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(4, entry.getValue());
		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(8, entry.getValue());
		/////////////////////////////////////////////
		expected = "INSERT INTO `lua_points` (`lua_points`.`x`, `lua_points`.`y`) VALUES (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?)";
		q = mysql.insert(points);
		// (0, 2), (4, 6), (8, 10), (12, 14)
		for (int i = 0; i < 8; i++)
			q = q.row().set(x, mysql.value(i * 4)).set(y, mysql.value(i * 4 + 2)).insert();

		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(16, values.size());

		for (int i = 0; i < 8; i++)
		{
			entry = values.get(i * 2);
			assertEquals(MysqlType.BIGINT, entry.getKey());
			assertEquals(i * 4, entry.getValue());
			entry = values.get(i * 2 + 1);
			assertEquals(MysqlType.BIGINT, entry.getKey());
			assertEquals(i * 4 + 2, entry.getValue());
		}
	}

	// READ
	@Test
	public void testSelectSyntax()
	{
		Dialect mysql = MySQLDialect.getInstance();

		Owner lua = mysql.owner("lua");
		TableMeta points = mysql.table(lua, "points");

		FieldMeta x = points.field("x");
		FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		SelectQuery q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = ?";
		q = mysql.select(x).from(points).where(x.is(EQUALS, mysql.value(1)));
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + ? = ?";
		q = mysql.select(x).from(points).where(x.op(ADD, mysql.value(1)).is(EQUALS, mysql.value(2)));
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
		q = mysql.select(x).from(points).where(x.is(EQUALS, mysql.value(10)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` != ?";
		q = mysql.select(x).from(points).where(x.is(NOT_EQUALS, mysql.value(1)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` < ?";
		q = mysql.select(x).from(points).where(x.is(LESS_THAN, mysql.value(1)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE ? = `lua_points`.`x`";
		q = mysql.select(x).from(points).where(mysql.value(1).is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE ? >= `lua_points`.`x`";
		q = mysql.select(x).from(points).where(mysql.value(1).is(GRTR_EQ_THAN, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(1, entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`x`";
		q = mysql.select(x).from(points).where(x.is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` = `lua_points`.`y`";
		q = mysql.select(x).from(points).where(x.is(EQUALS, y));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE `lua_points`.`y`";
		q = mysql.select(x).from(points).where(x.is(LIKE, y));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` LIKE ?";
		q = mysql.select(x).from(points).where(x.is(LIKE, mysql.value("https://%")));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.VARCHAR, entry.getKey());
		assertEquals("https://%", entry.getValue());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`y` = `lua_points`.`x`";
		q = mysql.select(x).from(points).where(y.is(EQUALS, x));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		expected = "SELECT `lua_points`.`x` FROM `lua_points` WHERE `lua_points`.`x` + ? = ?";
		q = mysql.select(x).from(points).where(x.op(ADD, mysql.value(1)).is(EQUALS, mysql.value(2)));
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
		q = mysql.select(x).from(points).where(x.op(ADD, mysql.value(1)).is(EQUALS, mysql.value(1).op(ADD, mysql.value(1))));
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
		q = mysql.select(x).from(points).where(x.op(ADD, y).is(EQUALS, mysql.value(10).op(ADD, mysql.value(10))));
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
		/////////////////////////////////////////////
		expected = "SELECT * FROM `lua_points` WHERE `lua_points`.`x` = ? AND (`lua_points`.`y` = ? OR `lua_points`.`y` = ?)";
		q = mysql.select().from(points).where(x.is(EQUALS, mysql.value(12)).and(y.is(EQUALS, mysql.value(8)).or(y.is(EQUALS, mysql.value(16))).group()));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(3, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(12, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(8, entry.getValue());

		entry = values.get(2);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(16, entry.getValue());
	}

	// UPDATE
	@Test
	public void testUpdateSyntax()
	{
		Dialect mysql = MySQLDialect.getInstance();

		Owner lua = mysql.owner("lua");
		TableMeta points = mysql.table(lua, "points");

		FieldMeta x = points.field("x");
		FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		UpdateQuery q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		expected = "UPDATE `lua_points` SET `lua_points`.`x` = ? WHERE `lua_points`.`x` = ?";
		q = mysql.update(points).set(x, mysql.value(5)).where(x.is(EQUALS, mysql.value(10)));
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(5, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());

		/////////////////////////////////////////////
		expected = "UPDATE `lua_points` SET `lua_points`.`x` = ? WHERE `lua_points`.`x` + `lua_points`.`y` = ? + ?";
		q = mysql.update(points).set(x, mysql.value(15)).where(x.op(ADD, y).is(EQUALS, mysql.value(30).op(ADD, mysql.value(45))));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(3, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(15, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(30, entry.getValue());

		entry = values.get(2);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(45, entry.getValue());

		/////////////////////////////////////////////
		expected = "UPDATE `lua_points` SET `lua_points`.`x` = ?, `lua_points`.`y` = ?";
		q = mysql.update(points).set(x, mysql.value(4)).set(y, mysql.value(8));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(4, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(8, entry.getValue());
		/////////////////////////////////////////////
		expected = "UPDATE `lua_points` SET `lua_points`.`x` = DEFAULT WHERE `lua_points`.`y` = ?";
		q = mysql.update(points).setDefault(x).where(y.is(EQUALS, mysql.value(3)));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(3, entry.getValue());
	}

	// DELETE
	@Test
	public void testDeleteSyntax()
	{
		Dialect mysql = MySQLDialect.getInstance();

		Owner lua = mysql.owner("lua");
		TableMeta points = mysql.table(lua, "points");

		FieldMeta x = points.field("x");
		FieldMeta y = points.field("y");

//		CREATE TABLE `lua_points` (
//			`x` DOUBLE,
//			`y` DOUBLE,
//			PRIMARY KEY (`x`,`y`)
//		) ENGINE=MyISAM;

		DeleteQuery q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		expected = "DELETE FROM `lua_points` WHERE `lua_points`.`x` = ?";
		q = mysql.delete(points).where(x.is(EQUALS, mysql.value(10)));
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(1, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(10, entry.getValue());

		/////////////////////////////////////////////
		expected = "DELETE FROM `lua_points` WHERE `lua_points`.`x` + `lua_points`.`y` = ? + ?";
		q = mysql.delete(points).where(x.op(ADD, y).is(EQUALS, mysql.value(33).op(ADD, mysql.value(66))));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(33, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(66, entry.getValue());

		/////////////////////////////////////////////
		expected = "DELETE FROM `lua_points`";
		q = mysql.delete(points);
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());

		/////////////////////////////////////////////
		expected = "DELETE FROM `lua_points` WHERE `lua_points`.`x` = ? AND `lua_points`.`y` = ?";
		q = mysql.delete(points).where(x.is(EQUALS, mysql.value(-3)).and(y.is(EQUALS, mysql.value(3))));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(-3, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.BIGINT, entry.getKey());
		assertEquals(3, entry.getValue());
	}

	@Test
	public void testCreateTableSyntax()
	{
		Dialect mysql = MySQLDialect.getInstance();

		CreateTableQuery q;
		HTMLText txt;
		String expected;
		Map.Entry<SQLType, Object> entry;
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();

		/////////////////////////////////////////////
		TableMeta names = mysql.table(Owner.SYSTEM, "cre_tbl_names");
		FieldMeta fname = names.field("fname");
		FieldMeta lname = names.field("lname");
		FieldMeta frequency = names.field("frequency");
		expected = "CREATE TABLE `sys_cre_tbl_names` (\n" +
				"`fname` VARCHAR(?),\n" +
				"`lname` VARCHAR(?),\n" +
				"`frequency` DOUBLE,\n" +
				"PRIMARY KEY (`fname`, `lname`)\n" +
				")";
		q = mysql.createTable(names)
				.column(fname, VARCHAR(256))
				.column(lname, VARCHAR(256))
				.column(frequency, DOUBLE())
				.extra("primary key", fname, lname);
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		entry = values.get(0);
		assertEquals(MysqlType.INT, entry.getKey());
		assertEquals(256, entry.getValue());

		entry = values.get(1);
		assertEquals(MysqlType.INT, entry.getKey());
		assertEquals(256, entry.getValue());
		/////////////////////////////////////////////
		TableMeta stores = mysql.table(mysql.owner("sales"), "stores");
		FieldMeta storeID = stores.field("store_id");
		FieldMeta storeName = stores.field("store_name");
		FieldMeta phone1 = stores.field("phone");
		FieldMeta email = stores.field("email");
		FieldMeta street = stores.field("street");
		FieldMeta city = stores.field("city");
		FieldMeta state = stores.field("state");
		FieldMeta zipCode = stores.field("zip_code");
		expected = "CREATE TABLE `sales_stores` (\n" +
				"`store_id` INT NOT NULL AUTO_INCREMENT,\n" +
				"`store_name` VARCHAR(?) NOT NULL,\n" +
				"`phone` VARCHAR(?),\n" +
				"`email` VARCHAR(?),\n" +
				"`street` VARCHAR(?),\n" +
				"`city` VARCHAR(?),\n" +
				"`state` VARCHAR(?),\n" +
				"`zip_code` VARCHAR(?),\n" +
				"PRIMARY KEY (`store_id`)\n" +
				")";
		q = mysql.createTable(stores)
				.column(storeID, INT().option("NOT NULL").option("AUTO_INCREMENT"))
				.column(storeName, VARCHAR(255).option("NOT NULL"))
				.column(phone1, VARCHAR(25))
				.column(email, VARCHAR(255))
				.column(street, VARCHAR(255))
				.column(city, VARCHAR(255))
				.column(state, VARCHAR(10))
				.column(zipCode, VARCHAR(5))
				.extra("primary key", storeID);
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(7, values.size());

		int[] vals = { 255, 25, 255, 255, 255, 10, 5 };
		for (int i = 0; i < vals.length; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(vals[i], entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta persons = mysql.table(Owner.TEST, "Persons");
		FieldMeta personID = persons.field("PersonID");
		FieldMeta lastName = persons.field("LastName");
		FieldMeta firstName = persons.field("FirstName");
		FieldMeta address = persons.field("Address");
		FieldMeta phone2 = persons.field("Phone");
		expected = "CREATE TABLE `tst_Persons` (\n" +
				"`PersonID` INT,\n" +
				"`LastName` VARCHAR(?),\n" +
				"`FirstName` VARCHAR(?),\n" +
				"`Address` VARCHAR(?),\n" +
				"`Phone` VARCHAR(?)\n" +
				")";
		q = mysql.createTable(persons)
				.column(personID, INT())
				.column(lastName, VARCHAR(1024))
				.column(firstName, VARCHAR(1024))
				.column(address, VARCHAR(1024))
				.column(phone2, VARCHAR(1024)); // illogical but y not
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(4, values.size());

		for (int i = 0; i < 4; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(1024, entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta differentCharsets = mysql.table(Owner.SYSTEM, "diff_charsets");
		FieldMeta charset1 = differentCharsets.field("charset1");
		FieldMeta charset2 = differentCharsets.field("charset2");
		FieldMeta charset3 = differentCharsets.field("charset3");
		FieldMeta charset4 = differentCharsets.field("charset4");
		FieldMeta charset5 = differentCharsets.field("charset5");
		FieldMeta charset6 = differentCharsets.field("charset6");
		FieldMeta charset7 = differentCharsets.field("charset7");
		expected = "CREATE TABLE `sys_diff_charsets` (\n" +
				"`charset1` VARCHAR(?) CHARSET utf8mb4 COLLATE utf8mb4_bin,\n" +
				"`charset2` VARCHAR(?) CHARSET utf8mb3 COLLATE utf8mb3_bin,\n" +
				"`charset3` VARCHAR(?) CHARSET utf8 COLLATE utf8_bin,\n" +
				"`charset4` CHAR(?) CHARSET ucs2 COLLATE ucs2_bin,\n" +
				"`charset5` CHAR(?) CHARSET utf16 COLLATE utf16_bin,\n" +
				"`charset6` CHAR(?) CHARSET utf16le COLLATE utf16le_bin,\n" +
				"`charset7` CHAR(?) CHARSET utf32 COLLATE utf32_bin\n" +
				")";
		q = mysql.createTable(differentCharsets)
				.column(charset1, VARCHAR(1).option("CHARACTER SET", "utf8mb4", "utf8mb4_bin"))
				.column(charset2, VARCHAR(2).option("CHArACTER_SET", "utf8mb3", "utf8mb3_bin"))
				.column(charset3, VARCHAR(3).option("CHArsET", "utf8", "utf8_bin"))
				.column(charset4, CHAR(4).option("char_set", "ucs2", "ucs2_bin"))
				.column(charset5, CHAR(5).option("COLLATE", "utf16", "utf16_bin"))
				.column(charset6, CHAR(6).option("collation", "utf16le", "utf16le_bin"))
				.column(charset7, CHAR(7).option("characterset", "utf32", "utf32_bin"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(7, values.size());

		for (int i = 0; i < 7; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(1 + i, entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta floatPoints = mysql.table(Owner.TEST, "vector_3");
		FieldMeta x1 = floatPoints.field("x");
		FieldMeta y1 = floatPoints.field("y");
		FieldMeta z1 = floatPoints.field("z");
		expected = "CREATE TABLE `tst_vector_3` (\n`x` FLOAT,\n`y` FLOAT,\n`z` FLOAT\n)";
		q = mysql.createTable(floatPoints)
				.column(x1, FLOAT())
				.column(y1, FLOAT())
				.column(z1, FLOAT());
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		// apparently unsigned floating point numbers are
		// deprecated as of MySQL 8, but o well
		TableMeta rand0 = mysql.table(Owner.USER, "rand0");
		FieldMeta rand0A = rand0.field("a");
		FieldMeta rand0B = rand0.field("b");
		FieldMeta rand0C = rand0.field("c");
		FieldMeta rand0D = rand0.field("d");
		expected = "CREATE TABLE `usr_rand0` (\n" +
				"`a` DOUBLE,\n" +
				"`b` DOUBLE UNSIGNED,\n" +
				"`c` FLOAT,\n" +
				"`d` FLOAT UNSIGNED\n" +
				")";
		q = mysql.createTable(rand0)
				.column(rand0A, DOUBLE())
				.column(rand0B, DOUBLE().option("UNSIGNED"))
				.column(rand0C, FLOAT())
				.column(rand0D, FLOAT().option("UNSIGNED"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		TableMeta rand1 = mysql.table(Owner.USER, "rand1");
		FieldMeta rand1A = rand1.field("a");
		FieldMeta rand1B = rand1.field("b");
		FieldMeta rand1C = rand1.field("c");
		FieldMeta rand1D = rand1.field("d");
		expected = "CREATE TABLE `usr_rand1` (\n" +
				"`a` TINYINT,\n" +
				"`b` TINYINT UNSIGNED,\n" +
				"`c` TINYINT(?),\n" +
				"`d` TINYINT(?) UNSIGNED\n" +
				")";
		q = mysql.createTable(rand1)
				.column(rand1A, TINYINT())
				.column(rand1B, TINYINT().option("UNSIGNED"))
				.column(rand1C, TINYINT(2))
				.column(rand1D, TINYINT(2).option("UNSIGNED"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());
		for (int i = 0; i < 2; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(2, entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand2 = mysql.table(Owner.USER, "rand2");
		FieldMeta rand2A = rand2.field("a");
		FieldMeta rand2B = rand2.field("b");
		FieldMeta rand2C = rand2.field("c");
		FieldMeta rand2D = rand2.field("d");
		expected = "CREATE TABLE `usr_rand2` (\n" +
				"`a` INT,\n" +
				"`b` INT UNSIGNED,\n" +
				"`c` INT(?),\n" +
				"`d` INT(?) UNSIGNED\n" +
				")";
		q = mysql.createTable(rand2)
				.column(rand2A, INT())
				.column(rand2B, INT().option("UNSIGNED"))
				.column(rand2C, INT(2))
				.column(rand2D, INT(2).option("UNSIGNED"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());
		for (int i = 0; i < 2; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(2, entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand3 = mysql.table(Owner.USER, "rand3");
		FieldMeta rand3A = rand3.field("a");
		FieldMeta rand3B = rand3.field("b");
		FieldMeta rand3C = rand3.field("c");
		FieldMeta rand3D = rand3.field("d");
		expected = "CREATE TABLE `usr_rand3` (\n" +
				"`a` BIGINT,\n" +
				"`b` BIGINT UNSIGNED,\n" +
				"`c` BIGINT(?),\n" +
				"`d` BIGINT(?) UNSIGNED\n" +
				")";
		q = mysql.createTable(rand3)
				.column(rand3A, BIGINT())
				.column(rand3B, BIGINT().option("UNSIGNED"))
				.column(rand3C, BIGINT(2))
				.column(rand3D, BIGINT(2).option("UNSIGNED"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());
		for (int i = 0; i < 2; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(2, entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand4 = mysql.table(Owner.USER, "rand4");
		FieldMeta rand4A = rand4.field("a");
		FieldMeta rand4B = rand4.field("b");
		FieldMeta rand4C = rand4.field("c");
		FieldMeta rand4D = rand4.field("d");
		FieldMeta rand4E = rand4.field("e");
		FieldMeta rand4F = rand4.field("f");
		expected = "CREATE TABLE `usr_rand4` (\n" +
				"`a` DECIMAL,\n" +
				"`b` DECIMAL(?),\n" +
				"`c` DECIMAL(?, ?),\n" +
				"`d` DECIMAL UNSIGNED,\n" +
				"`e` DECIMAL(?) UNSIGNED,\n" +
				"`f` DECIMAL(?, ?) UNSIGNED\n" +
				")";
		q = mysql.createTable(rand4)
				.column(rand4A, DECIMAL())
				.column(rand4B, DECIMAL(40))
				.column(rand4C, DECIMAL(30, 20))
				.column(rand4D, DECIMAL().option("UNSIGNED"))
				.column(rand4E, DECIMAL(65).option("UNSIGNED"))
				.column(rand4F, DECIMAL(65, 30).option("UNSIGNED"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(6, values.size());

		vals = new int[] { 40, 30, 20, 65, 65, 30 };
		for (int i = 0; i < vals.length; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(vals[i], entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta flags = mysql.table(Owner.USER, "flags");
		FieldMeta flag1 = flags.field("flag1");
		FieldMeta flag2 = flags.field("flag2");
		FieldMeta flag3 = flags.field("flag3");
		expected = "CREATE TABLE `usr_flags` (\n" +
				"`flag1` BOOLEAN,\n" +
				"`flag2` BIT(?),\n" +
				"`flag3` BIT(?)\n" +
				")";
		q = mysql.createTable(flags)
				.column(flag1, BOOLEAN())
				.column(flag2, BIT(1))
				.column(flag3, BIT(64));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(2, values.size());

		vals = new int[] { 1, 64 };
		for (int i = 0; i < vals.length; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(vals[i], entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand6 = mysql.table(Owner.USER, "rand6");
		FieldMeta rand6A = rand6.field("a");
		FieldMeta rand6B = rand6.field("b");
		FieldMeta rand6C = rand6.field("c");
		FieldMeta rand6D = rand6.field("d");
		expected = "CREATE TABLE `usr_rand6` (\n" +
				"`a` BINARY(?),\n" +
				"`b` BINARY(?),\n" +
				"`c` VARBINARY(?),\n" +
				"`d` VARBINARY(?)\n" +
				")";
		q = mysql.createTable(rand6)
				.column(rand6A, BINARY(1))
				.column(rand6B, BINARY(256))
				.column(rand6C, VARBINARY(2))
				.column(rand6D, VARBINARY(255));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		vals = new int[] { 1, 256, 2, 255 };
		for (int i = 0; i < vals.length; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(vals[i], entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand7 = mysql.table(Owner.USER, "rand7");
		FieldMeta rand7A = rand7.field("a");
		FieldMeta rand7B = rand7.field("b");
		FieldMeta rand7C = rand7.field("c");
		FieldMeta rand7D = rand7.field("d");
		FieldMeta rand7E = rand7.field("e");
		expected = "CREATE TABLE `usr_rand7` (\n" +
				"`a` TEXT,\n" +
				"`b` BLOB,\n" +
				"`c` TEXT CHARSET utf8mb4 COLLATE utf8mb4_bin,\n" +
				"`d` TEXT(?),\n" +
				"`e` BLOB(?)\n" +
				")";
		q = mysql.createTable(rand7)
				.column(rand7A, TEXT())
				.column(rand7B, BLOB())
				.column(rand7C, TEXT().option("charset", "utf8mb4", "utf8mb4_bin"))
				.column(rand7D, TEXT(12345))
				.column(rand7E, BLOB(54321));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		vals = new int[] { 12345, 54321 };
		for (int i = 0; i < vals.length; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(vals[i], entry.getValue());
		}
		/////////////////////////////////////////////
		TableMeta rand8 = mysql.table(Owner.USER, "rand8");
		FieldMeta rand8A = rand8.field("a");
		FieldMeta rand8B = rand8.field("b");
		FieldMeta rand8C = rand8.field("c");
		FieldMeta rand8D = rand8.field("d");
		expected = "CREATE TABLE `usr_rand8` (\n" +
				"`a` TINYTEXT,\n" +
				"`b` TEXT,\n" +
				"`c` MEDIUMTEXT,\n" +
				"`d` LONGTEXT\n" +
				")";
		q = mysql.createTable(rand8)
				.column(rand8A, TINYTEXT())
				.column(rand8B, TEXT())
				.column(rand8C, MEDIUMTEXT())
				.column(rand8D, LONGTEXT());
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		TableMeta rand9 = mysql.table(Owner.USER, "rand9");
		FieldMeta rand9A = rand9.field("a");
		FieldMeta rand9B = rand9.field("b");
		FieldMeta rand9C = rand9.field("c");
		FieldMeta rand9D = rand9.field("d");
		expected = "CREATE TABLE `usr_rand9` (\n" +
				"`a` TINYBLOB,\n" +
				"`b` BLOB,\n" +
				"`c` MEDIUMBLOB,\n" +
				"`d` LONGBLOB\n" +
				")";
		q = mysql.createTable(rand9)
				.column(rand9A, TINYBLOB())
				.column(rand9B, BLOB())
				.column(rand9C, MEDIUMBLOB())
				.column(rand9D, LONGBLOB());
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
		/////////////////////////////////////////////
		TableMeta rand10 = mysql.table(Owner.USER, "rand10");
		FieldMeta rand10A = rand10.field("a");
		FieldMeta rand10B = rand10.field("b");
		FieldMeta rand10C = rand10.field("c");
		FieldMeta rand10D = rand10.field("d");
		FieldMeta rand10E = rand10.field("e");
		FieldMeta rand10F = rand10.field("f");
		FieldMeta rand10G = rand10.field("g");
		FieldMeta rand10H = rand10.field("h");
		FieldMeta rand10I = rand10.field("i");
		FieldMeta rand10J = rand10.field("j");
		FieldMeta rand10K = rand10.field("k");
		FieldMeta rand10L = rand10.field("l");
		expected = "CREATE TABLE `usr_rand10` (\n" +
				"`a` DATETIME,\n" +
				"`b` TIMESTAMP,\n" +
				"`c` DATETIME(?),\n" +
				"`d` TIMESTAMP(?),\n" +
				"`e` DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
				"`f` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
				"`g` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
				"`h` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
				"`i` DATETIME(?) DEFAULT CURRENT_TIMESTAMP(?),\n" +
				"`j` TIMESTAMP(?) DEFAULT CURRENT_TIMESTAMP(?),\n" +
				"`k` DATETIME(?) DEFAULT CURRENT_TIMESTAMP(?) ON UPDATE CURRENT_TIMESTAMP(?),\n" +
				"`l` TIMESTAMP(?) DEFAULT CURRENT_TIMESTAMP(?) ON UPDATE CURRENT_TIMESTAMP(?)\n" +
				")";
		q = mysql.createTable(rand10)
				.column(rand10A, DATETIME())
				.column(rand10B, TIMESTAMP())
				.column(rand10C, DATETIME(6))
				.column(rand10D, TIMESTAMP(6))
				.column(rand10E, DATETIME().option("default", "now"))
				.column(rand10F, TIMESTAMP().option("default", "now"))
				.column(rand10G, DATETIME().option("default", "now").option("ON UPDATE CURRENT_TIMESTAMP"))
				.column(rand10H, TIMESTAMP().option("default", "now").option("on update"))
				.column(rand10I, DATETIME(6).option("default", "now"))
				.column(rand10J, TIMESTAMP(6).option("default", "now"))
				.column(rand10K, DATETIME(6).option("default", "now").option("on update"))
				.column(rand10L, TIMESTAMP(6).option("default", "now").option("on update"));
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertEquals(12, values.size());

		for (int i = 0; i < 12; i++)
		{
			entry = values.get(i);
			assertEquals(MysqlType.INT, entry.getKey());
			assertEquals(6, entry.getValue());
		}
		/////////////////////////////////////////////
		Owner lua = mysql.owner("lua");
		TableMeta points = mysql.table(lua, "points");
		FieldMeta x = points.field("x");
		FieldMeta y = points.field("y");
		expected = "CREATE TABLE `lua_points` (\n" +
				"`x` DOUBLE,\n" +
				"`y` DOUBLE,\n" +
				"PRIMARY KEY (`x`, `y`)\n" +
				") ENGINE=InnoDB";
		q = mysql.createTable(points)
				.column(x, DOUBLE())
				.column(y, DOUBLE())
				.extra("primary key", x, y)
				.extra("engine", "innodb");
		values.clear();
		txt = q.print(new HTMLText(), values);
		assertEquals(expected, txt.create());
		assertTrue(values.isEmpty());
	}

	@After
	public void tearDown() throws Exception
	{
		conn.close();
	}
}