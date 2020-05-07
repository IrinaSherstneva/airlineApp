package at.qe.sepm.skeleton.ui.controllers;

import java.io.Serializable;
import java.util.Date;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.EndBeforeStartException;
import at.qe.sepm.skeleton.model.NoVacationDaysLeftException;
//import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.VacationAlreadyInPeriodException;
import at.qe.sepm.skeleton.model.VacationPeriodMismatchException;
import at.qe.sepm.skeleton.services.FlightAssignmentCancelVacationException;
//import at.qe.sepm.skeleton.services.UserService;
import at.qe.sepm.skeleton.services.VacationService;

@Component
@Scope("view")
public class VacationDetailController implements Serializable{

	private static final long serialVersionUID = 1L;

    @Autowired
    private VacationService vacationService;
    
    private Date start;
    private Date end;
    private String errMsg;
    private String sucMsg;
    
    public void setStart(Date start){
    	//this.start = new Date();
    	this.start = start;
    }
    
    public void setEnd(Date end){
    	//this.end = new Date();
    	this.end = end;
    }
    
    public void setErrMsg(String errMsg){
    	this.errMsg = errMsg;
    }
    
    public void setSucMsg(String sucMsg){
    	this.sucMsg = sucMsg;
    }
   
    public Date getStart() {
    	return start;
    }
    
    public Date getEnd() {
    	return end;
    }
        
    public String getErrMsg(){
    	return errMsg;
    }
    
    public String getSucMsg(){
    	return sucMsg;
    }
    
    /**
     * Insert Vacation including error handling
     */
    public void doInsertVacation(){
    	try{
    		this.vacationService.insertVacation(start, end);
    		this.sucMsg = "The vacation was successfully recorded.";
    		PrimeFaces.current().executeScript("PF('successVacationDialog').show()");
    	} 
    	catch(NoVacationDaysLeftException e){
    		this.errMsg = "You don't have enough vacation days left!";
    		PrimeFaces.current().executeScript("PF('errorVacationDialog').show()");
    	}
    	catch(VacationAlreadyInPeriodException e){
    		this.errMsg = "You have already vacation days in this period. Please check!";
    		PrimeFaces.current().executeScript("PF('errorVacationDialog').show()");
    	}
    	catch(EndBeforeStartException e){
    		this.errMsg = "The end date cannot be before the start date. Please check!";
    		PrimeFaces.current().executeScript("PF('errorVacationDialog').show()");
    	}
    	catch(VacationPeriodMismatchException e){
    		this.errMsg = "The vacation cannot be in the past. Please check!";
    		PrimeFaces.current().executeScript("PF('errorVacationDialog').show()");
    	}
    	catch(FlightAssignmentCancelVacationException e){
    		this.errMsg = "You are assigned to a flight in this period. You don't have a permission for vacation in this period.";
    		PrimeFaces.current().executeScript("PF('errorVacationDialog').show()");
    	}
    	start = null;
    	end = null;
    }
}
