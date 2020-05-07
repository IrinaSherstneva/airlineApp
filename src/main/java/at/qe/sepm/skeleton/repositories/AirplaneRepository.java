package at.qe.sepm.skeleton.repositories;

import at.qe.sepm.skeleton.model.Airplane;

public interface AirplaneRepository extends AbstractRepository<Airplane, String>{

	Airplane findFirstByAirplaneId (String airplaneId);
}
