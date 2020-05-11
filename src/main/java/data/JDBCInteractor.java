package data;

import config.Config;
import io.vertx.core.AbstractVerticle;
import org.h2.tools.Server;
import org.pmw.tinylog.Logger;

import java.sql.SQLException;

public class JDBCInteractor extends AbstractVerticle {

    private Server dbServer;
    private Server webDB;

    private void startDBServer() {
        try {
            // start de DB
            dbServer = Server.createTcpServer().start();
            // start een web interface op poort 8082
            webDB = Server.createWebServer("-webPort", Config.DB_PORT).start();
            Logger.debug("Database Started");
        } catch (SQLException e) {
            Logger.warn("Error starting the database: {}", e.getLocalizedMessage());
            Logger.debug(e.getStackTrace());
        }
    }

    @Override
    public void start() {
        startDBServer();
    }

    @Override
    public void stop() {
        dbServer.stop();
        webDB.stop();
    }
}