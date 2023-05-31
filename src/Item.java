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
    private final int ItemType;
    private boolean isCollected = false;
    public static final int movingSpeed = 50;
    public Item(double x, double y, Image image, int ItemType) {
        super(x, y, 0, movingSpeed, 32, 32, image);
        this.ItemType = ItemType;
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
            double normalizedDirX = dirX / minDist;
            double normalizedDirY = dirY / minDist;

            //Calculate the angle between current velocity and desired velocity
            double angle = GameEngine.atan2(normalizedDirY * getVx() - normalizedDirX * getVy(),
                    normalizedDirX * getVx() + normalizedDirY * getVy());

            //Rotate the velocity vector towards the desired direction
            double rotateAngle = Math.min(dt * Math.PI, GameEngine.abs(angle));
            double newVx = getVx() * Math.cos(rotateAngle) + normalizedDirX * movingSpeed * Math.sin(rotateAngle);
            double newVy = getVy() * Math.cos(rotateAngle) + normalizedDirY * movingSpeed * Math.sin(rotateAngle);
            setVx(newVx);
            setVy(newVy);

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

    public int getItemType() {
        return ItemType;
    }
}
