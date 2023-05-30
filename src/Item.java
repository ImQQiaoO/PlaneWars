import java.awt.*;

public class Item extends GameObject{
    public static final int ITEM_TYPE_LIFE = 0;
    public static final int ITEM_TYPE_FIRE = 1;
    public static final int ITEM_TYPE_LASER = 2;
    public static final int ITEM_TYPE_MISSILE = 3;
    public static Image lifeItemImage = GameEngine.loadImage("src/resources/ItemLife.png");
    public static Image fireItemImage = GameEngine.loadImage("src/resources/ItemFire.png");
    public static Image laserItemImage = GameEngine.loadImage("src/resources/ItemLaser.png");
    public static Image missileItemImage = GameEngine.loadImage("src/resources/ItemMissile.png");
    public static Image[] itemImages = {lifeItemImage, fireItemImage, laserItemImage, missileItemImage};
    private final int ItemID;
    private boolean isCollected = false;
    public static final int movingSpeed = 50;
    public Item(double x, double y, Image image, int ItemID) {
        super(x, y, 0, movingSpeed, 32, 32, image);
        this.ItemID = ItemID;
    }

    public void updateLocation(double dt, PlayerPlane[] playerPlane) {
        // Recalculate position based on movement speed

        //Distance between item & player
        double[] dist = new double[2];
        double minDist = 1000000000;
        int minIndex = 0;
        for(int i = 0; i < PlayerPlane.playerNumber; i++) {
            dist[i] = GameEngine.distance(this.getX(), this.getY(), playerPlane[i].getX(), playerPlane[i].getY());
            if(dist[i] < minDist) {
                // Find the player that is closest to the item
                minDist = dist[i];
                minIndex = i;
            }
        }

        // If the distance between the item and the player are less than 200,
        // the item will automatically move towards the player
        if(minDist < 200) {
            //Direction from item-->player
            //playerPos - itemPos  item-->player
            double dirX = playerPlane[minIndex].getX() - this.getX();
            double dirY = playerPlane[minIndex].getY() - this.getY();

            //Normalized vector of direction
            setVx((dirX / minDist) * movingSpeed);
            setVy((dirY / minDist) * movingSpeed);

            //Change the velocity according to the distance between item and player
            //The closer the object is to the player, the faster the speed
            setVx(getVx() * (200 / minDist));
            setVy(getVy() * (200 / minDist));
        }
        //Assign the direction for item
        setX(getX() + getVx() * dt);
        setY(getY() + getVy() * dt);
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}
