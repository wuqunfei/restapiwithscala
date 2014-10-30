import com.poolingpeople.utils.neo4j.Neo4jRestApi;
import com.poolingpeople.utils.neo4j.Neo4jRestApiAdapter;
import org.anormcypher.CypherResultRow;
import org.anormcypher.CypherStatement;
import org.anormcypher.Neo4jREST;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StreamIterator;

import java.util.List;
import java.util.Map;

/**
 * Created by qunfei on 10/28/14.
 */
public class MyTest {
    public static void main(String args[]) {

        /***
         *
         create (germany {name:"Germany", population:81726000, type:"Country", code:"DEU"}),
         (france {name:"France", population:65436552, type:"Country", code:"FRA", indepYear:1790}),
         (monaco {name:"Monaco", population:32000, type:"Country", code:"MCO"});
         */

//
//        String sql = "start n=node(*) where n.type = 'Country' return n.code as code, n.name as name";
//        Neo4jREST connection = new Neo4jREST("localhost", 7474, "/db/data/", "", "", "cypher", false);
//        CypherStatement cypherStatement = new CypherStatement(sql, new HashMap());
//
//        Stream<CypherResultRow> countries = cypherStatement.apply(connection);
//        StreamIterator<CypherResultRow> it = (StreamIterator<CypherResultRow>) countries.iterator();
//        while (it.hasNext()) {
//            CypherResultRow row = it.next();
//            System.out.println(row.asList());
//            System.out.println(row.asMap());
//            System.out.println(row.data());
//            System.out.println(row.metaData());
//        }
//
//        System.out.print("finished");
        test();
    }

    public static void test() {
        Neo4jRestApiAdapter adapter = new Neo4jRestApi(null,true);
        java.util.HashMap<String, Object> params = new java.util.HashMap<>();
        params.put("pn", "Germany");
        List<Map<String, Object>> rows = adapter.runOneColumnCypherQuery("match (n) where n.name = {pn} return n", params);
//        List<Map<String, Object>> rows = adapter.runParametrizedCypherQuery("match (n),(u:person) where n.name = \"Germany\" and u.username=\"U-251051802\" return n.name,n.code");
        System.out.print(rows.size());
    }
}
