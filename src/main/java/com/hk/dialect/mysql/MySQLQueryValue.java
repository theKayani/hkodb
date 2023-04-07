package com.hk.dialect.mysql;

import com.hk.dialect.*;

public interface MySQLQueryValue extends Query.QueryValue, MySQLDialect.MySQLDialectOwner
{
	@Override
	default Query.Condition is(Query.QueryTest test, Query.QueryValue value)
	{
		return new MySQLCondition(this, test, value);
	}

	@Override
	default Query.QueryValue op(Query.QueryOperator op, Query.QueryValue value)
	{
		return new MySQLCompoundValue(this, op, value);
	}
}
