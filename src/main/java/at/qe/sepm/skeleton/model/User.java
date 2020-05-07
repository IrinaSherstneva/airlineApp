package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import java.util.Date;
//import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
//import javax.persistence.DiscriminatorColumn;
//import javax.persistence.DiscriminatorType;
//import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
//import javax.persistence.Inheritance;
//import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
//import javax.persistence.MappedSuperclass;
//import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import javax.persistence.JoinColumn;
import org.springframework.data.domain.Persistable;
//import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Entity representing users.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */

@Entity
public class User implements Persistable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 100)
    private String username;

    @ManyToOne(optional = false)
    private User createUser;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @ManyToOne(optional = true)
    private User updateUser;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int userNumber;
	private String locationCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date hoursWorkedThisWeek;
	private int daysOfVacationLeft;
	private boolean activeUser;
	
	@ElementCollection(targetClass = Pair.class, fetch = FetchType.EAGER)
	@CollectionTable(name="VACATION")
	@AttributeOverrides({
        @AttributeOverride(name="first", 
                           column=@Column(name="START_OF_VACATION")),
        @AttributeOverride(name="second", 
                           column=@Column(name="END_OF_VACATION"))
      })
	private Set<Pair> vacationList;	//stores the vacation of an user by start and end date

    boolean enabled;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "User_UserRole")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;				// tutor told me not to change it
    
    public boolean enoughVacationDaysLeft(int vacDays) throws NoVacationDaysLeftException{
    	if(this.daysOfVacationLeft - vacDays >= 0){
    		return true;
    	}
    	throw new NoVacationDaysLeftException();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
    	//store the hashed version of the password, instead of the plain text
    	this.password = new BCryptPasswordEncoder(12).encode(password);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
    public String getLocationCode() {
		return locationCode;
	}
	public Date getHoursWorkedThisWeek() {
		return hoursWorkedThisWeek;
	}
	public int getDaysOfVacationLeft() {
		return daysOfVacationLeft;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public void setHoursWorkedThisWeek(Date hoursWorkedThisWeek) {
		this.hoursWorkedThisWeek = hoursWorkedThisWeek;
	}
	public void setDaysOfVacationLeft(int daysOfVacationLeft) {
		this.daysOfVacationLeft = daysOfVacationLeft;
	}

    public Set<Pair> getVacationList() {
		return vacationList;
	}

	public void setVacationList(Set<Pair> vacationList) {
		this.vacationList = vacationList;
	}
	
	public void setActiveUser(boolean activeUser){
		this.activeUser = activeUser;
	}
	public boolean getActiveUser(){
		return activeUser;
	}
	
	/**
	 * Method changes the roles (enum) into an array of strings.
	 * @return the roles in an string array.
	 */
	public String[] getRolesAsString(){
		String[] userRoles = new String[roles.size()];
		int count = 0;
		for(UserRole u : roles){
			if(u.equals(UserRole.ADMIN)){
				userRoles[count] = "admin";
				count++;
			}
			else if(u.equals(UserRole.MANAGER)){
				userRoles[count] = "manager";
				count++;
			}
			else if(u.equals(UserRole.PILOT)){
				userRoles[count] = "pilot";
				count++;
			}
			else if(u.equals(UserRole.CREW)){
				userRoles[count] = "crew";
				count++;
			}
		}
		return userRoles;
	}
	
	@Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "at.qe.sepm.skeleton.model.User[ id=" + username + " ]";
    }

    @Override
    public String getId() {
        return getUsername();
    }

    public void setId(String id) {
        setUsername(id);
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }
    
    /**
     * Method return username with initial locationCode
     * @return String "username: locationCode"
     */
    public String userToString(){
    	return username + ": " + locationCode;
    }
    

}
