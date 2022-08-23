package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

public class MySQLPrimitiveValueMeta implements MySQLQueryValue, MySQLDialect.MySQLDialectOwner
{
	private final Object value;
	private final PrimitiveType type;

	MySQLPrimitiveValueMeta(Object value, PrimitiveType type)
	{
		this.value = value;
		this.type = type;
	}

	@Override
	public HTMLText print(HTMLText txt)
	{
		switch(type)
		{
			case INTEGER:
				return txt.wr(String.valueOf((int) value));
			case NULL:
			case DOUBLE:
			case STRING:
			case BOOLEAN:
			case DATE:
			default:
				throw new UnsupportedOperationException();
		}
	}

	enum PrimitiveType
	{
		NULL, DOUBLE, INTEGER, STRING, BOOLEAN, DATE;
	}
}
