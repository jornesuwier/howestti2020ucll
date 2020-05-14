package mechanics;

import config.Config;
import data.MySqlQuizRepo;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private final String UGID;
    private List<Player> players = new ArrayList<>();
    private List<Question> questions;
    private int activequestion = 0;
    private final EventBus bus;
    private final Vertx vertx;
    private int remaining;

    public Quiz(EventBus bus, Vertx vertx, String UGID) {
        MySqlQuizRepo db = MySqlQuizRepo.getInstance();
        questions = db.getQuestions();
        this.vertx = vertx;
        this.bus = bus;
        this.UGID = UGID;
    }

    public void start() {
        sendNextQuestion();
    }

    public void addPlayer(Player p) {
        players.add(p);
        sendPlayerAmount();
    }

    public void checkAnswer(String player, JsonObject content) {
        Player p = getPlayerByName(player);
        int questionId = content.getInteger("questionId");
        String answer = content.getString("answer");
        if (p != null && questions.get(questionId).isCorrect(answer)) {
            p.addScore(remaining * 50);
        }
    }

    private void sendNextQuestion() {
        JsonObject o = new JsonObject();
        if (activequestion < questions.size()) {
            o.put("UGID", UGID);
            o.put("type", "Question");
            o.put("questionId", activequestion);
            o.put("question", questions.get(activequestion).getQuestion());
            o.put("answers", questions.get(activequestion).getAnswers());
            putOnBus(o);
            activequestion++;
            remaining = Config.TIME_QUESTION;
            for (int i = 1; i < Config.TIME_QUESTION; i += 1000) {
                int finalI = i;
                vertx.setTimer(i, l -> {
                    remaining = (Config.TIME_QUESTION - finalI) / 1000;
                    JsonObject timeO = new JsonObject();
                    timeO.put("type", "Time");
                    timeO.put("time", remaining);
                    putOnBus(timeO);
                });
            }
            vertx.setTimer(10000, l -> {
                for (Player p : players) {
                    JsonObject score = new JsonObject();
                    score.put("type", "Score");
                    score.put("user", p.getName());
                    score.put("score", p.getScore());
                    putOnBus(score);
                }
                sendNextQuestion();
            });
        } else {
            o.put("type", "End");
            o.put("GID", UGID);
            putOnBus(o);
        }
    }


    private Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    private void sendPlayerAmount() {
        JsonObject o = new JsonObject();
        o.put("type", "Players");
        o.put("players", players.size());
        putOnBus(o);
    }

    public void reset() {
        MySqlQuizRepo db = MySqlQuizRepo.getInstance();
        questions = db.getQuestions();
        players = new ArrayList<>();
        activequestion = 0;
    }

    public void getScoreBoard() {
        JsonObject scoreboard = new JsonObject();
        scoreboard.put("type", "Scoreboard");
        JsonArray playerScore = new JsonArray();
        for (Player p : players) {
            JsonObject o = new JsonObject();
            o.put("user", p.getName());
            o.put("score", p.getScore());
            playerScore.add(o);
        }
        scoreboard.put("playerscores", playerScore);
        putOnBus(scoreboard);


    }

    private void putOnBus(JsonObject data) {
        try {
            bus.publish(Config.HANDLER_URL, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addQuestion(String question, String anwsers, int correct) {
        MySqlQuizRepo db = MySqlQuizRepo.getInstance();
        db.addQuestions(question, anwsers, correct);

    }


}
