package com.hk.dialect;

import com.hk.str.StringUtil;

public interface Query extends Dialect.DialectOwner
{
	interface Condition extends QueryValue
	{
		Condition and(Condition condition);

		Condition or(Condition condition);

		Condition not();

		Condition group();
	}

	interface QueryOperator extends Dialect.DialectOwner
	{
		default String getName()
		{
			return StringUtil.properCapitalize(name());
		}

		String name();
	}

	interface QueryTest extends Dialect.DialectOwner
	{
		default String getName()
		{
			return StringUtil.properCapitalize(name());
		}

		String name();
	}

	interface QueryValue extends Dialect.DialectOwner
	{
		Condition is(QueryTest test, QueryValue value);

		QueryValue op(QueryOperator op, QueryValue value);

		QueryValue group();
	}
}