package at.qe.sepm.skeleton.ui.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.AirplaneType;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.services.AirplaneService;

/**
 * Controller for a new airplane.
 */
@Component
@Scope("view")
public class NewAirplaneController {
	@Autowired
    private AirplaneService airplaneService;
    private Airplane airplane=new Airplane();
	private Map<String,String> iataCodes;
	private Map<String,AirplaneType> airplaneTypes;
    
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
        airplaneTypes = new HashMap<>();
        airplaneTypes.put("BOEING", AirplaneType.BOEING);
        airplaneTypes.put("AIRBUS", AirplaneType.AIRBUS);
        airplane.setTimeLastFlightEnded(LocalDateTime.now().plusMinutes(10));
    }

    public void initAirplane() {
    	airplane=new Airplane();
    	airplane.setTimeLastFlightEnded(LocalDateTime.now().plusMinutes(10));
    	airplane.setIsAssignedToFlight(false);
    	airplane.setNumPilotsNecessary(1);
    	airplane.setNumCabinCrewNecessary(1);
    }

	public Map<String, AirplaneType> getAirplaneTypes() {
		return airplaneTypes;
	}


	public void setAirplaneTypes(Map<String, AirplaneType> airplaneTypes) {
		this.airplaneTypes = airplaneTypes;
	}


	public Map<String, String> getIataCodes() {
		return iataCodes;
	}

	public void setIataCodes(Map<String, String> iataCodes) {
		this.iataCodes = iataCodes;
	}
    /**
     * Sets default values for a new airplane and returns it.
     *
     * @return
     */
    public Airplane getAirplane() {
    	return airplane;
    	
    }
    /**
     * Action to save the newly created airplane in DB.
     */
    public void doSaveAirplane() {
    	airplane = this.airplaneService.saveAirplane(airplane);
    }



}
