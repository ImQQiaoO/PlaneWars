import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class SimpleGame extends GameEngine {

    public static final int gameWidth = 600;
    public static final int gameHeight = 700;


    public static boolean isSinglePlayer; // make it static

    boolean isLeftKeyPressed = false;
    boolean isRightKeyPressed = false;
    boolean isUpKeyPressed = false;
    boolean isDownKeyPressed = false;
    boolean isAKeyPressed = false;
    boolean isDKeyPressed = false;
    boolean isWKeyPressed = false;
    boolean isSKeyPressed = false;

    private static Clip clip_background, clip_shoot;
    public static final PlayerPlane[] playerPlane = new PlayerPlane[2];

    private final boolean[] isNormal = new boolean[2];
    private final boolean[] isFire = new boolean[2];

    private final boolean[] isMissile = new boolean[2];

    private final boolean[] isLaser = new boolean[2];

    private final boolean[] isTypeSlash = new boolean[2];

    private final int[] fireCount = new int[2];

    private final int[] missileCount = new int[2];

    private final int[] missileTime = new int[2];
    private ArrayList<Enemy> enemyList;
    private ArrayList<Bullet> friendlyBulletList;

    private ArrayList<Bullet> enemyBulletList;
    private ArrayList<Item> itemList;
    private ArrayList<Explode> explodeList;
    private double waitTime = 0;
    private double commonEnemySpendTime = 0;
    private double specialEnemySpendTime = 0;
    private long startTime;

    //  Interval Counter
    private long intervalCounter = 0L;
    private ArrayList<Enemy> specialEnemyList;
    double specialEnemyWaitTime = 0;
    private boolean isSpecialEnemy = false;
    private int randBoss;

    SimpleGame(boolean isSinglePlayer) {
        SimpleGame.isSinglePlayer = isSinglePlayer;
        if (isSinglePlayer) {
            PlayerPlane.playerNumber = 1;
        } else {
            PlayerPlane.playerNumber = 2;
        }
    }

    public void init() {
        setWindowSize(gameWidth, gameHeight);
        Explode.loadExplodeImage();

        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("src/resources/fighting.wav"));
            clip_background = AudioSystem.getClip();
            clip_background.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip_background.loop(Clip.LOOP_CONTINUOUSLY);

        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            if (PlayerPlane.playerNumber == 1)
                playerPlane[pi] = new PlayerPlane(SimpleGame.gameWidth / 2.0, SimpleGame.gameHeight * 0.9, 1);
            else if (PlayerPlane.playerNumber == 2) {
                if (pi == 0)
                    playerPlane[pi] = new PlayerPlane(SimpleGame.gameWidth * 2.0 / 3.0, SimpleGame.gameHeight * 0.9, 1);
                else
                    playerPlane[pi] = new PlayerPlane(SimpleGame.gameWidth / 3.0, SimpleGame.gameHeight * 0.9, 2);
            }
        }
        for (int i = 0; i < 2; i++) {
            isNormal[i] = true;
            isFire[i] = false;
            isMissile[i] = false;
            isLaser[i] = false;
            isTypeSlash[i] = false;
            fireCount[i] = 0;
            missileCount[i] = 0;
            missileTime[i] = 0;
        }
        startTime = System.currentTimeMillis();// Get the start time of the game
        enemyList = new ArrayList<>(); // Store all enemies in the game
        friendlyBulletList = new ArrayList<>(); // Store all friendly bullets in the game
        specialEnemyList = new ArrayList<>();// Store all special enemies in the game
        specialEnemyWaitTime = new Random().nextDouble(24, 36) * 5; //2-3min
        specialEnemyWaitTime = 5; //TODO: Only for test

        itemList = new ArrayList<>(); // Store all items in the game
        enemyBulletList = new ArrayList<>(); // Store all enemy bullets in the game
        explodeList = new ArrayList<>(); // Store all explodes in the game
        randBoss = 0; // init randBoss

    }

    private void checkCollision() {
        // Check collision between player plane and walls
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            if (playerPlane[pi].getX() < playerPlane[pi].getWidth() / 2) {
                playerPlane[pi].setX(playerPlane[pi].getWidth() / 2);
                playerPlane[pi].setVx(0);
            } else if (playerPlane[pi].getX() > gameWidth - playerPlane[pi].getWidth() / 2) {
                playerPlane[pi].setX(gameWidth - playerPlane[pi].getWidth() / 2);
                playerPlane[pi].setVx(0);
            }
            if (playerPlane[pi].getY() < playerPlane[pi].getHeight() / 2) {
                playerPlane[pi].setY(playerPlane[pi].getHeight() / 2);
                playerPlane[pi].setVy(0);
            } else if (playerPlane[pi].getY() > gameHeight - playerPlane[pi].getHeight() / 2) {
                playerPlane[pi].setY(gameHeight - playerPlane[pi].getHeight() / 2);
                playerPlane[pi].setVy(0);
            }
        }


    }

    @Override
    public void update(double dt) {
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            playerPlane[pi].updatePlane(dt);
        }

        //Update the location of the enemies
        for (Enemy enemy : enemyList) {
            enemy.updateEnemy(dt);
        }

        //Update the location of special enemies
        EnemyType.specialEnemyPositionController(dt, randBoss, specialEnemyList);

        //Update the location of the items
        for (Item item : itemList) {
            item.updateLocation(dt, playerPlane);
        }

        //Update the frame of the explodes
        for (Explode explode : explodeList) {
            explode.updateExplosionIndex();
        }

        //Update the location of the bullets
        for (Bullet bullet : friendlyBulletList) {
            bullet.updateLocation(dt);
        }

        //Update the location of the enemy bullets
        for (Bullet bullet : enemyBulletList) {
            bullet.updateLocation(dt);
        }

        checkCollision();

        // Reset the generate time of special enemy
        if (isSpecialEnemy && specialEnemyList.size() == 0) {
            specialEnemyWaitTime = new Random().nextDouble(24, 36) * 5; //2-3min
            specialEnemyWaitTime = 5; //TODO: FOR TEST
            specialEnemySpendTime = 0;
            System.out.println("reset special enemy wait time");
            isSpecialEnemy = false;
        }

        // Generate enemies
        specialEnemySpendTime = specialEnemySpendTime + dt;
        if (specialEnemySpendTime <= specialEnemyWaitTime) {
            if (specialEnemyList.size() == 0) {
                commonEnemySpendTime = commonEnemySpendTime + dt;
                double generateEnemiesSeed = (double) ((System.currentTimeMillis() - startTime) / 1000) / 80 + 1;
                if (commonEnemySpendTime >= waitTime) {
                    commonEnemySpendTime = 0;
                    generateEnemies(EnemyType.NORMAL_ENEMY);
                    waitTime = new Random().nextDouble(0, 2) / generateEnemiesSeed;
                }
            }
        } else if (enemyList.size() == 0 && specialEnemyList.size() == 0) {
            randBoss = new Random().nextInt(1, 3);
//            randBoss = 2; //TODO: FOR TEST Boss TYPE 2 (IMPACT_BOSS)
            if (randBoss == EnemyType.THREE_MEMBER_GROUP) {
                generateEnemies(EnemyType.THREE_MEMBER_GROUP); // Generate special enemies
            }
            else if (randBoss == EnemyType.IMPACT_BOSS) {
                generateEnemies(EnemyType.IMPACT_BOSS); // Generate special enemies
                EnemyType.moveFrameCount = 0;
            }
            isSpecialEnemy = true;
        }


        // Generate items
        //TODO: edit generate rule
        int randNum = rand(100);
        int randItemNum = rand(4);
        if (randNum == 0) {
            itemList.add(new Item(50, 50, Item.itemImages[randItemNum], randItemNum));
        }

        createMissile();


        // Generate friendly bullets
        generateFriendlyBullets();

        // Generate special bullets
        generateSpecialEnemyBullets();

        // Check collision between player plane and enemies
        checkCollisionEnemies(enemyList);
        checkCollisionEnemies(specialEnemyList);
        checkCollisionEnemies(enemyBulletList);

        // Check collision between friendly bullets and enemies
        checkCollisionFriendlyBullets(friendlyBulletList);

        // Check collision between player plane and items
        checkCollisionItems(itemList);

        //Help Garbage Collection
        enemyList.removeIf(enemy -> (enemy.getY() > gameHeight + enemy.getHeight() / 2) || enemy.getEnemyHP() <= 0);
        friendlyBulletList.removeIf(bullet -> (bullet.getY() < -bullet.getHeight() / 2) || bullet.getY() > gameHeight + bullet.getHeight() / 2);
        specialEnemyList.removeIf(enemy -> (enemy.getY() > gameHeight + enemy.getHeight() / 2) || enemy.getEnemyHP() <= 0);
        enemyBulletList.removeIf(bullet -> (bullet.getY() < -bullet.getHeight() / 2) || bullet.getY() > gameHeight + bullet.getHeight() / 2);

        itemList.removeIf(item -> (item.getY() > gameHeight + item.getHeight() / 2) || item.getY() < -item.getHeight() / 2
                || item.getX() > gameWidth + item.getWidth() / 2 || item.getX() < -item.getWidth() / 2 || item.isCollected());
        explodeList.removeIf(explode -> explode.getExplosionIndex() == 0);
    }

    private void createMissile() {
        if (isTypeSlash[0]) {
            if(isMissile[0]){
                double bulletWidth = 38;
                double bulletHeight = 26;
                double bulletX1 = playerPlane[0].getX()-playerPlane[0].getWidth()/4;
                double bulletY1 = playerPlane[0].getY() - playerPlane[0].getHeight()/4;
                double bulletVx = -500;
                double bulletVy = -865;
                Image bulletImage = loadImage("src/resources/BulletMissile.png");
                double bulletX12 = playerPlane[0].getX()+playerPlane[0].getWidth()/4;
                double bulletY12 = playerPlane[0].getY() - playerPlane[0].getHeight()/4;
                double bulletVx2 = 500;
                double bulletVy2 = -865;
                Image bulletImage2 = loadImage("src/resources/BulletMissile.png");
                int bulletDamage = 10;
                int bulletIntervalP1 = 6;
                if(missileTime[0] % bulletIntervalP1 == 0){
                    if (missileCount[0] < 20){
                        friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP1));
                        friendlyBulletList.add(new Bullet(bulletX12, bulletY12, bulletVx2, bulletVy2, bulletWidth, bulletHeight, bulletImage2, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP1));
                        initialize_ShootSound();
                        clip_shoot.start();
                        missileCount[0]++;
                    }else {
                        missileTime[0]=0;
                        missileCount[0] = 0;
                        isMissile[0] = false;
                    }
                }
            }
            missileTime[0] ++;
        }
        if(PlayerPlane.playerNumber == 2){
            if (isTypeSlash[1]) {
                if(isMissile[1]){
                    double bulletWidth = 38;
                    double bulletHeight = 26;
                    double bulletX1 = playerPlane[1].getX()-playerPlane[1].getWidth()/4;
                    double bulletY1 = playerPlane[1].getY() - playerPlane[1].getHeight()/4;
                    double bulletVx = -500;
                    double bulletVy = -865;
                    Image bulletImage = loadImage("src/resources/BulletMissile.png");
                    double bulletX12 = playerPlane[1].getX()+playerPlane[1].getWidth()/4;
                    double bulletY12 = playerPlane[1].getY() - playerPlane[1].getHeight()/4;
                    double bulletVx2 = 500;
                    double bulletVy2 = -865;
                    Image bulletImage2 = loadImage("src/resources/BulletMissile.png");
                    int bulletDamage = 10;
                    int bulletIntervalP2 = 6;
                    if(missileTime[1] % bulletIntervalP2 == 0){
                        if (missileCount[1] < 20){
                            friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP2));
                            friendlyBulletList.add(new Bullet(bulletX12, bulletY12, bulletVx2, bulletVy2, bulletWidth, bulletHeight, bulletImage2, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP2));
                            initialize_ShootSound();
                            clip_shoot.start();
                            missileCount[1]++;
                        }else {
                            missileTime[1]=0;
                            missileCount[1] = 0;
                            isMissile[1] = false;
                        }
                    }
                }
                missileTime[1] ++;
            }
        }
    }

    /**
     * Check if the player's plane collides with enemies' bullets or enemies' planes.
     *
     * @param hostileList ArrayList of enemies or enemies' bullets
     */
    public void checkCollisionEnemies(ArrayList<? extends GameObject> hostileList) {
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            for (GameObject object : hostileList) {
                if (isCollision(playerPlane[pi], object)) {
                    System.out.println("Collision!"); //TODO: only for test
                    playerPlane[pi].decreaseLife();
                }
            }
        }
    }

    /**
     * Check if the enemies' plane collides with friendly bullets.
     * If collision, the enemy's HP will be reduced by the damage to the bullet.
     * The bullet will be removed from the friendlyBulletList.
     *
     * @param friendlyBulletList ArrayList of Friendly bullets
     */
    public void checkCollisionFriendlyBullets(ArrayList<Bullet> friendlyBulletList) {
        ArrayList<Enemy> currEnemyList = new ArrayList<>();
        currEnemyList.addAll(enemyList);
        currEnemyList.addAll(specialEnemyList);
        for (Enemy enemy : currEnemyList) {
            Iterator<Bullet> bulletIterator = friendlyBulletList.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (isCollision(enemy, bullet)) {
                    System.out.println("Hit!!"); //TODO: only for test
                    enemy.setEnemyHP(enemy.getEnemyHP() - bullet.getDamage());
                    if(enemy.getEnemyHP() <= 0) {
                        switch (enemy.getEnemyType()){
                            case EnemyType.NORMAL_ENEMY -> {
                                //TODO: add score
                                explodeList.add(new Explode(enemy.getX(), enemy.getY(), 1));
                            }
                            case EnemyType.THREE_MEMBER_GROUP, EnemyType.IMPACT_BOSS -> {
                                //TODO: add score
                                explodeList.add(new Explode(enemy.getX(), enemy.getY(), 2.5));
                            }
                        }
                    }
                    bulletIterator.remove();
                }
            }
        }
    }

    /**
     * Check collision between playerObjects plane and enemies or bullets use AABB method
     *
     * @param playerObjects PlayerPlane
     * @param enemyObjects  Enemy or Bullet
     * @return true if collision
     */
    @SuppressWarnings("Duplicates")
    public boolean isCollision(GameObject playerObjects, GameObject enemyObjects) {
        if (playerObjects instanceof PlayerPlane) {
            // If the object is playPlane
            double playerPlaneWingLeft = playerObjects.getX() - playerObjects.getWidth() / 2;
            double playerPlaneWingRight = playerObjects.getX() + playerObjects.getWidth() / 2;
            double playerPlaneHeadLeft = playerObjects.getX() - playerObjects.getWidth() / 2 + (playerObjects.getWidth() / 3);
            double playerPlaneHeadRight = playerObjects.getX() + playerObjects.getWidth() / 2 - (playerObjects.getWidth() / 3);
            double playerPlaneTop = playerObjects.getY() - playerObjects.getHeight() / 2;
            double playerPlaneBottom = playerObjects.getY() + playerObjects.getHeight() / 2;
            double playerPlaneWingTop = playerObjects.getY() - playerObjects.getHeight() / 2 + (playerObjects.getHeight() / 3);
            double enemyLeft = enemyObjects.getX() - enemyObjects.getWidth() / 2;
            double enemyRight = enemyObjects.getX() + enemyObjects.getWidth() / 2;
            double enemyTop = enemyObjects.getY() - enemyObjects.getHeight() / 2;
            double enemyBottom = enemyObjects.getY() + enemyObjects.getHeight() / 2;
            return !(playerPlaneHeadLeft > enemyRight || playerPlaneHeadRight < enemyLeft || playerPlaneTop > enemyBottom || playerPlaneWingTop < enemyTop)
                    || !(playerPlaneWingLeft > enemyRight || playerPlaneWingRight < enemyLeft || playerPlaneWingTop > enemyBottom || playerPlaneBottom < enemyTop);
        } else {
            // If the object is enemy or bullet
            double playerLeft = playerObjects.getX() - playerObjects.getWidth() / 2;
            double playerRight = playerObjects.getX() + playerObjects.getWidth() / 2;
            double playerTop = playerObjects.getY() - playerObjects.getHeight() / 2;
            double playerBottom = playerObjects.getY() + playerObjects.getHeight() / 2;
            double enemyLeft = enemyObjects.getX() - enemyObjects.getWidth() / 2;
            double enemyRight = enemyObjects.getX() + enemyObjects.getWidth() / 2;
            double enemyTop = enemyObjects.getY() - enemyObjects.getHeight() / 2;
            double enemyBottom = enemyObjects.getY() + enemyObjects.getHeight() / 2;
            return !(playerLeft > enemyRight || playerRight < enemyLeft || playerTop > enemyBottom || playerBottom < enemyTop);
        }
    }

    // check collision between player plane and items
    public void checkCollisionItems(ArrayList<Item> itemList) {
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            for (Item item : itemList) {
                if (isCollision(playerPlane[pi], item)) {
                    item.setCollected(true);
                    // Set item effect
                    switch (item.getItemType()) {
                        case Item.ITEM_TYPE_LIFE -> {
                            playerPlane[pi].increaseLife();
                            System.out.println("Collected: ITEM_TYPE_LIFE");
                        }
                        case Item.ITEM_TYPE_FIRE -> {
                            System.out.println("Collected: ITEM_TYPE_FIRE");    //TODO: add item effect
                            isFire[pi] = true;
                            isNormal[pi] = false;
                        }
                        case Item.ITEM_TYPE_LASER -> {
                            System.out.println("Collected: ITEM_TYPE_LASER");    //TODO: add item effect
                        }
                        case Item.ITEM_TYPE_MISSILE -> {
                            System.out.println("Collected: ITEM_TYPE_MISSILE");    //TODO: add item effect
                            isMissile[pi] = true;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creating Enemy Objects
     */
    public void generateEnemies(int enemyType) {
        if (enemyType == EnemyType.NORMAL_ENEMY) {
            // Create normal enemy
            // Create different kinds of enemy by random number
            int i = rand(9);
            if(i < 4){
                double enemyWidth = 31;
                double enemyHeight = 23;
                double enemyX = new Random().nextDouble(enemyWidth / 2, gameWidth - enemyWidth / 2);
                double enemyY = -enemyHeight / 2;
                double enemyVx = 0;
                double enemyVy = 200; //200
                Image enemyImage = loadImage("src/resources/Enemy01.png");
                int enemyHp = 5;
                enemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
            }
            else if(i < 7){
                double enemyWidth = 44;
                double enemyHeight = 67;
                double enemyX = new Random().nextDouble(enemyWidth / 2, gameWidth - enemyWidth / 2);
                double enemyY = -enemyHeight / 2;
                double enemyVx = 0;
                double enemyVy = 100; //100
                Image enemyImage = loadImage("src/resources/Enemy02.png");
                int enemyHp = 15;
                enemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
            }
            else{
                double enemyWidth = 52;
                double enemyHeight = 40;
                double enemyX = new Random().nextDouble(enemyWidth / 2, gameWidth - enemyWidth / 2);
                double enemyY = -enemyHeight / 2;
                double enemyVx = 0;
                double enemyVy = 150; //200
                Image enemyImage = loadImage("src/resources/Enemy03.png");
                int enemyHp = 10;
                enemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
            }

        } else if (enemyType == EnemyType.THREE_MEMBER_GROUP) {
            // Create three-member group
            double enemyWidth = 171;
            double enemyHeight = 111;
            double enemyXL = 100;
            double enemyXM = 300;
            double enemyXR = 500;
            double enemyYSide = -enemyHeight / 2 - enemyHeight;
            double enemyYMiddle = -enemyHeight / 2;
            double enemyVx = 0;
            double enemyVy = 200; //200
            Image enemyImage = loadImage("src/resources/EE0.png");
            int enemyHp = 100;
            Enemy groupEliteEnemyLeft = new Enemy(enemyXL, enemyYSide, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Enemy groupEliteEnemyMiddle = new Enemy(enemyXM, enemyYMiddle, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Enemy groupEliteEnemyRight = new Enemy(enemyXR, enemyYSide, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Collections.addAll(specialEnemyList, groupEliteEnemyLeft, groupEliteEnemyMiddle, groupEliteEnemyRight);
        } else if (enemyType == EnemyType.IMPACT_BOSS) {
            // Create impact boss
            double enemyWidth = 171;  //TODO: TO BE CHANGED
            double enemyHeight = 111;  //TODO: TO BE CHANGED
            double enemyX = gameWidth / 2.0;  //TODO: TO BE CHANGED
            double enemyY = -enemyHeight / 2;  //TODO: TO BE CHANGED
            double enemyVx = 0;
            double enemyVy = 100; //200
            Image enemyImage = loadImage("src/resources/EE0.png"); //TODO: TO BE CHANGED
            int enemyHp = 100;  //TODO: TO BE CHANGED
            specialEnemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
        }
    }

    /**
     * Generate friendly bullets
     */
    public void generateFriendlyBullets() {
        // Player 1
        intervalCounter++;  //千万别动！！
        if (isNormal[0]) {
            double bulletWidth = 14;
            double bulletHeight = 29;
            double bulletX1 = playerPlane[0].getX();
            double bulletY1 = playerPlane[0].getY() - playerPlane[0].getHeight() / 2;
            double bulletVx = 0;
            double bulletVy = -1000;
            Image bulletImage = loadImage("src/resources/Bullet01.png");
            int bulletDamage = 5;
            int bulletIntervalP1 = 30; // TODO: Shoot every 10 frames
            if (intervalCounter % bulletIntervalP1 == 0) {
                friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.NORMAL_BULLET, bulletDamage, bulletIntervalP1));
                initialize_ShootSound();
                clip_shoot.start();
            }
        }
        if (isFire[0]) {
            double bulletWidth = 32;
            double bulletHeight = 64;
            double bulletX1 = playerPlane[0].getX();
            double bulletY1 = playerPlane[0].getY() - playerPlane[0].getHeight() / 2;
            double bulletVx = 0;
            double bulletVy = -500;
            Image bulletImage = loadImage("src/resources/Bullet02.png");
            int bulletDamage = 5;
            int bulletIntervalP1 = 8; // TODO: Shoot every 10 frames
            if (intervalCounter % bulletIntervalP1 == 0) {
                if (fireCount[0] < 30) {
                    friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.FIRE_BULLET, bulletDamage, bulletIntervalP1));
                    initialize_ShootSound();
                    clip_shoot.start();
                    fireCount[0]++;
                } else {
                    fireCount[0] = 0;
                    isFire[0] = false;
                    isNormal[0] = true;
                }
            }

        }

        // Player 2
        if (PlayerPlane.playerNumber == 2) {
            if (isNormal[1]) {
                double bulletWidth = 14;
                double bulletHeight = 29;
                double bulletX2 = playerPlane[1].getX();
                double bulletY2 = playerPlane[1].getY() - playerPlane[1].getHeight() / 2;
                double bulletVx = 0;
                double bulletVy = -1000;
                Image bulletImage = loadImage("src/resources/Bullet01.png");
                int bulletDamage = 5;
                int bulletIntervalP2 = 5; // TODO: Shoot every 10 frames
                if (intervalCounter % bulletIntervalP2 == 0) {
                    friendlyBulletList.add(new Bullet(bulletX2, bulletY2, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.NORMAL_BULLET, bulletDamage, bulletIntervalP2));
                    initialize_ShootSound();
                    clip_shoot.start();
                }
            }
            if (isFire[1]) {
                double bulletWidth = 32;
                double bulletHeight = 64;
                double bulletX1 = playerPlane[1].getX();
                double bulletY1 = playerPlane[1].getY() - playerPlane[0].getHeight() / 2;
                double bulletVx = 0;
                double bulletVy = -500;
                Image bulletImage = loadImage("src/resources/Bullet02.png");
                int bulletDamage = 5;
                int bulletIntervalP2 = 8; // TODO: Shoot every 10 frames
                if (intervalCounter % bulletIntervalP2 == 0) {
                    if (fireCount[1] < 30) {
                        friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.FIRE_BULLET, bulletDamage, bulletIntervalP2));
                        initialize_ShootSound();
                        clip_shoot.start();
                        fireCount[1]++;
                    } else {
                        fireCount[1] = 0;
                        isFire[1] = false;
                        isNormal[1] = true;
                    }
                }
            }
        }
    }

    /**
     * Generate special enemy bullets
     */
    public void generateSpecialEnemyBullets() {

        for (Enemy enemy : specialEnemyList) {
            if (enemy.getEnemyType() == EnemyType.THREE_MEMBER_GROUP && EnemyType.timeToStop) {
                double bulletWidth_simple = 14;
                double bulletHeight_simple = 29;
                double bulletX_simple = enemy.getX();
                double bulletY_simple = enemy.getY() + enemy.getHeight() / 2;
                double bulletX_simple_left = enemy.getX() - 60;
                double bulletY_simple_left = enemy.getY() + enemy.getHeight() / 2;
                double bulletX_simple_right = enemy.getX() + 60;
                double bulletY_simple_right = enemy.getY() + enemy.getHeight() / 2;
                double bulletVx_simpe = 0;
                double bulletVy_simple = 1000;
                double bulletVy_simple_side = 200;
                Image bulletImage_simple = loadImage("src/resources/Bullet01.png");
                Image bulletImage_simple_side = loadImage("src/resources/Bullet04.png");
                int bulletDamage_simple = 1;
                int bulletDamage_simple_side = 2;
                int bulletInterval_simple = 30;// TODO: Shoot every 30 frames

                //Randomly fire the center and side rounds
                if(intervalCounter % bulletInterval_simple == 0){
                    int x = rand(10);
                    if(x < 3)
                    enemyBulletList.add(new Bullet(bulletX_simple_left, bulletY_simple_left, bulletVx_simpe, bulletVy_simple_side, bulletWidth_simple, bulletHeight_simple, bulletImage_simple_side, BulletType.NORMAL_BULLET, bulletDamage_simple_side, bulletInterval_simple));
                    else if(x < 6)
                    enemyBulletList.add(new Bullet(bulletX_simple_right, bulletY_simple_right, bulletVx_simpe, bulletVy_simple_side, bulletWidth_simple, bulletHeight_simple, bulletImage_simple_side, BulletType.NORMAL_BULLET, bulletDamage_simple_side, bulletInterval_simple));
                    else
                    enemyBulletList.add(new Bullet(bulletX_simple, bulletY_simple, bulletVx_simpe, bulletVy_simple, bulletWidth_simple, bulletHeight_simple, bulletImage_simple, BulletType.NORMAL_BULLET, bulletDamage_simple, bulletInterval_simple));
                }
            }
            else if(enemy.getEnemyType() == EnemyType.IMPACT_BOSS){
                //增加360度散弹
                double bulletWidth_circle = 12;
                double bulletHeight_circle = 12;
                double bulletX_circle = enemy.getX();
                double bulletY_circle = enemy.getY();
                double bulletVx_circle;
                double bulletVy_circle;
                Image bulletImage_circle = loadImage("src/resources/Bullet03.png");
                int bulletDamage_circle = 5;
                int bulletInterval_circle = 100;
                if(intervalCounter % bulletInterval_circle == 0) {
                    int x = rand(8);
                    if(x < 2){
                        //第四象限
                        for (int i = -100; i <= 100; i = i + 30) {
                            bulletVx_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVy_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第二象限
                        for (int i = 0; i <= 100; i = i + 20) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第三象限
                        for (int i = 0; i <= 100; i = i + 20) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第一象限
                        for (int i = 0; i <= 100; i = i + 20) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                    }
                    else if(x < 4){
                        //第四象限
                        for (int i = -100; i <= 100; i = i + 20) {
                            bulletVx_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVy_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第二象限
                        for (int i = 10; i <= 90; i = i + 15) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第三象限
                        for (int i = 10; i <= 90; i = i + 15) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第一象限
                        for (int i = 10; i <= 90; i = i + 15) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                    }
                    else if(x < 6){
                        //第四象限
                        for (int i = 10; i <= 100; i = i + 25) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第二象限
                        for (int i = 10; i <= 100; i = i + 25) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第三象限
                        for (int i = 10; i <= 100; i = i + 25) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第一象限
                        for (int i = 10; i <= 100; i = i + 25) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                    }
                    else {
                        //第四象限
                        for (int i = 50; i <= 100; i = i + 5) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第二象限
                        for (int i = 50; i <= 100; i = i + 5) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第三象限
                        for (int i = 50; i <= 100; i = i + 5) {
                            bulletVy_circle = i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = -abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                        //第一象限
                        for (int i = 50; i <= 100; i = i + 5) {
                            bulletVy_circle = -i;
                            double tep = sqrt(10000 - i * i);
                            bulletVx_circle = abs(tep);
                            enemyBulletList.add(new Bullet(bulletX_circle, bulletY_circle, bulletVx_circle, bulletVy_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, BulletType.CIRCLE_BULLET, bulletDamage_circle, bulletInterval_circle));
                        }
                    }
                }
            }
        }
    }

    //Add bullet firing sound
    private static void initialize_ShootSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/shoot.wav"));
            clip_shoot = AudioSystem.getClip();
            clip_shoot.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent() {
        // Clear the background to black
        changeBackgroundColor(black);
        clearBackground(gameWidth, gameHeight);

        // Draw the plane
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            // Draw the player plane
            int alpha = 255;
            if (playerPlane[pi].getProtectTime() > 0) {
                alpha = 150;
            }
            double playerPlaneWidth = playerPlane[pi].getWidth();
            double playerPlaneHeight = playerPlane[pi].getHeight();
            drawImage(playerPlane[pi].getImage(), playerPlane[pi].getX() - playerPlaneWidth / 2, playerPlane[pi].getY() - playerPlaneHeight / 2, playerPlaneWidth, playerPlaneHeight, alpha);
            // Draw the tail fire
            double tailFireWidth = PlayerPlane.normalTailFireImage.getWidth(null);
            double tailFireHeight = PlayerPlane.normalTailFireImage.getHeight(null);
            if (playerPlane[pi].getVy() < 0) {
                tailFireHeight = tailFireHeight * 2;    // If the plane is moving up, the tail fire is longer
            } else if (playerPlane[pi].getVy() > 0) {
                tailFireHeight = tailFireHeight / 2;    // If the plane is moving down, the tail fire is shorter
            }
            drawImage(PlayerPlane.normalTailFireImage, playerPlane[pi].getX() - tailFireWidth / 2.0, playerPlane[pi].getY() + playerPlaneHeight / 2, tailFireWidth, tailFireHeight, alpha);
            drawImage(PlayerPlane.normalTailFireImage, playerPlane[pi].getX()-tailFireWidth/2.0, playerPlane[pi].getY()+playerPlaneHeight/2, tailFireWidth, tailFireHeight, alpha);
            // Draw the plane explosion
            double explosionIndex = playerPlane[pi].getExplosionIndex();
            if(explosionIndex > 0){
                int explosionFrameIndex = floor(16 - explosionIndex);
                drawImage(Explode.frames[explosionFrameIndex], playerPlane[pi].getX()-playerPlaneWidth/2, playerPlane[pi].getY()-playerPlaneHeight/2, playerPlaneWidth, playerPlaneHeight);
                System.out.println("Explosion frame index: " + explosionFrameIndex);
            }
        }

        // Draw the enemies
        for (Enemy enemy : enemyList) {
            drawImage(enemy.getImage(), enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }

        //Draw three special enemy Group
        for (Enemy enemy : specialEnemyList) {
            drawImage(enemy.getImage(), enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }

        // Draw the bullets
        for (Bullet bullet : friendlyBulletList) {
            drawImage(bullet.getImage(), bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
        }

        // Draw the enemy bullets
        for (Bullet bullet : enemyBulletList) {
            drawImage(bullet.getImage(), bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
        }

        // Draw the explosion
        System.out.println("Explode list size: " + explodeList.size());
        for (Explode explode : explodeList) {
            System.out.println("Explosion index: " + explode.getExplosionIndex());
            double explodeWidth = explode.getWidth();
            double explodeHeight = explode.getHeight();
            int explodeFrameIndex = floor(16 - explode.getExplosionIndex());
            drawImage(Explode.frames[explodeFrameIndex], explode.getX() - explodeWidth / 2, explode.getY() - explodeHeight / 2, explodeWidth, explodeHeight);
        }

        changeColor(green);//TODO: for testing only
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            drawRectangle(playerPlane[pi].getX() - playerPlane[pi].getWidth() / 2, playerPlane[pi].getY() - playerPlane[pi].getHeight() / 2, playerPlane[pi].getWidth(), playerPlane[pi].getHeight());
        }

        changeColor(red);//TODO: for testing only
        for (Enemy enemy : enemyList) {
            drawRectangle(enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }

        changeColor(blue);//TODO: for testing only
        for (Bullet bullet : friendlyBulletList) {
            drawRectangle(bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
        }

        changeColor(white);//TODO: for testing only
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            drawText(20 + 300 * pi, 50, "Player " + (pi + 1) + " Life: " + playerPlane[pi].getLife());
        }

        // Draw the item
        for (Item item : itemList) {
            drawImage(item.getImage(), item.getX() - item.getWidth() / 2, item.getY() - item.getHeight() / 2, item.getWidth(), item.getHeight());
        }
    }

    // Called whenever a key is pressed
    @SuppressWarnings("Duplicates")
    public void keyPressed(KeyEvent e) {
        //-------------------------------------------------------
        // Player 1 Key Control
        //-------------------------------------------------------
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Move Left
            isLeftKeyPressed = true;
            if (playerPlane[0].getX() > playerPlane[0].getWidth() / 2) {
                playerPlane[0].setVx(-playerPlane[0].getMovingSpeed());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Move Right
            isRightKeyPressed = true;
            if (playerPlane[0].getX() < gameWidth - playerPlane[0].getWidth() / 2) {
                playerPlane[0].setVx(playerPlane[0].getMovingSpeed());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            // Move Up
            isUpKeyPressed = true;
            if (playerPlane[0].getY() > playerPlane[0].getHeight() / 2) {
                playerPlane[0].setVy(-playerPlane[0].getMovingSpeed());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Move Down
            isDownKeyPressed = true;
            if (playerPlane[0].getY() < gameHeight - playerPlane[0].getHeight() / 2) {
                playerPlane[0].setVy(playerPlane[0].getMovingSpeed());
            }
        }
        if (e.getKeyCode() == 47) { // Slash
            isTypeSlash[0] = true;
            // Shot a missile

        }
        //-------------------------------------------------------
        // Player 2 Key Control
        //-------------------------------------------------------
        if (!isSinglePlayer) {
            if (e.getKeyCode() == KeyEvent.VK_A) {
                // Move Left
                isAKeyPressed = true;
                if (playerPlane[1].getX() > playerPlane[1].getWidth() / 2) {
                    playerPlane[1].setVx(-playerPlane[1].getMovingSpeed());
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                // Move Right
                isDKeyPressed = true;
                if (playerPlane[1].getX() < gameWidth - playerPlane[1].getWidth() / 2) {
                    playerPlane[1].setVx(playerPlane[1].getMovingSpeed());
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
                // Move Up
                isWKeyPressed = true;
                if (playerPlane[1].getY() > playerPlane[1].getHeight() / 2) {
                    playerPlane[1].setVy(-playerPlane[1].getMovingSpeed());
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                // Move Down
                isSKeyPressed = true;
                if (playerPlane[1].getY() < gameHeight - playerPlane[1].getHeight() / 2) {
                    playerPlane[1].setVy(playerPlane[1].getMovingSpeed());
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                isTypeSlash[1] = true;
            }
        }
    }

    // Called whenever a key is released
    public void keyReleased(KeyEvent e) {
        //-------------------------------------------------------
        // Player 1 Key Control
        //-------------------------------------------------------
        // If player releases left key
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Stop moving
            isLeftKeyPressed = false;
            if (!isRightKeyPressed) {
                playerPlane[0].setVx(0);
            } else {
                playerPlane[0].setVx(playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases right key
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Stop moving
            isRightKeyPressed = false;
            if (!isLeftKeyPressed) {
                playerPlane[0].setVx(0);
            } else {
                playerPlane[0].setVx(-playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases up key
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            // Stop moving
            isUpKeyPressed = false;
            if (!isDownKeyPressed) {
                playerPlane[0].setVy(0);
            } else {
                playerPlane[0].setVy(playerPlane[0].getMovingSpeed());
            }
        }
        // If player releases down key
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Stop moving
            isDownKeyPressed = false;
            if (!isUpKeyPressed) {
                playerPlane[0].setVy(0);
            } else {
                playerPlane[0].setVy(-playerPlane[0].getMovingSpeed());
            }
        }

        if (e.getKeyCode() == 47) {
            isTypeSlash[0] = false;
            missileTime[0] = 0;
        }
        //-------------------------------------------------------
        // Player 2 Key Control
        //-------------------------------------------------------
        if (!isSinglePlayer) {
            // If player releases left key
            if (e.getKeyCode() == KeyEvent.VK_A) {
                // Stop moving
                isAKeyPressed = false;
                if (!isDKeyPressed) {
                    playerPlane[1].setVx(0);
                } else {
                    playerPlane[1].setVx(playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases right key
            if (e.getKeyCode() == KeyEvent.VK_D) {
                // Stop moving
                isDKeyPressed = false;
                if (!isAKeyPressed) {
                    playerPlane[1].setVx(0);
                } else {
                    playerPlane[1].setVx(-playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases up key
            if (e.getKeyCode() == KeyEvent.VK_W) {
                // Stop moving
                isWKeyPressed = false;
                if (!isSKeyPressed) {
                    playerPlane[1].setVy(0);
                } else {
                    playerPlane[1].setVy(playerPlane[1].getMovingSpeed());
                }
            }
            // If player releases down key
            if (e.getKeyCode() == KeyEvent.VK_S) {
                // Stop moving
                isSKeyPressed = false;
                if (!isWKeyPressed) {
                    playerPlane[1].setVy(0);
                } else {
                    playerPlane[1].setVy(-playerPlane[1].getMovingSpeed());
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_Q){
                isTypeSlash[1] = false;
                missileTime[1] = 0;
            }
        }
    }

}
