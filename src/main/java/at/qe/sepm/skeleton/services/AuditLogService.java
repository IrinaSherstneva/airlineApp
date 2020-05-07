package at.qe.sepm.skeleton.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.AuditLogEntry;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.AuditLogEntryType;
import at.qe.sepm.skeleton.repositories.AuditLogRepository;

@Component
@Scope("application")
public class AuditLogService {
	
	@Autowired
	private AuditLogRepository repo;
	
	public void logUserUpdate(String username, User changeUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.USER_UPDATE);
		entry.setMessage("User " + username + " was updated by " + changeUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}
	public void logUserDeleted(String username, User deleteUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.USER_DELETE);
		entry.setMessage("User " + username + " was deleted by " + deleteUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}
	public void logAirplaneUpdate(String airplaneId, User changeUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.AIRPLANE_UPDATE);
		entry.setMessage("Airplane " + airplaneId + " was updated by " + changeUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}
	public void logAirplaneDeleted(String airplaneId, User deleteUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.AIRPLANE_DELETE);
		entry.setMessage("Airplane " + airplaneId + " was deleted by " + deleteUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}
	public void logFlightUpdate(long flightNumber, User changeUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.FLIGHT_UPDATE);
		entry.setMessage("Flight " + flightNumber + " was updated by " + changeUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}
	public void logFlightDeleted(long flightNumber, User deleteUser) {
		AuditLogEntry entry=new AuditLogEntry();
		entry.setType(AuditLogEntryType.FLIGHT_DELETE);
		entry.setMessage("Flight " + flightNumber + " was deleted by " + deleteUser.getUsername() +
				" on " + new Date());
		repo.save(entry);
	}

}
