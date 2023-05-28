import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SimpleGame extends GameEngine{

    public static final int gameWidth = 600;
    public static final int gameHeight = 700;

    boolean isSinglePlayer;

    boolean isLeftKeyPressed = false;
    boolean isRightKeyPressed = false;
    boolean isUpKeyPressed = false;
    boolean isDownKeyPressed = false;
    boolean isAKeyPressed = false;
    boolean isDKeyPressed = false;
    boolean isWKeyPressed = false;
    boolean isSKeyPressed = false;

    private final PlayerPlane[] playerPlane = new PlayerPlane[2];
    private ArrayList<Enemy> enemyList;
    private double waitTime = 0;
    private double spendTime = 0;
    private long startTime;

    SimpleGame(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
        if (isSinglePlayer) {
            PlayerPlane.playerNumber = 1;
        } else {
            PlayerPlane.playerNumber = 2;
        }
    }

    public void init() {
        setWindowSize(gameWidth, gameHeight);
        Image[] playerImage = new Image[PlayerPlane.playerNumber];
        playerImage[0] = loadImage("src/resources/PlayerPlane01.png");
        if (!isSinglePlayer) {
            playerImage[1] = loadImage("src/resources/PlayerPlane02.png");
        }

        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            playerPlane[pi] = new PlayerPlane(playerImage[pi]);
        }

        startTime = System.currentTimeMillis();// Get the start time of the game
        enemyList = new ArrayList<>(); // Store all enemies in the game
    }

    private void checkCollision() {
        // Check collision between player plane and walls
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            if(playerPlane[pi].getX() < playerPlane[pi].getWidth()/2) {
                playerPlane[pi].setX(playerPlane[pi].getWidth()/2);
                playerPlane[pi].setVx(0);
            } else if(playerPlane[pi].getX() > gameWidth - playerPlane[pi].getWidth()/2) {
                playerPlane[pi].setX(gameWidth - playerPlane[pi].getWidth()/2);
                playerPlane[pi].setVx(0);
            }
            if(playerPlane[pi].getY() < playerPlane[pi].getHeight()/2) {
                playerPlane[pi].setY(playerPlane[pi].getHeight()/2);
                playerPlane[pi].setVy(0);
            } else if(playerPlane[pi].getY() > gameHeight - playerPlane[pi].getHeight()/2) {
                playerPlane[pi].setY(gameHeight - playerPlane[pi].getHeight()/2);
                playerPlane[pi].setVy(0);
            }
        }


    }

    @Override
    public void update(double dt) {
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            playerPlane[pi].updateLocation(dt);
        }

        //Update the location of the enemies
        for (Enemy enemy : enemyList) {
            enemy.updateLocation(dt);
        }

        checkCollision();

        // Generate enemies
        spendTime = spendTime + dt;
        double generateSeed = (double) ((System.currentTimeMillis() - startTime) / 1000) / 80 + 1;
        if (spendTime >= waitTime) {
            spendTime = 0;
            generateEnemies();
            waitTime = new Random().nextDouble(0, 2) / generateSeed;
        }

        //Help Garbage Collection
        enemyList.removeIf(enemy -> enemy.getY() > gameHeight + enemy.getHeight() / 2);
    }

    /**
     * Generate common enemies
     */
    public void generateEnemies() {
        // Enemy Type 1
        double enemyWidth = 31;
        double enemyHeight = 23;
        double enemyX = new Random().nextDouble(enemyWidth / 2, gameWidth - enemyWidth / 2);
        double enemyY = -enemyHeight / 2;
        double enemyVx = 0;
        double enemyVy = 200;
        Image enemyImage = loadImage("src/resources/Enemy01.png");
        int enemyType = 1;
        int enemyHp = 10;
        enemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
    }

    @Override
    public void paintComponent() {
        // Clear the background to black
        changeBackgroundColor(black);
        clearBackground(gameWidth, gameHeight);

        // Draw the player plane
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            drawImage(playerPlane[pi].getImage(), playerPlane[pi].getX()-playerPlane[pi].getWidth()/2, playerPlane[pi].getY()-playerPlane[pi].getHeight()/2, playerPlane[pi].getWidth(), playerPlane[pi].getHeight());
        }

        // Draw the enemies
        for (Enemy enemy : enemyList) {
            drawImage(enemy.getImage(), enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }

        changeColor(green);//TODO: for testing only
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            drawRectangle(playerPlane[pi].getX()-playerPlane[pi].getWidth()/2, playerPlane[pi].getY()-playerPlane[pi].getHeight()/2, playerPlane[pi].getWidth(), playerPlane[pi].getHeight());
        }

        changeColor(red);//TODO: for testing only
        for (Enemy enemy : enemyList) {
            drawRectangle(enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }
    }

    // Called whenever a key is pressed
    public void keyPressed(KeyEvent e) {
        //-------------------------------------------------------
        // Player 1 Key Control
        //-------------------------------------------------------
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Move Left
            isLeftKeyPressed = true;
            if(playerPlane[0].getX() > playerPlane[0].getWidth()/2) {
                playerPlane[0].setVx(-playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Move Right
            isRightKeyPressed = true;
            if(playerPlane[0].getX() < gameWidth - playerPlane[0].getWidth()/2) {
                playerPlane[0].setVx(playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            // Move Up
            isUpKeyPressed = true;
            if(playerPlane[0].getY() > playerPlane[0].getHeight()/2) {
                playerPlane[0].setVy(-playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Move Down
            isDownKeyPressed = true;
            if(playerPlane[0].getY() < gameHeight - playerPlane[0].getHeight()/2) {
                playerPlane[0].setVy(playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Shot a bullet
        }
        //-------------------------------------------------------
        // Player 2 Key Control
        //-------------------------------------------------------
        if (!isSinglePlayer) {
            if(e.getKeyCode() == KeyEvent.VK_A) {
                // Move Left
                isAKeyPressed = true;
                if(playerPlane[1].getX() > playerPlane[1].getWidth()/2) {
                    playerPlane[1].setVx(-playerPlane[1].getMovingSpeed());
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_D) {
                // Move Right
                isDKeyPressed = true;
                if(playerPlane[1].getX() < gameWidth - playerPlane[1].getWidth()/2) {
                    playerPlane[1].setVx(playerPlane[1].getMovingSpeed());
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_W) {
                // Move Up
                isWKeyPressed = true;
                if(playerPlane[1].getY() > playerPlane[1].getHeight()/2) {
                    playerPlane[1].setVy(-playerPlane[1].getMovingSpeed());
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_S) {
                // Move Down
                isSKeyPressed = true;
                if(playerPlane[1].getY() < gameHeight - playerPlane[1].getHeight()/2) {
                    playerPlane[1].setVy(playerPlane[1].getMovingSpeed());
                }
            }
            if(e.getKeyCode() == KeyEvent.VK_Q) {
                // Shot a bullet
            }
        }
    }

    // Called whenever a key is released
    public void keyReleased(KeyEvent e) {
        //-------------------------------------------------------
        // Player 1 Key Control
        //-------------------------------------------------------
        // If player releases left key
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Stop moving
            isLeftKeyPressed = false;
            if(!isRightKeyPressed){
                playerPlane[0].setVx(0);
            } else {
                playerPlane[0].setVx(playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases right key
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Stop moving
            isRightKeyPressed = false;
            if(!isLeftKeyPressed){
                playerPlane[0].setVx(0);
            } else {
                playerPlane[0].setVx(-playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases up key
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            // Stop moving
            isUpKeyPressed = false;
            if(!isDownKeyPressed){
                playerPlane[0].setVy(0);
            } else {
                playerPlane[0].setVy(playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases down key
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Stop moving
            isDownKeyPressed = false;
            if(!isUpKeyPressed){
                playerPlane[0].setVy(0);
            } else {
                playerPlane[0].setVy(-playerPlane[0].getMovingSpeed());
            }
        }
        //-------------------------------------------------------
        // Player 2 Key Control
        //-------------------------------------------------------
        if (!isSinglePlayer) {
            // If player releases left key
            if(e.getKeyCode() == KeyEvent.VK_A) {
                // Stop moving
                isAKeyPressed = false;
                if(!isDKeyPressed){
                    playerPlane[1].setVx(0);
                } else {
                    playerPlane[1].setVx(playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases right key
            if(e.getKeyCode() == KeyEvent.VK_D) {
                // Stop moving
                isDKeyPressed = false;
                if(!isAKeyPressed){
                    playerPlane[1].setVx(0);
                } else {
                    playerPlane[1].setVx(-playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases up key
            if(e.getKeyCode() == KeyEvent.VK_W) {
                // Stop moving
                isWKeyPressed = false;
                if(!isSKeyPressed){
                    playerPlane[1].setVy(0);
                } else {
                    playerPlane[1].setVy(playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases down key
            if(e.getKeyCode() == KeyEvent.VK_S) {
                // Stop moving
                isSKeyPressed = false;
                if(!isWKeyPressed){
                    playerPlane[1].setVy(0);
                } else {
                    playerPlane[1].setVy(-playerPlane[1].getMovingSpeed());
                }
            }
        }
    }
}
