package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

public class MySQLFieldMeta implements MySQLQueryValue, FieldMeta, MySQLDialect.MySQLDialectOwner
{
	final String fieldName;

	public MySQLFieldMeta(MySQLTableMeta table, String name)
	{
		this.fieldName = table.tableName + ".`" + name + "`";
	}

	@Override
	public boolean isValue()
	{
		return false;
	}

	@Override
	public HTMLText print(HTMLText txt)
	{
		return txt.wr(fieldName);
	}
}
