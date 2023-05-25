import java.awt.*;

public class SimpleGame extends GameEngine{

    public void init() {
        setWindowSize(600, 450);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        drawText(10,20,"SimpleGame");
    }
}
