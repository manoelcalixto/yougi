########################################################################
# Adds two columns in the table city to represent its exact geographic coordinates.
# 23/10/2011
# Hildeberto Mendonca
# Version 0.1
alter table city add latitude varchar(15) null;
alter table city add longitude varchar(15) null;