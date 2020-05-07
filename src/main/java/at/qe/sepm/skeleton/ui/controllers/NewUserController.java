package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.primefaces.PrimeFaces;
//import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.UserService;

@Component
@Scope("view")
public class NewUserController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private UserService userService;
	private User user = new User();
	private String[] roles;
	private String role;
	@Autowired
	private IataDropdownController iataCodes;
	
	/**
	 * Getter initializes the user.
	 * @return user
	 */
	public User getUser(){
		user.setDaysOfVacationLeft(25);
		//user.setHoursWorkedThisWeek(LocalDateTime.parse("2019-01-01T00:00:00"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		try {
			user.setHoursWorkedThisWeek(df.parse("1970-01-01T00:00:00"));
			//user.setTimeSinceLastFlight(df.parse("1970-01-01T00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		user.setActiveUser(true);
		//user.setIsAssignedToFlight(false);
		user.setEnabled(true);
		//user.setEmail("@");
		
		return user;		
	}
	
	public void setUser(User user){
		this.user = user;
	}
		
	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void initUser(){
		this.user = new User();
		this.roles = null;
		this.role = null;
	}
	
	/**
	 * Method saves the user
	 */
	public void doSaveUser(){
		this.roles = new String[]{role};
		this.user.setUserNumber(this.userService.generateUserNumberFromUsername(user));
		this.user = this.userService.setRoles(user, roles);
		this.user.setLocationCode(iataCodes.getIataCode());
		if(this.userService.isUsernameUnique(user)){
			if(this.userService.validateUser(user)){
				PrimeFaces.current().executeScript("PF('userCreateDialog').hide()");
				this.userService.saveUser(user);
				
			}
			else{
				PrimeFaces.current().executeScript("PF('errorCreate').show()");
			}
		}
		else{
			PrimeFaces.current().executeScript("PF('errorUniqueUsernameDialog').show()");
		}
		
		
	}
	
	
	
	
}
