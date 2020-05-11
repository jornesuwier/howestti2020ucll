package mechanics;

import config.Config;
import data.MySqlQuizRepo;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Player> players = new ArrayList<>();
    private List<Question> questions;
    private int activequestion = 0;
    private EventBus bus;
    private MySqlQuizRepo db = MySqlQuizRepo.getInstance();
    private Vertx vertx;

    public Quiz(EventBus bus, Vertx vertx) {
        questions = db.getQuestions();
        this.vertx = vertx;
        this.bus = bus;
    }

    public void start() {
        sendNextQuestion();
    }

    public void addPlayer(Player p){players.add(p);}

    public void checkAnswer(String player,JsonObject content){
        Player p = getPlayerByName(player);
        int questionId = content.getInteger("questionId");
        String answer = content.getString("answer");
        if(p!= null &&questions.get(questionId).isCorrect(answer)){
            p.addScore(50); //TODO timed score
            JsonObject o = new JsonObject();
            o.put("type","Score");
            o.put("user",p.getName());
            o.put("score",p.getScore());
            putOnBus(o);
        }
    }

    public void sendNextQuestion(){
        JsonObject o = new JsonObject();
        if(activequestion < questions.size()) {
            o.put("type", "Question");
            o.put("questionId", activequestion);
            o.put("question", questions.get(activequestion).getQuestion());
            o.put("answers", questions.get(activequestion).getAnswers());
            putOnBus(o);
            activequestion++;
            int time = 10000;
            for(int i=1;i<time;i+=1000){
                int finalI = i;
                vertx.setTimer(i, l -> {
                    JsonObject timeO = new JsonObject();
                    timeO.put("type", "Time");
                    timeO.put("time",(time- finalI)/1000);
                    putOnBus(timeO);
                });
            }
            vertx.setTimer(10000, l -> {
                sendNextQuestion();
            });
        }else {
            o.put("type", "End");
            putOnBus(o);
        }
    }

    private Player getPlayerByName(String name){
        for (Player player : players) {
            if (player.getName().equals(name)) {return player;}
        }
        return null;
    }
    private void putOnBus(JsonObject data) {
        try {
            bus.publish(Config.HANDLER_URL, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
