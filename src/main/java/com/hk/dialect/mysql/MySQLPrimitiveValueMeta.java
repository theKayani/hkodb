package com.hk.dialect.mysql;

import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;

import java.sql.SQLType;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

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
		values.add(new AbstractMap.SimpleImmutableEntry<>(type, value));
		return txt.wr("?");
	}
}
