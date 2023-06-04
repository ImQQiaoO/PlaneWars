import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ScorePanel extends GameEngine {
    boolean hasAddButtons = false;

    public static Map<Score, Integer> scoreMap = new TreeMap<>((Comparator<Object>) (o1, o2) -> {
        Score s1 = (Score) o1;
        Score s2 = (Score) o2;
        if (s1.getScore() > s2.getScore()) {
            return -1;
        } else if (s1.getScore() < s2.getScore()) {
            return 1;
        } else {
            return Long.compare(s1.getDate(), s2.getDate());
        }
    });

        public static String fileName = "src/resources/scoreList.txt";

    public ScorePanel() {
        super("Score");
        init();
    }

    public static void initScoreList() {
        readScore();
    }

    public void init() {
        setWindowSize(Launcher.WindowWidth,Launcher.WindowHeight);
        readScore();

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(black);
        clearBackground(Launcher.WindowWidth, Launcher.WindowHeight);
        changeColor(orange);
        drawBoldText(270, 70, "Score List", "Arial", 50);
        changeColor(white);
        drawText(100, 120, "Rank", "Arial", 30);
        drawText(210, 120, "GameMode", "Arial", 30);
        drawText(410, 120, "Score", "Arial", 30);
        drawText(580, 120, "Date", "Arial", 30);
        drawLine(80, 140, 740, 140, 3);

        for (int i = 0; i < scoreMap.size(); i++) {
            drawText(120, 170 + i * 39, String.valueOf(i + 1), "Arial", 23);
            int j = 0;
            for (Map.Entry<Score, Integer> entry : scoreMap.entrySet()) {
                if (j == i) {
                    drawText(220, 170 + i * 39, entry.getKey().getGameMode(), "Arial", 23);
                    drawText(425, 170 + i * 39, String.valueOf(entry.getKey().getScore()), "Arial", 23);
                    drawText(510, 170 + i * 39, GameUtil.getDate(entry.getKey().getDate()), "Arial", 23);
                    drawLine(80, 180 + i * 39, 740, 180 + i * 39, 2);
                }
                j++;
            }
        }
        if(!hasAddButtons){
            addFunButtons();
            hasAddButtons = true;
        }
    }

    public static void readScore() {
        scoreMap.clear();
        try {
            File file =new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(" ");
                scoreMap.put(new Score(temp[0], Integer.parseInt(temp[1]), Long.parseLong(temp[2])), Integer.parseInt(temp[1]));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeScore() {
        try{
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
                for (Map.Entry<Score, Integer> entry : scoreMap.entrySet()) {
                    bufferedWriter.write(entry.getKey().getGameMode() + " " + entry.getKey().getScore() + " " + entry.getKey().getDate());
                    bufferedWriter.newLine();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void checkHighestScore(String mode, int score) {
        if (scoreMap.size() <= 10) {
            addScore(mode, score);
        } else {
            for (Map.Entry<Score, Integer> entry : scoreMap.entrySet()) {
                if (entry.getValue() < score) {
                    addScore(mode, score);
                }
            }
        }
    }

    //Add score and sort the list
    public static void addScore(String map, int score) {
        Score s = new Score(map, score, GameUtil.getTimeStamp());
        scoreMap.put(s, score);
        if (scoreMap.size() > 10) {
            int count = 0;
            for (Map.Entry<Score, Integer> entry : scoreMap.entrySet()) {
                if (count == 10) {
                    scoreMap.remove(entry.getKey());
                }
                count++;
            }
        }
        writeScore();
    }

    private void addFunButtons() {
        JButton resetButton = GameUtil.createNormalButton("Reset", 150, 600, 220, 70, Color.RED);
        JButton backButton = GameUtil.createNormalButton("Back", 450, 600, 220, 70, Color.BLUE);
        this.mPanel.add(resetButton);
        this.mPanel.add(backButton);
        resetButton.addActionListener(e -> {
            MenuPanel.initialize_ChooseSound();
            MenuPanel.chooseClip.start();
            scoreMap.clear();
            writeScore();
            this.mPanel.requestFocus();
            this.mPanel.repaint();
        });
        backButton.addActionListener(e -> {
            MenuPanel.initialize_ChooseSound();
            MenuPanel.chooseClip.start();
            mFrame.dispose();
            MenuPanel.frame.setVisible(true);
        });
        this.mPanel.setLayout(null);
    }
}
