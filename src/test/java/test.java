import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        SQLCRUD crud = new SQLCRUD(vertx);
    }
}
