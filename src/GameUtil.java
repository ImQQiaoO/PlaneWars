//-------------------------------------------------------
// Game Util Overwrite the Style of Some Components in Java Swing
//-------------------------------------------------------

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameUtil {

    //Function to create the window and display it
    public static void setupWindows(JFrame frame, JPanel jPanel, String title) {
        frame.setLayout(new BorderLayout());
        frame.setSize(Launcher.WindowWidth, Launcher.WindowHeight);
        frame.setLocation(Launcher.WindowX,Launcher.WindowY);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(jPanel);
        frame.setVisible(true);
        frame.setResizable(false);

        //Resize the window (insets are just the boards that the Operating System puts on the board)
        Insets insets = frame.getInsets();
        frame.setSize(Launcher.WindowWidth + insets.left + insets.right,
                Launcher.WindowHeight + insets.top + insets.bottom);
    }

    public static JButton createMenuButton(String text, int x, int y, int width, int height, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x,y,width,height);
        button.setFont(new Font("Arial",Font.BOLD,50));
        button.setForeground(Color.white);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    public static JButton createNormalButton(String text, int x, int y, int width, int height, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x,y,width,height);
        button.setFont(new Font("Arial",Font.BOLD,20));
        button.setForeground(Color.white);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    //-------------------------------------------------------
    // Functions to center text and components
    //-------------------------------------------------------
    // Return the x position of the text to be centered according to the width and start position
    public static int centerTextX(Graphics g, String text, int startX, int width) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        return startX + (width - textWidth) / 2;
    }

    // Return the x position of the component to be centered according to the width and start position
    public static int centerComponentsX(int componentsWidth, int startX, int width) {
        return startX + (width - componentsWidth) / 2;
    }

    public static int centerTextY(Graphics g, int panelHeight) {
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getHeight();
        return (panelHeight - textHeight) / 2 + fm.getAscent();
    }

    public static Long getTimeStamp() {
        Date date = new Date();
        return date.getTime();
    }

    public static String getDate(Long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
