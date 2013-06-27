package uk.ac.dotrural.quality.mink.observation;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import uk.ac.dotrural.quality.mink.Data;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class Sighting {
	
	public String id;
	public String resultTime;
	public String samplingTime;
	public String reportedBy;
	public String cCode;
	public String cName;
	public String cDetails;
	public String reportedTo;
	public String catchment;
	public String river;		//FOI
	public String x;
	public String y;
	public String minkCount;
	public String status;
	public String followUp;
	public String comments;
	
	public Sighting(String id, String date, String reportedBy, String cCode, String cName, String cDetails, String reportedTo, String catchment, String river, String x, String y, String minkCount, String status, String followUp, String comments)
	{
		this.id = id;
		
		samplingTime = "" + dateToTimestamp(date);
		resultTime = "" + generateSamplingTime();
		
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
	}

	public Sighting(String id, String date, String reportedBy, String cCode, String cName, String cDetails, String reportedTo, String catchment, String river, String x, String y, String minkCount, String status, String followUp, String comments, String added)
	{
		this.id = id;
		
		samplingTime = added;
		resultTime = date;
		
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
	}
	
	private long generateSamplingTime()
	{
		long fortnight = 1209600000;
		double rand = Math.random();
		
		return (long)(Long.parseLong(samplingTime) + (rand * fortnight));
	}
	
	private long dateToTimestamp(String date)
	{
		String[] tokens = date.split("-");
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Integer.parseInt(tokens[0]), (Integer.parseInt(tokens[1])-1), Integer.parseInt(tokens[2]), 12, 0);
		return cal.getTimeInMillis();
	}
	
	public OntModel toOntModel()
	{
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		
		Resource obs = model.createResource(Data.MINKNS.concat("Observation".concat(id)));
		Resource obsRes = model.createResource(Data.MINKNS.concat("SensorOutput".concat(id)));
		Resource obsVal = model.createResource(Data.MINKNS.concat("ObservationValue".concat(id)));
		Resource foi = model.createResource(createFoiUri());
		Resource prop = model.createResource(Data.MINKNS.concat("MinkPresence"));
		Resource agent = model.createResource(createAgentUri());
		Resource org = model.createResource(createOrgUri());
		
		Statement obsTypeStmt = model.createStatement(obs, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.SSNNS.concat("Observation")));
		Statement obsEntTypeStmt = model.createStatement(obs, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.PROVNS.concat("Entity")));
		Statement obsResTypeStmt = model.createStatement(obsRes, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.SSNNS.concat("SensorOutput")));
		Statement obsValTypeStmt = model.createStatement(obsVal, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.SSNNS.concat("ObservationValue")));
		Statement agentTypeStmt = model.createStatement(agent, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.PROVNS.concat("Person")));
		//Statement agentFoafStmt = model.createStatement(agent, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.FOAFNS.concat("Person")));
		Statement orgTypeStmt = model.createStatement(org, model.createProperty(Data.RDFNS.concat("type")), model.createResource(Data.PROVNS.concat("Role")));
		
		Statement obsAttrStmt = model.createStatement(obs, model.createProperty(Data.PROVNS.concat("wasAttributedTo")), agent);
		Statement obsResStmt = model.createStatement(obs, model.createProperty(Data.SSNNS.concat("observationResult")), obsRes);
		Statement obsValStmt = model.createStatement(obsRes, model.createProperty(Data.SSNNS.concat("hasValue")), obsVal);
		
		Statement obsResTimeStmt = model.createStatement(obs, model.createProperty(Data.SSNNS.concat("observationResultTime")), model.createTypedLiteral(Long.parseLong(resultTime)));
		Statement obsSamTimeStmt = model.createStatement(obs, model.createProperty(Data.SSNNS.concat("observationSamplingTime")), model.createTypedLiteral(Long.parseLong(samplingTime)));
		
		Statement obsXCoordStmt = model.createStatement(obs, model.createProperty(Data.MINKNS.concat("x_coord")), model.createTypedLiteral(Double.parseDouble(x)));
		Statement obsYCoordStmt = model.createStatement(obs, model.createProperty(Data.MINKNS.concat("y_coord")), model.createTypedLiteral(Double.parseDouble(y)));
		
		Statement foiStmt = model.createStatement(obs, model.createProperty(Data.SSNNS.concat("featureOfInterest")), foi);
		Statement foiNameStmt = model.createStatement(foi, model.createProperty(Data.MINKNS.concat("name")), model.createTypedLiteral(river));
		Statement propStmt = model.createStatement(obs, model.createProperty(Data.SSNNS.concat("observedProperty")), prop);
		Statement propOfStmt = model.createStatement(prop, model.createProperty(Data.SSNNS.concat("propertyOf")), foi);
		
		Statement minkCountStmt = model.createStatement(obsVal, model.createProperty(Data.MINKNS.concat("count")), model.createTypedLiteral(Integer.parseInt(minkCount)));
		Statement minkStatStmt = model.createStatement(obsVal, model.createProperty(Data.MINKNS.concat("status")), model.createTypedLiteral(status));
		
		//Statement agentNameStmt;
		//if(!cCode.equals("unknown"))
			//agentNameStmt =  model.createStatement(agent, model.createProperty(Data.FOAFNS.concat("name")), model.createTypedLiteral(cCode));
		//else
			//agentNameStmt = model.createStatement(agent, model.createProperty(Data.FOAFNS.concat("name")), model.createTypedLiteral(cName));
		
		Statement agentOrgStmt = model.createStatement(agent, model.createProperty(Data.PROVNS.concat("hadRole")), org);
		
		model.add(obsTypeStmt);
		model.add(obsEntTypeStmt);
		model.add(obsResTypeStmt);
		model.add(obsValTypeStmt);
		model.add(obsAttrStmt);
		model.add(obsResStmt);
		model.add(obsValStmt);
		model.add(obsResTimeStmt);
		model.add(obsSamTimeStmt);
		model.add(obsXCoordStmt);
		model.add(obsYCoordStmt);
		model.add(foiStmt);
		model.add(propStmt);
		model.add(propOfStmt);
		model.add(foiNameStmt);
		model.add(minkCountStmt);
		model.add(minkStatStmt);
		model.add(agentTypeStmt);
		//model.add(agentFoafStmt);
		//model.add(agentNameStmt);
		model.add(orgTypeStmt);
		model.add(agentOrgStmt);
		
		return model;
	}
	
	public String toRDF()
	{
		String obsUri = Data.MINKNS.concat("Observation".concat(id));
		String obsResUri = Data.MINKNS.concat("ObservationResult".concat(id));
		String obsValUri = Data.MINKNS.concat("ObservationValue".concat(id));
		
		//Create agent URI
		String agentUri = createAgentUri();
		
		//Create FOI URI
		String foiUri = createFoiUri();
		
		//Create Org URI
		String orgUri = createOrgUri();
				
		StringBuilder sb = new StringBuilder();
		
		//Create Property URI
		String propUri = Data.MINKNS.concat("MinkPresence");
		
		sb.append("INSERT DATA {\n");
		
		//Observation
		sb.append("\t<" + obsUri + "> a <" + Data.MINKNS.concat("SightingObservation") + "> . \n");
		sb.append("\t<" + obsUri + "> a <" + Data.PROVNS.concat("Entity") + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("featureOfInterest") + "> <" + foiUri + "> . \n");
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("observationResultTime" + "> \"" + resultTime + "\" . \n"));
		sb.append("\t<" + obsUri + "> <" + Data.SSNNS.concat("observationSamplingTime" + "> \"" + samplingTime + "\" . \n"));
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
		//sb.append("\t<" + agentUri + "> a <" + Data.FOAFNS.concat("Person") + "> . \n");
		sb.append("\t<" + agentUri + "> a <" + Data.PROVNS.concat("Person") + "> . \n");
		//if(cName != null)
			//sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("name") + "> \"" + cName.split("\\s")[0] + "\" . \n");

		//sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("basedNear") + "> \"" + catchment + "\" . \n");
		//sb.append("\t<" + agentUri + "> <" + Data.FOAFNS.concat("member") + "> <" + orgUri + "> . \n\n");
		sb.append("\t<" + agentUri + "> <" + Data.PROVNS.concat("hadRole") + "> <" + orgUri + "> . \n\n");
		
		//Organisation
		sb.append("\t<" + orgUri + "> a <" + Data.PROVNS.concat("Role") + "> . \n");
		
		sb.append("}");
		
		return sb.toString();
	}
	
	private String createAgentUri()
	{
		String agentUri;
		if(cCode == null && cName != null)
		{
			String name[] = cName.split("\\s");
			if(name.length > 1)
				agentUri = Data.MINKNS.concat(name[0].concat(name[1]));
			else
				agentUri = Data.MINKNS.concat(name[0]);
		} else if(cCode == null) {
			agentUri = Data.MINKNS.concat("Null");
			String[] tokens = reportedBy.split("\\s");
			for(int i=0;i<tokens.length;i++)
				agentUri = agentUri.concat(tokens[i]);
		} else 
			agentUri = Data.MINKNS.concat(cCode);
		return agentUri;
	}
	
	private String createFoiUri()
	{
		String foiUri = Data.MINKNS;
		String foiTokens[] = river.split("\\p{Punct}*\\s");
		for(int i=0;i<foiTokens.length;i++)
			foiUri = foiUri.concat(foiTokens[i]);
		return foiUri;
	}
	
	private String createOrgUri()
	{
		String orgUri = Data.MINKNS;
		String orgTokens[] = reportedBy.split("\\s");
		for(int i=0;i<orgTokens.length;i++)
			orgUri = orgUri.concat(orgTokens[i]);
		return orgUri;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\r\rid: ");
		sb.append(id);
		sb.append("\rdate: ");
		sb.append(samplingTime);
		sb.append("\rreportedBy: ");
		sb.append(cName);
		sb.append("\rcatchment: ");
		sb.append(catchment);
		sb.append("\r# spotted: ");
		sb.append(minkCount);
		return sb.toString();
	}
	
}
