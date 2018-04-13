package coalcamps.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import coalcamps.classes.CoalCompany;

import java.util.*; 

public class CoalCompanyDAOImpl implements CoalCompanyDAO {
			
	// This is the bean from the applicationContext xml file
	private SessionFactory sessionFactory;  
		
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
	} 
			
	// ------ Hibernate Database Methods ------
	public int saveCoalCompany(CoalCompany company) {
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
			if (tx != null) tx.rollback();
			System.out.println(company.toString() + 
					" could not be saved. Maybe it is already in the database? " + 
					" ---- Exception Name PVE ---- " + ex.toString() + ex.getMessage() );
		} catch (org.springframework.dao.DataIntegrityViolationException ex) {
			if (tx != null) tx.rollback();
			System.out.println(company.toString() + 
				" could not be saved. Maybe it is already in the database? " + 
				" ---- Exception Name DIVE---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println(company.toString() + " could not be saved. Maybe it already exists? " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	}  // end saveCoalCompany(CoalCompany company)		
			
	public CoalCompany getCoalCompanyById(int ID) {
		CoalCompany co = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			co = (CoalCompany) session.get(CoalCompany.class, ID);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get coal company by ID (" + ID + "). " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get coal company by ID (" + ID + "). " +
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return co;
	} // end getCoalCompanyById
			
	// Retrieve a list of all CoalCompanies from the database 
	@SuppressWarnings("unchecked")
	public List<CoalCompany> getCoalCompanies() {   
	    List<CoalCompany> list = null;
		Session session = null;
		Transaction tx = null;
	    try {
		    session = sessionFactory.openSession();
		    // must be beginTransaction, not getTransaction unless also tx.begin
		    tx = session.beginTransaction(); 
			// loadAll only for HibernatgeTemplates	
		    Query query = session.createQuery("FROM CoalCompany");
		    list = query.list();
		    if (!tx.wasCommitted()) tx.commit();
		} catch (Exception ex) {
			// even though exception is caught, lots of exception trace is printed to the console.
			System.out.println("Could not get list of coalCompanies. " + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
			if (tx != null) tx.rollback();
	    } finally { 
		    if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	} // end getCoalCompanies
 
	@SuppressWarnings("unchecked")
	public List<CoalCompany> getCoalCompaniesWRONG() {			
		List<CoalCompany> list = null;
		Session session = null;
		Transaction tx = null;
		try {  // session doesnt implement auto-closeable, so no try with resources.
			session = sessionFactory.openSession();
			tx = session.getTransaction();   // THIS IS WRONG, use beginTransaction!
		    Query query = session.createQuery("FROM CoalCompany");
		    list = query.list();
			tx.commit();
		} catch (Exception ex) {
			// ... exception handler goes here.
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
		return list;  
	}  // getCoalCompaniesWRONG
		
	public int getCoalCompanyCount( ) {
		int result = 0;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query query = session.createQuery("SELECT count(*) FROM CoalCompany");
			result = ((Long) query.uniqueResult()).intValue();
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get a count of coal companies in the database " + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not get a count of coal companies in the database " + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) {session.close();}
		} // end try-catch-finally
		return result;
	}  // end getCoalCompanyCount

	public void updateCoalCompany(CoalCompany co) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.update(co);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not update coal company in the database. " + co + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not update coal company in the database. " + co + 
					" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end updateCoalCompany		

	public void deleteCoalCompany(CoalCompany co) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.delete(co);
			tx.commit();
		} catch (org.hibernate.HibernateException ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not delete coal company from the database. " + co + 
					" ---- Hibernate Exception Name ---- " + ex.toString() + ex.getMessage() );
		} catch (Exception ex) {
			if (tx != null) tx.rollback();
			System.out.println("Could not delete coal company from the database. " + co + 
				" ---- Exception Name ---- " + ex.toString() + ex.getMessage() );
		} finally {
			if (session != null) session.close();
		} // end try-catch-finally
	} // end deleteCoalCompany		
		
} // end class CoalCompanyDAOImpl
