import java.awt.*;

public class Enemy extends GameObject {

    private int enemyType;
    private int enemyHP;


    /**
     * Constructor of Enemy
     * There are many types of enemies in the game, each with different attributes.
     *
     * @param x         Enemy x coordinate
     * @param vx        Enemy x velocity
     * @param vy        Enemy y velocity
     * @param width     Enemy width
     * @param height    Enemy height
     * @param image     Enemy image
     * @param enemyType Enemy type
     * @param enemyHP   Enemy HP
     */

    public Enemy(double x, double y, double vx, double vy, double width, double height, Image image, int enemyType, int enemyHP) {
        super(x, y, vx, vy, width, height, image); // Enemy y coordinate is always 0 when it is created.
        this.enemyType = enemyType;
        this.enemyHP = enemyHP;
    }

    public void updateLocation(double dt) {
        setX(getX() + getVx() * dt);
        setY(getY() + getVy() * dt);
    }

    /**
     * 获取
     *
     * @return enemyType
     */
    public int getEnemyType() {
        return enemyType;
    }

    /**
     * 设置
     *
     * @param enemyType
     */
    public void setEnemyType(int enemyType) {
        this.enemyType = enemyType;
    }

    /**
     * 获取
     *
     * @return enemyHP
     */
    public int getEnemyHP() {
        return enemyHP;
    }

    /**
     * 设置
     *
     * @param enemyHP
     */
    public void setEnemyHP(int enemyHP) {
        this.enemyHP = enemyHP;
    }

    public String toString() {
        return "Enemy{enemyType = " + enemyType + ", enemyHP = " + enemyHP + "}";
    }
}
