package com.hk.dialect.mysql;

import com.hk.dialect.Dialect;
import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLType;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MySQLDialect implements Dialect
{
	@Override
	public Query select(FieldMeta... fields)
	{
		return new MySQLQuery(fields, null, null);
	}

	@Override
	public QueryValue value(Object value)
	{
		if(value == null)
			return new MySQLPrimitiveValueMeta(null, MysqlType.NULL);
		else if(value instanceof Double || value instanceof Float || value instanceof BigDecimal)
			return new MySQLPrimitiveValueMeta(value, MysqlType.DOUBLE);
		else if(value instanceof Long || value instanceof Integer ||
				value instanceof Short || value instanceof Byte || value instanceof BigInteger)
			return new MySQLPrimitiveValueMeta(value, MysqlType.INT);
		else if(value instanceof CharSequence)
			return new MySQLPrimitiveValueMeta(value, MysqlType.VARCHAR);
		else if(value instanceof Boolean)
			return new MySQLPrimitiveValueMeta(value, MysqlType.BOOLEAN);
		else if(value instanceof Date)
			return new MySQLPrimitiveValueMeta(value, MysqlType.DATE);
		else
			throw new UnsupportedOperationException("Cannot be turned into MySQL primitive: " + value);
	}

	@Override
	public TableMeta table(String owner, String name)
	{
		return new MySQLTableMeta(owner, name);
	}

	private static MySQLDialect instance;

	public static MySQLDialect getInstance()
	{
		if(instance == null)
			instance = new MySQLDialect();

		return instance;
	}

	interface MySQLDialectOwner extends DialectOwner
	{
		@Override
		default Dialect dialect() {
			return instance;
		}
	}

	public enum MySQLQueryTest implements QueryTest
	{
		EQUALS,
		NOT_EQUALS,
		LESS_THAN,
		LESS_EQ_THAN,
		GRTR_THAN,
		GRTR_EQ_THAN,
		LIKE;

		@Override
		public Dialect dialect()
		{
			return instance;
		}

		@Override
		public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
		{
			switch(this)
			{
				case EQUALS:
					return txt.wr("=");
				case NOT_EQUALS:
					return txt.wr("!=");
				case LESS_THAN:
					return txt.wr("<");
				case LESS_EQ_THAN:
					return txt.wr("<=");
				case GRTR_THAN:
					return txt.wr(">");
				case GRTR_EQ_THAN:
					return txt.wr(">=");
				case LIKE:
					return txt.wr("LIKE");
				default:
					throw new UnsupportedOperationException();
			}
		}
	}

	public enum MySQLQueryOperator implements QueryOperator
	{
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE,
		MODULO,
		BAND,
		BOR,
		BXOR;

		@Override
		public Dialect dialect()
		{
			return instance;
		}

		@Override
		public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
		{
			switch(this)
			{
				case ADD:
					return txt.wr("+");
				case SUBTRACT:
					return txt.wr("-");
				case MULTIPLY:
					return txt.wr("*");
				case DIVIDE:
					return txt.wr("/");
				case MODULO:
					return txt.wr("%");
				case BAND:
				case BOR:
				case BXOR:
				default:
					throw new UnsupportedOperationException();
			}
		}
	}

	private MySQLDialect()
	{}
}
