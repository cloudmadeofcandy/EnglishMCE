import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SQLCRUD {

    private final JDBCClient jdbc;

    public SQLCRUD(Vertx vertx) {
        JsonObject config = new JsonObject("{\n" +
                "  \"db\": {\n" +
                "    \"url\": \"jdbc:sqlserver://localhost:1433;databaseName=hello\",\n" +
                "    \"user\": \"sa\",\n" +
                "    \"password\": \"C@ndycloud1410\"\n" +
                "  }\n" +
                "}").getJsonObject("db");
        jdbc = JDBCClient.createShared(vertx,config,"EnglishMCETestBank");
    }

    public void startBackend(Handler<AsyncResult<SQLConnection>> next, Future<Void> fut) {
        this.jdbc.getConnection(ar -> {
            if (ar.failed()) {
                fut.onFailure(handler -> {
                    ar.cause();
                });
            } else {
                next.handle(Future.succeededFuture(ar.result()));
            }
        });
    }


    public void getAll(RoutingContext routingContext) {
        this.jdbc.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query("select * from question", result -> {
                List<JsonObject> results = new ArrayList<>();
                results = result.result().getRows();
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(results));
                connection.close(); // Close the connection
            });
        });
    }


}
