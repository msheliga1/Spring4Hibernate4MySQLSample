package coalcamps.dao;

import java.util.*; 
import coalcamps.classes.CoalCamp;

public interface CoalCampDAO {
	
	// ------ Hibernate Database Methods ------			
	public int saveCoalCamp(CoalCamp camp);
	
	public CoalCamp getCoalCampById(int ID);			
	public List<CoalCamp> getCoalCamps();
	public List<CoalCamp> getCoalCamps(boolean eager);		
	public int getCoalCampCount( );	

	public void updateCoalCamp(CoalCamp camp);

	public void deleteCoalCamp(CoalCamp camp);
			
} // end class CoalCampDAOImpl
