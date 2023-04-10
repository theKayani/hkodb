package com.hk.dialect.mysql;

import com.hk.dialect.ColumnMeta;

import static com.mysql.cj.MysqlType.*;

// its hideous but i think it works
@SuppressWarnings("unused")
public interface MySQLColumns
{
	static ColumnMeta CHAR(int length)
	{
		return new MySQLColumnMeta(CHAR, length);
	}

	static ColumnMeta VARCHAR(int length)
	{
		return new MySQLColumnMeta(VARCHAR, length);
	}

	static ColumnMeta BINARY(int length)
	{
		return new MySQLColumnMeta(BINARY, length);
	}

	static ColumnMeta VARBINARY(int length)
	{
		return new MySQLColumnMeta(VARBINARY, length);
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

	static ColumnMeta TINYTEXT()
	{
		return new MySQLColumnMeta(TINYTEXT);
	}

	static ColumnMeta TEXT(int length)
	{
		return new MySQLColumnMeta(TEXT, length);
	}

	static ColumnMeta TEXT()
	{
		return new MySQLColumnMeta(TEXT);
	}

	static ColumnMeta MEDIUMTEXT()
	{
		return new MySQLColumnMeta(MEDIUMTEXT);
	}

	static ColumnMeta LONGTEXT()
	{
		return new MySQLColumnMeta(LONGTEXT);
	}

	static ColumnMeta TINYBLOB()
	{
		return new MySQLColumnMeta(TINYBLOB);
	}

	static ColumnMeta BLOB(int length)
	{
		return new MySQLColumnMeta(BLOB, length);
	}

	static ColumnMeta BLOB()
	{
		return new MySQLColumnMeta(BLOB);
	}

	static ColumnMeta MEDIUMBLOB()
	{
		return new MySQLColumnMeta(MEDIUMBLOB);
	}

	static ColumnMeta LONGBLOB()
	{
		return new MySQLColumnMeta(LONGBLOB);
	}

	static ColumnMeta DATE()
	{
		return new MySQLColumnMeta(DATE);
	}

	static ColumnMeta DATETIME()
	{
		return new MySQLColumnMeta(DATETIME);
	}

	static ColumnMeta DATETIME(int fsp)
	{
		return new MySQLColumnMeta(DATETIME, fsp);
	}

	static ColumnMeta TIMESTAMP()
	{
		return new MySQLColumnMeta(TIMESTAMP);
	}

	static ColumnMeta TIMESTAMP(int fsp)
	{
		return new MySQLColumnMeta(TIMESTAMP, fsp);
	}
}