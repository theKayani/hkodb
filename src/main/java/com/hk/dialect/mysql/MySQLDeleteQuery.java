package com.hk.dialect.mysql;

import com.hk.dialect.DeleteQuery;
import com.hk.dialect.TableMeta;
import com.hk.dialect.UpdateQuery;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public class MySQLDeleteQuery implements DeleteQuery, MySQLDialect.MySQLDialectOwner
{
	final TableMeta table;
	final Condition condition;

	MySQLDeleteQuery(TableMeta table, Condition condition)
	{
		this.table = table;
		this.condition = condition;
	}

	@Override
	public DeleteQuery where(Condition condition)
	{
		if(condition == null)
			throw new NullPointerException("condition is null");
		return new MySQLDeleteQuery(table, condition);
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		txt.wr("DELETE FROM ");
		table.print(txt, values);
		if(condition != null)
		{
			txt.wr(" WHERE ");
			condition.print(txt, values);
		}
		return txt;
	}
}
