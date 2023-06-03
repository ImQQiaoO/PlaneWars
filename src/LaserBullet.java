import java.awt.*;

public class LaserBullet extends Bullet{

    public int getLaserNumber() {
        return laserNumber;
    }

    public void setLaserNumber(int laserNumber) {
        this.laserNumber = laserNumber;
    }

    /**
     * Constructor of Bullet
     *
     * @param x             Bullet x coordinate
     * @param y             Bullet y coordinate
     * @param vx            Bullet x velocity
     * @param vy            Bullet y velocity
     * @param width         Bullet width
     * @param height        Bullet height
     * @param image         Bullet image
     * @param bulletType    Bullet type
     * @param damage        Bullet damage
     * @param shootInterval
     */

    private int laserNumber;

    public LaserBullet(double x, double y, double vx, double vy, double width, double height, Image image, int bulletType, int damage, int shootInterval,int laserNumber) {
        super(x, y, vx, vy, width, height, image, bulletType, damage, shootInterval);
        this.laserNumber = laserNumber;
    }
}
