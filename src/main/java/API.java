import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class API {
    private final Vertx vertx;
    private final JDBCPool pool;

    public API(Vertx vertx) {
        this.vertx = vertx;
        pool = JDBCPool.pool(vertx, new JsonObject("{\n" +
                "  \"db\": {\n" +
                "    \"url\": \"jdbc:sqlserver://localhost:1433;databaseName=hello\",\n" +
                "    \"user\": \"sa\",\n" +
                "    \"password\": \"C@ndycloud1410\"\n" +
                "  }\n" +
                "}").getJsonObject("db"));
    }

    public void getTest() {

    }

    public CompletableFuture<List<JsonObject>> getAll() {
        CompletableFuture<List<JsonObject>> completableFuture = new CompletableFuture<>();
        List<JsonObject> result = new ArrayList<>();
        pool
                .query("select QUESTION, A, B, C, D from question")
                .execute()
                .onFailure(Throwable::printStackTrace)
                .onSuccess(rows -> {
                    for (Row row : rows) {
                        JsonObject object = new JsonObject();
                        object.put("QUESTION", row.getString(0));
                        object.put("A", row.getString(1));
                        object.put("B", row.getString(2));
                        object.put("C", row.getString(3));
                        object.put("D", row.getString(4));
                        result.add(object);
                    }
                    completableFuture.complete(result);
                });
        return completableFuture;
    }






//    public void getAllQuestions() {
//        List<JsonObject> result = new ArrayList<>();
//        pool
//                .query("SELECT * FROM users")
//                .execute()
//                .onFailure(Throwable::printStackTrace)
//                .onSuccess(rows -> {
//                    for (Row row : rows) {
//                        JsonObject object = new JsonObject();
//                        object.put("staffName", row.getString(0));
//                        object.put("username", row.getString(1));
//                        object.put("pass", row.getString(2));
//                        result.add(object);
//                    }
//                });
//    }

//    public void getOneWithUsername(String username) {
//        pool
//                .preparedQuery("SELECT * FROM users WHERE username = ?")
//                .execute(Tuple.of(username))
//                .onFailure(e -> {
//                    e.printStackTrace();
//                })
//                .onSuccess(rows -> {
//                    List<JsonObject> result = new ArrayList<>();
//                    for (Row row : rows) {
//                        JsonObject object = new JsonObject();
//                        object.put("staffName", row.getString(0));
//                        object.put("username", row.getString(1));
//                        object.put("pass", row.getString(2));
//                        result.add(object);
//                    }
//                });
//    }


//    public void updateOne(String[] query) {
//        pool
//                .preparedQuery("UPDATE users SET staffName = ? , username = ?, pass = ? WHERE username = ?;")
//                .execute(Tuple.from(query))
//                .onFailure(Throwable::printStackTrace)
//                .onSuccess(rows -> {
//                    try {
//                        getAll(true);
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                });
//    }



//    public void deleteOne(String query) {
//        pool
//                .preparedQuery("DELETE FROM users WHERE username = ?;")
//                .execute(Tuple.of(query))
//                .onFailure(Throwable::printStackTrace)
//                .onSuccess(rows -> {
//                    getAll();
//                });
//    }
//
//    public void deleteAll() {
//        pool
//                .preparedQuery("DELETE FROM users;")
//                .execute()
//                .onFailure(Throwable::printStackTrace)
//                .onSuccess(rows -> {
//                    getAll();
//                });
//    }
//
//
//    public void addRecord(String[] query) {
//        pool
//                .preparedQuery("INSERT INTO users values (?, ?, ?)")
//                .execute(Tuple.from(query))
//                .onFailure(Throwable::printStackTrace)
//                .onSuccess(rows -> {
//                    getAll();
//                });
//
//    }









//    public JsonArray getOneWithUsername(String username, boolean bool) {
//        JsonArray result = new JsonArray();
//        pool
//                .preparedQuery("SELECT * FROM users WHERE username = ?")
//                .execute(Tuple.of(username))
//                .onFailure(e -> {
//                    e.printStackTrace();
//                })
//                .onSuccess(rows -> {
//                    for (Row row : rows) {
//                        JsonObject object = new JsonObject();
//                        object.put("staffName", row.getString(0));
//                        object.put("username", row.getString(1));
//                        object.put("pass", row.getString(2));
//                        result.add(object);
//                    }
//                });
//        return result;
//    }

}