import java.awt.*;

public class PlayerPlane extends GameObject{

    public static int playerNumber = 0;
    private double movingSpeed;

    public PlayerPlane(Image image) {
        super(SimpleGame.gameWidth/2.0, SimpleGame.gameHeight*0.8, 0, 0, 61, 63, image);
        movingSpeed = 200;
    }

    public void updateLocation(double dt) {
//        System.out.println("Vx:" + getVx());
//        System.out.println("Vy:" + getVy());
        setX(getX() + getVx()*dt);
        setY(getY() + getVy()*dt);
    }

    public double getMovingSpeed() {
        return movingSpeed;
    }

    public void setMovingSpeed(double movingSpeed) {
        this.movingSpeed = movingSpeed;
    }
}
