public class Score {
    private final String gameMode;
    private final int score;
    private final long timeStamp;

    public Score(String map, int score, long timeStamp) {
        this.gameMode = map;
        this.score = score;
        this.timeStamp = timeStamp;
    }


    public String getGameMode() {
        return gameMode;
    }

    public int getScore() {
        return score;
    }

    public long getDate() {
        return timeStamp;
    }
}
