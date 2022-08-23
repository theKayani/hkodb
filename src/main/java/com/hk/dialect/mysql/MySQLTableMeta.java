package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

public class MySQLTableMeta implements TableMeta, MySQLDialect.MySQLDialectOwner
{
	final String tableName;

	public MySQLTableMeta(Owner owner, String name)
	{
		this.tableName = '`' + owner.getPrefix() + '_' + name + '`';
	}

	@Override
	public FieldMeta field(String name)
	{
		return new MySQLFieldMeta(this, name);
	}

	@Override
	public HTMLText print(HTMLText txt)
	{
		return txt.wr(tableName);
	}
}
