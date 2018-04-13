package coalcamps.classes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Mike Sheliga 3.31.18
 */
@Entity
@Table(name="Coal_Company")
public class CoalCompany implements Serializable {
	    
		static final long serialVersionUID = 1L;
		@Id
		@Column
		@GeneratedValue(strategy=GenerationType.AUTO) // using auto causes both save and persist problems.
		private int id;  
	    private String companyName; 
	    private int yearFounded; 
	    
	    // ----------------- Constructors --------------------------------------
	    public CoalCompany() { }
	    
	    public CoalCompany(String companyName, int yearFounded) {
	    	this.companyName = companyName;
	    	this.yearFounded = yearFounded;
	    }
	    
	    // --------------------- Standard Methods ----------------------
	    @Override public String toString() {
	    	return "Coal Company: ID: " + id + " Name: " + companyName + " Year Founded: " + yearFounded;
	    }
	    
	    // --------------------- Standard Getters and Setters --------------
	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	         this.id = id;
	    }

	    public String getCompanyName() {
	        return companyName;
	    }

	    public void setCompanyName(String companyName) {
	        this.companyName = companyName;
	    }

	    public int getYearFounded() {
	        return yearFounded;
	    }

	    public void setYearFounded(int yearFounded) {
	        this.yearFounded = yearFounded;
	    }

} // end class CoalCompany

