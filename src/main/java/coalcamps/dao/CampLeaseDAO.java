package coalcamps.dao;

import coalcamps.classes.CampLease;
import java.util.List; 

public interface CampLeaseDAO {

	// ------ Hibernate database routines ------
	public int saveCampLease(CampLease lease);
			
	public CampLease getCampLeaseById(int ID) ;			
	public List<CampLease> getCampLeases(); 
	// Return all campLeases with either lazy or eager fetching  
	public List<CampLease> getCampLeases(boolean eager);
	public int getCampLeaseCount( );
		
	public void updateCampLease(CampLease e);

	public void deleteCampLease(CampLease e);
			
} // end interface CampLeaseDao