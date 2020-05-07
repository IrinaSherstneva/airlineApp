package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.domain.Persistable;

/**
 */
@Entity
public class Airplane implements Persistable<String>, Serializable {

    private static final long serialVersionUID = 1L;
    
    @Override
	public String getId() {
		return airplaneId;
	}
	@Override
	public boolean isNew() {
		return (null == createDate);
	}
	@Override
    public String toString() {
        return airplaneType+ " #" + airplaneId + " with " + numSeats + " seats";
    }
	@Id 
	//@GeneratedValue(generator="system-uuid")
	//@GenericGenerator(name="system-uuid", strategy = "uuid")
    private String airplaneId;
    
 	@Enumerated(EnumType.STRING)
    private AirplaneType airplaneType;
    private int numPilotsNecessary;
    private int numCabinCrewNecessary;
    private int numSeats;
    private Boolean isAssignedToFlight;
    
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeLastFlightEnded;
    private String locationCode;
    
    
    @ManyToOne(optional = false)
    private User createUser;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @ManyToOne(optional = true)
    private User updateUser;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

	public String getAirplaneId() {
		return airplaneId;
	}
	public AirplaneType getAirplaneType() {
		return airplaneType;
	}
	public int getNumPilotsNecessary() {
		return numPilotsNecessary;
	}
	public int getNumCabinCrewNecessary() {
		return numCabinCrewNecessary;
	}
	public int getNumSeats() {
		return numSeats;
	}
	public Boolean getIsAssignedToFlight() {
		return isAssignedToFlight;
	}
	public String getTimeLastFlightEnded() {
		return timeLastFlightEnded.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toString();
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setAirplaneType(AirplaneType airplaneType) {
		this.airplaneType = airplaneType;
	}
	public void setNumPilotsNecessary(int numPilotsNecessary) {
		this.numPilotsNecessary = numPilotsNecessary;
	}
	public void setNumCabinCrewNecessary(int numCabinCrewNecessary) {
		this.numCabinCrewNecessary = numCabinCrewNecessary;
	}
	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}
	public void setIsAssignedToFlight(Boolean isAssignedToFlight) {
		this.isAssignedToFlight = isAssignedToFlight;
	}
	public void setTimeLastFlightEnded(String timeLastFlightEnded) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
		try {
		this.timeLastFlightEnded=LocalDateTime.parse(timeLastFlightEnded, formatter);
		} catch (Exception e) {
			System.out.println("Incorrect Date and Time Format");
		}
	}
	public void setTimeLastFlightEnded(LocalDateTime timeLastFlightEnded) {
		this.timeLastFlightEnded=timeLastFlightEnded;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public User getCreateUser() {
		return createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public User getUpdateUser() {
		return updateUser;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public void setAirplaneId(String airplaneId) {
		this.airplaneId = airplaneId;
	}
	
	
    
    
    
}