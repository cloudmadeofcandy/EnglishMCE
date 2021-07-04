import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        SQLCRUD crud = new SQLCRUD(vertx);
        JsonObject add = new JsonObject().put("name","Ha Anh").put("result","6");
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route("/").handler(ctx -> {
            ctx.response().setChunked(true);
            crud.insertRecord(add, ctx);
            ctx.response().end("Hello World");
        });

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router).listen(5000);
    }
}
