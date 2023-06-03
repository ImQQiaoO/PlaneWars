import java.awt.*;

public class PlayerPlane extends GameObject{

    public static int playerNumber = 0;
    private double movingSpeed;
    private final int playerID; // 1 for player 1, 2 for player 2
    private static int score = 0;
    private int max_hp = 5000;
    private int hp = 5000;
    private double protectTime = 0;
    private double explosionIndex = 0;
    private Image normalImage;
    private Image turnLeftImage;
    private Image turnRightImage;
    public static Image normalTailFireImage = GameEngine.loadImage("src/resources/TailFire01.png");
    public static Image superTailFireImage = GameEngine.loadImage("src/resources/TailFire02.png");

    public PlayerPlane(double x, double y,int playerID) {
        super(x, y, 0, 0, 61, 63, GameEngine.loadImage("src/resources/PlayerPlane0"+ playerID +".png"));
        this.playerID = playerID;
        score = 0;
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
        updateProtectTime(dt);
        updateExplosionIndex(dt);
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

    public void updateProtectTime(double dt) {
        if(protectTime > 0) {
            protectTime -= dt;
        }
    }

    public void updateExplosionIndex(double dt) {
        if(explosionIndex > 0) {
            explosionIndex -= 1;
        }
    }

    public int getPlaneHP() {
        return hp;
    }

    public void setPlaneHP(int hp) {
        this.hp = hp;
    }

    public double getMovingSpeed() {
        return movingSpeed;
    }

    public void setMovingSpeed(double movingSpeed) {
        this.movingSpeed = movingSpeed;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        PlayerPlane.score += score;
    }

    public int getHp() {
        return hp;
    }

    public void increaseHp() {
        if(this.hp <= max_hp - 500) {
            this.hp = this.hp + 500;
        }
        else if(this.hp < max_hp){
            this.hp = max_hp;
        }
    }

    public void decreaseHp() {
        protectTime = 1.0;
        setExplosionIndex();
    }

    public double getProtectTime() {
        return protectTime;
    }

    public void setExplosionIndex() {
        this.explosionIndex = 16;
    }

    public double getExplosionIndex() {
        return explosionIndex;
    }
}
