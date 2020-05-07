package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//@Entity
//@DiscriminatorValue("PER")
//@DiscriminatorValue("1")
// extends User 
public class Personal extends User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Boolean isAssignedToFlight;
	private String locationCode;
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeSinceLastFlight;
	@Temporal(TemporalType.TIMESTAMP)
	private Date hoursWorkedThisWeek;
	private int daysOfVacationLeft;
	
	//private User user;
	

	public Boolean getIsAssignedToFlight() {
		return isAssignedToFlight;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public Date getTimeSinceLastFlight() {
		return timeSinceLastFlight;
	}
	public Date getHoursWorkedThisWeek() {
		return hoursWorkedThisWeek;
	}
	public int getDaysOfVacationLeft() {
		return daysOfVacationLeft;
	}
	public void setIsAssignedToFlight(Boolean isAssignedToFlight) {
		this.isAssignedToFlight = isAssignedToFlight;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public void setTimeSinceLastFlight(Date timeSinceLastFlight) {
		this.timeSinceLastFlight = timeSinceLastFlight;
	}
	public void setHoursWorkedThisWeek(Date hoursWorkedThisWeek) {
		this.hoursWorkedThisWeek = hoursWorkedThisWeek;
	}
	public void setDaysOfVacationLeft(int daysOfVacationLeft) {
		this.daysOfVacationLeft = daysOfVacationLeft;
	}
	
	

}
