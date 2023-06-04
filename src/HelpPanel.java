import javax.swing.*;
import java.awt.*;

public class HelpPanel extends GameEngine {
    boolean hasAddButtons = false;
    public HelpPanel() {
        super("Help");
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
        changeColor(Color.cyan);
        drawBoldText(350, 60, "Help", "Arial", 50);

        paintPlayer();
        paintBasicRules();
        paintItemRules();

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
        drawBoldText(GameUtil.centerTextX(mGraphics,"Basic Rules", 50, 340), 460, "Basic Rules", "Arial", 25);
        drawText(60, 510, "* Move to avoid enemies and enemy bullets.", "Arial", 16);
        drawText(60, 540, "* Shoot enemies to get points.", "Arial", 16);
        drawText(60, 570, "* HP will decrease after being hit by enemies.", "Arial", 16);
        drawText(60, 600, "* Game will over when any player's HP is 0.", "Arial", 16);
        drawText(60, 630, "* Pick up items to enhance your weapon.", "Arial", 16);
    }

    private void paintItemRules() {
        changeColor(white);
        drawRectangle(420, 430, 340, 250);
        drawBoldText(GameUtil.centerTextX(mGraphics,"Item Rules", 400, 340), 460, "Item Rules", "Arial", 25);
        Image itemLife = loadImage("src/resources/ItemLife.png");
        Image itemFire = loadImage("src/resources/ItemFire.png");
        Image itemLaser = loadImage("src/resources/ItemLaser.png");
        Image itemMissile = loadImage("src/resources/ItemMissile.png");
        drawText(480, 490, "Items will randomly drop ", "Arial", 20);
        drawText(490, 510, "after the enemy dies.", "Arial", 20);
        drawImage(itemLife, 450, 520, 35, 35);  //ItemLife
        drawImage(itemFire, 450, 560, 35, 35);  //ItemFire
        drawImage(itemLaser, 450, 600, 35, 35); //ItemLaser
        drawImage(itemMissile, 450, 640, 35, 35);   //ItemMissile
        drawText(500, 543, "Add 200 HP.", "Arial", 20);
        drawText(500, 583, "Launch 30 dual fire bullets.", "Arial", 20);
        drawText(500, 623, "Launch laser beam.", "Arial", 20);
        drawText(500, 663, "Store 10 missiles to shoot.", "Arial", 20);
    }
}
