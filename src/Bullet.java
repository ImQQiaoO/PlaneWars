import java.awt.*;

public class Bullet extends GameObject{

    private int bulletType;
    public Bullet(double x, double y, double vx, double vy, double width, double height, Image image, int bulletType) {
        super(x, y, vx, vy, width, height, image);
        this.bulletType = bulletType;
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

    public String toString() {
        return "Bullet{bulletType = " + bulletType + "}";
    }
}
