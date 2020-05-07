package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.ManyToOne;
import org.springframework.data.domain.Persistable;

@Entity
public class AuditLogEntry implements Persistable<Long>, Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long ID;
	@Column(length=250)
	private String message;
	@Enumerated(EnumType.ORDINAL)
	private AuditLogEntryType type;

	@Override
	public Long getId() {
		return this.ID;
	}

	@Override
	public boolean isNew() {
		return true;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AuditLogEntryType getType() {
		return type;
	}

	public void setType(AuditLogEntryType type) {
		this.type = type;
	}

	
	
	

}