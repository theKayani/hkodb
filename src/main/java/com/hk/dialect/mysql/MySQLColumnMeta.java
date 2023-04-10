package com.hk.dialect.mysql;

import com.hk.dialect.ColumnMeta;
import com.hk.str.HTMLText;
import com.hk.util.KeyValue;
import com.mysql.cj.MysqlType;

import java.sql.SQLType;
import java.util.*;

public class MySQLColumnMeta extends ColumnMeta implements MySQLDialect.MySQLDialectOwner
{
	private Boolean notNull, autoIncrement, ctOnUpdate;
	private Map.Entry<MysqlType, Object> defaultValue;
	private KeyValue<String> characterSet;

	public MySQLColumnMeta(MysqlType type, Object... args)
	{
		super(type, args);
		// just verify args for use in 'print'
		switch (type)
		{
			case TEXT:
			case BLOB:
				if(args.length == 0)
					break;
			case BINARY:
			case VARBINARY:
			case CHAR:
			case VARCHAR:
				if(args.length == 0)
					throw new IllegalArgumentException(type + " type needs a length argument");
				if(!(args[0] instanceof Integer))
					throw new IllegalArgumentException("first argument (length) integer expected");
				if(((Integer) args[0]) < 1 || ((Integer) args[0]) > 65535)
					throw new IllegalArgumentException("first argument (length) out of bounds");
				if(args.length > 1)
					throw new IllegalArgumentException("expected only one argument, not " + Arrays.toString(args));
				break;
			case BIT:
				if(args.length == 0)
					throw new IllegalArgumentException("BIT type needs a length argument");
				if(!(args[0] instanceof Integer))
					throw new IllegalArgumentException("first argument (length) integer expected");
				if(((Integer) args[0]) < 1 || ((Integer) args[0]) > 64)
					throw new IllegalArgumentException("first argument (length) out of bounds");
				if(args.length > 1)
					throw new IllegalArgumentException("expected only one argument, not " + Arrays.toString(args));
				break;
			case DATETIME:
			case TIMESTAMP:
			case TIME:
				if(args.length == 1)
				{
					if(!(args[0] instanceof Integer))
						throw new IllegalArgumentException("first argument (fsp) integer expected");
					if(((Integer) args[0]) < 0 || ((Integer) args[0]) > 6)
						throw new IllegalArgumentException("first argument (fsp) out of bounds");
				}
				else if(args.length > 1)
					throw new IllegalArgumentException("expected none or only one argument, not " + Arrays.toString(args));
				break;
			case DATE:
			case TINYTEXT:
			case TINYBLOB:
			case MEDIUMTEXT:
			case MEDIUMBLOB:
			case LONGTEXT:
			case LONGBLOB:
			case BOOLEAN:
			case FLOAT:
			case DOUBLE:
				if(args.length > 0)
					throw new IllegalArgumentException(type + "expected no argument, not " + Arrays.toString(args));
				break;
			case DECIMAL:
				if(args.length == 1 || args.length == 2)
				{
					if(!(args[0] instanceof Integer))
						throw new IllegalArgumentException("first argument (precision) integer expected");
					if(((Integer) args[0]) < 1 || ((Integer) args[0]) > 65)
						throw new IllegalArgumentException("first argument (precision) out of bounds");
					if(args.length == 2)
					{
						if(!(args[1] instanceof Integer))
							throw new IllegalArgumentException("second argument (scale) integer expected");
						if(((Integer) args[1]) < 1 || ((Integer) args[1]) > 30)
							throw new IllegalArgumentException("second argument (scale) out of bounds");
					}
				}
				else if(args.length != 0)
					throw new IllegalArgumentException("expected none, one, or two arguments, not " + Arrays.toString(args));
				break;
			case TINYINT:
			case SMALLINT:
			case MEDIUMINT:
			case INT:
			case BIGINT:
				if(args.length == 1)
				{
					if(!(args[0] instanceof Integer))
						throw new IllegalArgumentException("first argument (digits) integer expected");
					if(((Integer) args[0]) < 1)
						throw new IllegalArgumentException("first argument (digits) must be positive");
				}
				else if(args.length != 0)
					throw new IllegalArgumentException("expected no argument or amount of digits, not " + Arrays.toString(args));
				break;
			default:
				throw new Error("TODO");
		}
	}

