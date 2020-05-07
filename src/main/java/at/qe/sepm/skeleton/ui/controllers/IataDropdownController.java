package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class IataDropdownController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String iataCode;  
	private Map<String,String> iataCodes;
	     
    @PostConstruct
    public void init() {
        iataCodes  = new HashMap<String, String>();
        iataCodes.put("AGB", "AGB");
        iataCodes.put("FRA", "FRA");
        iataCodes.put("MUC", "MUC");
        iataCodes.put("DEL", "DEL");
        iataCodes.put("HRG", "HRG");
        iataCodes.put("LXR", "LXR");
        iataCodes.put("MLE", "MLE");
        iataCodes.put("TUS", "TUS");
        iataCodes.put("DXB", "DXB");
        iataCodes.put("TLV", "TLV");
        iataCodes.put("NRT", "NRT");
        iataCodes.put("EZE", "EZE");
    }

	public String getIataCode() {
		return iataCode;
	}

	public Map<String, String> getIataCodes() {
		return iataCodes;
	}

	public void setIataCode(String iataCode) {
		this.iataCode = iataCode;
	}

	public void setIataCodes(Map<String, String> iataCodes) {
		this.iataCodes = iataCodes;
	}
	 
	public String getIataCodeAirport(){
		//String iata = this.iataCode;
		//this.iataCode = null;
		//init();
		return(iataCode);
	}

}
