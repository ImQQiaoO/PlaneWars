import javax.sound.sampled.*;
import javax.swing.*;
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
    private final int bulletDamage_simple = 100;
    private final int bulletDamage_simple_side = 200;
    private final int bulletDamage_circle = 200;
    private int score;

    public static boolean isSinglePlayer; // make it static

    boolean isLeftKeyPressed = false;
    boolean isRightKeyPressed = false;
    boolean isUpKeyPressed = false;
    boolean isDownKeyPressed = false;
    boolean isAKeyPressed = false;
    boolean isDKeyPressed = false;
    boolean isWKeyPressed = false;
    boolean isSKeyPressed = false;
    boolean hasAddButtons = false;
    boolean isPaused = false;
    boolean isFail = false;
    boolean hasCalculateScore = false;

    private static Clip clip_background, clip_shoot, clip_explode, clip_missile, clip_laser;
    public static final PlayerPlane[] playerPlane = new PlayerPlane[2];

    private final boolean[] isNormal = new boolean[2];
    private final boolean[] isFire = new boolean[2];

    private final boolean[] isMissile = new boolean[2];

    private final boolean[] isLaser = new boolean[2];

    private final boolean[] isTypeSlash = new boolean[2];

//    private final boolean[] isCreateLaser = new boolean[2];

    private final int[] fireCount = new int[2];

    private final int[] missileCount = new int[2];

    private final int[] missileTime = new int[2];

    private final int[] laserCount = new int[2];
    private ArrayList<Enemy> enemyList;
    public static ArrayList<Enemy> missileEnemyList;
    private ArrayList<Bullet> friendlyBulletList;

    public static ArrayList<Bullet> enemyBulletList;
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
            laserCount[i] = 0;

        }
        startTime = System.currentTimeMillis();// Get the start time of the game
        enemyList = new ArrayList<>(); // Store all enemies in the game
        missileEnemyList = new ArrayList<>(); // Store all missile in the game
        friendlyBulletList = new ArrayList<>(); // Store all friendly bullets in the game
        specialEnemyList = new ArrayList<>();// Store all special enemies in the game
        specialEnemyWaitTime = new Random().nextDouble(24, 36) * 5; //2-3min
        specialEnemyWaitTime = 10; //TODO: Only for test

        itemList = new ArrayList<>(); // Store all items in the game
        enemyBulletList = new ArrayList<>(); // Store all enemy bullets in the game
        explodeList = new ArrayList<>(); // Store all explodes in the game
        randBoss = 0; // init randBoss

    }

    private void addPauseButtons() {
        if(!hasAddButtons){
            hasAddButtons = true;
            JButton continueButton = GameUtil.createNormalButton("Continue", 150, 360, 130, 50, Color.GREEN);
            JButton quitButton = GameUtil.createNormalButton("Quit", 320, 360, 130, 50, Color.RED);
            this.mPanel.add(continueButton);
            this.mPanel.add(quitButton);
            continueButton.addActionListener(e -> {
                hasAddButtons = false;
                isPaused = false;
                this.mPanel.requestFocus();
                this.mPanel.remove(continueButton);
                this.mPanel.remove(quitButton);
            });
            quitButton.addActionListener(e -> {
                clip_background.stop();
                this.mFrame.dispose();
                MenuPanel.replayBackgroundClip();
                MenuPanel.frame.setVisible(true);
            });
            this.mPanel.setLayout(null);
        }
    }

    private void addFailButtons() {
        if(!hasAddButtons){
            hasAddButtons = true;
//            JButton continueButton = GameUtil.createNormalButton("Continue", 150, 360, 130, 50, Color.GREEN);
            JButton quitButton = GameUtil.createNormalButton("Quit", 235, 400, 130, 50, Color.RED);
            this.mPanel.add(quitButton);
            quitButton.addActionListener(e -> {
                clip_background.stop();
                this.mFrame.dispose();
                MenuPanel.replayBackgroundClip();
                MenuPanel.frame.setVisible(true);
            });
            this.mPanel.setLayout(null);
        }
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
        if(isPaused || isFail){
            return;
        }
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            playerPlane[pi].updatePlane(dt);
        }

        //Update the location of the enemies
        for (Enemy enemy : enemyList) {
            enemy.updateEnemy(dt);
        }

        for(Enemy enemy : missileEnemyList){
            enemy.updateEnemy(dt, playerPlane);
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
            bullet.updateBullet(dt);
        }

        //Update the location of the enemy bullets
        for (Bullet bullet : enemyBulletList) {
            bullet.updateBullet(dt);
        }

        checkCollision();

        // Reset the generate time of special enemy
        if (isSpecialEnemy && specialEnemyList.size() == 0) {
            specialEnemyWaitTime = new Random().nextDouble(24, 36) * 5; //2-3min
            specialEnemyWaitTime = 10; //TODO: FOR TEST
            specialEnemySpendTime = 0;
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
            randBoss = new Random().nextInt(1, 4);
            if (randBoss == EnemyType.THREE_MEMBER_GROUP) {
                generateEnemies(EnemyType.THREE_MEMBER_GROUP); // Generate special enemies
            }
            else if (randBoss == EnemyType.IMPACT_BOSS) {
                generateEnemies(EnemyType.IMPACT_BOSS); // Generate special enemies
                EnemyType.moveFrameCount = 0;
            } else if (randBoss == EnemyType.MISSILE_ENEMY) {
                generateEnemies(EnemyType.MISSILE_ENEMY); // Generate special enemies
                EnemyType.moveFrameCount = 0;
            }
            isSpecialEnemy = true;
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
        checkCollisionEnemies(missileEnemyList);

        // Check collision between friendly bullets and enemies
        checkCollisionFriendlyBullets(friendlyBulletList);

        // Check collision between player plane and items
        checkCollisionItems(itemList);

        //Help Garbage Collection
        enemyList.removeIf(enemy -> (enemy.getY() > gameHeight + enemy.getHeight() / 2));

        Iterator<Enemy> enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.getEnemyHP() <= 0) {
                enemyIterator.remove();
                generateItems(20, enemy.getX(), enemy.getY());
            }
        }

        Iterator<Enemy> specialEnemyIterator = specialEnemyList.iterator();
        while (specialEnemyIterator.hasNext()) {
            Enemy enemy = specialEnemyIterator.next();
            if (enemy.getEnemyHP() <= 0 && specialEnemyList.size() <= 1) {
                specialEnemyIterator.remove();
                int numItem = new Random().nextInt(2, 4);
                generateItems(enemy.getX(), enemy.getY(), numItem);
            }
        }

        missileEnemyList.removeIf(enemy -> (enemy.getY() > gameHeight + enemy.getHeight() / 2) || enemy.getY() < -enemy.getHeight()
                || enemy.getX() > gameWidth + enemy.getWidth() / 2 || enemy.getX() < -enemy.getWidth() / 2 || enemy.getEnemyHP() <= 0);

        Iterator<Bullet> bulletIterator = friendlyBulletList.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if (bullet instanceof LaserBullet) {
                if(bullet.getBulletType()==BulletType.LASER_BULLET &&(((laserCount[0] == 0)||bullet.getRestInterval()==0)&&((LaserBullet) bullet).getLaserNumber()==1)){
                    bulletIterator.remove();
                }
                if(PlayerPlane.playerNumber == 2){
                if(bullet.getBulletType()==BulletType.LASER_BULLET &&(((laserCount[1] == 0)||bullet.getRestInterval()==0)&&((LaserBullet) bullet).getLaserNumber()==2)){
                    bulletIterator.remove();
                }}
            }
            if ((bullet.getY() < -bullet.getHeight() / 2 || bullet.getY() > gameHeight + bullet.getHeight() / 2)&&bullet.getBulletType()!=BulletType.LASER_BULLET) {
                bulletIterator.remove();
            }
        }

        specialEnemyList.removeIf(enemy -> (enemy.getY() > gameHeight + enemy.getHeight() / 2) || enemy.getEnemyHP() <= 0);
        enemyBulletList.removeIf(bullet -> (bullet.getY() < -bullet.getHeight() / 2) || (bullet.getY() > gameHeight + bullet.getHeight() / 2));

        itemList.removeIf(item -> (item.getY() > gameHeight + item.getHeight() / 2) || item.getY() < -item.getHeight() / 2
                || item.getX() > gameWidth + item.getWidth() / 2 || item.getX() < -item.getWidth() / 2 || item.isCollected());
        explodeList.removeIf(explode -> explode.getExplosionIndex() == 0);

        if(PlayerPlane.playerNumber == 1){
            if (playerPlane[0].getHp() <= 0){
                playerPlane[0].setPlaneHP(0);
                explodeList.add(new Explode(playerPlane[0].getX(), playerPlane[0].getY(), 2));
                isFail = true;}
        }else {
            for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
                if (playerPlane[pi].getHp() <= 0){
                    playerPlane[pi].setPlaneHP(0);
                    isFail = true;
                }
            }
        }

        if(isFail && !hasCalculateScore){
            recordScore();
        }
    }

    /**
     * Generate Item
     * @param probability probability of generating item
     */
    public void generateItems(int probability, double x, double y) {
        int randNum = rand(100);
        int randItemNum = rand(4);
        if (randNum <= probability) {
            itemList.add(new Item(x, y, Item.itemImages[randItemNum], randItemNum));
        }
    }
    public void generateItems(double x, double y, int num) {
        for (int i = 0; i < num; i++) {
            int randItemNum = rand(4);
            itemList.add(new Item(x + 35 * i, y, Item.itemImages[randItemNum], randItemNum));
        }
    }

    private void createMissile() {
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++){
            if (isTypeSlash[pi]){
                if(isMissile[pi]){
                    double bulletWidth = 38;
                    double bulletHeight = 26;
                    double bulletLeftX = playerPlane[pi].getX()-playerPlane[pi].getWidth()/4;
                    double bulletRightX = playerPlane[pi].getX()+playerPlane[pi].getWidth()/4;
                    double bulletY = playerPlane[pi].getY() - playerPlane[pi].getHeight()/4;
                    double bulletVx;
                    double bulletVy;
                    Image bulletImage = loadImage("src/resources/BulletMissile.png");
                    int bulletDamage = 150;
                    int bulletIntervalP1 = 6;
                    if(missileTime[pi] % bulletIntervalP1 == 0){
                        if (missileCount[pi] < 10){
                            initialize_MissileSound();
                            clip_missile.start();
                            if (missileCount[pi] % 2 == 0){
                                bulletVx = -500;
                                bulletVy = -800;
                            } else {
                                bulletVx = -300;
                                bulletVy = -900;
                            }
                            friendlyBulletList.add(new Bullet(bulletLeftX, bulletY, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP1));
                            friendlyBulletList.add(new Bullet(bulletRightX, bulletY, -bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.MISSILE_BULLET, bulletDamage, bulletIntervalP1));
                            initialize_ShootSound();
                            clip_shoot.start();
                            missileCount[pi]++;
                        }else {
                            missileTime[pi]=0;
                            missileCount[pi] = 0;
                            isMissile[pi] = false;
                        }
                    }
                }
                missileTime[pi] ++;
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
                    if (object instanceof Enemy enemy) {
                        if (playerPlane[pi].getProtectTime() <= 0) {
                            int playerDamage = 200;
                            enemy.setEnemyHP(enemy.getEnemyHP() - playerDamage);
                            checkEnemyHP(enemy);
                            initialize_ExplodeSound();
                            clip_explode.start();
                            if (((Enemy) object).getEnemyType() == EnemyType.NORMAL_ENEMY) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - 200);
                            } else if (((Enemy) object).getEnemyType() == EnemyType.THREE_MEMBER_GROUP) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - 400);
                            } else if (((Enemy) object).getEnemyType() == EnemyType.IMPACT_BOSS) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - 1000);
