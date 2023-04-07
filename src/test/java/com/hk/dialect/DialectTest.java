package com.hk.dialect;

import com.hk.dialect.mysql.SyntaxTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DialectTest
{
	public static Connection getMySQLConnection() throws Exception
	{
		Properties properties = new Properties();
		properties.load(SyntaxTest.class.getResourceAsStream("/database.properties"));
		String title = properties.getProperty("database.title");
		String user = properties.getProperty("database.user");
		String pass = properties.getProperty("database.pass");

		Class.forName("com.mysql.cj.jdbc.Driver");

		return DriverManager.getConnection("jdbc:mysql://" + user + ":" + pass + "@localhost:3306/" + title);
	}
}
