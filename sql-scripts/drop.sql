-- Run the script (F5) to drop all tables, then recreate the tables using the latest create.sql
drop table transaction purge;
drop table bookauthor purge;
drop table book purge;
drop table author purge;
drop table shelf purge;
drop table librarian purge;
drop table patron purge;
drop sequence patron_id_seq;
drop sequence transaction_id_seq;

COMMIT;