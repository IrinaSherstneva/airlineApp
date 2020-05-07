package at.qe.sepm.skeleton.services;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.EndBeforeStartException;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.NoVacationDaysLeftException;
import at.qe.sepm.skeleton.model.Pair;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.model.VacationAlreadyInPeriodException;
import at.qe.sepm.skeleton.model.VacationPeriodMismatchException;
import at.qe.sepm.skeleton.repositories.FlightRepository;
import at.qe.sepm.skeleton.repositories.UserRepository;

@Component
@Scope("application")
public class VacationService implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private FlightRepository flightRepository;
	
	
	/**
     * Checks if the desired vacation period fits to the vacation list.
     * 
     * @param newVac is the desired vacation period given by Pair (start and end date of the period)
     * @return true if the vacation is possible, otherwise false
     */
    public boolean checkVacationTime(Pair newVac) throws VacationAlreadyInPeriodException{
    	Set<Pair> vacList = getAuthenticatedUser().getVacationList();
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
     * Checks if the start date of the vacation is before the end date.
     * 
     * @param start is the start date of the vacation
     * @param end is the end date of the vacation
     * @return true if the start date is before the end date, otherwise false
     */
    private boolean checkStartBeforeEnd(Date start, Date end) throws EndBeforeStartException{
    	if(start.before(end)){
    		return true;
    	}
    	//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", "Watch out for PrimeFaces."));
    	throw new EndBeforeStartException();
    	//return false;
    }
    
    /**
     * Method checks if to given dates are in the future
     * 
     * @param start
     * @param end
     * @return true if the dates are in the future
     * @throws VacationPeriodMismatchException
     */
    private boolean isPeriodInFuture(Date start, Date end) throws VacationPeriodMismatchException{
    	if(start.after(new Date()) && end.after(new Date())){
    		return true;
    	}
    	throw new VacationPeriodMismatchException();
    }
    
    /**
     * Method finds all flight dates from user
     * 
     * @param user
     * @return set of all flight dates
     */
	public Set<Pair> getFlightDatesFromUser(User user){
    	//System.out.println(user.getUsername());
    	Set<User> userSet = new HashSet<>();
    	userSet.add(user);
    	List<Flight> flights = flightRepository.findByPersonal(userSet);
    	Set<Pair> flightDates = new HashSet<>();
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	for(Flight f : flights){
    		//Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    		try {
				flightDates.add(new Pair(df.parse(f.getDepartureDateTime()), df.parse(f.getArrivalDateTime())));
    			//flightDates.add(new Pair(Date.parse(f.getDepartureDateTime()), Date.parse(f.getArrivalDateTime())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return flightDates;
    }
    
	/**
	 * Method checks if the user is not assigned to a flight in this period.
	 * @param user
	 * @param vacDates - period (start, end)
	 * @return true if the user is not assigned
	 * @throws FlightAssignmentCancelVacationException
	 */
    private boolean isNotAssignedToFlightInPeriod(User user, Pair vacDates) throws FlightAssignmentCancelVacationException {
    	if(user.getRoles().contains(UserRole.ADMIN) || user.getRoles().contains(UserRole.MANAGER)){
    		return true;
    	}
    	Set<Pair> flightDates = getFlightDatesFromUser(user);
    	for(Pair p : flightDates){
    		if(vacDates.isInSameTimePeriod(p)){
    			//return false;
    			throw new FlightAssignmentCancelVacationException();
    		}
    	}
    	return true;
    }
    
    /**
     * Computes the days between two days.
     * 
     * @param firstDate
     * @param secondDate
     * @return the number of days between first and second date
     */
    private long betweenDates(Date firstDate, Date secondDate) {
    	return TimeUnit.DAYS.convert(secondDate.getTime() - firstDate.getTime(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Insert the vacation to user's vacation list if there are enough days of vacation left,
     * there is no vacation of the user in the desired period and the start date is before the end date.
     * 
     * @param start date of the vacation
     * @param end date of the vacation
     * @return the saved user
     */
    public User insertVacation(Date start, Date end) throws NoVacationDaysLeftException, VacationAlreadyInPeriodException, EndBeforeStartException, VacationPeriodMismatchException, FlightAssignmentCancelVacationException{
    	User user = getAuthenticatedUser();
    	//System.out.println(user.getUsername());
    	Pair newVac = new Pair(start, end);
    	int daysBetween = (int)betweenDates(start, end);
    	if(checkVacationTime(newVac) && user.enoughVacationDaysLeft(daysBetween) && checkStartBeforeEnd(start,end)
    			&& isPeriodInFuture(start, end) && isNotAssignedToFlightInPeriod(user, newVac)){
    		user.getVacationList().add(newVac);
    		user.setDaysOfVacationLeft(user.getDaysOfVacationLeft()-daysBetween);
    	}
    	return userRepository.save(user);
    }
    
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

}
