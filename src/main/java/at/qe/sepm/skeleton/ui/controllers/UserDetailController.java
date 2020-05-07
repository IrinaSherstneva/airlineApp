package at.qe.sepm.skeleton.ui.controllers;


import at.qe.sepm.skeleton.model.NoEmployeeAvailableException;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.UserService;

import java.io.Serializable;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * Controller for the user detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class UserDetailController implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;
    

    /**
     * Attribute to cache the currently displayed user
     */
    private User user;
    private String[] roles;
    private String role;

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        this.roles = user.getRolesAsString();
        this.role = roles[0];
        doReloadUser();
    }
    
    public void setRoles(String[] roles){
    	this.roles = roles;
    }
    
    public void setRole(String role){
    	this.role = role;
    }
    
    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public User getUser() {
        return user;
    }
       
    public String[] getRoles(){
    	return roles;
    }
    
    public String getRole(){
    	return role;
    }
    

    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getUsername());
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
    	this.roles = new String[]{role};
    	user = this.userService.setRoles(user, roles);
    	if(this.userService.validateUser(user)){
			PrimeFaces.current().executeScript("PF('userEditDialog').hide()");
			this.userService.saveUser(user);
			
		}
		else{ 
			PrimeFaces.current().executeScript("PF('errorEditDialog').show()");
		}
    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        try {
			this.userService.deleteUser(user);
		} catch (NoEmployeeAvailableException e) {
			PrimeFaces.current().executeScript("PF('errorDeleteDialog').show()");
			//e.printStackTrace();
		}
        //user = null;
    }
               
}
