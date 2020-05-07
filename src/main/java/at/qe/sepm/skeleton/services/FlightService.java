package at.qe.sepm.skeleton.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.ArrivalBeforeDepartureException;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.FlightInPastException;
import at.qe.sepm.skeleton.model.NoEmployeeAvailableException;
import at.qe.sepm.skeleton.model.Pair;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.model.VacationAlreadyInPeriodException;
import at.qe.sepm.skeleton.repositories.AirplaneRepository;
import at.qe.sepm.skeleton.repositories.FlightRepository;
import at.qe.sepm.skeleton.repositories.UserRepository;


@Component
@Scope("application")
public class FlightService{

	@Autowired
    private FlightRepository flightRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private AirplaneRepository airplaneRepository;
	@Autowired
	private AuditLogService logService;
	@Autowired
	private VacationService vacationService;
	
	/**
     * Returns a collection of all flights.
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Collection<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
    
    /**
     * Loads a single flight identified by its flightNumber.
     *
     * @param flightNumber to search for
     * @return the flight with the given flightNumber
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Flight loadFlight(long flightNumber) {
        return flightRepository.findFirstByFlightNumber(flightNumber);
    }
    /**
     * Saves the flight. This method will also set {@link Flight#createDate} for new
     * entities or {@link Flight#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link User#createdBy}
     * or {@link User#updatedBy} respectively.
     *
     * @param the flight to save
     * @return the updated flight
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Flight saveFlight(Flight flight) {
        if (flight.isNew()) {
            flight.setCreateDate(new Date());
            flight.setCreatedBy(getAuthenticatedUser());
            flight.setMissingEmployee(false);
        } else {
        	flight.setMissingEmployee(false);
            flight.setUpdateDate(new Date());
            flight.setUpdatedBy(getAuthenticatedUser());
        }
        //assignAirplaneToFlight(flight);
        flightRepository.save(flight);
        try {
        	assignPilotsToFlight(flight);
        	assignCrewToFlight(flight);
        } catch (NullPointerException e) {
        	PrimeFaces.current().executeScript("PF('selectPlaneFirstDialog').show()");
        }
        logService.logFlightUpdate(flight.getFlightNumber(), getAuthenticatedUser());
        return flightRepository.save(flight);
    }
    
    /**
     * Save and log a flight. The flight is not edited in this method.
     * 
     * @param flight is the flight to be stored
     * @return the stored flight
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Flight saveFlightWithoutAssigning(Flight flight) {
        if (flight.isNew()) {
            flight.setCreateDate(new Date());
            flight.setCreatedBy(getAuthenticatedUser());
            flight.setMissingEmployee(false);
        } else {
            flight.setUpdateDate(new Date());
            flight.setUpdatedBy(getAuthenticatedUser());
        }
        logService.logFlightUpdate(flight.getFlightNumber(), getAuthenticatedUser());
        return flightRepository.save(flight);
    }
    
    /**
     * Deletes the flight.
     *
     * @param the flight to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public void deleteFlight(Flight flight) {
    	if (flight.getAirplane()!=null) {
    		flight.getAirplane().setIsAssignedToFlight(false);
    		airplaneRepository.save(flight.getAirplane());
    	}
    	if (flight.getPersonal()!=null) {
    		flight.getPersonal().clear();
    		/*for (User u : flight.getPersonal()) {
    			//u.setIsAssignedToFlight(false);
    			userRepository.save(u);
    		}*/
    	}
        flightRepository.delete(flight);
        logService.logFlightDeleted(flight.getFlightNumber(), getAuthenticatedUser());
    }
    
    /**
     * Method returns the authenticated user
     * @return - authenticated user
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

    public Flight findFlightByPlane(Airplane airplane) {
    	return flightRepository.findByAirplane(airplane).get(0);
    }
    public void assignAirplaneToFlight (Flight flight) {
		List <Airplane> allAirplanes=airplaneRepository.findAll();
		
		for (Airplane a : allAirplanes) {
			if(isAvailable(a,flight)){
				a.setIsAssignedToFlight(true);
				airplaneRepository.save(a);
				flight.setAirplane(a);
				flight.setNumPassengers(a.getNumSeats());
				return;
			}
		}
    }
    public void assignPilotsToFlight (Flight flight) {
		List <User> allPilots=userRepository.findByRole(UserRole.PILOT);
		Collections.shuffle(allPilots);
		int numPilots=flight.getAirplane().getNumPilotsNecessary();
		for (User u : allPilots) { 
			if (isAvailable(u, flight)) {
			//u.setIsAssignedToFlight(true); 
			//userRepository.save(u);
			//System.out.println(u.getUsername());
			flight.getPersonal().add(u);
			//System.out.println("heeeeeeeeeeeeeeee");
			numPilots--;
			if (numPilots==0) return; } }
		if (numPilots>0) //System.out.println("NOT ENOUGH PILOTS");
			flight.setMissingEmployee(true);
			PrimeFaces.current().executeScript("PF('notEnoughtCrewDialog').show()");
	}
    public void assignCrewToFlight (Flight flight) {
		List <User> allCrew=userRepository.findByRole(UserRole.CREW);
		Collections.shuffle(allCrew);
		int numCrew=flight.getAirplane().getNumCabinCrewNecessary();
		for (User u : allCrew) { 
			if (isAvailable(u, flight)) {
			//u.setIsAssignedToFlight(true); 
			//userRepository.save(u);
			
			flight.getPersonal().add(u);
			//System.out.println(u.getUsername());
			numCrew--;
			if (numCrew==0) return; } }
		if (numCrew>0) //System.out.println("NOT ENOUGH CABIN CREW");
			flight.setMissingEmployee(true);
			PrimeFaces.current().executeScript("PF('notEnoughtPilotsDialog').show()");
	}
    
    /**
     * Method assigns only one crew member to the given flight. The role of the crew member is
     * described by the userRole.
     * 
     * @param flight - is the flight with the missing crew member.
     * @param userRole - describes the role of the missing crew member.
     * @return the flight with a new added crew member
     * @throws NoEmployeeAvailableException if no employee is available
     */
    public Flight assignCrewMemberToFlight(Flight flight, UserRole userRole) throws NoEmployeeAvailableException{
    	List <User> allEmployees=userRepository.findByRole(userRole);
		boolean employeeFound = false;
		for(User u : allEmployees){
			if(isAvailable(u, flight)){
				//u.setIsAssignedToFlight(true);
				//userRepository.save(u);
				flight.getPersonal().add(u);
				flight.setMissingEmployee(false);
				
				flight = flightRepository.save(flight);
				employeeFound = true;
			}
		}
		if(employeeFound == false && flight.getMissingEmployee() == true){
			throw new NoEmployeeAvailableException();
		}
		return flight;
    }
    
    /*public boolean isAvailable(User u, Flight flight) {
    	return !u.getIsAssignedToFlight() &&
    			u.getLocationCode().equals(flight.getCodeAirportDeparture()) &&
    			TimeUnit.HOURS.convert(System.currentTimeMillis() - u.getTimeSinceLastFlight().getTime(), TimeUnit.MILLISECONDS)>=12 &&
    			TimeUnit.HOURS.convert(u.getHoursWorkedThisWeek().getTime(), TimeUnit.MILLISECONDS)<40;
    }*/
    
    /**
     * Method finds all flights from user
     * @param user
     * @return all flights from given user
     */
    private List<Flight> findAllFlightsFromUser(User user){
    	Set<User> userSet = new HashSet<>();
    	userSet.add(user);
    	List<Flight> flights = flightRepository.findByPersonal(userSet);
    	return flights;
    }
    
    /**
     * Method finds the last airport from given user at the wished start date
     * 
     * @param user
     * @param wishedStartDate - departure date of the flight
     * @return the last airport from given user at the wished start date
     */
    private String findLastAirportFromUser(User user, Date wishedStartDate){
    	List<Flight> flights = findAllFlightsFromUser(user);
    	if(flights.isEmpty()){
    		return user.getLocationCode();
    	}
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	Flight lastFlight = flights.get(0);
    	for(Flight f : flights){
			try {
				Date flightDate = df.parse(f.getDepartureDateTime());
	    		Date minimalFlight = df.parse(lastFlight.getDepartureDateTime());
	    		if(flightDate.before(wishedStartDate) && flightDate.after(minimalFlight)){
	    			lastFlight = f;
	    		}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		
    	}
    	return(lastFlight.getCodeAirportArrival());
    	
    }
    
    /**
     * Method finds the last airport form given airplane at wished start date
     * 
     * @param airplane
     * @param wishedStartDate - departure date of the flight
     * @return last airport form given airplane at wished start date
     */
    private String findLastAirportFromAirplane(Airplane airplane, Date wishedStartDate){
    	List<Flight> flights = flightRepository.findByAirplane(airplane);
    	if(flights.isEmpty()){
    		return airplane.getLocationCode();
    	}
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	Flight lastFlight = flights.get(0);
    	for(Flight f : flights){
			try {
				Date flightDate = df.parse(f.getDepartureDateTime());
	    		Date minimalFlight = df.parse(lastFlight.getDepartureDateTime());
	    		if(flightDate.before(wishedStartDate) && flightDate.after(minimalFlight)){
	    			lastFlight = f;
	    			System.out.println(f.getCodeAirportArrival());
	    		}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		
    	}
    	return(lastFlight.getCodeAirportArrival());
    	
    }
    
    /**
     * Checks if the flightDate is in a free period from the user
     * no other flight should be in this period
     * 
     * @param user
     * @param flightDate
     * @return true if the flightDate is possible, otherwise false
     */
    private boolean isFlightPossibleDueDates(User user, Pair flightDate){
    	Pair pufferHours = flightDate;
    	pufferHours.addHoursToPair(12);
    	Set<Pair> flightDates = vacationService.getFlightDatesFromUser(user);
    	if(flightDates.isEmpty()){
			return true;
		}
		for(Pair p : flightDates){
			if(p.isInSameTimePeriod(pufferHours)){
				return false;	
			}
		}
		return true;
    }
    
    /**
     * Method return the flight dates from the airplane
     * 
     * @param airplane
     * @return a set of flight dates
     */
    public Set<Pair> getFlightDatesFromAirplane(Airplane airplane){
    	List<Flight> flights = flightRepository.findByAirplane(airplane);
    	Set<Pair> flightDates = new HashSet<>();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	for(Flight f : flights){
    		try {
				flightDates.add(new Pair(df.parse(f.getDepartureDateTime()), df.parse(f.getArrivalDateTime())));
    		} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return flightDates;
    }
    
    /**
     * Method checks if the airplane has no other flight at flightDate
     * 
     * @param airplane
     * @param flightDate
     * @return true, if the flight is possible, otherwise false
     */
    private boolean isFlightPossibleDueDates(Airplane airplane, Pair flightDate){
    	Pair pufferHours = flightDate;
    	pufferHours.addHoursToPair(2);
    	Set<Pair> flightDates = getFlightDatesFromAirplane(airplane);
    	if(flightDates.isEmpty()){
    		return true;
		}
    	else{
			for(Pair p : flightDates){
				if(p.isInSameTimePeriod(pufferHours)){
					return false;
				}
			}
    	}
		
		
		return true;
    }
    
    /**
     * Method checks if the user is for a given flight available. User has no vacation
     * at this time. User has no other flight at this time. The puffer hours are also 
     * checked. But it is not checked, if an user works more than 40 hours in one week.
     * Furthermore it is checked, if the user is at the airport.
     * 
     * @param user
     * @param flight
     * @return true if the user is available, otherwise false
     */
    public boolean isAvailable(User user, Flight flight){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	Pair requestedFlightDate;
		try {
			requestedFlightDate = new Pair(df.parse(flight.getDepartureDateTime()), df.parse(flight.getArrivalDateTime()));
			//System.out.println(isFlightPossibleDueDates(user, requestedFlightDate));
			if(findLastAirportFromUser(user, df.parse(flight.getDepartureDateTime())).equals(flight.getCodeAirportDeparture()) &&
	    			TimeUnit.HOURS.convert(user.getHoursWorkedThisWeek().getTime(), TimeUnit.MILLISECONDS) < 40 && 
	    			checkVacationTime(requestedFlightDate, user)
	    			&& isFlightPossibleDueDates(user, requestedFlightDate)){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (VacationAlreadyInPeriodException e){
			return false;
		}
		return false;
    }
    
    public boolean checkVacationTime(Pair newVac, User user) throws VacationAlreadyInPeriodException{
    	Set<Pair> vacList = user.getVacationList();
    	//Pair newVac = new Pair(start, end);
    	for(Pair v : vacList){
    		if(v.isInSameTimePeriod(newVac)){
    			throw new VacationAlreadyInPeriodException();
    			//return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Method checks if an airplane is available for the given flight. There is no other flight
     * in this period. The airplane is at the airport.
     * 
     * @param airplane
     * @param flight
     * @return true if the flight is possible, otherwise false
     */
    public boolean isAvailable(Airplane airplane, Flight flight){
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	Pair requestedFlightDate;
		try {
			requestedFlightDate = new Pair(df.parse(flight.getDepartureDateTime()), df.parse(flight.getArrivalDateTime()));
			//System.out.println(isFlightPossibleDueDates(airplane, requestedFlightDate));
			if(findLastAirportFromAirplane(airplane, df.parse(flight.getDepartureDateTime())).equals(flight.getCodeAirportDeparture()) &&
	    			isFlightPossibleDueDates(airplane, requestedFlightDate)){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    /**
     * All flights with missing employees (crew member or pilot) are searched and put into
     * a collection.
     * 
     * @return a all flights with missing employees
     */
    public Collection<Flight> getAllMissingEmployeeFlights(){
    	return flightRepository.findAllMissingEmployee();
    }
    public Collection<Flight> getAllMissingAirplaneFlights(){
    	return flightRepository.findAllMissingAirplane();
    }
    
    /**
     * Method checks if the given flight has the correct number of personal. This issue is splitted
     * into the correct number of pilots and the correct number of cabin crew members.
     *     
     * @param flight - flight to be checked, concerning correct number of personal.
     * @return true if number of pilots and number of cabin crew fulfill the restrictions.
     */
    public boolean hasFlightCorrectNumberOfPersonal(Flight flight){
    	int neccCabinCrew = flight.getAirplane().getNumCabinCrewNecessary();
    	int neccPilots = flight.getAirplane().getNumPilotsNecessary();
    	Set<User> personal = flight.getPersonal();
    	for(User p : personal){
    		if(p.getRoles().iterator().next().equals(UserRole.PILOT)){
    			neccPilots--;
    		}
    		else{
    			neccCabinCrew--;
    		}
    	}
    	if(neccPilots == 0 && neccCabinCrew == 0){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Checks the departure and arrival time.
     * 
     * @param departure
     * @param arrival
     * @return true if departure is not before arrival, and dates are in future
     * @throws ArrivalBeforeDepartureException
     * @throws FlightInPastException
     */
    public boolean checkDepartureAndArrivalTime(Date departure, Date arrival) throws ArrivalBeforeDepartureException, FlightInPastException {
    	if(arrival.before(departure)){
    		throw new ArrivalBeforeDepartureException();
    	}
    	else if(departure.before(new Date()) || arrival.before(new Date())){
    		throw new FlightInPastException();
    	}
    	return true;
    }
}