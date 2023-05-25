//-------------------------------------------------------
// Game Util Overwrite the Style of Some Components in Java Swing
//-------------------------------------------------------

import javax.swing.*;
import java.awt.*;

public class GameUtil {

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
}
