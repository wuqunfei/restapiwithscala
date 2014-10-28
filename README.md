Java Rest API for neo4j
================

1. java version 1.7
2. scala version 2.11.0
3. http lib from http://www.bigbeeconsultants.co.uk/
4. scala neo4j from https://github.com/AnormCypher/AnormCypher
5. maven scala plugin http://davidb.github.io/scala-maven-plugin/example_compile.html

Example:
================
Neo4jREST connection = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);
String sql = "start n=node(*) where n.type = 'Country' return n.code as code, n.name as name";
CypherStatement cypherStatement = new CypherStatement(sql, new HashMap());
Stream<CypherResultRow> countries = cypherStatement.apply(connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) countries.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            System.out.println(row.asList());
            System.out.println(row.asMap());
            System.out.println(row.data());
            System.out.println(row.metaData());
 }