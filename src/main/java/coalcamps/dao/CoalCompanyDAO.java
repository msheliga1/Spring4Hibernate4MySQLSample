package coalcamps.dao;

import java.util.List;
import coalcamps.classes.CoalCompany; 

public interface CoalCompanyDAO {
		
	// ------ hibernate database routines ------
	public int saveCoalCompany(CoalCompany co);

	public CoalCompany getCoalCompanyById(int ID);
	// (no need to worry about lazy vs eager since no foreign keys).  
	public List<CoalCompany> getCoalCompanies();
	public List<CoalCompany> getCoalCompaniesWRONG();
	public int getCoalCompanyCount( );	

	public void updateCoalCompany(CoalCompany e);

	public void deleteCoalCompany(CoalCompany e);
		
} // end interface CoalCompanyDao

