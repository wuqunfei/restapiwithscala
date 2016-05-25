Rest API for neo4j, mix java and scala
================
Why develop this lib?
================
In Java, there is not original and good api for neo4j REST service, so I just scala lib and java to build one.
Easy to convert neo4j Cypher Query Response, Easy to use in Java maven Dependency.

Dependency  
================
1. java version 1.7
2. scala version 2.11.0
3. maven scala plugin http://davidb.github.io/scala-maven-plugin/example_compile.html

Example:
================

```
1.Neo4jREST connection = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);
2.String sql = "start n=node(*) where n.type = 'Country' return n.code as code, n.name as name";
3.CypherStatement cypherStatement = new CypherStatement(sql, new HashMap());
4.Stream<CypherResultRow> countries = cypherStatement.apply(connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) countries.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            System.out.println(row.asList());
            System.out.println(row.asMap());
            System.out.println(row.data());
            System.out.println(row.metaData());
 }
```
