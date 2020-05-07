package at.qe.sepm.skeleton.ui.controllers;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.ArrivalBeforeDepartureException;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.FlightInPastException;
import at.qe.sepm.skeleton.services.AirplaneService;
import at.qe.sepm.skeleton.services.FlightService;

/**
 * Controller for the flight detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class FlightDetailController {

	@Autowired
    private FlightService flightService;
	@Autowired
    private AirplaneService airplaneService;
    /**
     * Attribute to cache the currently displayed flight
     */
    private Flight flight;
    private Map<String,String> availablePlanes;
    private String selectedAirplaneId;
    private boolean airplaneNotSelected=true;
    private Date arrivalDateTime;
    private Date departureDateTime;
    private Date now= convertToDate(LocalDateTime.now().plusMinutes(10));
    /**
     * Sets the currently displayed flight and reloads it form db. This flight is
     * targeted by any further calls of
     * {@link #doReloadFlight()}, {@link #doSaveFlight()} and
     * {@link #doDeleteFlight()}.
     *
     * @param flight
     */
    
    public void setFlight(Flight flight) {
        this.flight = flight;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            this.arrivalDateTime = df.parse(flight.getArrivalDateTime());
			this.departureDateTime = df.parse(flight.getDepartureDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        doReloadFlight();
    }
    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
      }
    /**
     * Initializes the date times.
     * 
     * @return true if everything worked
     */
    public boolean initDateTimes(){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	if(flight.getArrivalDateTime().equals(df.format(arrivalDateTime).toString()) 
    			&& flight.getDepartureDateTime().equals(df.format(departureDateTime).toString())){
    		return true;
    	}
    	try {
			flightService.checkDepartureAndArrivalTime(departureDateTime, arrivalDateTime);
	    	flight.setArrivalDateTime(df.format(arrivalDateTime).toString());
	    	flight.setDepartureDateTime(df.format(departureDateTime).toString());
		} catch (ArrivalBeforeDepartureException e) {
			PrimeFaces.current().executeScript("PF('errorArrivalBeforeDepartureDialog').show()");
		} catch (FlightInPastException e) {
			PrimeFaces.current().executeScript("PF('errorFlightInPastDialog').show()");
		}
    	return true;
    }
    
    public boolean isAirplaneNotSelected() {
		return airplaneNotSelected;
	}

	public void setAirplaneNotSelected(boolean airplaneNotSelected) {
		this.airplaneNotSelected = airplaneNotSelected;
	}

	public void findAvailableAirplane() {
		initDateTimes();
    	if (flight.getCodeAirportDeparture()=="")
    		PrimeFaces.current().executeScript("PF('selectDepartureDialog').show()");
    	else {
	    	availablePlanes=new HashMap<>();
	    	Collection<Airplane> allPlanes = airplaneService.getAllAirplanes();
	    	for (Airplane a : allPlanes) {
	    		if(flightService.isAvailable(a,flight)){
	    			availablePlanes.put(a.toString(), a.getAirplaneId());
	    		}
	    	}
	    	if (availablePlanes.isEmpty()) {
	    		PrimeFaces.current().executeScript("PF('noPlanesDialog').show()");
	    	}
	    	else{
	    		PrimeFaces.current().executeScript("PF('selectNewAirplaneDialog').show()");
	    	}
	    }
    	
    }
    /**
     * Returns the currently displayed flight.
     *
     * @return
     */
    
    public Flight getFlight() {
        return flight;
    }
    
    

    public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(Date arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(Date departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public String getSelectedAirplaneId() {
		return selectedAirplaneId;
	}
	public void setSelectedAirplaneId(String selectedAirplaneId) {
		this.selectedAirplaneId = selectedAirplaneId;
	}
	public Map<String, String> getAvailablePlanes() {
		return availablePlanes;
	}
	public void setAvailablePlanes(Map<String, String> availablePlanes) {
		this.availablePlanes = availablePlanes;
	}
	/**
     * Action to force a reload of the currently displayed flight.
     */
    public void doReloadFlight() {
    	flight = flightService.loadFlight(flight.getFlightNumber());
    }
    
    public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
	/**
     * Action to save the currently displayed flight.
     */
    public void doAssignAirplane() {
    	try {
	    	Airplane a = airplaneService.loadAirplane(selectedAirplaneId);
	    	a.setIsAssignedToFlight(true);
	    	airplaneService.saveAirplane(a);
	    	flight.setAirplane(a);
	    	flight.setNumPassengers(a.getNumSeats());
	    	setAirplaneNotSelected(false);
    	} catch (NullPointerException e) {
    		System.out.println("plane not assigned");
    	}
    }
    
    public void doSaveFlight() {
    	if(initDateTimes()){
    		flight = this.flightService.saveFlight(flight);
    		PrimeFaces.current().executeScript("PF('flightEditDialog').hide()");
    	}
    }

    /**
     * Action to delete the currently displayed flight.
     */
    public void doDeleteFlight() {
        this.flightService.deleteFlight(flight);
        flight = null;
    }
    public void onDateSelect(SelectEvent event) {
    	DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    	try {
			this.departureDateTime=df.parse(event.getObject().toString());
		} catch (ParseException e) {
			System.out.println("Incorrect Date and Time Format");
		}
    	
    }
}
