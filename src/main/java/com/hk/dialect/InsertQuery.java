package com.hk.dialect;

/*	NOTE:
either insert row by row
or by bulk, not both

NOTE:
if there are values missing between fields within
rows, they should revert to the default value for
that field, which is SQL implementation dependant */
public interface InsertQuery extends Query
{
	// bulk
	InsertQuery cols(FieldMeta... fields);

	// bulk
	InsertQuery values(QueryValue[]... values);

	// individual row
	InsertRow row();
}
