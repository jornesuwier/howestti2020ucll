import data.JDBCInteractor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import verticles.BusHandler;
import verticles.WebAPI;

public class server extends AbstractVerticle {
    public static void main(String... args) {
        Vertx vertx = Vertx.vertx();
        new server().deploy(vertx);

    }

    private void deploy(Vertx vertx) {
        vertx.deployVerticle(new JDBCInteractor());
        vertx.deployVerticle(new BusHandler());
        vertx.deployVerticle(new WebAPI());
    }

    @Override
    public void start() {
        deploy(vertx);
    }
}
