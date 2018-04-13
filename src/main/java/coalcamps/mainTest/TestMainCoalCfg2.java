package coalcamps.mainTest;

import java.util.List;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import coalcamps.classes.CampLease;
import coalcamps.classes.CoalCamp;
import coalcamps.classes.CoalCompany;
import coalcamps.dao.CampLeaseDAO;
import coalcamps.dao.CoalCampDAO;
import coalcamps.dao.CoalCompanyDAO;
import java.util.logging.Level;
import java.util.stream.Stream; 

public class TestMainCoalCfg2 {
	
	// class to test a Legacy Spring Hibernate example using class annotations.
	// Note that some old features are allowed since this is a demonstration 
	// of older tech.  MJS 4.1.18
	

	public static void main(String[] args) {
		System.out.println("Starting TestMainCoalCfg2 main.");
		// Use spring.xml for spring3 example.
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring4.xml");	
		CoalCompanyDAO compDao = (CoalCompanyDAO) context.getBean("compDaoBean");  		
		CoalCampDAO campDao    = (CoalCampDAO)    context.getBean("campDaoBean");
		CampLeaseDAO leaseDao  = (CampLeaseDAO)   context.getBean("leaseDaoBean");
		// show fewer log msgs - way too many to read.
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
				
		if (compDao.getCoalCompanyCount() == 0) {
		 	addCoalCampData(compDao, campDao, leaseDao);
		}
				
		// Couldnt use lambdas originally as java version was 1.5 in project and 1.6 in pom.
		// Changed project properties->BuildPath->Libraries->Edit to 1.8JDK. 
		// Also verified compile options and changed pom.xml version.
		compDao.getCoalCompanies().stream().forEach((co)->System.out.println(co));
		System.out.println(" ------ getCoalCompanies(using lambda) above -------- ");
		
		boolean eager = true;  // LazyInitializationError if non-eager fetch
		campDao.getCoalCamps(eager).stream().forEach(System.out::println); 
		System.out.println(" ------ getCoalCamps(eager=true above) -------- ");
		// note lazy initialization => dont get back companyBuilding => toString
		// will throw exception which will be caught, and company wont be print.
		CoalCamp byId1 = campDao.getCoalCampById(1);  // note this cant be static since sessionFactory not static
		System.out.println("The coal camp with id=1 is " + byId1);
		
		for (CampLease cl: leaseDao.getCampLeases(eager)) {
			System.out.println(cl.toString());
		}
		System.out.println(" ------ getCampLeases(eager=true above) -------- ");
		
		// CoalCamp camp1 = getCoalCampByID(1);
		
		//close resources
		context.close();	
			
		System.out.println("Ending TestMainCoalCfg2");
	} // end main
		
		
	// addCoalCampData (will replicate data if it already exists).
	public static void addCoalCampData(CoalCompanyDAO compDao, CoalCampDAO campDao, CampLeaseDAO leaseDao) { 
		System.out.println("Inserting records into database.");

		CoalCompany co = null;		
		CoalCamp camp = null;
		CampLease lease = null;
	
		co = new CoalCompany("Rochester and Pittsburgh", 1885);
		CoalCompany randp = co;
		System.out.println("RandP id unset, value is " + randp.getId());
		co.setId(8);  // this has no effect upon save since id is generated as identity
		System.out.println("RandP id set to 8, value is " + randp.getId() + " ... saving .... ");
		compDao.saveCoalCompany(co);
		System.out.println("RandP saved, id is " + randp.getId());
		// R&P coal camps
		campDao.saveCoalCamp(new CoalCamp("Iselin", 1905, randp));
		// campDao.saveCoalCamp(new CoalCamp("Hart Town", 1906, randp));
		// campDao.saveCoalCamp(new CoalCamp("Whiskey Run", 1906, randp));	
		// campDao.saveCoalCamp(new CoalCamp("Nesbitt Run", 1906, randp));
		// campDao.saveCoalCamp(new CoalCamp("Earnest", 1910, randp));
		CoalCamp lucerne = new CoalCamp("Lucerne", 1912, randp);
		campDao.saveCoalCamp(lucerne);
		campDao.deleteCoalCamp(lucerne);
		
		co = new CoalCompany("Cambria Steel", 1852);
		compDao.saveCoalCompany(co);
		CoalCamp slickville = new CoalCamp("slickville", 1917, co);
		campDao.saveCoalCamp(slickville);
		slickville.setCampName("Slickville");
		campDao.updateCoalCamp(slickville);
		// campDao.saveCoalCamp(new CoalCamp("Wherum", 1890, co));
		// campDao.saveCoalCamp(new CoalCamp("Cokeville", 1910, co));
		
		co = new CoalCompany("Edwards", 1912);
		compDao.saveCoalCompany(co);
		// campDao.saveCoalCamp(new CoalCamp("Edwards", 1920, co));
		
		lease = new CampLease(slickville, randp, 1922, 1924);
		leaseDao.saveCampLease(lease);
	} // end addCoalCampData 
	
} // end class TestMainCoalCfg2