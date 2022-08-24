package com.hk.dialect;

import com.hk.str.HTMLText;
import com.hk.str.StringUtil;

import java.sql.SQLType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Dialect
{
	Query select(FieldMeta... fields);

	QueryValue value(Object value);

	TableMeta table(String owner, String name);

	default TableMeta table(Owner owner, String name)
	{
		return table(owner.prefix, name);
	}

//	static String toString(DialectOwner o)
//	{
//		return toString(o, Collections.emptyList());
//	}
//
//	static String toString(DialectOwner o, List<Map.Entry<SQLType, Object>> values)
//	{
//		return o.print(new HTMLText(), values).create();
//	}

	interface Query extends DialectOwner
	{
		Query from(TableMeta... tables);

		Query where(Condition condition);
	}

	interface QueryValue extends DialectOwner
	{
		Condition is(QueryTest test, QueryValue value);

		QueryValue op(QueryOperator op, QueryValue value);
	}

	interface QueryTest extends DialectOwner
	{
		default String getName()
		{
			return StringUtil.properCapitalize(name());
		}

		String name();
	}

	interface QueryOperator extends DialectOwner
	{
		default String getName()
		{
			return StringUtil.properCapitalize(name());
		}

		String name();
	}

	interface FieldMeta extends QueryValue
	{}

	interface TableMeta extends DialectOwner
	{
		FieldMeta field(String name);
	}

	interface Condition extends QueryValue
	{
		Condition and(Condition condition);

		Condition or(Condition condition);

		Condition not();
	}

	interface DialectOwner
	{
		Dialect dialect();

		HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values);

		default void test(DialectOwner dialectOwner)
		{
			if(dialectOwner == null)
				throw new NullPointerException("dialect owner is null");
			if(dialect() != dialectOwner.dialect())
				throw new IllegalArgumentException("SQL dialects do not match");
		}
	}

	enum Owner
	{
		SYSTEM("sys"),
		LUA("lua"),
		USER("usr"),
		TEST("tst");

		public final String prefix;

		Owner(String prefix)
		{
			this.prefix = prefix;
		}
	}
}
