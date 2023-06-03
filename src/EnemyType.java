import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

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
    public static final int MISSILE_ENEMY = 3;
    public static final int MISSILE = 4;

    /**
     * Boss AI related variables.
     */
    public static boolean timeToStop = false;
    private static boolean goLeft = true;
    public static int moveFrameCount = 0;
    private static int missileCnt = 0;

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
        switch (enemyType) {
            case NORMAL_ENEMY:
                for (Enemy enemy : specialEnemyList) {
                    enemy.updateEnemy(dt);
                }
                break;
            case THREE_MEMBER_GROUP:
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
                break;
            case IMPACT_BOSS:
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
                            moveToTarget(specialEnemyList, targetX);
                        } else {
                            sprintToTarget(specialEnemyList);
                        }
                    } else {
                        if (moveFrameCount <= 500) {
                            double playerX1 = SimpleGame.playerPlane[0].getX();
                            double playerX2 = SimpleGame.playerPlane[1].getX();
                            double distanceToPlayer1 = Math.abs(playerX1 - specialEnemyList.get(0).getX());
                            double distanceToPlayer2 = Math.abs(playerX2 - specialEnemyList.get(0).getX());
                            double targetX;
                            if (distanceToPlayer1 < distanceToPlayer2) {
                                targetX = playerX1 - specialEnemyList.get(0).getX();
                            } else {
                                targetX = playerX2 - specialEnemyList.get(0).getX();
                            }
                            moveToTarget(specialEnemyList, targetX);
                        } else {
                            sprintToTarget(specialEnemyList);
                        }
                    }
                }
                break;
            case MISSILE_ENEMY:
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
                            enemy.setVx(-50);
                        }
                    } else if (!goLeft && specialEnemyList.get(specialEnemyList.size() - 1).getX() + specialEnemyList.get(specialEnemyList.size() - 1).getWidth() / 2 < SimpleGame.gameWidth) {
                        for (Enemy enemy : specialEnemyList) {
                            enemy.setVx(50);
                        }
                    }

                    if (specialEnemyList.get(0).getX() - 200 <= 0) {
                        goLeft = false;
                    } else if (specialEnemyList.get(specialEnemyList.size() - 1).getX() + 200 >= SimpleGame.gameWidth) {
                        goLeft = true;
                    }
                    if (moveFrameCount % 200 == 0) {
                        missileCnt++;
                        SimpleGame.missileEnemyList.add(new Enemy(specialEnemyList.get(0).getX(), specialEnemyList.get(0).getY() + specialEnemyList.get(0).getHeight() / 2, 0, 100, 50, 50, GameEngine.loadImage("src/resources/BulletAutoMissile.png"), 3, 100));
                        moveFrameCount = moveFrameCount - 20;
                        if (missileCnt == 3) {
                            missileCnt = 0;
                            moveFrameCount = 0;
                        }
                    }
                    moveFrameCount++;
                }
                break;
        }
    }

    private static void moveToTarget(ArrayList<Enemy> specialEnemyList, double targetX) {
        moveFrameCount++;
        if (targetX > 0) {
            for (Enemy enemy : specialEnemyList) {
                double vx = targetX * targetX * 0.02;
                if (vx > 100) {
                    vx = 100;
                }
                enemy.setVx(vx);
            }
        } else if (targetX < 0) {
            for (Enemy enemy : specialEnemyList) {
                double vx = -targetX * targetX * 0.02;
                if (vx < -100) {
                    vx = -100;
                }
                enemy.setVx(vx);
            }
        }
    }

    private static void sprintToTarget(ArrayList<Enemy> specialEnemyList) {
        for (Enemy enemy : specialEnemyList) {
            enemy.setVx(0);
        }
        //At the same time, the boss sprint to the player's position.
        for (Enemy enemy : specialEnemyList) {
            enemy.setVy(900);
            // If the boss is out of the screen, reset the position of the boss.
            if (enemy.getY() > SimpleGame.gameHeight) {
                enemy.setY(-enemy.getHeight());
//                                enemy.setX(SimpleGame.gameWidth / 2.0);
                moveFrameCount = 0;
                timeToStop = false;
            }
        }
    }


    public static void clearVelocity(ArrayList<Enemy> specialEnemyList) {
        for (Enemy enemy : specialEnemyList) {
            enemy.setVx(0);
            enemy.setVy(0);
            timeToStop = true;
        }

    }

    public static void BossBullet(int x, double bulletX_circle, double bulletY_circle, double bulletWidth_circle, double bulletHeight_circle, Image bulletImage_circle, int bulletDamage_circle, int bulletInterval_circle) {
        double bulletVx_circle;
        double bulletVy_circle;
        if (x < 2) {
            //第四象限
            for (int i = -100; i <= 100; i = i + 30) {
                bulletVx_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVy_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第二象限
            for (int i = 0; i <= 100; i = i + 20) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第三象限
            for (int i = 0; i <= 100; i = i + 20) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第一象限
            for (int i = 0; i <= 100; i = i + 20) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
        } else if (x < 4) {
            //第四象限
            for (int i = -100; i <= 100; i = i + 20) {
                bulletVx_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVy_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第二象限
            for (int i = 10; i <= 90; i = i + 15) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第三象限
            for (int i = 10; i <= 90; i = i + 15) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第一象限
            for (int i = 10; i <= 90; i = i + 15) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
        } else if (x < 6) {
            //第四象限
            for (int i = 10; i <= 100; i = i + 25) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第二象限
            for (int i = 10; i <= 100; i = i + 25) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第三象限
            for (int i = 10; i <= 100; i = i + 25) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第一象限
            for (int i = 10; i <= 100; i = i + 25) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
        } else {
            //第四象限
            for (int i = 50; i <= 100; i = i + 5) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第二象限
            for (int i = 50; i <= 100; i = i + 5) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第三象限
            for (int i = 50; i <= 100; i = i + 5) {
                bulletVy_circle = i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = -abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
            //第一象限
            for (int i = 50; i <= 100; i = i + 5) {
                bulletVy_circle = -i;
                double tep = sqrt(10000 - i * i);
                bulletVx_circle = abs(tep);
                SimpleGame.enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
            }
        }
    }


}
