package uk.ac.dotrural.quality.mink.mysql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import uk.ac.dotrural.quality.mink.logger.Logger;

public class Database {
	
	private String url;
	private String username;
	private String password;
	
	public Database()
	{
		getDetails();
		
		if(username.length() > 0)
		{
			Logger.info("Database","Hello, " + username);
		}
	}
	
	/**
	 * Read database details from a local config file
	 */
	private void getDetails()
	{
		BufferedReader br = null;
		Logger.info("Database","Reading details from config file");
		
		try
		{
			String in;
			br = new BufferedReader(new FileReader("config/db.cfg"));
			for(int i=0;i<3;i++)
			{
				in = br.readLine();
				String tokens[] = in.split("=");
				String item = tokens[1];
				
				switch(i)
				{
				case 0:
					url = item;
					break;
				case 1:
					username = item;
				case 2:
					password = item;
				}
			}
		}
		catch(Exception ex)
		{
			Logger.error("Database","Couldn't open config file: " + ex);
		}
		finally
		{
			if(br != null)
			{
				try
				{
					br.close();
				}
				catch(Exception ex)
				{
					Logger.error("Database","Couldn't close BufferedReader: " + ex);
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Execute an SQL Query
	 * @param query The Query to be executed
	 * @return The Query's ResultSet
	 */
	public ResultSet executeQuery(String query)
	{
		Logger.info("Database","Executing query: " + query);
		ResultSet rs = null;
		Statement st = null;
		Connection con = getConnection();
		
		if(con != null)
		{
			try
			{
				st = con.createStatement();
				rs = st.executeQuery(query);
				
				countResults(rs);
			}
			catch(Exception ex)
			{
				Logger.error("Database","Couldn't query MySQL: " + ex);
				ex.printStackTrace();
			}
		}
		
		return rs;
	}
	
	/**
	 * Count the results of a SELECT query
	 */
	private void countResults(ResultSet rs)
	{
		int count = 0;
		try
		{
			while(rs.next())
				count++;
			
			rs.beforeFirst();
		}
		catch(Exception ex)
		{
			Logger.error("Database","Exception while counting results: " + ex);
			ex.printStackTrace();
		}
		Logger.info("Database","Databased returned " + count + " results");
	}
	
	/**
	 * Create a MySQL Connection
	 */
	private Connection getConnection()
	{
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(url, username, password);
			return con;
		}
		catch(Exception ex)
		{
			Logger.error("Database","Couldn't create MySQL Connection: " + ex);
			ex.printStackTrace();
		}
		return null;
	}

}
