Technical Notes: MJS 4.8.18

Basic Purpose: Convert project from just using ApplicationContext.xml to 
both this file and hibernate.cfg.xml.  This breaks up the configuration 
into database (hibernate) and domain specific (applicationContext) parts. 

This worked without much trouble.  Couldn't find a full example on-line, but 
in hibernate its property name while in appContext its prop key.  Also 
no need for dataSource wrapper in hibernate, while you need one in appContext. 
Overall once this was started it worked well.

4.12.18 - Came back and improved db routines to include try blocks.
Changed save to return primary key values.