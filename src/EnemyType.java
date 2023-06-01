import java.util.ArrayList;

public class EnemyType {
    /*
        TODO: 修改敌人的图片！！！
     */

    /**
     * Enumeration of enemy types.
     * The type of the enemy.
     */
    public static final int NORMAL_ENEMY = 0;
    public static final int THREE_MEMBER_GROUP = 1;
    public static final int IMPACT_BOSS = 2;

    /**
     * Boss AI related variables.
     */
    public static boolean timeToStop = false;
    private static boolean goLeft = true;
    public static int moveFrameCount = 0;

    /**
     * This class is not allowed to be instantiated.
     */
    private EnemyType() {
    }

    /**
     * This method is used to control the position of the enemy.
     * The enemy will move down, and then move to the left and right.
     *
     * @param dt               The time interval between two frames.
     * @param enemyType        The type of the enemy.
     * @param specialEnemyList The list of special enemies.
     */
    public static void specialEnemyPositionController(double dt, int enemyType, ArrayList<Enemy> specialEnemyList) {
        if (enemyType == THREE_MEMBER_GROUP) {
            for (Enemy enemy : specialEnemyList) {
                enemy.updateEnemy(dt);
                if ((enemy == specialEnemyList.get(specialEnemyList.size() - 1)) && (enemy.getY() > 200)) {
                    // If the position of last enemy in the list is larger than 100,
                    // clear the velocity of all enemies in the list.
                    clearVelocity(specialEnemyList);
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
        } else if (enemyType == IMPACT_BOSS) {
            // 这个boss在就位后会锁定玩家位置，然后向玩家位置冲刺
            for (Enemy enemy : specialEnemyList) {
                enemy.updateEnemy(dt);
                if (enemy.getY() > 150 && moveFrameCount <= 500) {
                    // If the position of last enemy in the list is larger than 150,
                    // clear the velocity of all enemies in the list.
                    clearVelocity(specialEnemyList);
                }
            }
            if (timeToStop) {
                if (specialEnemyList.isEmpty()) {
                    timeToStop = false;
                    return;
                }
                if (SimpleGame.isSinglePlayer) {
                    if (moveFrameCount <= 500) {
                        // If the game is single player mode, the boss will move to the player's position, only X axis.
                        double playerX = SimpleGame.playerPlane[0].getX();
                        double targetX = playerX - specialEnemyList.get(0).getX();
                        moveFrameCount++;
                        if (targetX > 0) {
                            for (Enemy enemy : specialEnemyList) {
                                enemy.setVx(50);
                            }
                        } else if (targetX < 0) {
                            for (Enemy enemy : specialEnemyList) {
                                enemy.setVx(-50);
                            }
                        }
                    } else {
                        for (Enemy enemy : specialEnemyList) {
                            enemy.setVx(0);
                        }
                        //At the same time, the boss sprint to the player's position.
                        for (Enemy enemy : specialEnemyList) {
                            enemy.setVy(900);
                            // If the boss is out of the screen, reset the position of the boss.
                            if (enemy.getY() > SimpleGame.gameHeight) {
                                enemy.setY(-enemy.getHeight());
                                enemy.setX(SimpleGame.gameWidth / 2.0);
                                moveFrameCount = 0;
                                timeToStop = false;
                            }
                        }
                    }
                }
            }
        } else if (1 > 0) {

        }
    }

    public static void clearVelocity(ArrayList<Enemy> specialEnemyList) {
        for (Enemy enemy : specialEnemyList) {
            enemy.setVx(0);
            enemy.setVy(0);
            timeToStop = true;
        }

    }


}
