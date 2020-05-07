package at.qe.sepm.skeleton.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Embeddable
public class Pair implements Serializable{
 
	private static final long serialVersionUID = 1L;
	
	public Date first;
    public Date second;
    
    /**
     * Constructor for a Pair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public Pair() {};
    public Pair(Date first, Date second) {
        this.first = first;
        this.second = second;
        
    }

    
    @Override
    public String toString() {
    	return this.first.toString()+" - "+this.second.toString();
    }
    
    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link Pair} to which this one is to be checked for equality
     * @return true if the underlying objects of the Pair are both considered
     *         equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair p = (Pair) o;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
    }
 

	/**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }
    
    public boolean betweenDates(Date date){
    	if(date.after(this.first) && date.before(this.second)){
    		return true;
    	}
    	return false;
    }

    public boolean isInSameTimePeriod(Pair period){
    	if(this.equals(period) || this.betweenDates(period.getFirst()) || this.betweenDates(period.getSecond())){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Method checks if a pair of dates is between to other pair of dates.
     * 
     * @param first - starting pair
     * @param second - ending pair
     * @return true if the pair is between, otherwise false
     */
    public boolean isBetweenPair(Pair first, Pair second){
    	if(this.first.after(first.getFirst()) && this.second.after(first.second) 
    			&& this.first.before(second.getFirst()) && this.second.before(second.getSecond())){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Checks if there is a free period in a set of dates.
     * 
     * @param pairs - set of pairs
     * @return true if there is a free period
     */
    public boolean isFreePeriodInSetOfDates(Set<Pair> pairs){
    	Iterator<Pair> it= pairs.iterator();
    	boolean isFree = false;
    	while(it.hasNext()){
    		Pair p = it.next();
    		if(this.first.after(p.getSecond())){
    			isFree = true;
    		}
    		if(it.hasNext()){
    			p = it.next();
    		}
    		else{
    			return isFree;
    		}
    		if(this.second.before(p.getFirst()) && isFree){
    			isFree = true;
    		}
    		else{
    			isFree = false;
    		}
    	}
    	return isFree;
    }
    
    /**
     * Method adds the given hours to the pair
     *    
     * @param hours - hours to be added (positive) or subtracted (negative)
     */
    public void addHoursToPair(int hours){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(this.second);
    	cal.add(Calendar.HOUR_OF_DAY, hours);
    	this.second = cal.getTime();
    	cal.setTime(this.first);
    	cal.add(Calendar.HOUR_OF_DAY, -hours);
    	this.first = cal.getTime();
    }
    
    /**
     * 
     * @return the first date of pair
     */
    public Date getFirst(){
    	return(this.first);
    }
    
    /**
     * 
     * @return the second date of pair
     */
    public Date getSecond(){
    	return(this.second);
    }
}
