package coalcamps.classes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
// import javax.persistence.FetchMode; // wont work
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfiles;

@Entity
@Table(name="Coal_Camp")
// @FetchProfiles({ @FetchProfile(name = "camp_builder", fetchOverrides = 
// 	{ @FetchProfile.FetchOverride(entity = CoalCamp.class, association = "companyBuilding", mode = FetchMode.JOIN ) }) })
public class CoalCamp implements Serializable {
	
	static final long serialVersionUID = 1L;
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;  
	private String campName; 
	private int yearBuilt;
	@ManyToOne(fetch=FetchType.LAZY, optional=false)  // optional=false
	private CoalCompany companyBuilding;  // foreign key in DB
	
	// ------ Constructors ------------------------
	public CoalCamp( ) {};
	public CoalCamp(String campName, int yearBuilt, CoalCompany companyBuilding) {
		this.campName = campName;
		this.yearBuilt = yearBuilt;
		this.companyBuilding = companyBuilding;
	}
	
	// ------ standard getters and setters --------
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	
	public String getCampName() {return campName;}
	public void setCampName(String campName) {this.campName = campName;}

	public int getYearBuilt() {return yearBuilt;}	
	public void setYearBuilt(int yearBuilt) {this.yearBuilt = yearBuilt;}
	
	public CoalCompany getCompanyBuilding() {return companyBuilding;}
	public void setCompanyBuilding(CoalCompany companyBuilding) {this.companyBuilding = companyBuilding;}
	
	@Override
	public String toString() {
		String result = "CoalCamp: ID=" + id + " Name=" + campName + " Year Built=" + yearBuilt;
		if (companyBuilding == null) return result;	
		try {
			// next line produces a LazyInitializationException
			String company = " Company Building=" + companyBuilding.getCompanyName();
			result = result + company;
		} catch (org.hibernate.LazyInitializationException ex) {
			System.out.println("LazyInitializationException in CoalCamp.toString(). " + 
					"Returning result with no company.");
		} catch (Exception ex) {
			// Swallow exception, print result without company.
		}
		return result;
	}
	
} // end class CoalCamp
