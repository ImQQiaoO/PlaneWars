import java.awt.*;

public class PlayerPlane extends GameObject{

    public static int playerNumber = 0;
    private double movingSpeed;
    private final int playerID; // 1 for player 1, 2 for player 2
    private Image normalImage;
    private Image turnLeftImage;
    private Image turnRightImage;
    public static Image normalTailFireImage = GameEngine.loadImage("src/resources/TailFire01.png");
    public static Image superTailFireImage = GameEngine.loadImage("src/resources/TailFire02.png");

    public PlayerPlane(int playerID) {
        super(SimpleGame.gameWidth/2.0, SimpleGame.gameHeight*0.8, 0, 0, 61, 63, GameEngine.loadImage("src/resources/PlayerPlane0"+ playerID +".png"));
        this.playerID = playerID;
        movingSpeed = 200;
        loadImage();
    }

    private void loadImage(){
        normalImage = GameEngine.loadImage("src/resources/PlayerPlane0"+ playerID +".png");
        turnLeftImage = GameEngine.loadImage("src/resources/PlayerPlaneLeft0"+ playerID +".png");
        turnRightImage = GameEngine.loadImage("src/resources/PlayerPlaneRight0"+ playerID +".png");
    }

    public void updatePlane(double dt){
        updateLocation(dt);
        updateImage();
    }

    public void updateLocation(double dt) {
        // Recalculate position based on movement speed
        setX(getX() + getVx()*dt);
        setY(getY() + getVy()*dt);
    }

    public void updateImage(){
        // Change the aircraft image according to the direction of movement
        if (getVx() > 0) {
            setImage(turnRightImage);
        } else if (getVx() < 0) {
            setImage(turnLeftImage);
        } else {
            setImage(normalImage);
        }
    }

    public double getMovingSpeed() {
        return movingSpeed;
    }

    public void setMovingSpeed(double movingSpeed) {
        this.movingSpeed = movingSpeed;
    }
}
