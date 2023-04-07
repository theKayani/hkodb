package com.hk.dialect;

import com.hk.str.HTMLText;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
public abstract class Dialect
{
	//// CRUD OPERATIONS
	// Create
	public abstract InsertQuery insert(TableMeta table);

	// Read
	public abstract SelectQuery select(FieldMeta... fields);

	// Update
	public abstract UpdateQuery update(TableMeta table);

	// Delete
	public abstract DeleteQuery delete(TableMeta table);

	//// TABLE OPERATIONS

	public abstract CreateTableQuery createTable(TableMeta table);

	////

	public abstract Query.QueryValue value(Object value);

	public abstract TableMeta table(Owner owner, String name);

	public Owner owner(String name)
	{
		return Owner.as(name);
	}

	public abstract Query.QueryTest[] getQueryTests();

	public abstract Query.QueryOperator[] getQueryOperators();

	public static PreparedStatement prepareStatement(Connection conn, Query query) throws SQLException
	{
		List<Map.Entry<SQLType, Object>> values = new ArrayList<>();
		HTMLText txt = new HTMLText();
		query.print(txt, values);
		PreparedStatement statement = conn.prepareStatement(txt.create());

		Map.Entry<SQLType, Object> entry;
		for (int i = 0; i < values.size(); i++)
		{
			entry = values.get(i);
			statement.setObject(i + 1, entry.getValue(), entry.getKey());
		}

		return statement;
	}

	public static String toString(DialectOwner o)
	{
		return o.print(new HTMLText(), Collections.emptyList()).create();
	}

	public interface DialectOwner
	{
		Dialect dialect();

		HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values);

		default void test(DialectOwner dialectOwner)
		{
			if (dialectOwner == null)
				throw new NullPointerException("dialect owner is null");
			if (dialect() != dialectOwner.dialect())
				throw new IllegalArgumentException("SQL dialects do not match");
		}
	}
}