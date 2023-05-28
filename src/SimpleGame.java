import java.awt.*;
import java.awt.event.KeyEvent;

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
