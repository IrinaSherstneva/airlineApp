package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.FlightService;
import at.qe.sepm.skeleton.services.UserService;


@Component
@Scope("view")
public class UserErrorController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private FlightService flightService;
	@Autowired
	private UserService userService;
	
	
	private Flight flight;
	private Map<String,String> availableEmployees;
	private String selectedEmployee;

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
		doReloadFlight();
		//System.out.println(flight.getFlightNumber());
	}
	
	public Map<String, String> getAvailableEmployees() {
		return availableEmployees;
	}

	public void setAvailableEmployees(Map<String, String> availableEmployees) {
		this.availableEmployees = availableEmployees;
	}

	public String getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(String selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	/**
	 * Reload flight from database.
	 */
	public void doReloadFlight(){
		flight = flightService.loadFlight(flight.getFlightNumber());
	}
	
	/**
	 * Method finds all available employees for the flight of this object.
	 */
	public void findAvailableEmployees(){
		if(flight.getAirplane() != null){
			PrimeFaces.current().executeScript("PF('flightEditDialog').show()");
			Collection<User> users = userService.getAllUsersForErrorFlight(flight);
			availableEmployees = new HashMap<>();
			for(User u : users){
				//System.out.println(u.getUsername() + u.getLocationCode());
				if(flightService.isAvailable(u, this.flight)){
					availableEmployees.put(u.userToString(), u.getUsername());
				}
			}
			if(availableEmployees.isEmpty()){
				PrimeFaces.current().executeScript("PF('errorNoEmployeeDialog').show()");
			}
		}
		else{
			//PrimeFaces.current().executeScript("PF('flightEditDialog').hide()");
			PrimeFaces.current().executeScript("PF('missingAirplaneErrorDialog').show()");
		}
	}

	
	/**
	 * Save the flight.
	 */
	public void doSaveFlight(){
		this.flight = userService.addPersonalToFlight(flight, selectedEmployee);
		if(flightService.hasFlightCorrectNumberOfPersonal(flight)){
			flight.setMissingEmployee(false);
			PrimeFaces.current().executeScript("PF('successEmployeeInsertDialog').show()");
		}
		else{
			PrimeFaces.current().executeScript("PF('missingEmployeeDialog').show()");
		}
		flightService.saveFlightWithoutAssigning(flight);
	}
	

}
