package com.hk.dialect.mysql;

import com.hk.str.HTMLText;
import com.mysql.cj.MysqlType;

import java.sql.SQLType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MySQLPrimitiveValueMeta implements MySQLQueryValue, MySQLDialect.MySQLDialectOwner
{
	private final Object value;
	private final MysqlType type;

	MySQLPrimitiveValueMeta(Object value, MysqlType type)
	{
		this.value = value;
		this.type = type;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(values == null)
		{
			switch (type)
			{
				case NULL:
					return txt.wr("NULL");
				case BOOLEAN:
					return txt.wr((boolean) value ? "TRUE" : "FALSE");
				case BIT:
					return txt.wr((boolean) value ? "1" : "0");
				case DECIMAL:
				case DECIMAL_UNSIGNED:
				case FLOAT:
				case FLOAT_UNSIGNED:
				case DOUBLE:
				case DOUBLE_UNSIGNED:
					return txt.wr(Double.toString((double) value));
				case TINYINT:
				case TINYINT_UNSIGNED:
				case SMALLINT:
				case SMALLINT_UNSIGNED:
				case INT:
				case INT_UNSIGNED:
				case MEDIUMINT:
				case MEDIUMINT_UNSIGNED:
				case BIGINT:
				case BIGINT_UNSIGNED:
					return txt.wr(Long.toString((long) value));
				case DATE:
				case TIME:
				case DATETIME:
				case YEAR:
				case TIMESTAMP:
					return txt.wr(dateFormat.format((Date) value));
				case VARCHAR:
				case TINYTEXT:
				case MEDIUMTEXT:
				case LONGTEXT:
				case TEXT:
				case CHAR:
					throw new RuntimeException("TODO");
				case JSON:
				case ENUM:
				case SET:
				case TINYBLOB:
				case BLOB:
				case MEDIUMBLOB:
				case LONGBLOB:
				case VARBINARY:
				case BINARY:
				case GEOMETRY:
				case UNKNOWN:
					throw new UnsupportedOperationException("Unsupported value: " + type);
				default:
					throw new IllegalStateException("Unexpected value: " + type);
			}
		}
		else
		{
			values.add(new AbstractMap.SimpleImmutableEntry<>(type, value));
			return txt.wr("?");
		}
	}

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
