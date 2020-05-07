package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.services.FlightService;


@Component
@Scope("view")
public class FlightErrorListController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Autowired
    private FlightService flightService;

    /**
     * Returns a list of all users.
     *
     * @return
     */
    public Collection<Flight> getErrorFlights() {
    	Collection<Flight> result=new ArrayList<>();
    	result.addAll(flightService.getAllMissingAirplaneFlights());
    	result.addAll(flightService.getAllMissingEmployeeFlights());
        return result;
    }

}
