package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLTableMeta implements TableMeta, MySQLDialect.MySQLDialectOwner
{
	final String tableName;

	public MySQLTableMeta(String owner, String name)
	{
		this.tableName = owner + '_' + name;
	}

	@Override
	public FieldMeta field(String name)
	{
		return new MySQLFieldMeta(this, name);
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		return txt.wr("`").wr(tableName).wr("`");
	}
}
