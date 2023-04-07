package com.hk.dialect;

public interface TableMeta extends Dialect.DialectOwner
{
	FieldMeta field(String name);
}
