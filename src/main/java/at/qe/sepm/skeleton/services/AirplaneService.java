package at.qe.sepm.skeleton.services;

import java.util.Collection;
import java.util.Date;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.AirplaneRepository;
import at.qe.sepm.skeleton.repositories.UserRepository;

@Component
@Scope("application")
public class AirplaneService {

	@Autowired
    private AirplaneRepository airplaneRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private AuditLogService logService;
	
	/**
     * Returns a collection of all airplanes.
     *
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Collection<Airplane> getAllAirplanes() {
        return airplaneRepository.findAll();
    }
    /**
     * Loads a single airplane identified by its airplane id.
     *
     * @param airplaneId to search for
     * @return the airplane with the given airplaneId
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Airplane loadAirplane(String airplaneId) {
        return airplaneRepository.findFirstByAirplaneId(airplaneId);
    }
    /**
     * Saves the airplane. This method will also set {@link Airplane#createDate} for new
     * entities or {@link Airplane#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link User#createUser}
     * or {@link User#updateUser} respectively.
     *
     * @param the airplane to save
     * @return the updated airplane
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public Airplane saveAirplane(Airplane airplane) {
        if (airplane.isNew()) {
        	airplane.setCreateDate(new Date());
        	airplane.setCreateUser(getAuthenticatedUser());
        } else {
        	airplane.setUpdateDate(new Date());
        	airplane.setUpdateUser(getAuthenticatedUser());
        }
        logService.logAirplaneUpdate(airplane.getAirplaneId(), getAuthenticatedUser());
        return airplaneRepository.save(airplane);
    }
    
    /**
     * Deletes the airplane.
     *
     * @param the airplane to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public void deleteAirplane(Airplane airplane) {
    	airplaneRepository.delete(airplane);
    	logService.logAirplaneDeleted(airplane.getAirplaneId(), getAuthenticatedUser());
    }
    
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }
}
