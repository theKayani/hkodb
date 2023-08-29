package com.hk.dialect;

import java.util.Map;

public interface UpdateQuery extends Query
{
	UpdateQuery set(FieldMeta field, QueryValue value);

	UpdateQuery set(Map<FieldMeta, QueryValue> valueMap);

	UpdateQuery setDefault(FieldMeta field);

	UpdateQuery setDefaults(Iterable<FieldMeta> fields);

	UpdateQuery where(Condition condition);
}
