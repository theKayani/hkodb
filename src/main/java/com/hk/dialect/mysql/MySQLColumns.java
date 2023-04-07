package com.hk.dialect.mysql;

import com.hk.dialect.ColumnMeta;

import static com.mysql.cj.MysqlType.*;

// its hideous but i think it works
@SuppressWarnings("unused")
public interface MySQLColumns
{
	static ColumnMeta VARCHAR(int length)
	{
		return new MySQLColumnMeta(VARCHAR, length);
	}

	static ColumnMeta CHAR(int length)
	{
		return new MySQLColumnMeta(CHAR, length);
	}

	static ColumnMeta FLOAT()
	{
		return new MySQLColumnMeta(FLOAT);
	}

	static ColumnMeta DOUBLE()
	{
		return new MySQLColumnMeta(DOUBLE);
	}

	static ColumnMeta DECIMAL()
	{
		return new MySQLColumnMeta(DECIMAL);
	}

	static ColumnMeta DECIMAL(int precision)
	{
		return new MySQLColumnMeta(DECIMAL, precision);
	}

	static ColumnMeta DECIMAL(int precision, int scale)
	{
		return new MySQLColumnMeta(DECIMAL, precision, scale);
	}

	static ColumnMeta BOOLEAN()
	{
		return new MySQLColumnMeta(BOOLEAN);
	}

	static ColumnMeta BIT(int length)
	{
		return new MySQLColumnMeta(BIT, length);
	}

	static ColumnMeta TINYINT()
	{
		return new MySQLColumnMeta(TINYINT);
	}

	static ColumnMeta TINYINT(int digits)
	{
		return new MySQLColumnMeta(TINYINT, digits);
	}

	static ColumnMeta SMALLINT()
	{
		return new MySQLColumnMeta(SMALLINT);
	}

	static ColumnMeta SMALLINT(int digits)
	{
		return new MySQLColumnMeta(SMALLINT, digits);
	}

	static ColumnMeta MEDIUMINT()
	{
		return new MySQLColumnMeta(MEDIUMINT);
	}

	static ColumnMeta MEDIUMINT(int digits)
	{
		return new MySQLColumnMeta(MEDIUMINT, digits);
	}

	static ColumnMeta INT()
	{
		return new MySQLColumnMeta(INT);
	}

	static ColumnMeta INT(int digits)
	{
		return new MySQLColumnMeta(INT, digits);
	}

	static ColumnMeta BIGINT()
	{
		return new MySQLColumnMeta(BIGINT);
	}

	static ColumnMeta BIGINT(int digits)
	{
		return new MySQLColumnMeta(BIGINT, digits);
	}
}
