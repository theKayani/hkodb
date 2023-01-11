package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLCondition implements MySQLQueryValue, Condition, MySQLDialect.MySQLDialectOwner
{
	private final QueryValue value1, value2;
	private final QueryTest test;

	public MySQLCondition(QueryValue value1, QueryTest test, QueryValue value2)
	{
		this.value1 = value1;
		this.test = test;
		this.value2 = value2;
	}

	@Override
	public Condition and(Condition condition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Condition or(Condition condition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Condition not()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(value1 != null)
			value1.print(txt, values);

		if(test != null)
		{
			if(value1 != null)
				txt.wr(" ");
			test.print(txt, values);
			if(value2 != null)
				txt.wr(" ");
		}

		if(value2 != null)
			value2.print(txt, values);

		return txt;
	}
}
