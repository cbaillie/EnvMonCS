package uk.ac.dotrural.quality.mink.sesame;

import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class Updater {
	
	public void sendUpdate(String endpoint, String query)
	{
		UpdateRequest request = UpdateFactory.create();
		request.add(query);
		
		UpdateProcessor update = UpdateExecutionFactory.createRemoteForm(request, endpoint);
		update.execute();
	}

}
