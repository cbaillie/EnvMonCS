package uk.ac.dotrural.quality.mink.observation;

import uk.ac.dotrural.quality.mink.Data;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class Sighting {
	
	public String id;
	public String date;
	public String reportedBy;
	public String cCode;
	public String cName;
	public String cDetails;
	public String reportedTo;
	public String catchment;
	public String river;
	public String x;
	public String y;
	public String minkCount;
	public String status;
	public String followUp;
	public String comments;
	public String added;

	public Sighting(String id, String date, String reportedBy, String cCode, String cName, String cDetails, String reportedTo, String catchment, String river, String x, String y, String minkCount, String status, String followUp, String comments, String added)
	{
		this.id = id;
		this.date = date;
		this.reportedBy = reportedBy;
		this.cCode = (cCode == null || cCode.length() < 1) ? null : cCode.trim();
		this.cName = (cName == null || cName.length() < 1) ? null : cName.trim();
		this.cDetails = cDetails;
		this.reportedTo = reportedTo;
		this.catchment = catchment;
		this.river = (river == null || river.length() < 1) ? "RiverNull" : river.trim();
		
		//Convert GridReference to lat/long
		OSRef osr = new OSRef(Double.parseDouble(x), Double.parseDouble(y));
		LatLng ll = osr.toLatLng();
		
		this.x = "" + ll.getLat();
		this.y = "" + ll.getLng();
		this.minkCount = minkCount;
		this.status = status;
		this.followUp = followUp;
		this.comments = comments;
		this.added = added;
	}
	
	public String toRDF()
	{
		String obsUri = Data.MINKNS.concat("Observation".concat(id));
		String obsResUri = Data.MINKNS.concat("ObservationResult".concat(id));
		String obsValUri = Data.MINKNS.concat("ObservationValue".concat(id));
		
		//Create agent URI
		String agentUri;
		if(cCode == null && cName != null)
		{
			String name[] = cName.split("\\s");
			if(name.length > 1)
				agentUri = Data.MINKNS.concat(name[0].concat(name[1]));
			else
				agentUri = Data.MINKNS.concat(name[0]);
		} else if(cCode == null) {
			agentUri = Data.MINKNS.concat("null");
		} else 
			agentUri = Data.MINKNS.concat(cCode);
		
		
		//Create FOI URI
		String foiUri = Data.MINKNS;
		String foiTokens[] = river.split("\\p{Punct}*\\s");
		for(int i=0;i<foiTokens.length;i++)
			foiUri = foiUri.concat(foiTokens[i]);
		
		//Create Org URI
		String orgUri = Data.MINKNS;
		String orgTokens[] = reportedBy.split("\\s");
		for(int i=0;i<orgTokens.length;i++)
			orgUri = orgUri.concat(orgTokens[i]);
				
		StringBuilder sb = new StringBuilder();
		
		//Create Property URI
		String propUri = Data.MINKNS.concat("MinkPresence");
		
		sb.append("INSERT DATA {\n");
		
		//Observation
		sb.append("\t<" + obsUri + "> a <" + Data.MINKNS.concat("SightingObservation") + "> . \n");
		sb.append("\t<" + obsUri + "> a <" + Data.PROVNS.concat("Entity") + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("featureOfInterest") + "> <" + foiUri + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("observationResult") + "> <" + obsResUri + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.PROVNS.concat("wasAttributedTo") + "> <" + agentUri + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.MINKNS.concat("x_coord") + "> \"" + x + "\" . \n");
		sb.append("\t<" + obsUri + "> <" + Data.MINKNS.concat("y_coord") + "> \"" + y + "\" . \n");
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("observedProperty") + "> <" + propUri + "> . \n\n");
		
		//Observation Result
		sb.append("\t<" + obsResUri + "> a <" + Data.SSNNS.concat("SensorOutput") + "> . \n");
		sb.append("\t<" + obsResUri + "> <" + Data.SSNNS.concat("hasValue") + "> <" + obsValUri + "> . \n");
		
		//Observation Value
		sb.append("\t<" + obsValUri + "> a <" + Data.SSNNS.concat("ObservationValue") + "> . \n");
		sb.append("\t<" + obsValUri + "> <" + Data.MINKNS.concat("count") + "> \"" + minkCount + "\" . \n");
		sb.append("\t<" + obsValUri + "> <" + Data.MINKNS.concat("status") + "> \"" + status + "\" . \n");
		
		//Feature of Interest
		sb.append("\t<" + foiUri + "> a <" + Data.SSNNS.concat("FeatureOfInterest") + "> . \n\n");
		sb.append("\t<" + foiUri + "> <" + Data.MINKNS.concat("name") + "> \"" + river + "\" . \n\n");
		
		//Property
		sb.append("\t<" + propUri + "> a <" + Data.SSNNS.concat("Property") + "> . \n");
		sb.append("\t<" + propUri + "> <" + Data.SSNNS.concat("propertyOf") + "> <" + foiUri + "> . \n\n");
		
		//Agent
		sb.append("\t<" + agentUri + "> a <" + Data.FOAFNS.concat("Person") + "> . \n");
		if(cName != null)
			sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("name") + "> \"" + cName.split("\\s")[0] + "\" . \n");

		sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("basedNear") + "> \"" + catchment + "\" . \n");
		sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("member") + "> <" + orgUri + "> . \n\n");
		
		//Organisation
		sb.append("\t<" + orgUri + "> a <" + Data.FOAFNS.concat("Organisation") + "> . \n");
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\r\rid: ");
		sb.append(id);
		sb.append("\rdate: ");
		sb.append(date);
		sb.append("\rreportedBy: ");
		sb.append(cName);
		sb.append("\rcatchment: ");
		sb.append(catchment);
		sb.append("\r# spotted: ");
		sb.append(minkCount);
		return sb.toString();
	}
	
}
