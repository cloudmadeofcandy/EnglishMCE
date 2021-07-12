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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        AtomicReference<Integer> num = new AtomicReference<>();
        router.route(HttpMethod.POST,"/main").handler(ctx -> {
            String res = ctx.getBodyAsString();
            Pattern p = Pattern.compile("(name)=([A-Za-z0-9\\s]+)&(num)=([0-9\\s]+)");
            Matcher m;
            m = p.matcher(res);
            if (m.find()) {
                body.set(m.group(2));
                num.set(Integer.parseInt(m.group(4)));
            }
            System.out.println(body + "------" + num);
            ctx.response().putHeader("content-type", "text/html");
            ctx.response().sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/testview.html");
        });

        router.route(HttpMethod.GET, "/test").handler(ctx -> {
            controller.getNumret(num, ctx);
        });

        router.route(HttpMethod.GET, "/listofids").handler(ctx -> {
            controller.getAllAdmin(ctx);
        });

        router.route(HttpMethod.GET, "/records").handler(ctx -> {
            controller.getAllrecords(ctx);
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

        router.route(HttpMethod.GET, "/myquestion").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/myquestion.html");
        });

        router.route(HttpMethod.GET, "/myadmin").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/myadmin.html");
        });


        router.route(HttpMethod.GET, "/myrecords").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.TEXT_HTML).sendFile("/home/logiciel/Documents/Academy of Cryptography Techniques/EnglishMCE/src/main/webapp/myrecords.html");
        });



        router.route(HttpMethod.POST, "/admin").handler(ctx -> {
            String q = ctx.getBodyAsString();
            System.out.println("-----------"+q);
            Pattern p = Pattern.compile("(NAME)=([A-Za-z0-9\\s]+)&(PASSWORD)=([A-Za-z0-9\\s]+)");
            Matcher m;
            m = p.matcher(q);
            String[] ret = new String[4];
            if (m.find()) {
                ret[0] = m.group(1);
                ret[1] = m.group(2);
                ret[2] = m.group(3);
                ret[3] = m.group(4);
            }
            JsonObject info = new JsonObject().put(ret[0], ret[1]).put(ret[2], ret[3]);
            ctx.response().putHeader("content-type", "text/html");
            ctx.response().setChunked(true);
            controller.returnUsers(info, ctx);
        });




        router.route("/createquestion").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            controller.createQuestion(json, ctx);
        });

        router.route("/updatequestion").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            controller.updateQuestion(json, ctx);
        });

        router.route("/deletequestion").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            System.out.println(json);
            controller.deleteQuestion(json, ctx);
        });


        router.route("/createadmin").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            controller.createAdmin(json, ctx);
        });

        router.route("/updateadmin").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            controller.updateAdmin(json, ctx);
        });

        router.route("/deleteadmin").handler(ctx -> {
            JsonObject json = ctx.getBodyAsJson();
            controller.deleteAdmin(json, ctx);
        });



        server.requestHandler(router).listen(5000);

    }
}
