package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import mechanics.Player;
import mechanics.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BusHandler extends AbstractVerticle {
    private EventBus bus;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private List<Quiz> quizzes = new ArrayList<>();

    private void handle(final Message message) {
        JsonObject data = (JsonObject) message.body();
        JsonObject content = data.getJsonObject("content");
        String type = data.getString("type");
        String user = data.getString("user");
        int GID = (data.getInteger("GID") == null) ? 0 : data.getInteger("GID");
        logger.info(GID + "this is teh session we looking for");
        switch (type) {
            case "Create":
                    GID = quizzes.size()-1;
                    quizzes.add(new Quiz(vertx.eventBus(), vertx, GID));
                    JsonObject object = new JsonObject()
                    .put("user",user).put("GID",GID);
                    bus.publish(Config.HANDLER_URL, object);
                break;
            case "Connect":
                quizzes.get(GID).addPlayer(new Player(user));
                logger.info(user + " Connected to the EB");
                break;
            case "Start":
                quizzes.get(GID).start();
                logger.info(user + " started the quiz");
                break;
            case "Answer":
                String answer = content.getString("answer");
                quizzes.get(GID).checkAnswer(user,content);
                logger.info(user + " has answered: "+answer);
                break;
            case "End":
                quizzes.get(GID).reset();
                break;
            default:
                logger.info(data.toString());
        }

    }


    @Override
    public void start() {
        bus = vertx.eventBus();
        bus.consumer(Config.HANDLER_URL, this::handle);
    }
}
