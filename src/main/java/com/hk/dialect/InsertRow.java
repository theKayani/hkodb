package com.hk.dialect;

public interface InsertRow
{
	InsertRow set(FieldMeta field, Query.QueryValue value);

	InsertRow removeValue(FieldMeta field);

	InsertRow clear();

	InsertQuery insert();
}
