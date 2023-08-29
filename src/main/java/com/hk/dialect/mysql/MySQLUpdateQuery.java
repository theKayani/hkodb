package com.hk.dialect.mysql;

import com.hk.dialect.FieldMeta;
import com.hk.dialect.TableMeta;
import com.hk.dialect.UpdateQuery;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.*;

public class MySQLUpdateQuery implements UpdateQuery, MySQLDialect.MySQLDialectOwner
{
	final TableMeta table;
	final Map<FieldMeta, QueryValue> values;
	final Condition condition;

	MySQLUpdateQuery(TableMeta table, Map<FieldMeta, QueryValue> values, Condition condition)
	{
		this.table = table;
		if(values == null)
			values = new HashMap<>();
		else
			values = new HashMap<>(values);
		this.values = values;
		this.condition = condition;
	}

	@Override
	public UpdateQuery set(FieldMeta field, QueryValue value)
	{
		if(field == null)
			throw new NullPointerException("field is null");
		if(!field.table().equals(table))
			throw new IllegalArgumentException("field [" + field + "] does not belong to table [" + table + "]");
		if(value == null)
			throw new NullPointerException("value is null");
		values.put(field, value);
		return this;
	}

	@Override
	public UpdateQuery set(Map<FieldMeta, QueryValue> valueMap)
	{
		for (Map.Entry<FieldMeta, QueryValue> entry : valueMap.entrySet())
		{
			if(entry.getKey() == null)
				throw new NullPointerException("field is null");
			if(!entry.getKey().table().equals(table))
				throw new IllegalArgumentException("field [" + entry.getKey() + "] does not belong to table [" + table + "]");
			if(entry.getValue() == null)
				throw new NullPointerException("value for [" + entry.getKey() + "] is null");
		}
		values.putAll(valueMap);
		return this;
	}

	@Override
	public UpdateQuery setDefault(FieldMeta field)
	{
		if(field == null)
			throw new NullPointerException("field is null");
		if(!field.table().equals(table))
			throw new IllegalArgumentException("field [" + field + "] does not belong to table [" + table + "]");
		values.put(field, null);
		return this;
	}

	@Override
	public UpdateQuery setDefaults(Iterable<FieldMeta> fields)
	{
		for (FieldMeta field : fields)
		{
			if(field == null)
				throw new NullPointerException("field is null");
			if(!field.table().equals(table))
				throw new IllegalArgumentException("field [" + field + "] does not belong to table [" + table + "]");
			values.put(field, null);
		}
		return this;
	}

	@Override
	public UpdateQuery where(Condition condition)
	{
		return new MySQLUpdateQuery(table, values, condition);
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		txt.wr("UPDATE ");
		table.print(txt, values);
		txt.wr(" SET ");
		Iterator<Map.Entry<FieldMeta, QueryValue>> iterator = this.values.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<FieldMeta, QueryValue> value = iterator.next();
			value.getKey().print(txt, values);
			txt.wr(" = ");
			if(value.getValue() == null)
				txt.wr("DEFAULT");
			else
				value.getValue().print(txt, values);

			if (iterator.hasNext())
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
