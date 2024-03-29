package com.hk.dialect.mysql;

import com.hk.dialect.Query;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLCondition implements MySQLQueryValue, Query.Condition, MySQLDialect.MySQLDialectOwner
{
	private final Query.QueryValue value1, value2;
	private final Query.QueryTest test;
	private boolean group = false;

	public MySQLCondition(Query.QueryValue value1, Query.QueryTest test, Query.QueryValue value2)
	{
		this.value1 = value1;
		this.test = test;
		this.value2 = value2;
	}

	@Override
	public Query.Condition and(Query.Condition condition)
	{
		return new MySQLCondition(this, MySQLDialect.MySQLQueryTest.AND, condition);
	}

	@Override
	public Query.Condition or(Query.Condition condition)
	{
		return new MySQLCondition(this, MySQLDialect.MySQLQueryTest.OR, condition);
	}

	@Override
	public Query.Condition not()
	{
		return new MySQLCondition(null, MySQLDialect.MySQLQueryTest.NOT, this.group());
	}

	@Override
	public Query.Condition group()
	{
		group = true;
		return this;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(group)
			txt.wr("(");
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
		if(group)
			txt.wr(")");

		return txt;
	}
}
