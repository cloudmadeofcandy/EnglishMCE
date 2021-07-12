import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Vertx vertx = Vertx.vertx();
        SQLCRUD crud = new SQLCRUD(vertx);
        //JsonObject add = new JsonObject().put("ID", "1019").put("QUESTION","Quan").put("A","6").put("B","6").put("C","6").put("D","6").put("answer","6");
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.POST, "/").handler(ctx -> {
            String q = ctx.getBodyAsString();
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
            crud.returnUsers(info, ctx);
        });

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router).listen(4000);
    }
}
