import java.awt.*;

public class Bullet extends GameObject{

    private int bulletType;
    private int damage;

    /**
     * shootInterval: The interval between two shots of the same type of bullet(Frames).
     */
    private int shootInterval;

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
    }

    public void updateLocation(double dt) {
        setX(getX() + getVx()*dt);
        setY(getY() + getVy()*dt);
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
}
