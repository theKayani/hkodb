package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLFieldMeta implements MySQLQueryValue, FieldMeta, MySQLDialect.MySQLDialectOwner
{
	final String fieldName;

	public MySQLFieldMeta(MySQLTableMeta table, String name)
	{
		this.fieldName = "`" + table.tableName + "`.`" + name + "`";
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		return txt.wr(fieldName);
	}
}
