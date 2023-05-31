import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

//-------------------------------------------------------
// MenuPanel is used to display the main menu interface.
//-------------------------------------------------------
public class MenuPanel extends JPanel {
    public static JFrame frame = new JFrame();
    private static Clip clip, clip2;
    public MenuPanel() {
        setupWindow(Launcher.WindowWidth,Launcher.WindowHeight);
        setupButtons();
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("src/resources/background.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    //Create the buttons in Menu
    private void setupButtons(){
        JButton singlePlayerButton = GameUtil.createMenuButton("Single Player",GameUtil.centerComponentsX(500,0,Launcher.WindowWidth),300,500,100,Color.green);
        singlePlayerButton.addActionListener(e -> {
            initialize_ChooseSound();
            clip2.start();
            clip.stop();
            GameEngine.createGame(new SimpleGame(true), 60);
            frame.setVisible(false);
        });
        this.add(singlePlayerButton);

        JButton doublePlayerButton = GameUtil.createMenuButton("Double Player",GameUtil.centerComponentsX(500,0,Launcher.WindowWidth),430,500,100,Color.blue);
        doublePlayerButton.addActionListener(e -> {
            initialize_ChooseSound();
            clip2.start();
            clip.stop();
            GameEngine.createGame(new SimpleGame(false), 60);
            frame.setVisible(false);
        });
        this.add(doublePlayerButton);

        JButton statsButton = GameUtil.createMenuButton("Stats",100,560,180,100,Color.orange);
        statsButton.addActionListener(e -> {
            //TODO: Add stats page
            initialize_ChooseSound();
            clip2.start();
            frame.setVisible(false);
        });
        this.add(statsButton);

        JButton helpButton = GameUtil.createMenuButton("Help",310,560,180,100,Color.cyan);
        helpButton.addActionListener(e -> {
            //TODO: Add help page
            initialize_ChooseSound();
            clip2.start();
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
        this.setBackground(new Color(21, 21, 21));
        g.drawImage(Launcher.logoIcon.getImage(), 150, 20, 500, 250, null);
    }

    private static void initialize_ChooseSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/choose.wav"));
            clip2 = AudioSystem.getClip();
            clip2.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
