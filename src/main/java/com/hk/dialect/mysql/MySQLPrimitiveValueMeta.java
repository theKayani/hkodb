package com.hk.dialect.mysql;

import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;

import java.sql.SQLType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MySQLPrimitiveValueMeta implements MySQLQueryValue, MySQLDialect.MySQLDialectOwner
{
	private final Object value;
	private final MysqlType type;

	MySQLPrimitiveValueMeta(Object value, MysqlType type)
	{
		this.value = value;
		this.type = type;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		Objects.requireNonNull(values);
		values.add(new AbstractMap.SimpleImmutableEntry<>(type, value));
		return txt.wr("?");
	}

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
