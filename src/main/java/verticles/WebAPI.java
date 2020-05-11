package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class WebAPI extends AbstractVerticle {

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route(Config.BUS_ENDPOINT).handler(new SockJSConstructor(vertx).create());
        router.route(Config.STATIC_ENDPOINT).handler(StaticHandler.create().setCachingEnabled(false));
        server.requestHandler(router::accept).listen(Config.WEB_PORT);
    }
}
