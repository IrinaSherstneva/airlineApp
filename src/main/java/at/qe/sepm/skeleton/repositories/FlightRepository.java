package at.qe.sepm.skeleton.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.qe.sepm.skeleton.model.Airplane;
import at.qe.sepm.skeleton.model.Flight;
import at.qe.sepm.skeleton.model.User;

public interface FlightRepository extends AbstractRepository<Flight, Long>{

	Flight findFirstByFlightNumber (long flightNumber);
		
	@Query("SELECT f FROM Flight f WHERE :usr MEMBER OF f.personal")
	List<Flight> findByPersonal(@Param("usr") Set<User> user);

	@Query("SELECT f FROM Flight f WHERE :plane = f.airplane")
	List<Flight> findByAirplane(@Param("plane") Airplane plane);

	
	@Query("SELECT f FROM Flight f WHERE f.missingEmployee = true AND f.airplane IS NOT NULL")
	Collection<Flight> findAllMissingEmployee();
	
	@Query("SELECT f FROM Flight f WHERE f.airplane IS NULL")
	Collection<Flight> findAllMissingAirplane();

	

}
