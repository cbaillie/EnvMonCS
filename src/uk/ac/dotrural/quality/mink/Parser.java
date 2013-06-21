package uk.ac.dotrural.quality.mink;

import java.sql.ResultSet;
import java.util.ArrayList;

import uk.ac.dotrural.quality.mink.logger.Logger;
import uk.ac.dotrural.quality.mink.mysql.Database;
import uk.ac.dotrural.quality.mink.observation.Sighting;
import uk.ac.dotrural.quality.mink.sesame.Updater;

public class Parser {
	
	private int mode = 1;
	private String endpoint = "http://dtp-126.sncs.abdn.ac.uk:8080/openrdf-sesame/repositories/MinkApp/statements";
	
	public static void main(String[] args)
	{
		new Parser();
	}
	
	public Parser()
	{
		if(mode == 1)
		{
			ArrayList<Sighting> sightings = getSightings();
			
			for(int i=0;i<sightings.size();i++)
			{
				Sighting s = (Sighting)sightings.get(i);
				String query = s.toRDF();
				
				try
				{
					new Updater().sendUpdate(endpoint, query);
				}
				catch(Exception ex)
				{
					Logger.error("Parser", ex.toString());
					System.out.println(query);
				}
				
				Logger.info("Parser", "Observation " + (i+1) + " of " + sightings.size() + " sent.");
			}
		}
	}
	
	private ArrayList<Sighting> getSightings()
	{
		ArrayList<Sighting> sightings = new ArrayList<Sighting>();
		String query = "SELECT * FROM test_MinkSightings";
		Database db = new Database();
		ResultSet rs = db.executeQuery(query);
		
		try
		{
			rs.beforeFirst();
			while(rs.next())
			{
				Sighting s = new Sighting(
								"" + rs.getInt(1),
								"" + rs.getDate(2),
								rs.getString(3),
								rs.getString(4),
								rs.getString(5),
								rs.getString(6),
								rs.getString(7),
								rs.getString(8),
								rs.getString(9),
								"" + rs.getInt(10),
								"" + rs.getInt(11),
								"" + rs.getInt(12),
								rs.getString(13),
								rs.getString(14),
								rs.getString(15),
								"0"
							);
				sightings.add(s);
			}
		}
		catch(Exception ex)
		{
			Logger.error("Parser", "Error traversing ResultSet: " + ex);
			ex.printStackTrace();
		}
		return sightings;
	}

}
