package com.hk.dialect;

import java.sql.SQLType;

public abstract class ColumnMeta implements Dialect.DialectOwner
{
	protected SQLType type;
	protected Object[] args;

	protected ColumnMeta(SQLType type, Object... args)
	{
		this.type = type;
		this.args = args;
	}

	public ColumnMeta option(String name)
	{
		return option(name, null, true);
	}

	public ColumnMeta option(String name, Object value)
	{
		return option(name, null, value);
	}

	public abstract ColumnMeta option(String name, Object index, Object value);
}