	@Override
	public ColumnMeta option(String name, Object index, Object value)
	{
		switch (name.toLowerCase(Locale.ROOT))
		{
			case "null":
				if(index != null)
					throw new IllegalArgumentException("unexpected index: " + index);
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("unexpected value: " + value);

				notNull = !((Boolean) value);
				break;
			case "not null":
			case "notnull":
				if(index != null)
					throw new IllegalArgumentException("unexpected index: " + index);
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("unexpected value: " + value);

				notNull = (Boolean) value;
				break;
			case "def":
			case "default":
				switch ((MysqlType) type)
				{
					case TINYTEXT:
					case TINYBLOB:
					case MEDIUMTEXT:
					case MEDIUMBLOB:
					case TEXT:
					case BLOB:
					case LONGTEXT:
					case LONGBLOB:
						throw new IllegalArgumentException(type + " type cannot have a default value");
					default:
						break;
				}
				if((type == MysqlType.TIMESTAMP || type == MysqlType.DATETIME) && index == null && value instanceof String)
				{
					switch (((String) value).toLowerCase(Locale.ROOT))
					{
						case "current timestamp":
						case "current_timestamp":
						case "current date":
						case "current_date":
						case "now":
							defaultValue = new AbstractMap.SimpleImmutableEntry<>(null, "CURRENT_TIMESTAMP");
							break;
						default:
							throw new IllegalArgumentException("unexpected default value for timestamp/datetime: " + value);
					}
				}
				else
				{
					if(!(index instanceof MysqlType))
						throw new IllegalArgumentException("expected index to be of MysqlType not " + index);

					defaultValue = new AbstractMap.SimpleImmutableEntry<>((MysqlType) index, value);
				}
				break;
			case "auto increment":
			case "auto_increment":
			case "autoincrement":
			case "auto":
				switch ((MysqlType) type)
				{
					case FLOAT:
					case FLOAT_UNSIGNED:
					case DOUBLE:
					case DOUBLE_UNSIGNED:
					case TINYINT:
					case TINYINT_UNSIGNED:
					case SMALLINT:
					case SMALLINT_UNSIGNED:
					case MEDIUMINT:
					case MEDIUMINT_UNSIGNED:
					case INT:
					case INT_UNSIGNED:
					case BIGINT:
					case BIGINT_UNSIGNED:
						break;
					default:
						throw new IllegalArgumentException("auto_increment does not apply to " + type);
				}
				if(index != null)
					throw new IllegalArgumentException("unexpected index: " + index + ", should be null");
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("expected boolean value, got " + value);

				autoIncrement = (Boolean) value;
				break;
			case "on update current_timestamp":
			case "current_timestamp":
			case "on update":
			case "on_update":
				switch ((MysqlType) type)
				{
					case TIMESTAMP:
					case DATETIME:
						break;
					default:
						throw new IllegalArgumentException("setting current timestamp on update does not apply to " + type);
				}
				if(index != null)
					throw new IllegalArgumentException("unexpected index: " + index + ", should be null");
				if(!(value instanceof Boolean))
					throw new IllegalArgumentException("expected boolean value, got " + value);

				ctOnUpdate = (Boolean) value;
				break;
			case "character set":
			case "character_set":
			case "characterset":
			case "char set":
			case "char_set":
			case "charset":
			case "collate":
			case "collation":
				switch ((MysqlType) type)
				{
					case VARCHAR:
					case CHAR:
					case ENUM:
					case SET:
					case TINYTEXT:
					case MEDIUMTEXT:
					case TEXT:
					case LONGTEXT:
						break;
					default:
						throw new IllegalArgumentException("character_set does not apply to " + type);
				}
				if(index == null && value == null)
					throw new NullPointerException("expected string index (character set) or value (collation), both null");
				if(index != null && !(index instanceof String))
					throw new IllegalArgumentException("expected string index (character set), got " + index);
				else if (index != null && !((String) index).matches("\\w+"))
					throw new IllegalArgumentException("possible SQL injection? invalid character set: " + index);
				if(value != null && !(value instanceof String))
					throw new IllegalArgumentException("expected string value (collation), got " + value);
				else if (value != null && !((String) value).matches("\\w+"))
					throw new IllegalArgumentException("possible SQL injection? invalid collation: " + value);

				characterSet = new KeyValue<>((String) index, (String) value);
				break;
			case "signed":
			case "unsigned":
				switch ((MysqlType) type)
				{
					case DECIMAL:
					case DOUBLE:
					case FLOAT:
					case TINYINT:
					case SMALLINT:
					case MEDIUMINT:
					case INT:
					case BIGINT:
						if(name.equalsIgnoreCase("unsigned"))
						{
							type = MysqlType.valueOf(((MysqlType) type).name() + "_UNSIGNED");
							break;
						}
					case DECIMAL_UNSIGNED:
					case DOUBLE_UNSIGNED:
					case FLOAT_UNSIGNED:
					case TINYINT_UNSIGNED:
					case SMALLINT_UNSIGNED:
					case MEDIUMINT_UNSIGNED:
					case INT_UNSIGNED:
					case BIGINT_UNSIGNED:
						if(name.equalsIgnoreCase("signed"))
						{
							type = MysqlType.valueOf(((MysqlType) type).name().substring(0, ((MysqlType) type).name().length() - 9));
							break;
						}
					default:
						throw new IllegalArgumentException(name + " does not apply to " + type);
				}
				break;
			default:
				throw new IllegalArgumentException("unknown option: " + name);
		}
		return this;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		switch ((MysqlType) type)
		{
			case DATE:
			case TINYTEXT:
			case TINYBLOB:
			case MEDIUMTEXT:
			case MEDIUMBLOB:
			case LONGTEXT:
			case LONGBLOB:
			case BOOLEAN:
				txt.wr(((MysqlType) type).name());
				break;
			case DATETIME:
			case TIMESTAMP:
			case TIME:
			case TEXT:
			case BLOB:
				if(args.length == 0)
				{
					txt.wr(((MysqlType) type).name());
					break;
				}
			case BINARY:
			case VARBINARY:
			case CHAR:
			case VARCHAR:
			case BIT:
				txt.wr(((MysqlType) type).name());
				values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[0]));
				txt.wr("(?)");
				break;
			case DECIMAL:
			case DECIMAL_UNSIGNED:
			case FLOAT:
			case FLOAT_UNSIGNED:
			case DOUBLE:
			case DOUBLE_UNSIGNED:
			case TINYINT:
			case TINYINT_UNSIGNED:
			case SMALLINT:
			case SMALLINT_UNSIGNED:
			case MEDIUMINT:
			case MEDIUMINT_UNSIGNED:
			case INT:
			case INT_UNSIGNED:
			case BIGINT:
			case BIGINT_UNSIGNED:
				String name = ((MysqlType) type).name();
				if(((MysqlType) type).isAllowed(MysqlType.FIELD_FLAG_UNSIGNED))
					name = name.substring(0, name.length() - 9);
				txt.wr(name);
				if(args.length == 1)
				{
					values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[0]));
					txt.wr("(?)");
				}
				else if(args.length == 2)
				{
					values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[0]));
					values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[1]));
					txt.wr("(?, ?)");
				}
				else if(args.length != 0)
					throw new UnsupportedOperationException("invalid arguments for " + type + ": " + Arrays.toString(args));
				if(((MysqlType) type).isAllowed(MysqlType.FIELD_FLAG_UNSIGNED))
					txt.wr(" UNSIGNED");
				break;
			default:
				throw new Error("TODO");
		}
		/*
		 * POSSIBLE SQL INJECTION BECAUSE THE CHARACTER SET
		 * AND COLLATION AREN'T SANITIZED
		 *
		 * other than the pattern matching in option(String, Object, Object)
		 */
		if(characterSet != null)
		{
			String charset = characterSet.getKey();
			if(charset != null)
			{
//				from the mysql documentation:
//				CHARSET is a synonym for CHARACTER SET.
				txt.wr(" CHARSET ").wr(charset);
			}
			String collation = characterSet.getValue();
			if(collation != null)
				txt.wr(" COLLATE ").wr(collation);
		}
		if(notNull != null && notNull)
			txt.wr(" NOT NULL");
		if(defaultValue != null)
		{
			if (defaultValue.getKey() != null || !"CURRENT_TIMESTAMP".equals(defaultValue.getValue()))
			{
				values.add(new AbstractMap.SimpleImmutableEntry<>(defaultValue.getKey(), defaultValue.getValue()));
				txt.wr(" DEFAULT ?");
			}
			else
				txt.wr(" DEFAULT CURRENT_TIMESTAMP");
			if(args.length > 0)
			{
				values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[0]));
				txt.wr("(?)");
			}
		}
		if(ctOnUpdate != null && ctOnUpdate)
		{
			txt.wr(" ON UPDATE CURRENT_TIMESTAMP");
			if(args.length > 0)
			{
				values.add(new AbstractMap.SimpleImmutableEntry<>(MysqlType.INT, args[0]));
				txt.wr("(?)");
			}
		}
		if(autoIncrement != null && autoIncrement)
			txt.wr(" AUTO_INCREMENT");
		return txt;
	}
}
