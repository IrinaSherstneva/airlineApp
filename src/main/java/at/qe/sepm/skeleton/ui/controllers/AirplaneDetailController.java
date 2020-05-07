package at.qe.sepm.skeleton.ui.controllers;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.services.AirplaneService;
import at.qe.sepm.skeleton.services.FlightService;

/**
 * Controller for the airplane detail view.
 */
@Component
@Scope("view")
public class AirplaneDetailController {
	
	@Autowired
    private AirplaneService airplaneService;
	@Autowired
    private FlightService flightService;
	 private String pilotDeletedMsg="Airplane was successfully deleted";

    /**
     * Attribute to cache the currently displayed airplane
     */
    private Airplane airplane;

    
    public String getPilotDeletedMsg() {
		return pilotDeletedMsg;
	}

	public void setPilotDeletedMsg(String pilotDeletedMsg) {
		this.pilotDeletedMsg = pilotDeletedMsg;
	}

	/**
     * Sets the currently displayed airplane and reloads it form db. This airplane is
     * targeted by any further calls of
     * {@link #doReloadAirplane()}, {@link #doSaveAirplane()} and
     * {@link #doDeleteAirplane()}.
     *
     * @param airplane
     */
    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
        doReloadAirplane();
    }

    /**
     * Returns the currently displayed airplane.
     *
     * @return
     */
    public Airplane getAirplane() {
    	return airplane;
    	
    }

    /**
     * Action to force a reload of the currently displayed airplane.
     */
    public void doReloadAirplane() {
    	airplane = airplaneService.loadAirplane(airplane.getAirplaneId());
    }

    /**
     * Action to save the currently displayed airplane.
     */
    public void doSaveAirplane() {
    	airplane = this.airplaneService.saveAirplane(airplane);
    }
    

    /**
     * Action to delete the currently displayed airplane.
     */
    public void doDeleteAirplane() {
    	if (airplane.getIsAssignedToFlight()) {
    		Flight f=flightService.findFlightByPlane(airplane);
    		f.setAirplane(null);
    		flightService.saveFlight(f);
    		setPilotDeletedMsg("This airplane was assigned to the flight #"+f.getFlightNumber()+
    				". Please, select a new airplane for the flight #"+f.getFlightNumber()+" from "+f.getCodeAirportDeparture());
    		this.airplaneService.deleteAirplane(airplane);
            airplane = null;
    		//PrimeFaces.current().executeScript("PF('pilotDeletedDialog').show()");
    	}
    	else {
        this.airplaneService.deleteAirplane(airplane);
        airplane = null;
    	}
    }

}
