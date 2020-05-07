package at.qe.sepm.skeleton.ui.controllers;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.services.AirplaneService;

/**
 * Controller for the airplane list view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class AirplaneListController {
	@Autowired
    private AirplaneService airplaneService;

    /**
     * Returns a list of all flights.
     *
     * @return
     */
    public Collection<Airplane> getAirplanes() {
        return airplaneService.getAllAirplanes();
    }

}
