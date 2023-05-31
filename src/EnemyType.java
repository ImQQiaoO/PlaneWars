import java.util.ArrayList;

public class EnemyType {
    public static final int NORMAL_ENEMY = 0;
    public static final int THREE_MEMBER_GROUP = 1;

    private static boolean timeToStop = false;
    private static boolean goLeft = true;

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
        if (enemyType == THREE_MEMBER_GROUP) {
            for (Enemy enemy : specialEnemyList) {
                enemy.updateLocation(dt);
                if ((enemy == specialEnemyList.get(specialEnemyList.size() - 1)) && (enemy.getY() > 200)) {
                    // If the position of last enemy in the list is larger than 100,
                    // clear the velocity of all enemies in the list.
                    clearVerticalVelocity(specialEnemyList);
                }
            }
            if (timeToStop) {
                if (specialEnemyList.isEmpty()) {
                    timeToStop = false;
                    return;
                }
                if (goLeft && specialEnemyList.get(0).getX() - specialEnemyList.get(0).getWidth() / 2 > 0) {
                    for (Enemy enemy : specialEnemyList) {
                        enemy.setVx(-30);
                    }
                } else if (!goLeft && specialEnemyList.get(specialEnemyList.size() - 1).getX() + specialEnemyList.get(specialEnemyList.size() - 1).getWidth() / 2 < SimpleGame.gameWidth) {
                    for (Enemy enemy : specialEnemyList) {
                        enemy.setVx(30);
                    }
                }

               if (specialEnemyList.get(0).getX() - specialEnemyList.get(0).getWidth() / 2 <= 0) {
                    goLeft = false;
                } else if (specialEnemyList.get(specialEnemyList.size() - 1).getX() + specialEnemyList.get(specialEnemyList.size() - 1).getWidth() / 2 >= SimpleGame.gameWidth) {
                    goLeft = true;
                }
            }
        }

    }

    public static void clearVerticalVelocity(ArrayList<Enemy> specialEnemyList) {
        for (Enemy enemy : specialEnemyList) {
            enemy.setVx(0);
            enemy.setVy(0);
            timeToStop = true;
        }

    }


}
