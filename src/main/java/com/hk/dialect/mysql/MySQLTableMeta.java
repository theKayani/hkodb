package com.hk.dialect.mysql;

import com.hk.dialect.FieldMeta;
import com.hk.dialect.Owner;
import com.hk.dialect.TableMeta;
import com.hk.str.HTMLText;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MySQLTableMeta implements TableMeta, MySQLDialect.MySQLDialectOwner, Comparable<MySQLTableMeta>
{
	final String tableName;

	public MySQLTableMeta(Owner owner, String name)
	{
		this.tableName = owner.prefix + '_' + name;
	}

	@Override
	public FieldMeta field(String name)
	{
		return new MySQLFieldMeta(this, name);
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		return txt.wr("`").wr(tableName).wr("`");
	}

	@Override
	public int compareTo(@NotNull MySQLTableMeta o)
	{
		return tableName.compareTo(o.tableName);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return tableName.equals(((MySQLTableMeta) o).tableName);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(tableName);
	}
}
