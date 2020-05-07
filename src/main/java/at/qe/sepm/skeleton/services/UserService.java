package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.NoEmployeeAvailableException;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.repositories.FlightRepository;
import at.qe.sepm.skeleton.repositories.UserRepository;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Service for accessing and manipulating user data.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("application")
public class UserService implements Serializable{

    
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private AuditLogService logService;
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private FlightService flightService;

    /**
     * Returns a collection of all users.
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Method checks if pilots or cabin crew members are missing at the flight and return all pilots or
     * all cabin crew members.
     * 
     * @param flight - the flight with missing employees
     * @return all pilots (if a pilot is missing) otherwise all cabin crew members.
     */
    public Collection<User> getAllUsersForErrorFlight(Flight flight){
    	int neccCabinCrew = flight.getAirplane().getNumCabinCrewNecessary();
    	Set<User> assignedEmployees = flight.getPersonal();
    	int cabinCrewTotal = 0;
    	for(User u : assignedEmployees){
    		if(u.getRoles().iterator().next().equals(UserRole.CREW)){
    			cabinCrewTotal++;
    		}
    	}
    	if(cabinCrewTotal < neccCabinCrew){
    		return userRepository.findByRole(UserRole.CREW);
    	}
    	else{
    		return userRepository.findByRole(UserRole.PILOT);
    	}
    }

    /**
     * Loads a single user identified by its username.
     *
     * @param username the username to search for
     * @return the user with the given username
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER') or principal.username eq #username")
    public User loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
     * Saves the user. This method will also set {@link User#createDate} for new
     * entities or {@link User#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link User#createDate}
     * or {@link User#updateUser} respectively.
     *
     * @param user the user to save
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public User saveUser(User user) {
        if (user.isNew()) {
            user.setCreateDate(new Date());
            user.setCreateUser(getAuthenticatedUser());
        } else {
            user.setUpdateDate(new Date());
            user.setUpdateUser(getAuthenticatedUser());
        }
        logService.logUserUpdate(user.getUsername(), getAuthenticatedUser());
        return userRepository.save(user);
    }

    /**
     * Deletes the user.
     *
     * @param user the user to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public void deleteUser(User user) throws NoEmployeeAvailableException{
    	//new AuditLogService().logUserDeleted(user.getUsername(), getAuthenticatedUser());
    	List<Flight> flights = new ArrayList<Flight>();
    	user.setActiveUser(false);
    	if(isUserAssignedToFlight(user)){
    		//System.out.println(user.getUsername());
    		flights = deleteUserFromFlight(user);
    		UserRole userRole = user.getRoles().iterator().next();
    		userRepository.save(user);
    		findNewUserForFlight(flights, userRole);
    	}
    	else{
    		userRepository.save(user);
    	}
        logService.logUserDeleted(user.getUsername(), getAuthenticatedUser());
        //System.out.println(userRole);
    }
    
    /**
     * Method checks if a user is assigned to any flight.
     * 
     * @param user - to check if he/she is assigned to flight.
     * @return true if user is not assigned to flight, otherwise false (not intuitive)
     */
    private boolean isUserAssignedToFlight(User user){
    	Set<User> userSet = new HashSet<>();
    	userSet.add(user);
    	if(!flightRepository.findByPersonal(userSet).isEmpty()){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Method deletes user from all flights (past and future)
     * 
     * @param user - to be deleted from all flights
     * @return the list of flights, where the user was deleted
     */
    private List<Flight> deleteUserFromFlight(User user){
    	List<Flight> flights = findAllFlightsFromUser(user);
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    	for(Flight f : flights){
    		try {
				if(df.parse(f.getDepartureDateTime()).after(new Date())){
					//System.out.println(f.getFlightNumber());
					f.getPersonal().remove(user);
					f.setMissingEmployee(true);
					flightRepository.save(f);
				}
				else{
					f.setMissingEmployee(false);
					flightRepository.save(f);
					flights.remove(f);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	return flights;
    }
    
    /**
     * Method finds employees with userRole for all of the given flights.
     * 
     * @param flights - a list of flights with missing personal
     * @param userRole - the role which is missing (pilot, crew)
     * @throws NoEmployeeAvailableException if no employee is available
     */
    private void findNewUserForFlight(List<Flight> flights, UserRole userRole) throws NoEmployeeAvailableException {
    	for(Flight f : flights){
    		f = flightService.assignCrewMemberToFlight(f, userRole);
    		flightRepository.save(f);
    	}
    }
    
    /**
     * Method finds all flights from given user
     * 
     * @param user
     * @return a list of the flights
     */
    private List<Flight> findAllFlightsFromUser(User user){
    	Set<User> userSet = new HashSet<>();
    	userSet.add(user);
    	List<Flight> flights = flightRepository.findByPersonal(userSet);
    	return flights;
    }
    
    /**
     * Method adds a certain employee given by username to flight. There is no further check if this
     * person can or should be added. Please do the testing before.
     * 
     * @param flight - flight with missing employee. Employee should be added there.
     * @param username - username states an user by its id (username)
     * @return the flight with inserted user
     */
    public Flight addPersonalToFlight(Flight flight, String username){
    	flight.getPersonal().add(userRepository.findFirstByUsername(username));    
    	return flight;
    }
    
    /**
     * Method sets the role to admin.
     * 
     * @return a set, where the role is inserted
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    private Set<UserRole> setAdminRole(){
    	Set<UserRole> roleList = new HashSet<>();
    	roleList.add(UserRole.ADMIN);
    	//roleList.add(UserRole.MANAGER);
    	//roleList.add(UserRole.PILOT);
    	//roleList.add(UserRole.CREW);
    	return(roleList);
    }
    
    /**
     * Method sets roles given by roles to a certain user.
     * 
     * @param user - the roles are set by this user
     * @param roles - the roles which the user should have
     * @return the user with inserted roles
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public User setRoles(User user, String[] roles){
    	Set<UserRole> roleList = new HashSet<>();
    	for(String s : roles){
    		if(s.equals("admin")){
    			roleList = setAdminRole();
    		}
    		else if(s.equals("manager")){
    			roleList.add(UserRole.MANAGER);
    			//roleList.add(UserRole.PILOT);
    	    	//roleList.add(UserRole.CREW);
    		}
    		else if(s.equals("pilot")){
    			roleList.add(UserRole.PILOT);
    		}
    		else if(s.equals("crew")){
    			roleList.add(UserRole.CREW);
    		}
    	}
    	user.setRoles(roleList);
    	return(user);
    }
    
    /**
     * Validates a user. Username, Firstname, lastname, mail, phone, password or roles are not allowed to
     * be empty.
     * 
     * @param user - to be validated
     * @return true if the user has no empty fields, otherwise false
     */
    public boolean validateUser(User user){
    	if(user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getPhone().isEmpty()
    			|| user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getRoles().isEmpty()){
    		return false;
    	}
    	return true;
    }
    
    /**
     * Method checks if the username is unique
     * 
     * @param user
     * @return true if the username is unique, otherwise false
     */
    public boolean isUsernameUnique(User user){
    	if(userRepository.findAllUsernames().contains(user.getUsername())){
    		return false;
    	}
    	return true;
    }
    
    /**
     * Method to generate the user number based on the username.
     * User number is never used in the programm. Only implemented
     * due to task description.
     * 
     * @param user
     * @return
     */
    public int generateUserNumberFromUsername(User user){
    	char[] letters = user.getUsername().toCharArray();
    	int sum = 0;
    	for (char letter : letters) {
    		sum = sum + (int)letter;
    	}
    	return sum;    	
    }
    
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

}
