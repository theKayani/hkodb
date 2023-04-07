package com.hk.dialect.mysql;

import com.hk.dialect.FieldMeta;
import com.hk.str.HTMLText;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MySQLFieldMeta implements MySQLQueryValue, FieldMeta, MySQLDialect.MySQLDialectOwner, Comparable<MySQLFieldMeta>
{
	final MySQLTableMeta table;
	final String fieldName;

	public MySQLFieldMeta(MySQLTableMeta table, String name)
	{
		this.table = table;
		this.fieldName = "`" + name + "`";
	}

	@Override
	public MySQLTableMeta table()
	{
		return table;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values, boolean qualified)
	{
		if(qualified)
			txt.wr("`").wr(table.tableName).wr("`.");
		return txt.wr(fieldName);
	}

	@Override
	public int compareTo(@NotNull MySQLFieldMeta o)
	{
		int i = table.compareTo(o.table);
		return i == 0 ? fieldName.compareTo(o.fieldName) : i;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return fieldName.equals(((MySQLFieldMeta) o).fieldName);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(fieldName);
	}
}
