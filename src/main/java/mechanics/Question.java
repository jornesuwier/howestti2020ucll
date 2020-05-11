package mechanics;

import java.util.List;

public class Question {
    private String question;
    private List<String> answers;
    private int correctAnswer;

    public Question(String question, List<String> answers, int correct) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correct;
    }

    public String getQuestion() {return question;}

    public List<String> getAnswers() {return answers;}

    boolean isCorrect(String answer){
        return answer.equals(answers.get(correctAnswer));
    }
}
