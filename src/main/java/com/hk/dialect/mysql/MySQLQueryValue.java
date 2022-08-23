package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

public interface MySQLQueryValue extends QueryValue, MySQLDialect.MySQLDialectOwner
{
	@Override
	default Condition is(QueryTest test, QueryValue value)
	{
		return new MySQLCondition(this, test, value);
	}

	@Override
	default QueryValue op(QueryOperator op, QueryValue value)
	{
		return new MySQLCompoundValue(this, op, value);
	}
}
