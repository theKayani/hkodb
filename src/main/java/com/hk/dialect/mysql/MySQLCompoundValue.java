package com.hk.dialect.mysql;

import com.hk.dialect.Dialect.*;
import com.hk.str.HTMLText;

public class MySQLCompoundValue implements MySQLQueryValue, MySQLDialect.MySQLDialectOwner
{
	private final QueryValue value1, value2;
	private final QueryOperator op;

	public MySQLCompoundValue(QueryValue value1, QueryOperator op, QueryValue value2)
	{
		this.value1 = value1;
		this.op = op;
		this.value2 = value2;
		System.out.println("op = " + op);
		System.out.println("value1 = " + value1);
		System.out.println("value2 = " + value2);
	}

	@Override
	public HTMLText print(HTMLText txt)
	{
		if(value1 != null)
			value1.print(txt);

		if(op != null)
		{
			txt.wr(" ");
			op.print(txt);
			txt.wr(" ");
		}

		if(value2 != null)
			value2.print(txt);

		return txt;
	}
}
