Project to demonstrate java spring hibernate example with annotated classes. 
Author: MJS 4.8.18.

This project demonstrates spring 4.0.3 and hibernate 4.3.5 using annotated 
classes to manipulate a 3 table database.  While this technology is somewhat 
out dated as of today (Spring 2018), it reflects coding methods that are only a few years 
old and are likely to be found in legacy code. 

The project was modified to allow use of JavaSE1.8 and a sample lambda was 
implemented to verify this version works.

The database tables are automatically created when running the application.  In 
production mode this feature would be changed.

Common database routines are demonstrated including save, getById, getAll, getCountOfRecords, 
update and delete.  A getAll "eager" method which includes foreign key values is also used to overcome 
lazy initialization issues when printing out foreign key data. For example, when displaying 
a town (ie. a coal camp), it is nice to display the company that built this town.  In order 
to do this an overloaded getAll method is supplied that can retrieve both the town and information 
about the company building the town. The original getAll method is more efficient but does not display as much data when, for example, a list of all towns are printed out.

All database routines have transaction management and exception handling. 

Configuration data (ie. database access information and class beans) has been put into two 
xml files.  A hibernate xml file (hibernate.cfg.xml) contains the database information, while 
a spring bean xml file (spring4.xml) contains the class beans.  It was felt that having the 
database info in a separate file was more modular than putting the info in a single 
applicationContext.xml file. 