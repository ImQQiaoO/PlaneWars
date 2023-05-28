import java.awt.*;

public class Bullet extends GameObject{

    private int bulletType;
    private int damage;

    public Bullet(double x, double y, double vx, double vy, double width, double height, Image image, int bulletType, int damage) {
        super(x, y, vx, vy, width, height, image);
        this.bulletType = bulletType;
        this.damage = damage;
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

    public String toString() {
        return "Bullet{bulletType = " + bulletType + "}";
    }
}
