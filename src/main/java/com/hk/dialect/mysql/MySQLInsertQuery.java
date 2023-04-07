package com.hk.dialect.mysql;

import com.hk.dialect.*;
import com.hk.str.HTMLText;

import java.sql.SQLType;
import java.util.*;

public class MySQLInsertQuery implements InsertQuery, MySQLDialect.MySQLDialectOwner
{
	private final TableMeta table;
	private Boolean bulk;
	// common
	private Set<FieldMeta> fields;
	// inserted bulk
	private QueryValue[][] values;
	// inserted row-by-row
	private List<Map<FieldMeta, QueryValue>> rows;

	public MySQLInsertQuery(TableMeta table)
	{
		this.table = Objects.requireNonNull(table);
	}

	@Override
	public MySQLInsertQuery cols(FieldMeta... fields)
	{
		if(bulk != null && !bulk)
			throw new IllegalStateException("cannot insert bulk after trying row-by-row");
		bulk = true;
		if(this.fields != null)
			throw new IllegalStateException("already specified list of fields");
		Objects.requireNonNull(fields);

		this.fields = new LinkedHashSet<>();
		for (int i = 0; i < fields.length; i++)
		{
			FieldMeta field = fields[i];
			Objects.requireNonNull(field, "fields[" + i + "] is null");
			if (!table.equals(field.table()))
				throw new IllegalStateException("field (" + field + ") does not belong to table (" + table + ")");
			if (!this.fields.add(field))
				throw new IllegalStateException("duplicate field (" + field + ")");
		}
		return this;
	}

	@Override
	public MySQLInsertQuery values(QueryValue[]... rows)
	{
		if(bulk == null)
			throw new IllegalStateException("cannot specify values before fields");
		if(!bulk)
			throw new IllegalStateException("cannot insert bulk after trying row-by-row");
		Objects.requireNonNull(rows);
		for (int i = 0; i < rows.length; i++)
		{
			QueryValue[] row = rows[i];
			Objects.requireNonNull(row, "rows[" + i + "] is null");
			if (row.length != fields.size())
				throw new IllegalStateException("each row should be size of " + fields.size() + ", not " + row.length);
		}
		values = rows;
		return this;
	}

	@Override
	public MySQLInsertRow row()
	{
		if(bulk != null && bulk)
			throw new IllegalStateException("cannot insert row-by-row after trying bulk");
		bulk = false;
		if(rows == null)
			rows = new ArrayList<>();
		if(fields == null)
			fields = new TreeSet<>(Comparator.comparing(o -> ((MySQLFieldMeta) o)));

		return new MySQLInsertRow();
	}

	@Override
	public HTMLText print(HTMLText txt, List<Map.Entry<SQLType, Object>> values)
	{
		if(bulk == null)
			throw new IllegalStateException("cannot build statement before any data was given");
		if(bulk && this.values == null)
			throw new IllegalStateException("missing values for provided fields");

		txt.wr("INSERT INTO ");
		table.print(txt, values);

		txt.wr(" (");
		FieldMeta[] fds = new FieldMeta[fields.size()];
		int idx = 0;
		Iterator<FieldMeta> itr = fields.iterator();
		while(itr.hasNext())
		{
			fds[idx] = itr.next();
			fds[idx].print(txt, values);
			if(itr.hasNext())
				txt.wr(", ");
			idx++;
		}
		txt.wr(") VALUES ");
		if(bulk)
		{
			QueryValue[][] queryValues = this.values;
			for (int i = 0; i < queryValues.length; i++)
			{
				QueryValue[] value = queryValues[i];
				txt.wr("(");
				for (int j = 0; j < value.length; j++)
				{
					if(value[j] == null)
					{
						txt.wr("DEFAULT(");
						fds[j].print(txt, values);
						txt.wr(")");
					}
					else
						value[j].print(txt, values);

					if(j < value.length - 1)
						txt.wr(", ");
				}
				txt.wr(")");
				if(i < queryValues.length - 1)
					txt.wr(", ");
			}
		}
		else
		{
			for (int i = 0; i < rows.size(); i++)
			{
				Map<FieldMeta, QueryValue> row = rows.get(i);
				txt.wr("(");
				// should be sorted by fields because it's a TreeMap
				Iterator<FieldMeta> itr2 = fields.iterator();
				while(itr2.hasNext())
				{
					FieldMeta field = itr2.next();
					QueryValue value = row.get(field);

					if(value == null)
					{
						txt.wr("DEFAULT(");
						field.print(txt, values);
						txt.wr(")");
					}
					else
						value.print(txt, values);

					if (itr2.hasNext())
						txt.wr(", ");
				}
				txt.wr(")");
				if(i < rows.size() - 1)
					txt.wr(", ");
			}
		}
		return txt;
	}

	public class MySQLInsertRow implements InsertRow
	{
		private Map<FieldMeta, QueryValue> values;

		private MySQLInsertRow()
		{
			values = new TreeMap<>(Comparator.comparing(o -> ((MySQLFieldMeta) o)));
		}

		@Override
		public MySQLInsertRow set(FieldMeta field, QueryValue value)
		{
			if(values == null)
				throw new IllegalStateException("row already inserted");
			if(!table.equals(field.table()))
				throw new IllegalStateException("field (" + field + ") does not belong to table (" + table + ")");

			values.put(field, value);
			return this;
		}

		@Override
		public MySQLInsertRow removeValue(FieldMeta field)
		{
			if(values == null)
				throw new IllegalStateException("row already inserted");

			values.remove(field);
			return this;
		}

		@Override
		public MySQLInsertRow clear()
		{
			if(values == null)
				throw new IllegalStateException("row already inserted");

			values.clear();
			return this;
		}

		@Override
		public MySQLInsertQuery insert()
		{
			if(values == null)
				throw new IllegalStateException("row already inserted");

			fields.addAll(values.keySet());
			rows.add(values);
			values = null;
			return MySQLInsertQuery.this;
		}
	}
}
