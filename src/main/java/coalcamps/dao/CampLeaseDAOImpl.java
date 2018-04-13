package coalcamps.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import coalcamps.classes.CampLease;

import java.util.*;

public class CampLeaseDAOImpl implements CampLeaseDAO {
		
	// This is the dependency injected bean from the applicationContext xml file
	private SessionFactory sessionFactory;  
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
	}
	
	// ------ Hibernate Database Methods (in CRUD order) ------			
	public int saveCampLease(CampLease lease) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			// save and persist are slightly different
			result = (Integer) session.save(lease);  // save creates 3 of 3 records - MJS 4.1.18
			// persist creates 2 of 3 companies MJS 4.1.18 (randp id=8 rolled back).
			// session.persist(company);  // creates 2 of 3 companies 4.1.18 MJS
			tx.commit();
		} catch (org.hibernate.PropertyValueException | 
				 org.springframework.dao.DataIntegrityViolationException ex) {
				System.out.println(lease.toString() + 
					" could not be saved. Is it already in the database? " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
				if (tx != null) tx.rollback();
		} catch (Exception ex) {
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(lease.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	}  // end saveCampLease(CampLease lease)
				
	public CampLease getCampLeaseById(int ID) {
		CampLease lease = null;
		Session session = null;
		Transaction tx = null;
		try { // no try with resources since session not auto-closeable
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			lease = (CampLease) session.get(CampLease.class, ID);
			tx.commit();
		} catch (org.hibernate.PropertyValueException | 
				 org.springframework.dao.DataIntegrityViolationException ex) {
			if (tx != null) tx.rollback();
			System.out.println(lease.toString() + 
					" could not be saved. Is it already in the database? " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(lease.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return lease;
	} // end getCampLeaseById(int ID)
					
	// Return all CampLeases 
	@SuppressWarnings("unchecked")
	public List<CampLease> getCampLeases() {  
	    List<CampLease> list = null;
		Session session = null;
		Transaction tx = null;
		try {
		    session = sessionFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction(); 
			// loadAll only for HibernatgeTemplates	
		    Query query = session.createQuery("FROM CampLease");
		    list = query.list();
		    if (!tx.wasCommitted()) tx.commit();
		} catch (Exception ex) { 
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not get list of campLeases. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
		} finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCampLeases
	
	/*** 
	* Return all campLeases either with or without foreign key company 
	* already fetched/initialized. If eager is false use the default fetching scheme.
	 */
	@SuppressWarnings("unchecked")  // expect warning from query.list conversion
	public List<CampLease> getCampLeases(boolean eager) { 
		List<CampLease> ccList = null;
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
			    hql = "SELECT lease FROM CampLease AS lease LEFT JOIN FETCH lease.campLeased camp " + 
		    			" LEFT JOIN FETCH lease.companyLeasing co";
			    // hql = "FROM CampLease";
		    	Query query = session.createQuery(hql);
		    	ccList = query.list();   // old Way - ccList = template.find(hql);
		    	for (CampLease cc: ccList) {  // getCompanyBuilding is null here!
		    		if (cc != null && cc.getCampLeased() != null) {
		    			// System.out.println("Eager-loaded CampLeased " +cc.getCampLeased().getCampName());
		    		}
		    	}		    	
		    } else { // Could NOT  enforce lazy retrieval ... just live with default fetching. MJS 3.28.18
			    // template.loadAll(CampLease.class);  // hib3 only - can lead to lazy init error
		    	ccList = getCampLeases();
		    } // end if eager ... else
		    tx.commit();
	    }  catch (Exception ex ) {
	    	if (!tx.wasCommitted()) tx.rollback();
	    	System.out.println("Exception in getCampLeases(eager=" + eager + "). " + ex.getMessage());
	    	ex.printStackTrace();
	    } finally {
	    	if (session != null) session.close();	    	
	    }  // if eager retrieval or not
	    return ccList; // return ccList;  
	} // end getCampLeases

	public int getCampLeaseCount( ) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try { // no try with resources since session not auto-closeable
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query query = session.createQuery("SELECT count(*) FROM CampLease");
			result = ((Long) query.uniqueResult()).intValue();
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println(" Could not count camp leases. " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not count camp leases. " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	} // end getCampLeaseCount
		
	public void updateCampLease(CampLease lease) {
		Session session = null;
		Transaction tx = null;
		try { // no try with resources since session not auto-closeable
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.update(lease);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println(" Could not update camp lease. " + lease +  
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not update camp lease. " + lease + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end updateCampLease
	
	public void deleteCampLease(CampLease lease) {
		Session session = null;
		Transaction tx = null;
		try { // no try with resources since session not auto-closeable
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.delete(lease);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println(" Could not delete camp lease. " + lease +  
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(" Could not delete camp lease. " + lease + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end deleteCampLease
	
} // end class CampLeaseDaoImpl

