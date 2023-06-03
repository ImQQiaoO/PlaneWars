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
        drawBoldText(330, 60, "Help", "Arial", 50);

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
        drawRectangle(140, 100, 250, 300);
        drawBoldText(200, 140, "Player One", "Arial", 25);
        Image playerOnePlane = loadImage("src/resources/PlayerPlane01.png");
        drawImage(playerOnePlane, 240, 160, 50, 50);
//        drawText(330, 190, "Use Keys to Move", "Arial", 25);
        drawSolidRectangle(240, 220, 50, 50);   //Up Key
        drawSolidRectangle(240, 280, 50, 50);   //Down Key
        drawSolidRectangle(180, 280, 50, 50);   //Left Key
        drawSolidRectangle(300, 280, 50, 50);   //Right Key
        changeColor(blue);
        drawText(GameUtil.centerComponentsX(1, 240, 50), 250, "↑", "Arial", 25);

//        g.setFont(new Font("Arial", Font.BOLD, 25));
//        //Draw Player One
//        g.setColor(Color.WHITE);
//        g.drawRect(180, 100, 250, 280);
//        g.drawString("Player One", GameUtil.centerTextX(g,"Player One", 180, 250), 140);
////        Launcher.headIcon.paintIcon(this, g, 275, 160);
////        Launcher.dotIcon[0].paintIcon(this, g, 295, 160);
////        Launcher.dotIcon[0].paintIcon(this, g, 315, 160);
//        g.drawString("Use Keys to Move", GameUtil.centerTextX(g,"Use Keys to Move", 180, 250), 350);
//        g.fillRect(280, 200, 50, 50);   //Up Key
//        g.fillRect(280, 260, 50, 50);   //Down Key
//        g.fillRect(220, 260, 50, 50);   //Left Key
//        g.fillRect(340, 260, 50, 50);   //Right Key
//        g.setColor(new Color(0, 211, 0));
//        g.drawString("↑", GameUtil.centerTextX(g,"↑", 280, 50), 230);   //Up Key
//        g.drawString("↓", GameUtil.centerTextX(g,"↓", 280, 50), 290);   //Down Key
//        g.drawString("←", GameUtil.centerTextX(g,"←", 220, 50), 290);   //Left Key
//        g.drawString("→", GameUtil.centerTextX(g,"→", 340, 50), 290);   //Right Key
//        //Draw Player Two
//        g.setColor(Color.WHITE);
//        g.drawRect(470, 100, 250, 280);
//        g.drawString("Player Two", GameUtil.centerTextX(g,"Player Two", 470, 250), 140);
////        Launcher.headIcon.paintIcon(this, g, 565, 160);
////        Launcher.dotIcon[1].paintIcon(this, g, 585, 160);
////        Launcher.dotIcon[1].paintIcon(this, g, 605, 160);
//        g.drawString("Use Keys to Move", GameUtil.centerTextX(g,"Use Keys to Move", 470, 250), 350);
//        g.fillRect(570, 200, 50, 50);   //Up Key
//        g.fillRect(570, 260, 50, 50);   //Down Key
//        g.fillRect(510, 260, 50, 50);   //Left Key
//        g.fillRect(630, 260, 50, 50);   //Right Key
//        g.setColor(new Color(0, 123, 211));
//        g.drawString("W", GameUtil.centerTextX(g,"W", 570, 50), 235);   //Up Key
//        g.drawString("S", GameUtil.centerTextX(g,"S", 570, 50), 295);   //Down Key
//        g.drawString("A", GameUtil.centerTextX(g,"A", 510, 50), 295);   //Left Key
//        g.drawString("D", GameUtil.centerTextX(g,"D", 630, 50), 295);   //Right Key
    }

    private void paintBasicRules() {
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
