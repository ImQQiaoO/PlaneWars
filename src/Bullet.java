import java.awt.*;

public class Bullet extends GameObject{

    private int bulletType;
    private int damage;
    private double bulletAngle;

    /**
     * shootInterval: The interval between two shots of the same type of bullet(Frames).
     */
    private int shootInterval;
    private int restInterval;

    public int getRestInterval() {
        return restInterval;
    }

    public void setRestInterval(int restInterval) {
        this.restInterval = restInterval;
    }



    /**
     * Constructor of Bullet
     * @param x              Bullet x coordinate
     * @param y              Bullet y coordinate
     * @param vx             Bullet x velocity
     * @param vy             Bullet y velocity
     * @param width          Bullet width
     * @param height         Bullet height
     * @param image          Bullet image
     * @param bulletType     Bullet type
     * @param damage         Bullet damage
     */
    public Bullet(double x, double y, double vx, double vy, double width, double height, Image image, int bulletType, int damage, int shootInterval) {
        super(x, y, vx, vy, width, height, image);
        this.bulletType = bulletType;
        this.damage = damage;
        this.shootInterval = shootInterval;
        this.restInterval = 2;
    }


    public void updateBullet(double dt) {
        updateLocation(dt);
        updateAngle();
    }

    public void updateLocation(double dt) {
        setX(getX() + getVx()*dt);
        setY(getY() + getVy()*dt);
    }

    public void updateAngle() {
        setBulletAngle(GameEngine.atan2(getVx(), getVy()));
    }


    /**
     * 获取
     * @return bulletType
     */
    public int getBulletType() {
        return bulletType;
    }

    /**
     * 设置
     * @param bulletType
     */
    public void setBulletType(int bulletType) {
        this.bulletType = bulletType;
    }

    /**
     * 获取
     * @return damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * 设置
     * @param damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * 获取
     * @return shootInterval
     */
    public int getShootInterval() {
        return shootInterval;
    }

    /**
     * 设置
     * @param shootInterval
     */
    public void setShootInterval(int shootInterval) {
        this.shootInterval = shootInterval;
    }

    public String toString() {
        return "Bullet{bulletType = " + bulletType + "}";
    }

    public double getBulletAngle() {
        return bulletAngle;
    }

    public void setBulletAngle(double bulletAngle) {
        this.bulletAngle = bulletAngle;
    }
}
