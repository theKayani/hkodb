package com.hk.dialect.mysql;

import com.hk.dialect.FieldMeta;
import com.hk.dialect.SelectQuery;
import com.hk.dialect.TableMeta;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLSelectQuery implements SelectQuery, MySQLDialect.MySQLDialectOwner
{
	final FieldMeta[] fields;
	final TableMeta[] tables;
	final Condition condition;

	MySQLSelectQuery(FieldMeta[] fields, TableMeta[] tables, Condition condition)
	{
		this.fields = fields;
		this.tables = tables;
		this.condition = condition;
	}

	@Override
	public SelectQuery from(TableMeta... tables)
	{
		return new MySQLSelectQuery(fields, tables, null);
	}

	@Override
	public SelectQuery where(Condition condition)
	{
		return new MySQLSelectQuery(fields, tables, condition);
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		txt.wr("SELECT ");
		if (fields != null && fields.length > 0)
		{
			for (int i = 0; i < fields.length; i++)
			{
				fields[i].print(txt, values);

				if(i < fields.length - 1)
					txt.wr(", ");
			}
		}
		else
			txt.wr("*");

		txt.wr(" FROM ");
		for (int i = 0; i < tables.length; i++)
		{
			tables[i].print(txt, values);

			if(i < tables.length - 1)
				txt.wr(", ");
		}
		if(condition != null)
		{
			txt.wr(" WHERE ");
			condition.print(txt, values);
		}
		return txt;
	}
}