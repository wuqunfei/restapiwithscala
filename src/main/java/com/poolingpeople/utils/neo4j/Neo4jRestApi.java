package com.poolingpeople.utils.neo4j;

import org.anormcypher.CypherResultRow;
import org.anormcypher.CypherStatement;
import org.anormcypher.Neo4jREST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StreamIterator;

import java.util.*;

/**
 * Created by qunfei on 10/28/14.
 */
public class Neo4jRestApi implements Neo4jRestApiAdapter {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Neo4jREST connection = null;
    Boolean isFilterName = true;

    public Neo4jRestApi(Neo4jREST rest, Boolean isFilterName) {
        this.isFilterName = isFilterName;
        if (rest == null) {
            this.connection = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);
        } else {
            this.connection = rest;
        }

    }

    public List<Map<String, Object>> runParametrizedCypherQuery(String query, Map<String, Object> params) {
        List<Map<String, Object>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(query, JavaConverters.mapAsScalaMapConverter(params).asScala().toMap(Predef.<Tuple2<String, Object>>conforms()));
        Stream<CypherResultRow> rows = cypherStatement.apply(this.connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> map = row.asMap();
            Map<String, Object> javaMap = JavaConverters.mapAsJavaMapConverter(map).asJava();
            if (this.isFilterName) {
                Map<String, Object> filterMap = new HashMap<>();
                Iterator<String> itt = javaMap.keySet().iterator();
                while (itt.hasNext()) {
                    String key = itt.next();
                    if (key.contains(".")) {
                        String[] keys = key.split("\\.");
                        String newKey = keys[keys.length - 1];
                        filterMap.put(newKey, javaMap.get(key));
                    }
                }
                data.add(filterMap);
            } else {
                data.add(javaMap);
            }

        }
        return data;
    }


    public List<Map<String, Object>> runParametrizedCypherQuery(String query) {
        return this.runParametrizedCypherQuery(query, new HashMap<String, Object>());
    }


    public List<Map<String, Object>> runOneColumnCypherQuery(String query, Map<String, Object> params) {
        List<Map<String, Object>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(query, JavaConverters.mapAsScalaMapConverter(params).asScala().toMap(Predef.<Tuple2<String, Object>>conforms()));
        Stream<CypherResultRow> rows = cypherStatement.apply(this.connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> map = row.asMap();
            if (row.metaData().columnCount() == 1) {
                scala.collection.immutable.List<String> keys = row.metaData().availableColumns();
                String key = keys.head();
                scala.Some some = (scala.Some) map.get(key);
                scala.collection.Map<String, Object> ds = (scala.collection.Map<String, Object>) some.x();
                scala.Some insideSome = (scala.Some) ds.get("data");
                scala.collection.immutable.Map<String, Object> insideds = (scala.collection.immutable.Map<String, Object>) insideSome.x();
                Map<String, Object> insideMap = JavaConverters.mapAsJavaMapConverter(insideds).asJava();
                data.add(insideMap);
            }
        }
        return data;
    }

    public List<Map<String, Map<String, Object>>> runMulticolumnCypherQuery(String query, Map<String, Object> params) {
        List<Map<String, Map<String, Object>>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(query, JavaConverters.mapAsScalaMapConverter(params).asScala().toMap(Predef.<Tuple2<String, Object>>conforms()));
        Stream<CypherResultRow> rows = cypherStatement.apply(this.connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> map = row.asMap();
            scala.collection.immutable.List<String> keys = row.metaData().availableColumns();
            scala.collection.Iterator<String> itKeys = keys.iterator();
            Map<String, Map<String, Object>> outsideMap = new HashMap<>();
            while (itKeys.hasNext()) {
                String key = itKeys.next();
                scala.Some some = (scala.Some) map.get(key);
                scala.collection.immutable.HashMap<String, Object> ds = (scala.collection.immutable.HashMap<String, Object>) some.x();
                scala.Some insideSome = (scala.Some) ds.get("data");
                scala.collection.immutable.Map<String, Object> insideds = (scala.collection.immutable.Map<String, Object>) insideSome.x();
                Map<String, Object> insideMap = JavaConverters.mapAsJavaMapConverter(insideds).asJava();
                outsideMap.put(key, insideMap);
            }
            data.add(outsideMap);
        }
        return data;
    }


    public String runQuery(String query, Map<String, Object> params) {
        CypherStatement cypherStatement = new CypherStatement(query, JavaConverters.mapAsScalaMapConverter(params).asScala().toMap(Predef.<Tuple2<String, Object>>conforms()));
        return cypherStatement.applyString(this.connection);
    }


    public boolean schemaIsCorrectlyLoaded() {
        List<Map<String, Object>> constraints = this.getConstraints();

        /*
          TODO: this is only temporarily for convenience and should be removed in production!
          We could define a map with constraints that have to be present. If one is missing we can set it.
          */
        if (constraints.isEmpty()) {
            this.logger.warn("DB schema is invalid! Setting UUID constraint!");
            this.createConstraint("UUID", "uuid");
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> getConstraints() {
        List<Map<String, Object>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(null, new scala.collection.immutable.HashMap<String, Object>());
        Stream<CypherResultRow> rows = cypherStatement.applyPath(this.connection, "schema/constraint");
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> map = row.asMap();
            Map<String, Object> javaMap = JavaConverters.mapAsJavaMapConverter(map).asJava();
            data.add(javaMap);
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> getIndexes() {
        List<Map<String, Object>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(null, new scala.collection.immutable.HashMap<String, Object>());
        Stream<CypherResultRow> rows = cypherStatement.applyPath(this.connection, "schema/index");
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> map = row.asMap();
            Map<String, Object> javaMap = JavaConverters.mapAsJavaMapConverter(map).asJava();
            data.add(javaMap);
        }
        return data;
    }

    @Override
    public void createIndex(String label, String property) {
        String query = "CREATE INDEX ON :" + label + "(" + property + ")";
        CypherStatement cypherStatement = new CypherStatement(query, new scala.collection.immutable.HashMap<String, Object>());
        cypherStatement.execute(this.connection);

    }

    @Override
    public void dropIndex(String label, String property) {
        String query = "DROP INDEX ON :" + label + "(" + property + ")";
        CypherStatement cypherStatement = new CypherStatement(query, new scala.collection.immutable.HashMap<String, Object>());
        cypherStatement.execute(this.connection);
    }

    @Override
    public void createConstraint(String label, String property) {
        String query = "CREATE CONSTRAINT ON (a:" + label + ") ASSERT a." + property + " IS UNIQUE";
        CypherStatement cypherStatement = new CypherStatement(query, new scala.collection.immutable.HashMap<String, Object>());
        cypherStatement.execute(this.connection);
    }

    @Override
    public void dropConstraint(String label, String property) {
        String query = "DROP CONSTRAINT ON (a:" + label + ") ASSERT a." + property + " IS UNIQUE";
        CypherStatement cypherStatement = new CypherStatement(query, new scala.collection.immutable.HashMap<String, Object>());
        cypherStatement.execute(this.connection);
    }
}
