import java.awt.*;
import java.awt.event.KeyEvent;

public class SimpleGame extends GameEngine{

    public static final int gameWidth = 600;
    public static final int gameHeight = 700;

    boolean isSinglePlayer;

    private PlayerPlane[] playerPlane = new PlayerPlane[2];

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
        checkCollision();
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

        changeColor(green);//TODO: for testing only
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            drawRectangle(playerPlane[pi].getX()-playerPlane[pi].getWidth()/2, playerPlane[pi].getY()-playerPlane[pi].getHeight()/2, playerPlane[pi].getWidth(), playerPlane[pi].getHeight());
        }
    }

    // Called whenever a key is pressed
    public void keyPressed(KeyEvent e) {
        //-------------------------------------------------------
        // Player 1 Key Control
        //-------------------------------------------------------
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Move Left
            if(playerPlane[0].getX() > playerPlane[0].getWidth()/2) {
                System.out.println(playerPlane[0].getX() + " " + playerPlane[0].getWidth()/2);
                playerPlane[0].setVx(-playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Move Right
            if(playerPlane[0].getX() < gameWidth - playerPlane[0].getWidth()/2) {
                playerPlane[0].setVx(playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            // Move Up
            if(playerPlane[0].getY() > playerPlane[0].getHeight()/2) {
                playerPlane[0].setVy(-playerPlane[0].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Move Down
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
        if(e.getKeyCode() == KeyEvent.VK_A) {
            // Move Left
            if(playerPlane[1].getX() > playerPlane[1].getWidth()/2) {
                playerPlane[1].setVx(-playerPlane[1].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
            // Move Right
            if(playerPlane[1].getX() < gameWidth - playerPlane[1].getWidth()/2) {
                playerPlane[1].setVx(playerPlane[1].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_W) {
            // Move Up
            if(playerPlane[1].getY() > playerPlane[1].getHeight()/2) {
                playerPlane[1].setVy(-playerPlane[1].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
            // Move Down
            if(playerPlane[1].getY() < gameHeight - playerPlane[1].getHeight()/2) {
                playerPlane[1].setVy(playerPlane[1].getMovingSpeed());
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_Q) {
            // Shot a bullet
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
            playerPlane[0].setVx(0);
        }
        // If player releases right key
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Stop moving
            playerPlane[0].setVx(0);
        }
        // If player releases up key
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            // Stop moving
            playerPlane[0].setVy(0);
        }
        // If player releases down key
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Stop moving
            playerPlane[0].setVy(0);
        }
        //-------------------------------------------------------
        // Player 2 Key Control
        //-------------------------------------------------------
        // If player releases left key
        if(e.getKeyCode() == KeyEvent.VK_A) {
            // Stop moving
            playerPlane[1].setVx(0);
        }
        // If player releases right key
        if(e.getKeyCode() == KeyEvent.VK_D) {
            // Stop moving
            playerPlane[1].setVx(0);
        }
        // If player releases up key
        if(e.getKeyCode() == KeyEvent.VK_W) {
            // Stop moving
            playerPlane[1].setVy(0);
        }
        // If player releases down key
        if(e.getKeyCode() == KeyEvent.VK_S) {
            // Stop moving
            playerPlane[1].setVy(0);
        }
    }
}
