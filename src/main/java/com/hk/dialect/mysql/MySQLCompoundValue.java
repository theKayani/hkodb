package com.hk.dialect.mysql;

import com.hk.dialect.Query;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLCompoundValue implements MySQLQueryValue, MySQLDialect.MySQLDialectOwner
{
	private final Query.QueryValue value1, value2;
	private final Query.QueryOperator op;

	public MySQLCompoundValue(Query.QueryValue value1, Query.QueryOperator op, Query.QueryValue value2)
	{
		this.value1 = value1;
		this.op = op;
		this.value2 = value2;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(value1 != null)
			value1.print(txt, values);

		if(op != null)
		{
			if(value1 != null)
				txt.wr(" ");
			op.print(txt, values);
			if(value2 != null)
				txt.wr(" ");
		}

		if(value2 != null)
			value2.print(txt, values);

		return txt;
	}
}
