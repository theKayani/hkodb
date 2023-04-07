package com.hk.dialect;

public interface SelectQuery extends Query
{
	SelectQuery from(TableMeta... tables);

	SelectQuery where(Condition condition);
}
