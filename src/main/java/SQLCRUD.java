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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        jdbc = JDBCClient.createShared(vertx, config, "EnglishMCETestBank");
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
                for (JsonObject i : results) {
                    this.getObjects().add(i);
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(results));
                connection.close(); // Close the connection
            });
        });
    }


    public void getAllAdmin(RoutingContext routingContext) {
        objects = new ArrayList<>();
        this.jdbc.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query("select * from human", result -> {
                List<JsonObject> results = new ArrayList<>();
                results = result.result().getRows();
                for (JsonObject i : results) {
                    this.getObjects().add(i);
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(results));
                connection.close(); // Close the connection
            });
        });
    }


    public void getAllrecords(RoutingContext routingContext) {
        objects = new ArrayList<>();
        this.jdbc.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query("select * from record", result -> {
                List<JsonObject> results = new ArrayList<>();
                results = result.result().getRows();
                for (JsonObject i : results) {
                    this.getObjects().add(i);
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(results));
                connection.close(); // Close the connection
            });
        });
    }


    public void createQuestion(JsonObject json, RoutingContext ctx) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                JsonArray arr = new JsonArray();
                for (Map.Entry<String, Object> i : json) {
                    arr.add(i.getValue());
                }
                if (arr.size() == 7) {
                    arr.remove(0);
                }
                connection.updateWithParams(
                        "INSERT INTO question (QUESTION, A, B, C, D, answer) " +
                                "VALUES (?, ?, ?, ?, ?, ?)", arr, event1 -> {
                            if (event1.failed()) {
                                connection.close();
                                ctx.response().setStatusCode(400).end("Sorry");
                                event1.cause().printStackTrace();
                            } else {
                                connection.close();
                                ctx.response().setStatusCode(200).end();
                            }
                        });
            }
        });
    }


    public void createAdmin(JsonObject json, RoutingContext ctx) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                JsonArray arr = new JsonArray();
                for (Map.Entry<String, Object> i : json) {
                    arr.add(i.getValue());
                }
                if (arr.size() == 3) {
                    arr.remove(0);
                }
                connection.updateWithParams("INSERT INTO human (NAME, PASSWORD) VALUES (?, ?)", arr, event1 -> {
                    if (event1.succeeded()) {
                        connection.close();
                        ctx.response().setStatusCode(200).end();
                    }
                    else {
                        connection.close();
                        event1.cause().printStackTrace();
                        ctx.response().setStatusCode(400).end();
                    }
                });

            }
        });
    }

    public void deleteQuestion(JsonObject id, RoutingContext routingContext) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                connection.execute("DELETE FROM question WHERE ID = '" + id.getString("ID") + "'", res -> {
                    if (res.succeeded()) {
                        routingContext.response().setStatusCode(200).end();
                    } else {
                        routingContext.response().setStatusCode(400).end();
                        res.cause().printStackTrace();
                    }
                    connection.close();
                });
            }
        });
    }

    public void deleteAdmin(JsonObject id, RoutingContext routingContext) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                connection.execute("DELETE FROM human WHERE ID = '" + id.getString("ID") + "'", res -> {
                    if (res.succeeded()) {
                        routingContext.response().setStatusCode(200).end();
                    } else {
                        routingContext.response().setStatusCode(400).end();
                        res.cause().printStackTrace();
                    }
                    connection.close();
                });
            }
        });
    }

    public void updateQuestion(JsonObject info, RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        String id = info.getString("ID");
        for (Map.Entry<String, Object> i : info) {
            arr.add(i.getValue());
        }
        arr.remove(0);
        arr.add(id);

        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                connection.updateWithParams("UPDATE question SET QUESTION = ?, A = ?, B = ?, C = ?, D = ?, answer = ? WHERE ID = ?"
                        , arr, result -> {
                            if (result.succeeded()) {
                                connection.close();
                                routingContext.response().setStatusCode(200).end();
                            } else {
                                connection.close();
                                routingContext.response().setStatusCode(400).end();
                                result.cause().printStackTrace();
                            }

                        });
            }
        });
    }

    public void updateAdmin(JsonObject info, RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        String id = info.getString("ID");
        for (Map.Entry<String, Object> i : info) {
            arr.add(i.getValue());
        }
        arr.remove(0);
        arr.add(id);

        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                connection.updateWithParams("UPDATE human SET PASSWORD = ? WHERE ID = ?"
                        , arr, result -> {
                            if (result.succeeded()) {
                                connection.close();
                                routingContext.response().setStatusCode(200).end();
                            } else {
                                connection.close();
                                routingContext.response().setStatusCode(400).end();
                                result.cause().printStackTrace();
                            }
                        });
            }
        });
    }

    public void returnUsers(JsonObject info, RoutingContext ctx) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                JsonArray arr = new JsonArray();
                for (Map.Entry<String, Object> i: info) {
                    arr.add(i.getValue());
                };
                connection.queryWithParams("SELECT * from human WHERE NAME = ? AND PASSWORD = ?",arr ,
                        event1 -> {
                    if (event1.result().getResults().size() != 0) {
                        connection.close();
                        ctx.response().setStatusCode(200);
                        ctx.response().sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/admininterface.html");
                    }
                    else {
                        System.out.println(arr.encodePrettily());
                        System.out.println("Checkpoint 3");
                        connection.close();
                        ctx.response().setStatusCode(400).end();
                    }
                        });
            }
        });
    }


    public void insertRecord(JsonObject info, RoutingContext ctx) {
        this.jdbc.getConnection(event -> {
            if (event.succeeded()) {
                SQLConnection connection = event.result();
                java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
                System.out.println(date);
                connection.updateWithParams("INSERT INTO RECORD (name, testdate, result) VALUES (?, ?, ?)", new JsonArray().add(info.getValue("name")).add(date).add(info.getValue("result")), event1 -> {
                    if (event1.failed()) {
                        event1.cause().printStackTrace();
                        connection.close();
                    } else {
                        System.out.println(event1.result().toJson());
                        connection.close();
                    }
                });
            }
        });
    }

    public int returnGrade(List<JsonObject> testresults, JsonObject testanswer) {
        int grade = 0;
        for (JsonObject i : testresults) {
            if (i.getString("answer").equals(testanswer.getString(i.getString("ID")))) {
                System.out.println((i.getString("answer") + " ? " + testanswer.getString(i.getString("ID"))));
                grade++;
            }
        }
        return grade;
    }




}
