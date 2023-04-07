package com.hk.dialect;

import java.util.Locale;

public class Owner
{
	public final String prefix;
	public final boolean builtIn;

	private Owner(String prefix, boolean builtIn)
	{
		this.prefix = prefix;
		this.builtIn = builtIn;
	}

	static Owner as(String name)
	{
		switch (name.toLowerCase(Locale.ROOT))
		{
			case "sys":
				return Owner.SYSTEM;
			case "usr":
				return Owner.USER;
			case "tst":
				return Owner.TEST;
		}
		return new Owner(name, false);
	}

	public static final Owner SYSTEM = new Owner("sys", true);
	public static final Owner USER = new Owner("usr", true);
	public static final Owner TEST = new Owner("tst", true);
}
