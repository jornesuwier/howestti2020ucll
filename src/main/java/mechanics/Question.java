package mechanics;

import java.util.List;

public class Question {
    private final String question;
    private final List<String> answers;
    private final int correctAnswer;

    public Question(String question, List<String> answers, int correct) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correct;
    }

    String getQuestion() {
        return question;
    }

    List<String> getAnswers() {
        return answers;
    }

    boolean isCorrect(String answer) {
        return answer.equals(answers.get(correctAnswer));
    }
}
