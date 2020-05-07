package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.domain.Persistable;

/**
 * Entity representing users.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Entity
public class Flight implements Persistable<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    //@Column(length = 100)
    private long flightNumber;
    
    @ManyToOne(optional = false)
    private User createdBy;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @ManyToOne(optional = true)
    private User updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    
    private String codeAirportDeparture;
    private String codeAirportArrival;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private boolean missingEmployee;
    
    //@ElementCollection(targetClass = User.class, fetch = FetchType.EAGER)
    //@CollectionTable(name = "Flight_Personal")
    //@Column(name = "Flight_Personal", nullable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Flight_Personal")
    private Set<User> personal;
    
    private int numPassengers;
    @OneToOne (optional = true)
    private Airplane airplane;

	@Override
	public Long getId() {
		return flightNumber;
	}
	public void setId(long id) {
		this.flightNumber = id;
    }

	@Override
	public boolean isNew() {
		return (null == createDate);
	}

	@Override
    public String toString() {
        return "Flight #" + flightNumber;
    }
	public long getFlightNumber() {
		return flightNumber;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getCodeAirportDeparture() {
		return codeAirportDeparture;
	}

	public void setCodeAirportDeparture(String codeAirportDeparture) {
		this.codeAirportDeparture = codeAirportDeparture;
	}

	public String getCodeAirportArrival() {
		return codeAirportArrival;
	}

	public void setCodeAirportArrival(String codeAirportArrival) {
		this.codeAirportArrival = codeAirportArrival;
	}

	public String getDepartureDateTime() {
		return departureDateTime.toString();
	}

	public void setDepartureDateTime(String departureDateTime) {
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.departureDateTime=LocalDateTime.parse(departureDateTime);
	}

	public String getArrivalDateTime() {
		return arrivalDateTime.toString();
	}

	public void setArrivalDateTime(String arrivalDateTime) {
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.arrivalDateTime=LocalDateTime.parse(arrivalDateTime);
	}

	public Set<User> getPersonal() {
		 if (personal == null) {
		        personal = new HashSet<>();
		    }
		return personal;
	}

	public void setPersonal(Set<User> personal) {
		this.personal = personal;
	}

	public Airplane getAirplane() {
		return airplane;
	}

	public void setAirplane(Airplane airplane) {
		this.airplane = airplane;
	}


	public void setFlightNumber(long flightNumber) {
		this.flightNumber = flightNumber;
	}

	public int getNumPassengers() {
		return numPassengers;
	}

	public void setNumPassengers(int numPassengers) {
		this.numPassengers = numPassengers;
	}
	
	public boolean getMissingEmployee(){
		return missingEmployee;
	}

	public void setMissingEmployee(boolean missingEmployee){
		this.missingEmployee = missingEmployee;
	}
}