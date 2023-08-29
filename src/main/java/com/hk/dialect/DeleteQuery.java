package com.hk.dialect;

public interface DeleteQuery extends Query
{
	DeleteQuery where(Condition condition);
}
