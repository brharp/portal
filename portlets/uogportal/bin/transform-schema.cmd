java -Djdbc.drivers=org.postgresql.Driver -Djdbc.user=postgres -Djdbc.password=postgres ca.uoguelph.ccs.db.SchemaTool transform jdbc:postgresql://localhost/portal-unstable file:res/schema.xml
