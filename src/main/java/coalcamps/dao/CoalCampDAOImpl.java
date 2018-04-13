package coalcamps.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.*;
import coalcamps.classes.CoalCamp;

public class CoalCampDAOImpl implements CoalCampDAO {

    private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
    
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	// ------ Hibernate Database Methods ------	
	/*** 
	 * Save the CoalCamp to the database
	 */
	public int saveCoalCamp(CoalCamp company) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			// save and persist are slightly different - using GenerationType.IDENTITY or AUTO
			result = (Integer) session.save(company);  // save creates 3 of 3 records - MJS 4.1.18
			// persist creates 2 of 3 companies MJS 4.1.18 (randp id=8 rolled back).
			// session.persist(company);  // creates 2 of 3 companies 4.1.18 MJS
			tx.commit();
		} catch (org.hibernate.PropertyValueException ex) {
				System.out.println(company.toString() + 
					" could not be saved. Maybe it is already in the database? " + 
						" ---- Exception Name PVE ---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (org.springframework.dao.DataIntegrityViolationException ex) {
				System.out.println(company.toString() + 
					" could not be saved. Maybe it is already in the database? " + 
					" ---- Exception Name DIVE---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (Exception ex) {
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(company.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	}  // end saveCoalCamp(CoalCamp company)
	
	/*** 
	 * Return CoalCamp based upon primary key Id
	 */
	public CoalCamp getCoalCampById(int ID) {
		CoalCamp result = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			result = (CoalCamp) session.get(CoalCamp.class, ID);
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null) tx.rollback();
				System.out.println(" Could not get coal camp for ID (" + ID + "). " +
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not get coal camp for ID (" + ID + "). " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	} // end getCoalCampById
				
	/*** 
	 * Return a list of all CoalCamps
	 */
	@SuppressWarnings("unchecked")
	public List<CoalCamp> getCoalCamps() {  
	    List<CoalCamp> list = null;
		Session session = null;
		Transaction tx = null;
		try {
		    session = sessionFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction();  
			// loadAll only for HibernatgeTemplates	
		    Query query = session.createQuery("FROM CoalCamp");
		    list = query.list();
		    if (!tx.wasCommitted()) tx.commit();
		} catch (Exception ex) { 
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not get list of all coalCamps. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			ex.printStackTrace();
			if (tx != null) tx.rollback();
		    } finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCoalCamps
	
	/**
	* Return all coalCamps either with or without the foreign key company 
	* data already fetched. If eager is true use eager fetching/initialization, 
	* other use the default fetching strategy.
	*/
	@SuppressWarnings("unchecked")  // expect warning from query.list conversion
	public List<CoalCamp> getCoalCamps(boolean eager) { 
		List<CoalCamp> ccList = null;
		Session session = null;
		Transaction tx = null;
		String hql = null;
		try { 
			session = sessionFactory.openSession();  // not sure if to use classic subtype or not
			tx = session.beginTransaction();
		    if (eager) {
				// For hbm.xml files, We CAN force eager with either crit.createAlias(fkField, alias, LEFT_JOIN)
				// or with setFetchMode(fkField, FetchMode.EAGER). Works for crit or detachedCrit MJS 3.28.18
		    	// For Annotation Lazy Fetching (ie. @ManyToOne(fetch=FetchType.LAZY, optional=false), 
		    	// forced eager fetching after many, many tries.  MJS 4.1.18
		    	hql = "SELECT camp FROM CoalCamp camp LEFT JOIN FETCH camp.companyBuilding co";
		    	// hql = "FROM CoalCamp AS camp LEFT JOIN FETCH camp.companyBuilding";  // also works
		    	Query query = session.createQuery(hql);
		    	ccList = query.list();   // old Way - ccList = template.find(hql);
		    	for (CoalCamp cc: ccList) {  // getCompanyBuilding is null here!
		    		if (cc != null && cc.getCompanyBuilding() != null) {
		    			System.out.println("Eager-loaded CompanyBuilding " +cc.getCompanyBuilding().getCompanyName());
		    		}
		    	}    	
		    } else { // Want to enforce lazy retrieval for foreign keys here.
		    	// Could NOT do so ... just live with eager fetching if thats the default.		    		
		    	ccList = getCoalCamps();
		    } // end if eager ... else
		    tx.commit();
	    }  catch (Exception ex ) {
	    	if (!tx.wasCommitted()) tx.rollback();
	    	System.out.println("Exception in getCoalCamps(eager=" + eager + "). " + ex.getMessage());
	    	ex.printStackTrace();
	    } finally {
	    	if (session != null) session.close();	    	
	    }  // if eager retrieval or not
	    // return template.loadAll(CoalCamp.class);  // can lead to lazy init error
	    return ccList; // return ccList;  
	} // end getCoalCamps

	/*** 
	 * Return a count of the number of CoalCamps
	 */
	public int getCoalCampCount( ) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query query = session.createQuery("SELECT count(*) FROM CoalCamp");
			result = ((Long) query.uniqueResult()).intValue();
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null) tx.rollback();
				System.out.println(" Could not get count of coal camps in the database. " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not get count of coal camps in the database. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	} // end getCoalCampCount

	/*** 
	 * Updates the data in a CoalCamp database record
	 */
	public void updateCoalCamp(CoalCamp camp) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.update(camp);
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null) tx.rollback();
				System.out.println(" Could not update coal camp. " + camp + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not update coal camp. " + camp +  
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
	} // end updateCoalCamp
	
	/*** 
	 * Deletes the CoalCamp from the database
	 */
	public void deleteCoalCamp(CoalCamp camp) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.delete(camp);
			tx.commit();
		} catch (HibernateException ex) {
			if (tx != null) tx.rollback();
				System.out.println(" Could not delete coal camp. " + camp + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not delete coal camp. " + camp +  
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
	} // end deleteCoalCamp
			
} // end class CoalCampDAOImpl
