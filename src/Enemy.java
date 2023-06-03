import java.awt.*;

public class Enemy extends GameObject {

    private int enemyType;
    private int enemyHP;
    private double enemyAngle;


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

    public void updateEnemy(double dt) {
        updateLocation(dt);
    }

    public void updateEnemy(double dt, PlayerPlane[] playerPlane) {
        updateLocation(dt, playerPlane);
    }

    public void updateLocation(double dt) {
        setX(getX() + getVx() * dt);
        setY(getY() + getVy() * dt);
    }

    public void updateLocation(double dt, PlayerPlane[] playerPlane) {
        // Recalculate position based on movement speed

        //Distance between enemy & player
        double[] dist = new double[2];
        double minDist = 1000000000;
        int minIndex = 0;
        for(int i = 0; i < PlayerPlane.playerNumber; i++) {
            dist[i] = GameEngine.distance(this.getX(), this.getY(), playerPlane[i].getX(), playerPlane[i].getY());
            if(dist[i] < minDist) {
                // Find the player that is closest to the enemy
                minDist = dist[i];
                minIndex = i;
            }
        }

        //Direction from enemy-->player
        //playerPos - enemyPos  enemy-->player
        double dirX = playerPlane[minIndex].getX() - this.getX();
        double dirY = playerPlane[minIndex].getY() - this.getY();

        //Normalized vector of direction
        double normalizedDirX = dirX / minDist;
        double normalizedDirY = dirY / minDist;

        //Calculate the angle between current velocity and desired velocity
        double angle = GameEngine.atan2(normalizedDirY * getVx() - normalizedDirX * getVy(),
                normalizedDirX * getVx() + normalizedDirY * getVy());

        //Rotate the velocity vector towards the desired direction
        double rotateAngle = Math.min(dt * Math.PI, GameEngine.abs(angle));
        double newVx = getVx() * Math.cos(rotateAngle) + normalizedDirX * 50 * Math.sin(rotateAngle);
        double newVy = getVy() * Math.cos(rotateAngle) + normalizedDirY * 50 * Math.sin(rotateAngle);
        setVx(newVx);
        setVy(newVy);
        //Calculate the angle between current velocity and Y-axis forward direction
        enemyAngle = GameEngine.atan2(getVx(), getVy());
//        System.out.println(enemyAngle);
        //Assign the direction for enemy
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

    public double getEnemyAngle() {
        return enemyAngle;
    }
}
