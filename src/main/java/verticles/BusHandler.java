package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import mechanics.Player;
import mechanics.Quiz;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BusHandler extends AbstractVerticle {
    private EventBus bus;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Map<String, Quiz> quizzes = new HashMap<>();

    private void handle(final Message message) {
        JsonObject data = (JsonObject) message.body();
        JsonObject content = data.getJsonObject("content");
        String type = data.getString("type");
        String user = data.getString("user");
        String GID = (data.getString("GID") == null) ? "0" : data.getString("GID");
        logger.info(GID + " this is the session we looking for");
        switch (type) {
            case "Create":
                    quizzes.put(GID,new Quiz(vertx.eventBus(), vertx, GID));
                    JsonObject object = new JsonObject()
                    .put("type", "loginReply").put("user",user).put("GID",GID);
                    bus.publish(Config.HANDLER_URL, object);
                    logger.info(user + " created room: " + GID);
                    quizzes.get(GID).addPlayer(new Player(user));
                break;
            case "Join":
                quizzes.get(GID).addPlayer(new Player(user));
                logger.info(user + " joined room: " + GID);

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
            case "Question":
                String question = content.getString("question");
                String answers = content.getString("answers");
                int correct = content.getInteger("correct");
                quizzes.get(GID).addQuestion(question,answers,correct);
                logger.info("new question added:" + content);
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
