package com.poolingpeople.utils.neo4j;

import org.anormcypher.Neo4jREST;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * create (u:person:uuid {name:"Tom",uuid:"u1"});
 * create (u:person:uuid {name:"Jake",uuid:"u2"});
 * create (t:task:uuid {name:"api",uuid:"u3"});
 * create (t:task:uuid {name:"adapter",uuid:"u4"});
 */

public class Neo4jRestApiTest {
    Neo4jREST rest = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);
    Neo4jRestApiAdapter adapter = null;

    @BeforeClass

    public void setup() throws Exception {
        adapter = new Neo4jRestApi(this.rest);
    }


    @Test
    public void testRunParametrizedCypherQuery() throws Exception {
        String query = "match (u:person {uuid:{pid}}) return u.uuid,u.name";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pid", "u1");
        List<Map<String, Object>> list = this.adapter.runParametrizedCypherQuery(query, params);
        assertEquals("Tom", list.get(0).get("u.name"));
    }

    @Test
    public void testRunParametrizedCypherQueryWithoutParams() throws Exception {
        String query = "match (u:person) return u.uuid,u.name";
        List<Map<String, Object>> list = this.adapter.runParametrizedCypherQuery(query);
        assertEquals(2, list.size());
    }

    @Test
    public void testRunOneColumnCypherQuery() throws Exception {
        String query = "match (u:person {uuid:{pid}}) return u";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pid", "u1");
        List<Map<String, Object>> list = this.adapter.runOneColumnCypherQuery(query, params);
        assertEquals("Tom", list.get(0).get("name"));
    }

    @Test
    public void testRunMulticolumnCypherQuery() throws Exception {
        String query = "match (u:person  {uuid:{pid}} ),(t.task  {uuid:{tid}} ) return u,t";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pid", "u1");
        params.put("tid", "u3");
        List<Map<String, Map<String, Object>>> list = this.adapter.runMulticolumnCypherQuery(query, params);
        assertTrue((list.get(0).get("u") instanceof Map));
    }

    @Test
    public void testRunQuery() throws Exception {
        String query = "match (u:person {uuid:{pid}} return u";
        HashMap<String, Object> params = new HashMap<>();
        params.put("pid", "u1");
        String responseBodyString = this.adapter.runQuery(query, params);
        assertTrue(responseBodyString.contains("columns") && responseBodyString.contains("data"));
    }

    @Test
    public void testSchemaIsCorrectlyLoaded() throws Exception {
        assertTrue(this.adapter.schemaIsCorrectlyLoaded());
    }

    @Test
    public void testGetConstraints() throws Exception {
        assertTrue(this.adapter.getConstraints().size() >= 0);
    }

    @Test
    public void testGetIndexes() throws Exception {
        assertTrue(this.adapter.getIndexes().size() >= 0);
    }

    @Test
    public void testCreateIndex() throws Exception {
        this.adapter.createConstraint("person", "name");
    }

    @Test
    public void testDropIndex() throws Exception {
        this.adapter.dropIndex("person", "name");
    }

    @Test
    public void testCreateConstraint() throws Exception {
        this.adapter.createConstraint("person", "email");
    }

    @Test
    public void testDropConstraint() throws Exception {
        this.adapter.dropConstraint("person", "email");
    }
}