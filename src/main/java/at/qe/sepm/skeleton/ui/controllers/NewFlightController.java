package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;
import javax.annotation.PostConstruct;

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
 * Controller for a new flight.
 */
@Component
@Scope("view")
public class NewFlightController implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private FlightService flightService;
	@Autowired
    private AirplaneService airplaneService;
    private Flight flight=new Flight();
    private Map<String,String> iataCodes;
    private Map<String,String> availablePlanes;
    private String selectedAirplaneId;
    private String noPlanesMsg="Unfortunately there are no planes available at the given location and time";
    private String notEnoughtPilotsMsg="Not enough pilots available for this flight. Visit the error page";
    private String notEnoughtCrewMsg="Not enough crew members available for this flight. Visit the error page";
    private String selectDepartureMsg="Please fill out all fields in order to proceed";
    private boolean airplaneNotSelected=true;
    private Date departureDateTime;
    private Date arrivalDateTime;
    
    public void initFlight() {
    	flight=new Flight();
    	departureDateTime=convertToDate(LocalDateTime.now().plusMinutes(10));
    	arrivalDateTime=convertToDate(LocalDateTime.now().plusMinutes(10));
    	setAirplaneNotSelected(true);
    }
    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
      }
    
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
			PrimeFaces.current().executeScript("PF('errorNewArrivalBeforeDepartureDialog').show()");
		} catch (FlightInPastException e) {
			PrimeFaces.current().executeScript("PF('errorNewFlightInPastDialog').show()");
		}
    	return true;
    }
    
    public void findAvailableAirplane() {
    	initDateTimes();
    	if (flight.getCodeAirportDeparture()=="" || flight.getCodeAirportArrival()=="" ||
    			departureDateTime==null || arrivalDateTime==null)
    		PrimeFaces.current().executeScript("PF('selectDepartureDialog').show()");
    	else {
    		if(flight.getAirplane()!=null) {
    			//System.out.println("a");
        		Airplane a = flight.getAirplane();
        		a.setIsAssignedToFlight(false);
        		flight.setAirplane(null);
        		airplaneService.saveAirplane(a);
        		if(!flight.getPersonal().isEmpty()){
        			flight.getPersonal().clear();
        		}
        	}
    		availablePlanes=new HashMap<>();
	    	Collection<Airplane> allPlanes = airplaneService.getAllAirplanes();
	    	for (Airplane a : allPlanes) {
	    		if(flightService.isAvailable(a, flight)){
	    			availablePlanes.put(a.toString(), a.getAirplaneId());
	    		}
	    	}
	    	if (availablePlanes.isEmpty()) 
	    		PrimeFaces.current().executeScript("PF('noPlanesDialog').show()");
	    	else
	    		PrimeFaces.current().executeScript("PF('selectAirplaneDialog').show()");

    		
    		
    	}
    }

    
	public boolean isAirplaneNotSelected() {
		return airplaneNotSelected;
	}

	public void setAirplaneNotSelected(boolean airplaneSelected) {
		this.airplaneNotSelected = airplaneSelected;
	}

	public String getSelectDepartureMsg() {
		return selectDepartureMsg;
	}

	public void setSelectDepartureMsg(String selectDepartureMsg) {
		this.selectDepartureMsg = selectDepartureMsg;
	}

	public String getNotEnoughtPilotsMsg() {
		return notEnoughtPilotsMsg;
	}

	public void setNotEnoughtPilotsMsg(String notEnoughtPilotsMsg) {
		this.notEnoughtPilotsMsg = notEnoughtPilotsMsg;
	}

	public String getNotEnoughtCrewMsg() {
		return notEnoughtCrewMsg;
	}

	public void setNotEnoughtCrewMsg(String notEnoughtCrewMsg) {
		this.notEnoughtCrewMsg = notEnoughtCrewMsg;
	}

	public String getNoPlanesMsg() {
		return noPlanesMsg;
	}

	public void setNoPlanesMsg(String noPlanesMsg) {
		this.noPlanesMsg = noPlanesMsg;
	}

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


	public Map<String, String> getIataCodes() {
		return iataCodes;
	}

	public void setIataCodes(Map<String, String> iataCodes) {
		this.iataCodes = iataCodes;
	}
	
	

	public Date getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(Date departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public Date getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(Date arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	/**
     * Sets default values for a new airplane and returns it.
     *
     * @return
     */
    public Flight getFlight() {
    	
    	flight.setDepartureDateTime("2020-01-31T00:00:00");
    	flight.setArrivalDateTime("2020-01-31T09:00:00");
    	
    	return flight;
    	
    }
    
    
    public void setFlight(Flight flight) {
		this.flight = flight;
		initDateTimes();
	}


	public Map<String,String> getAvailablePlanes() {
		return availablePlanes;
	}


	public void setAvailablePlanes(Map<String,String> availablePlanes) {
		this.availablePlanes = availablePlanes;
	}
	
	public String getSelectedAirplaneId() {
		return selectedAirplaneId;
	}


	public void setSelectedAirplaneId(String selectedAirplaneId) {
		this.selectedAirplaneId = selectedAirplaneId;
	}


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
	/**
     * Action to save the newly created flight in DB.
     */
    public void doSaveFlight() {
    	if(initDateTimes()){
    		flight = this.flightService.saveFlight(flight);
    		PrimeFaces.current().executeScript("PF('flightCreateDialog').hide()");
    	}
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
