package mechanics;

public class Player {
    private final String name;
    private int score;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    String getName() {
        return name;
    }

    int getScore() {
        return score;
    }

    void addScore(int amount) {
        score += amount;
    }
}
