package com.hk.dialect;

import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.List;
import java.util.Map;

public interface FieldMeta extends Query.QueryValue
{
	TableMeta table();

	@Override
	default HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		return print(txt, values, true);
	}

	HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values, boolean qualified);
}
