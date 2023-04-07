package com.hk.dialect;

public interface CreateTableQuery extends Query
{
	CreateTableQuery column(FieldMeta field, ColumnMeta datatype);

	CreateTableQuery extra(String key, Object... values);
}
