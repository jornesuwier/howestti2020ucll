package verticles;

import config.Config;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import mechanics.Player;
import mechanics.Quiz;

import java.util.logging.Logger;

public class BusHandler extends AbstractVerticle {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Quiz quiz;

    private void handle(final Message message) {
        JsonObject data = (JsonObject) message.body();
        JsonObject content = data.getJsonObject("content");
        String type = data.getString("type");
        String user = data.getString("user");
        switch (type) {
            case "Connect":
                quiz.addPlayer(new Player(user));
                logger.info(user + " Connected to the EB");
                break;
            case "Start":
                quiz.start();
                logger.info(user + " started the quiz");
                break;
            case "Answer":
                String answer = content.getString("answer");
                quiz.checkAnswer(user,content);
                logger.info(user + " has answered: "+answer);
                break;
            case "End":
                quiz.reset();
                break;
            case "Question":
                String question = content.getString("question");
                String answers = content.getString("answers");
                int correct = content.getInteger("correct");
                quiz.addQuestion(question,answers,correct);
                logger.info("new question added:" + content);
            default:
                logger.info(data.toString());
        }
    }


    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        quiz = new Quiz(eb,vertx);
        eb.consumer(Config.HANDLER_URL, this::handle);
    }
}
