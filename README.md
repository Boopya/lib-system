## Creating the database
  1. Setup a database, using the Database Configuration Assistant. You can search it in the search box of Windows.
  2. Click next
  3. Choose "Create a database" and click next
  4. Choose "General Purpose or Transaction Processesing" and click next
  5. Put "libsysdb" in Global Database Name. The SID should also be "libsysdb". Click next
  6. Just click next again
  7. Choose "Use the same administrative password for all accounts" and type in "password" for the password. Click next, then yes.
  8. Choose "Use database file locations from template". Click next
  9. Just click next again
  10. Click next again
  11. Click next
  12. Click next
  13. Click finish
  14. After creating the database, open the command prompt and type in "sqlplus / as sysdba"
  15. You should be connected now to Oracle 11g. Then create a user and password by typing in "create user okichinko identified by okichinko;"
  16. After that, type "grant all privileges to okichinko;"
  17. Open SQL Developer, and go to "Databases Detected" in the Welcome page
  18. Click on "LIBSYSDB"
  19. You should see the LIBSYSDB under the Oracle connections now. Double click on it, and type in "okichinko" for both username and password.
  20. You should be successfully connected to the database now.

## Connecting the database to Netbeans project
  1. Download ojdbc6.jar
  2. Open Netbeans
  3. From menu bar, click Windows > Services
  4. Right click on Drivers > New Driver > Add, then select the ojdbc6.jar. Click OK. Oracle Thin should be added in Drivers already.
  5. Drop-down the Drivers folder, right-click on Oracle Thin, then click "Connect Using..."
  6. Fill the Service ID (SID), username, and password, then test the connection by clicking "Test Connection" (SID: libsysdb)
  7. Wait for a new database to pop-up. It must be something like "jdbc:oracle:thin:@localhost:1521:libsysdb"
  8. Right-click on it, then click Connect using the username and password (okichinko)
  
## Create new project
  1. Create a new project "Library System"
  2. Copy the java files in this repository
  3. Run the file to test connection
