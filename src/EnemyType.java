import java.util.ArrayList;

public class EnemyType {
    public static final int NORMAL_ENEMY = 0;
    public static final int THREE_MEMBER_GROUP = 1;

    /**
     * This class is not allowed to be instantiated.
     */
    private EnemyType() {
    }

    /**
     * This method is used to control the position of the enemy.
     *
     * @param dt               The time interval between two frames.
     * @param enemyType        The type of the enemy.
     * @param specialEnemyList The list of special enemies.
     */
    public static void specialEnemyPositionController(double dt, int enemyType, ArrayList<Enemy> specialEnemyList) {
        for (Enemy enemy : specialEnemyList) {
            if (specialEnemyList.get(0).getY() < 100) { //189
                enemy.updateLocation(dt);
            }
        }
    }

}
