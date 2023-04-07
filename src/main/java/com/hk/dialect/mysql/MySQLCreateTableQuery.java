package com.hk.dialect.mysql;

import com.hk.dialect.ColumnMeta;
import com.hk.dialect.CreateTableQuery;
import com.hk.dialect.FieldMeta;
import com.hk.dialect.TableMeta;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.*;

public class MySQLCreateTableQuery implements CreateTableQuery, MySQLDialect.MySQLDialectOwner
{
	private final TableMeta table;
	private final Map<FieldMeta, ColumnMeta> columns;
	private boolean ifNotExists = false;
	private FieldMeta[] primaries;
	private String engine;

	public MySQLCreateTableQuery(TableMeta table)
	{
		this.table = table;
		columns = new LinkedHashMap<>();
	}

	@Override
	public MySQLCreateTableQuery column(FieldMeta field, ColumnMeta datatype)
	{
		if(!table.equals(field.table()))
			throw new IllegalArgumentException("field (" + field + ") does not belong to table (" + table + ")");
		if(columns.put(field, datatype) != null)
			throw new IllegalArgumentException("already have a column with that name");
		return this;
	}

	@Override
	public MySQLCreateTableQuery extra(String key, Object... values)
	{
		switch (key.toLowerCase(Locale.ROOT))
		{
			case "primary":
			case "primaries":
			case "primary key":
			case "primary keys":
			case "primarykeys":
			case "primarykey":
			case "pk":
			case "pks":
				FieldMeta[] fields = new FieldMeta[values.length];
				for (int i = 0; i < values.length; i++)
				{
					if(!(values[i] instanceof FieldMeta))
						throw new IllegalArgumentException("already have a column with that name");
					fields[i] = (FieldMeta) values[i];
				}
				primaries(fields);
				break;
			case "if not exists":
			case "ifnotexists":
			case "ine":
				if(values.length > 1 || values.length == 1 && !(values[0] instanceof Boolean))
					throw new IllegalArgumentException("expect nothing or boolean value");
				ifNotExists = values.length == 0 || (Boolean) values[0];
				break;
			case "engine":
			case "eng":
			case "e":
				if(values.length != 1 || !(values[0] instanceof String))
					throw new IllegalArgumentException("expect engine name");
				engine((String) values[0]);
				break;
			default:
				throw new IllegalArgumentException("unexpected extra key: " + key);
		}
		return this;
	}

	public MySQLCreateTableQuery ifNotExists()
	{
		ifNotExists = true;
		return this;
	}

	public MySQLCreateTableQuery ifNotExists(boolean ifNotExists)
	{
		this.ifNotExists = ifNotExists;
		return this;
	}

	public MySQLCreateTableQuery primaries(FieldMeta... fields)
	{
		for (FieldMeta field : fields)
		{
			if(!table.equals(field.table()))
				throw new IllegalArgumentException("field (" + field + ") does not belong to table (" + table + ")");
			if(!columns.containsKey(field))
				throw new IllegalArgumentException("field (" + field + ") not added as a column");
		}
		this.primaries = fields;
		return this;
	}

	public MySQLCreateTableQuery engine(String engine)
	{
		switch (engine.toLowerCase(Locale.ROOT))
		{
			case "innodb":
				engine = "InnoDB";
				break;
			case "myisam":
				engine = "MyISAM";
				break;
			case "memory":
				engine = "Memory";
				break;
			case "csv":
				engine = "CSV";
				break;
			case "archive":
				engine = "Archive";
				break;
			default:
				throw new IllegalArgumentException("unsupported MySQL table engine: " + engine);
		}
		this.engine = engine;
		return this;
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(columns.isEmpty())
			throw new IllegalArgumentException("cannot create table query with no columns");

		txt.wr("CREATE TABLE ");
		if(ifNotExists)
			txt.wr("IF NOT EXISTS ");

		table.print(txt, values);

		txt.wr(" (\n");
		Iterator<Map.Entry<FieldMeta, ColumnMeta>> itr = columns.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry<FieldMeta, ColumnMeta> column = itr.next();
			FieldMeta field = column.getKey();
			ColumnMeta columnMeta = column.getValue();

			field.print(txt, values, false);
			txt.wr(" ");
			columnMeta.print(txt, values);

			if(itr.hasNext())
				txt.wr(",\n");
		}
		if(primaries != null)
		{
			txt.wr(",\nPRIMARY KEY (");
			for (int i = 0; i < primaries.length; i++)
			{
				primaries[i].print(txt, values, false);

				if(i < primaries.length - 1)
					txt.wr(", ");
			}
			txt.wr(")");
		}
		txt.wr("\n)");
		if(engine != null)
			txt.wr(" ENGINE=").wr(engine);

		return txt;
	}
}
