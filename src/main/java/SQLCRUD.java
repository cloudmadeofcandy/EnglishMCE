import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLCRUD {

    private final JDBCClient jdbc;
    private List<JsonObject> objects = new ArrayList<>();
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

    public List<JsonObject> getObjects() {
        return objects;
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


//    public void getAll(RoutingContext routingContext) {
//        this.jdbc.getConnection(ar -> {
//            SQLConnection connection = ar.result();
//            connection.query("select * from question", result -> {
//                List<JsonObject> results = new ArrayList<>();
//                results = result.result().getRows();
//                routingContext.response()
//                        .putHeader("content-type", "application/json; charset=utf-8")
//                        .end(Json.encodePrettily(results));
//                connection.close(); // Close the connection
//            });
//        });
//    }

    public void getAllret(RoutingContext routingContext) {
        objects = new ArrayList<>();
        this.jdbc.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query("select * from question", result -> {
                List<JsonObject> results = new ArrayList<>();
                results = result.result().getRows();
                for (JsonObject i: results) {
                    this.getObjects().add(i);
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(results));
                connection.close(); // Close the connection
            });
        });
    }


    public void insertRecord(JsonObject info, RoutingContext ctx) {
         this.jdbc.getConnection(event -> {
             if (event.succeeded()) {
                 SQLConnection connection = event.result();
                 java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
                 System.out.println(date);
                 connection.updateWithParams("INSERT INTO RECORD (name, testdate, result) VALUES (?, ?, ?)", new JsonArray().add(info.getValue("name")).add(date).add(info.getValue("result")), event1 -> {
                     if(event1.failed()) {
                         event1.cause().printStackTrace();
                         connection.close();
                     }
                     else {
                         System.out.println(event1.result().toJson());
                         connection.close();
                     }
                 });
             }
         });
    }

    public int returnGrade(List<JsonObject> testresults, JsonObject testanswer) {
        int grade = 0;
        for (JsonObject i: testresults) {
            if (i.getString("answer").equals(testanswer.getString(i.getString("ID")))) {
                System.out.println((i.getString("answer") + " ? " + testanswer.getString(i.getString("ID"))));
                grade++;
            }
        }
        return grade;
    }


}
