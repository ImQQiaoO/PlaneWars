import javax.swing.*;
import java.awt.*;

//-------------------------------------------------------
// MenuPanel is used to display the main menu interface.
//-------------------------------------------------------
public class MenuPanel extends JPanel {
    public static JFrame frame = new JFrame();
    public MenuPanel() {
        setupWindow(Launcher.WindowWidth,Launcher.WindowHeight);
        setupButtons();
    }

    //Create the buttons in Menu
    private void setupButtons(){
        JButton singlePlayerButton = GameUtil.createMenuButton("Single Player",GameUtil.centerComponentsX(500,0,Launcher.WindowWidth),300,500,100,Color.green);
        singlePlayerButton.addActionListener(e -> {
            GameEngine.createGame(new SimpleGame());
            frame.setVisible(false);
        });
        this.add(singlePlayerButton);

        JButton doublePlayerButton = GameUtil.createMenuButton("Double Player",GameUtil.centerComponentsX(500,0,Launcher.WindowWidth),430,500,100,Color.blue);
        doublePlayerButton.addActionListener(e -> {
            GameEngine.createGame(new SimpleGame());
            frame.setVisible(false);
        });
        this.add(doublePlayerButton);

        JButton statsButton = GameUtil.createMenuButton("Stats",100,560,180,100,Color.orange);
        statsButton.addActionListener(e -> {
            //TODO: Add stats page
            frame.setVisible(false);
        });
        this.add(statsButton);

        JButton helpButton = GameUtil.createMenuButton("Help",310,560,180,100,Color.cyan);
        helpButton.addActionListener(e -> {
            //TODO: Add help page
            frame.setVisible(false);
        });
        this.add(helpButton);

        JButton exitButton = GameUtil.createMenuButton("Exit",520,560,180,100,Color.red);
        exitButton.addActionListener(e -> System.exit(0));
        this.add(exitButton);

        setLayout(null);
    }

    //Function to create the window and display it
    public void setupWindow(int width, int height) {
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setLocation(Launcher.WindowX,Launcher.WindowY);
        frame.setTitle("Plane Wars");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false);

        //Resize the window (insets are just the boards that the Operating System puts on the board)
        Insets insets = frame.getInsets();
        frame.setSize(width + insets.left + insets.right,
                height + insets.top + insets.bottom);
    }

    //This gets called any time the Operating System
    //tells the program to paint itself
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.BLACK);
        g.drawImage(Launcher.logoIcon.getImage(), 150, 20, 500, 250, null);
    }
}
