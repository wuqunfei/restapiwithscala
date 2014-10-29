package com.poolingpeople.utils.neo4j;
import java.util.List;
import java.util.Map;
/**
 * Created by alacambra on 24.06.14.
 */
public interface Neo4jRestApiAdapter {
    /**
     * executes a cypher query where the return attributes must be explicit indicated in the return statement.
     *
     * @param query
     * @param params
     * @return
     */
    public List<Map<String, Object>> runParametrizedCypherQuery(String query, Map<String, Object> params);

    /**
     * executes a cypher query where the return attributes must be explicit indicated in the return statement.
     *
     * @param query
     * @return
     * @deprecated
     */
    public List<Map<String, Object>> runParametrizedCypherQuery(String query);

    /**
     * executes a cypher query where the attributes must not be explicitly indicated. Only one column can be used.
     *
     * @param query
     * @return A list of maps with the json data field key values. Each new map represents a different row.
     */
    public List<Map<String, Object>> runOneColumnCypherQuery(String query, Map<String, Object> params);

    /**
     * executes a cypher query where the attributes must not be explicitly indicated. More than one column can be used.
     * it must return a list with the returned rows and its columns with their the key-value attributes
     *
     * @param query
     * @return A List<Map<String, Map<String,Object>>>
     */
    public List<Map<String, Map<String, Object>>> runMulticolumnCypherQuery(String query, Map<String, Object> params);

    /**
     * Returns the complete server response into a String
     */
    public String runQuery(String query, Map<String, Object> params);

    public boolean schemaIsCorrectlyLoaded();

    public List<Map<String, Object>> getConstraints();

    public List<Map<String, Object>> getIndexes();

    public void createIndex(String label, String property);

    public void dropIndex(String label, String property);

    public void createConstraint(String label, String property);

    public void dropConstraint(String label, String property);
}
