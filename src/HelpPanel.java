import javax.swing.*;
import java.awt.*;

public class HelpPanel extends GameEngine {
    boolean hasAddButtons = false;
    public HelpPanel() {
        init();
    }

    public void init() {
        setWindowSize(Launcher.WindowWidth,Launcher.WindowHeight);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {

        changeBackgroundColor(black);
        clearBackground(Launcher.WindowWidth, Launcher.WindowHeight);
        changeColor(white);
        drawBoldText(350, 60, "Help", "Arial", 50);

        paintPlayer();
        paintBasicRules();
        paintScoreRules();
        paintMysteryRules();

        if(!hasAddButtons){
            addFunButtons();
            hasAddButtons = true;
        }

    }

    private void addFunButtons() {
        JButton backButton = GameUtil.createNormalButton("Back", 670, 20, 100, 50, Color.red);
        backButton.addActionListener(e -> {
            MenuPanel.initialize_ChooseSound();
            MenuPanel.chooseClip.start();
            mFrame.dispose();
            MenuPanel.frame.setVisible(true);
        });
        this.mPanel.add(backButton);
        this.mPanel.setLayout(null);
    }

    private void paintPlayer() {
        changeColor(white);
        //Draw Player One
        drawRectangle(140, 100, 250, 310);
        drawBoldText(185, 140, "Player One", "Arial", 30);
        Image playerOnePlane = loadImage("src/resources/PlayerPlane01.png");
        drawImage(playerOnePlane, 240, 160, 50, 50);
        drawSolidRectangle(180, 220, 50, 50);   //Shoot Key
        drawSolidRectangle(240, 220, 50, 50);   //Up Key
        drawSolidRectangle(240, 280, 50, 50);   //Down Key
        drawSolidRectangle(180, 280, 50, 50);   //Left Key
        drawSolidRectangle(300, 280, 50, 50);   //Right Key
        changeColor(blue);
        drawText(GameUtil.centerTextX(mGraphics, "/", 180,50), 255, "/", "Arial", 25);   //Shoot Key
        drawText(GameUtil.centerTextX(mGraphics, "↑", 240,50), 250, "↑", "Arial", 25);   //Up Key
        drawText(GameUtil.centerTextX(mGraphics, "↓", 240,50), 310, "↓", "Arial", 25);   //Down Key
        drawText(GameUtil.centerTextX(mGraphics, "←", 180,50), 310, "←", "Arial", 25);   //Left Key
        drawText(GameUtil.centerTextX(mGraphics, "→", 300,50), 310, "→", "Arial", 25);   //Right Key
        changeColor(white);
        drawText(GameUtil.centerTextX(mGraphics, "Use Keys to Move", 140,250), 360, "Use Keys to Move", "Arial", 25);
        drawText(GameUtil.centerTextX(mGraphics, "and Shoot Missiles", 140,250), 390, "and Shoot Missiles", "Arial", 25);
        //Draw Player Two
        drawRectangle(420, 100, 250, 310);
        drawBoldText(465, 140, "Player Two", "Arial", 30);
        Image playerTwoPlane = loadImage("src/resources/PlayerPlane02.png");
        drawImage(playerTwoPlane, 520, 160, 50, 50);
        drawSolidRectangle(580, 220, 50, 50);   //Shoot Key
        drawSolidRectangle(520, 220, 50, 50);   //Up Key
        drawSolidRectangle(520, 280, 50, 50);   //Down Key
        drawSolidRectangle(460, 280, 50, 50);   //Left Key
        drawSolidRectangle(580, 280, 50, 50);   //Right Key
        changeColor(red);
        drawText(GameUtil.centerTextX(mGraphics, "R", 580,50), 255, "R", "Arial", 25);   //Shoot Key
        drawText(GameUtil.centerTextX(mGraphics, "W", 520,50), 255, "W", "Arial", 25);   //Up Key
        drawText(GameUtil.centerTextX(mGraphics, "S", 520,50), 315, "S", "Arial", 25);   //Down Key
        drawText(GameUtil.centerTextX(mGraphics, "A", 460,50), 315, "A", "Arial", 25);   //Left Key
        drawText(GameUtil.centerTextX(mGraphics, "D", 580,50), 315, "D", "Arial", 25);   //Right Key
        changeColor(white);
        drawText(GameUtil.centerTextX(mGraphics, "Use Keys to Move", 420,250), 360, "Use Keys to Move", "Arial", 25);
        drawText(GameUtil.centerTextX(mGraphics, "and Shoot Missiles", 420,250), 390, "and Shoot Missiles", "Arial", 25);
    }

    private void paintBasicRules() {
        changeColor(white);
        drawRectangle(50, 430, 340, 250);
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Arial", Font.BOLD, 25));
//        g.drawRect(50, 400, 250, 300);
//        g.drawString("Basic Rules", GameUtil.centerTextX(g,"Game Rules", 50, 250), 440);
////        Launcher.wallIcon.paintIcon(this, g, 70, 420);
////        Launcher.wallIcon.paintIcon(this, g, 270, 420);
//        //Rules Text
//        g.setFont(new Font("Arial", Font.BOLD, 16));
//        g.drawString("* Use Keys to Move", 70, 480);
//        g.drawString("* Eat Apples to Grow", 70, 510);
//        g.drawString("* Don't Hit Yourself", 70, 540);
//        g.drawString("* Don't Hit Other Snakes", 70, 570);
//        g.drawString("* Don't Hit the Walls", 70, 600);
    }

    private void paintScoreRules() {
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Arial", Font.BOLD, 25));
//        g.drawRect(325, 400, 250, 300);
//        g.drawString("Score Rules", GameUtil.centerTextX(g,"Score Rules", 325, 250), 440);
////        Launcher.appleIcon.paintIcon(this, g, 345, 420);
////        Launcher.appleIcon.paintIcon(this, g, 535, 420);
//        //Rules Text
//        g.setFont(new Font("Arial", Font.BOLD, 16));
//        g.drawString("* 1 Apple = 10 Point", 345, 480);
//        g.drawString("* 1 Remaining Life = 50 Point", 345, 510);
//        g.drawString("* 1 Mystery = 50 Point(25%)", 345, 540);
//        g.drawString("* Speed Level:", 345, 570);
//        g.setFont(new Font("Arial", Font.BOLD, 15));
//        g.drawString("  - Slow = x1.0", 345, 600);
//        g.drawString("  - Normal = x1.5", 345, 620);
//        g.drawString("  - Fast = x2.0", 345, 640);
    }

    private void paintMysteryRules() {
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Arial", Font.BOLD, 25));
//        g.drawRect(600, 400, 250, 300);
//        g.drawString("Mystery Rules", GameUtil.centerTextX(g,"Mystery Rules", 600, 250), 440);
////        Launcher.mysteryIcon.paintIcon(this, g, 610, 420);
////        Launcher.mysteryIcon.paintIcon(this, g, 820, 420);
//        //Rules Text
//        g.setFont(new Font("Arial", Font.BOLD, 18));
//        g.drawString("* 20% chance to generate", 620, 480);
//        g.drawString("  after eating an apple.", 620, 500);
//        g.drawString("* Will disappear after", 620, 530);
//        g.drawString("  20 frames.", 620, 550);
//        g.drawString("* After eating:", 620, 580);
//        g.setFont(new Font("Arial", Font.BOLD, 14));
//        g.drawString("  + 25%: Add One Life.", 620, 610);
//        g.drawString("  - 25%: Lose One Life.", 620, 630);
//        g.drawString("  + 25%: Add 50 Scores.", 620, 650);
//        g.drawString("  - 25%: Freeze Snake Briefly.", 620, 670);
    }
}
