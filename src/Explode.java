import java.awt.*;

public class Explode extends GameObject {
    private static Image sheet;
    public static Image[] frames;
    private double explosionIndex = 0;

    public Explode(double x, double y, double scale) {
        super(x, y, 0, 0, 71 * scale, 100 * scale, frames[0]);
        setExplosionIndex();
    }

    public static void loadExplodeImage(){
        // Load Explode Animation in a 1D array
        sheet = GameEngine.loadImage("src/resources/Explosion.png");
        frames = new Image[16];
        // Load Images in a 1D array
        for(int iy = 0; iy < 4; iy++)
        {
            for(int ix = 0; ix < 4; ix++)
            {
                //Flatten out into 1D array index
                frames[iy * 4 + ix] = GameEngine.subImage(sheet, ix * 71, iy * 100, 71, 100);
            }
        }
    }

    public void updateExplosionIndex() {
        if(explosionIndex > 0) {
            explosionIndex -= 1;
        }
    }

    public void setExplosionIndex() {
        this.explosionIndex = 16;
    }

    public double getExplosionIndex() {
        return explosionIndex;
    }
}