//                            } else if (((Enemy) object).getEnemyType() == EnemyType.MISSILE) {
//                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - 400);
                            } else if(((Enemy) object).getEnemyType() == EnemyType.MISSILE_ENEMY){
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - 600);
                            }
                            playerPlane[pi].decreaseHp();
                        }
                    } else if (object instanceof Bullet) {
                        if (playerPlane[pi].getProtectTime() <= 0) {
                            initialize_ExplodeSound();
                            clip_explode.start();
                            if (((Bullet) object).getBulletType() == BulletType.NORMAL_BULLET) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - bulletDamage_simple);
                            } else if (((Bullet) object).getBulletType() == BulletType.NORMAL_BULLET_SIDE) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - bulletDamage_simple_side);
                            } else if (((Bullet) object).getBulletType() == BulletType.CIRCLE_BULLET) {
                                playerPlane[pi].setPlaneHP(playerPlane[pi].getPlaneHP() - bulletDamage_circle);
                            }
                            playerPlane[pi].decreaseHp();
                        }
                    }
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
        currEnemyList.addAll(missileEnemyList);
        for (Enemy enemy : currEnemyList) {
            Iterator<Bullet> bulletIterator = friendlyBulletList.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (isCollision(enemy, bullet)) {
                    enemy.setEnemyHP(enemy.getEnemyHP() - bullet.getDamage());
                    checkEnemyHP(enemy);
                    if(bullet.getBulletType() != BulletType.LASER_BULLET)
                        bulletIterator.remove();
                }
            }
        }
    }

    public void checkEnemyHP(Enemy enemy){
        if(enemy.getEnemyHP() <= 0) {
            switch (enemy.getEnemyType()){
                case EnemyType.NORMAL_ENEMY -> {
                    score += 1;
                    explodeList.add(new Explode(enemy.getX(), enemy.getY(), 1));
                }
                case EnemyType.THREE_MEMBER_GROUP, EnemyType.IMPACT_BOSS, EnemyType.MISSILE_ENEMY -> {
                    score += 30;
                    explodeList.add(new Explode(enemy.getX(), enemy.getY(), 2.5));
                    initialize_ExplodeSound();
                    clip_explode.start();
                }
                case EnemyType.MISSILE -> {
                    score += 30;
                    explodeList.add(new Explode(enemy.getX(), enemy.getY(), 1.5));
                    initialize_ExplodeSound();
                    clip_explode.start();
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
                            playerPlane[pi].increaseHp();
                        }
                        case Item.ITEM_TYPE_FIRE -> {
                            isFire[pi] = true;
                            isNormal[pi] = false;
                            fireCount[pi] = 0;
                        }
                        case Item.ITEM_TYPE_LASER -> {
                            initialize_LaserSound();
                            clip_laser.start();
                            isLaser[pi] =true;
                            laserCount[pi] = 0;
                        }
                        case Item.ITEM_TYPE_MISSILE -> {
                            isMissile[pi] = true;
                            missileCount[pi] = 0;
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
                int enemyHp = 50;
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
                int enemyHp = 100;
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
                int enemyHp = 200;
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
            Image enemyImage = loadImage("src/resources/SpecialEnemy1.png");
            int enemyHp = 2000;
            EnemyType.fullHp = 2000 * 3;
            Enemy groupEliteEnemyLeft = new Enemy(enemyXL, enemyYSide, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Enemy groupEliteEnemyMiddle = new Enemy(enemyXM, enemyYMiddle, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Enemy groupEliteEnemyRight = new Enemy(enemyXR, enemyYSide, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp);
            Collections.addAll(specialEnemyList, groupEliteEnemyLeft, groupEliteEnemyMiddle, groupEliteEnemyRight);
        } else if (enemyType == EnemyType.IMPACT_BOSS) {
            // Create impact boss
            double enemyWidth = 171;
            double enemyHeight = 111;
            double enemyX = gameWidth / 2.0;
            double enemyY = -enemyHeight / 2;
            double enemyVx = 0;
            double enemyVy = 100; //200
            Image enemyImage = loadImage("src/resources/SpecialEnemy2.jpg");
            int enemyHp = 3000;
            EnemyType.fullHp = 3000;
            specialEnemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
        } else if (enemyType == EnemyType.MISSILE_ENEMY) {
            // Create impact boss
            double enemyWidth = 171;
            double enemyHeight = 111;
            double enemyX = gameWidth / 2.0;
            double enemyY = -enemyHeight / 2;
            double enemyVx = 0;
            double enemyVy = 100; //200
            Image enemyImage = loadImage("src/resources/SpecialEnemy3.jpg");
            int enemyHp = 4000;
            EnemyType.fullHp = 4000;
            specialEnemyList.add(new Enemy(enemyX, enemyY, enemyVx, enemyVy, enemyWidth, enemyHeight, enemyImage, enemyType, enemyHp));
        }

        if(enemyType == EnemyType.MISSILE){
            //test to generate a missile
            missileEnemyList.add(new Enemy(100, 100, 0, 100, 50, 50, loadImage("src/resources/BulletAutoMissile.png"), 3, 100));
        }
    }

    /**
     * Generate friendly bullets
     */
    public void generateFriendlyBullets() {
        // Player 1
        intervalCounter++;  //千万别动！！
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            if (isNormal[pi]) {
                double bulletWidth = 14;
                double bulletHeight = 29;
                double bulletX1 = playerPlane[pi].getX();
                double bulletY1 = playerPlane[pi].getY() - playerPlane[pi].getHeight() / 2;
                double bulletVx = 0;
                double bulletVy = -1000;
                Image bulletImage = loadImage("src/resources/Bullet01.png");
                int bulletDamage = 50;
                int bulletIntervalP1 = 10;
                if (intervalCounter % bulletIntervalP1 == 0) {
                    friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.NORMAL_BULLET, bulletDamage, bulletIntervalP1));
                    initialize_ShootSound();
                    clip_shoot.start();
                }
            }
            if (isFire[pi]) {
                double bulletWidth = 32;
                double bulletHeight = 64;
                double bulletX1 = playerPlane[pi].getX();
                double bulletY1 = playerPlane[pi].getY() - playerPlane[pi].getHeight() / 2;
                double bulletVx = 0;
                double bulletVy = -500;
                Image bulletImage = loadImage("src/resources/Bullet02.png");
                int bulletDamage = 100;
                int bulletIntervalP1 = 8;
                if (intervalCounter % bulletIntervalP1 == 0) {
                    if (fireCount[pi] < 30) {
                        friendlyBulletList.add(new Bullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.FIRE_BULLET, bulletDamage, bulletIntervalP1));
                        initialize_ShootSound();
                        clip_shoot.start();
                        fireCount[pi]++;
                    } else {
                        fireCount[pi] = 0;
                        isFire[pi] = false;
                        isNormal[pi] = true;
                    }
                }

            }
            if(isLaser[pi]){
                double bulletWidth = 40;
                double bulletHeight = 1000;
                double bulletX1 = playerPlane[pi].getX();
                double bulletY1 = playerPlane[pi].getY() - playerPlane[pi].getHeight()*7.7;
                double bulletVx = playerPlane[pi].getVx();
                double bulletVy = playerPlane[pi].getVy();
                Image bulletImage = loadImage("src/resources/BulletLaser.png");
                int bulletDamage = 10;
                if (laserCount[pi] < 200) {
                    laserCount[pi]++;
                }else{
                    laserCount[pi] = 0;
                    isLaser[pi] = false;
                }
                friendlyBulletList.add(new LaserBullet(bulletX1, bulletY1, bulletVx, bulletVy, bulletWidth, bulletHeight, bulletImage, BulletType.LASER_BULLET, bulletDamage, 1,pi+1));
                for(Bullet bullet : friendlyBulletList){
                    if(bullet instanceof LaserBullet laserBullet){
                        if(bullet.getBulletType() == BulletType.LASER_BULLET && laserBullet.getLaserNumber()==pi+1){
                            bullet.setRestInterval(bullet.getRestInterval()-1);
                        }
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

                int bulletInterval_simple = 30;

                //Randomly fire the center and side rounds
                if(intervalCounter % bulletInterval_simple == 0){
                    int x = rand(10);
                    if(x < 3)
                    enemyBulletList.add(new Bullet(bulletX_simple_left, bulletY_simple_left, bulletVx_simpe, bulletVy_simple_side, bulletWidth_simple, bulletHeight_simple, bulletImage_simple_side, BulletType.NORMAL_BULLET_SIDE, bulletDamage_simple_side, bulletInterval_simple));
                    else if(x < 6)
                    enemyBulletList.add(new Bullet(bulletX_simple_right, bulletY_simple_right, bulletVx_simpe, bulletVy_simple_side, bulletWidth_simple, bulletHeight_simple, bulletImage_simple_side, BulletType.NORMAL_BULLET_SIDE, bulletDamage_simple_side, bulletInterval_simple));
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
                Image bulletImage_circle = loadImage("src/resources/Bullet03.png");
                int bulletInterval_circle = 100;
                if(intervalCounter % bulletInterval_circle == 0) {
                    //修改发射散弹函数
                    int x = rand(8);
                    EnemyType.BossBullet(x,bulletX_circle, bulletY_circle, bulletWidth_circle, bulletHeight_circle, bulletImage_circle, bulletDamage_circle, bulletInterval_circle);
                }
            }
        }
    }

    private void recordScore() {
        hasCalculateScore = true;
        String gameMode;
        if(isSinglePlayer){
            gameMode = "SinglePlayer";
        } else{
            gameMode = "DoublePlayer";
        }
        ScorePanel.checkHighestScore(gameMode, score);
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

    //Add explode sound
    private static void initialize_ExplodeSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/explode.wav"));
            clip_explode = AudioSystem.getClip();
            clip_explode.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    //Add missile sound
    private static void initialize_MissileSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/missile.wav"));
            clip_missile = AudioSystem.getClip();
            clip_missile.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    //Add laser sound
    private static void initialize_LaserSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/laser.wav"));
            clip_laser = AudioSystem.getClip();
            clip_laser.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent() {
        // Clear the background to black
        Image BackgroundImage = loadImage("src/resources/background.png");
        clearBackground(gameWidth, gameHeight);
        drawImage(BackgroundImage,0,0,600,700);
        // Draw the enemies
        for (Enemy enemy : enemyList) {
            drawImage(enemy.getImage(), enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }

        //Draw three special enemy Group
        for (Enemy enemy : specialEnemyList) {
            drawImage(enemy.getImage(), enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
        }


        //Draw the missile
        for (Enemy enemy : missileEnemyList) {
            // Rotate the image based on enemyAngle
            saveCurrentTransform();
            translate(enemy.getX(), enemy.getY());
            rotate(-enemy.getEnemyAngle());
            changeColor(pink);
//            drawRectangle(- enemy.getWidth() / 2, - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
            drawImage(enemy.getImage(), - enemy.getWidth() / 2, - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
            restoreLastTransform();
        }

        // Draw the bullets
        for (Bullet bullet : friendlyBulletList) {
            switch (bullet.getBulletType()){
                case BulletType.MISSILE_BULLET -> {
                    saveCurrentTransform();
                    translate(bullet.getX(), bullet.getY());
                    rotate(-bullet.getBulletAngle());
                    drawImage(bullet.getImage(), - bullet.getWidth() / 2, - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
                    restoreLastTransform();
                }
                case BulletType.LASER_BULLET -> {
                    LaserBullet laserBullet = (LaserBullet) bullet;
                    int laserNumber = laserBullet.getLaserNumber();
                    int alpha;
                    int count = laserCount[laserNumber-1];
                    if(count > 300-25){
                        alpha = 255-(count - (300-25)) * 10;
                    } else {
                        alpha = 255;
                    }
                    drawImage(bullet.getImage(), bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight(), alpha);
                }
                default -> drawImage(bullet.getImage(), bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
            }
        }

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
            if (playerPlane[pi].getVy() < 0 && !isPaused) {
                tailFireHeight = tailFireHeight * 2;    // If the plane is moving up, the tail fire is longer
            } else if (playerPlane[pi].getVy() > 0 && !isPaused) {
                tailFireHeight = tailFireHeight / 2;    // If the plane is moving down, the tail fire is shorter
            }
            drawImage(PlayerPlane.normalTailFireImage, playerPlane[pi].getX() - tailFireWidth / 2.0, playerPlane[pi].getY() + playerPlaneHeight / 2, tailFireWidth, tailFireHeight, alpha);
            drawImage(PlayerPlane.normalTailFireImage, playerPlane[pi].getX()-tailFireWidth/2.0, playerPlane[pi].getY()+playerPlaneHeight/2, tailFireWidth, tailFireHeight, alpha);
            // Draw the plane explosion
            double explosionIndex = playerPlane[pi].getExplosionIndex();
            if(explosionIndex > 0){
                int explosionFrameIndex = floor(16 - explosionIndex);
                drawImage(Explode.frames[explosionFrameIndex], playerPlane[pi].getX()-playerPlaneWidth/2, playerPlane[pi].getY()-playerPlaneHeight/2, playerPlaneWidth, playerPlaneHeight);
            }
        }

        // Draw the enemy bullets
        for (Bullet bullet : enemyBulletList) {
            drawImage(bullet.getImage(), bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
        }

        // Draw the explosion
        for (Explode explode : explodeList) {
            double explodeWidth = explode.getWidth();
            double explodeHeight = explode.getHeight();
            int explodeFrameIndex = floor(16 - explode.getExplosionIndex());
            drawImage(Explode.frames[explodeFrameIndex], explode.getX() - explodeWidth / 2, explode.getY() - explodeHeight / 2, explodeWidth, explodeHeight);
        }

        // Draw the item
        for (Item item : itemList) {
            drawImage(item.getImage(), item.getX() - item.getWidth() / 2, item.getY() - item.getHeight() / 2, item.getWidth(), item.getHeight());
        }

//        changeColor(green);
//        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
//            drawRectangle(playerPlane[pi].getX() - playerPlane[pi].getWidth() / 2, playerPlane[pi].getY() - playerPlane[pi].getHeight() / 2, playerPlane[pi].getWidth(), playerPlane[pi].getHeight());
//        }
//
//        changeColor(red);
//        for (Enemy enemy : enemyList) {
//            drawRectangle(enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
//        }
//        for (Enemy enemy : missileEnemyList) {
//            drawRectangle(enemy.getX() - enemy.getWidth() / 2, enemy.getY() - enemy.getHeight() / 2, enemy.getWidth(), enemy.getHeight());
//        }
//
//        changeColor(blue);
//        for (Bullet bullet : friendlyBulletList) {
//            drawRectangle(bullet.getX() - bullet.getWidth() / 2, bullet.getY() - bullet.getHeight() / 2, bullet.getWidth(), bullet.getHeight());
//        }
//
        changeColor(white);
        for (int pi = 0; pi < PlayerPlane.playerNumber; pi++) {
            drawText(20 + 300 * pi, 50, "Player " + (pi + 1) + " HP: " + playerPlane[pi].getHp(), "Arial", 30);
        }
        if(isFail){
            //Game over
            changeColor(new Color(255, 0, 0, 50));
            drawSolidRectangle(gameWidth/6.0, gameHeight/3.0, gameWidth-gameWidth/3.0, gameHeight-gameHeight/1.5);
            addFailButtons();
            changeColor(Color.red);
            drawRectangle(gameWidth/6.0, gameHeight/3.0, gameWidth-gameWidth/3.0, gameHeight-gameHeight/1.5);
            drawText(200, 300, "Game over", "Arial", 40);
            drawText(150, 360, "You final score is: " + score, "Arial", 30);
        } else {
            if (isPaused) {
                //Game Pause
                changeColor(new Color(255, 255, 255, 50));
                drawSolidRectangle(gameWidth/6.0, gameHeight/3.0, gameWidth-gameWidth/3.0, gameHeight-gameHeight/1.5);
                addPauseButtons();
                changeColor(Color.white);
                drawRectangle(gameWidth/6.0, gameHeight/3.0, gameWidth-gameWidth/3.0, gameHeight-gameHeight/1.5);
                drawText(160, 320, "Game Paused", "Arial", 40);
            }
        }
        if (EnemyType.restHP != 0) {
            changeColor(new Color(152, 140, 84));
            drawSolidRectangle(25,55,550,20);
            changeColor(new Color(243, 0, 0));
            drawSolidRectangle(25,55,EnemyType.restHP*550 / EnemyType.fullHp,20);
        }
    }

    // Called whenever a key is pressed
    @SuppressWarnings("Duplicates")
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            // Pause the game
            if(!isPaused){
                isPaused = true;
            }
        }
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
            if (e.getKeyCode() == KeyEvent.VK_R) {
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
            if (e.getKeyCode() == KeyEvent.VK_R){
                isTypeSlash[1] = false;
                missileTime[1] = 0;
            }
        }
    }

}
