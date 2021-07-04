import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class Runner {
    public static void main(String[] args) {
        
        
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        
        SQLCRUD controller = new SQLCRUD(vertx);
        
        StaticHandler shander = StaticHandler.create("src/main/resources");
        
        BodyHandler bhandler = BodyHandler.create();
        
        router.route("/resources/*").handler(shander);
        
        router.route().handler(BodyHandler.create());

        router.route("/").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML);
            ctx.response().sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/login.html");
        });


        AtomicReference<String> body = new AtomicReference<>();
        router.route(HttpMethod.POST,"/main").handler(ctx -> {
            String[] res = ctx.getBodyAsString().split("=");
            body.set(res[1]);
            System.out.println(body);
            ctx.response().putHeader("content-type", "text/html");
            ctx.response().sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/testview.html");
        });

        router.route(HttpMethod.GET, "/test").handler(ctx -> {
            controller.getAllret(ctx);
        });


        final int[] d = {0};
        router.route(HttpMethod.POST, "/form").handler(rc -> {
            JsonObject json = new JsonObject();
            json = rc.getBodyAsJson();
            List<JsonObject> answer = controller.getObjects();
            d[0] = controller.returnGrade(answer, json);
            JsonObject ret = new JsonObject().put("name", body.get()).put("result", d[0] + " out of " + json.size());
            System.out.println(ret.encodePrettily());
            controller.insertRecord(ret, rc);
            rc.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).send(ret.encode());
        });

        router.route(HttpMethod.GET, "/result").handler(ctx -> {

        });


        server.requestHandler(router).listen(5000);

    }
}
