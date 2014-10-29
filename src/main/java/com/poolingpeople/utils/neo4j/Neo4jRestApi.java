package com.poolingpeople.utils.neo4j;

import org.anormcypher.CypherResultRow;
import org.anormcypher.CypherStatement;
import org.anormcypher.Neo4jREST;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StreamIterator;
import scala.collection.JavaConversions;
import scala.tools.cmd.gen.AnyVals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by qunfei on 10/28/14.
 */
public class Neo4jRestApi implements Neo4jRestApiAdapter {
    Logger logger = Logger.getLogger(Neo4jRestApi.class.getName());

    Neo4jREST connection = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);

    @Override
    public List<Map<String, Object>> runParametrizedCypherQuery(String query, Map<String, Object> params) {
        List<Map<String, Object>> data = new ArrayList<>();
        CypherStatement cypherStatement = new CypherStatement(query, JavaConverters.mapAsScalaMapConverter(params).asScala().toMap(Predef.<Tuple2<String, Object>>conforms()));
        Stream<CypherResultRow> rows = cypherStatement.apply(this.connection);
        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) rows.iterator();
        while (it.hasNext()) {
            CypherResultRow row = it.next();
            scala.collection.Map<String, Object> smap = row.asMap();
            java.util.HashMap<String, Object> jmap = new java.util.HashMap<>();
            scala.collection.Iterator<Tuple2<String, Object>> itt = smap.iterator();
            while (itt.hasNext()) {
                Tuple2<String, Object> tt = itt.next();
                jmap.put(tt._1(), tt._2());
            }
            data.add(jmap);
        }

        return data;
    }

    @Override
    public List<Map<String, Object>> runParametrizedCypherQuery(String query) {
        return this.runParametrizedCypherQuery(query, new java.util.HashMap<String, Object>());
    }

    @Override
    public boolean schemaIsCorrectlyLoaded() {
        return false;
    }

    @Override
    public List<Map<String, Object>> getConstraints() {
        return null;
    }

    @Override
    public List<Map<String, Object>> getIndexes() {
        return null;
    }

    @Override
    public void createIndex(String label, String property) {

    }

    @Override
    public void dropIndex(String label, String property) {

    }

    @Override
    public void createConstraint(String label, String property) {

    }

    @Override
    public void dropConstraint(String label, String property) {

    }
}
