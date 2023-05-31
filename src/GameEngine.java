import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import java.util.Stack;
import java.util.Random;

import javax.imageio.*;
import javax.sound.sampled.*;

@SuppressWarnings("unused")
public abstract class GameEngine implements KeyListener, MouseListener, MouseMotionListener {
    //-------------------------------------------------------
    // Game Engine Frame and Panel
    //-------------------------------------------------------
    JFrame mFrame;
    GamePanel mPanel;
    int mWidth, mHeight;
    Graphics2D mGraphics;
    boolean initialised = false;

    //-------------------------------------------------------
    // Time-Related functions
    //-------------------------------------------------------

    // Returns the time in milliseconds
    public long getTime() {
        // Get the current time from the system
        return System.currentTimeMillis();
    }

    // Waits for ms milliseconds
    public void sleep(double ms) {
        try {
            // Sleep
            Thread.sleep((long)ms);
        } catch(Exception e) {
            // Do Nothing
        }
    }

    //-------------------------------------------------------
    // Functions to control the framerate
    //-------------------------------------------------------
    // Two variables to keep track of how much time has passed between frames
    long time = 0, oldTime = 0;

    // Returns the time passed since this function was last called.
    public long measureTime() {
        time = getTime();
        if(oldTime == 0) {
            oldTime = time;
        }
        long passed = time - oldTime;
        oldTime = time;
        return passed;
    }

    //-------------------------------------------------------
    // Functions for setting up the window
    //-------------------------------------------------------
    // Function to create the window and display it
    public void setupWindow(int width, int height) {
        mFrame = new JFrame();
        mPanel = new GamePanel();

        mWidth = width;
        mHeight = height;

        mFrame.setSize(width, height);
        mFrame.setLocation(200,50);
        mFrame.setTitle("Window");
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.add(mPanel);
        mFrame.setVisible(true);

        mPanel.setDoubleBuffered(true);
        mPanel.addMouseListener(this);
        mPanel.addMouseMotionListener(this);

        // Register a key event dispatcher to get a turn in handling all
        // key events, independent of which component currently has the focus
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        switch (e.getID()) {
                            case KeyEvent.KEY_PRESSED:
                                GameEngine.this.keyPressed(e);
                                return false;
                            case KeyEvent.KEY_RELEASED:
                                GameEngine.this.keyReleased(e);
                                return false;
                            case KeyEvent.KEY_TYPED:
                                GameEngine.this.keyTyped(e);
                                return false;
                            default:
                                return false; // do not consume the event
                        }
                    }
                });

        // Resize the window (insets are just the boarders that the Operating System puts on the board)
        Insets insets = mFrame.getInsets();
        mFrame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    public void setWindowSize(final int width, final int height) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Resize the window (insets are just the boarders that the Operating System puts on the board)
                Insets insets = mFrame.getInsets();
                mWidth = width;
                mHeight = height;
                mFrame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
                mPanel.setSize(width, height);
            }
        });
    }

    // Return the width of the window
    public int width() {
        return mWidth;
    }

    // Return the height of the window
    public int height() {
        return mHeight;
    }

    //-------------------------------------------------------
    // Main Game function
    //-------------------------------------------------------

    // GameEngine Constructor
    public GameEngine() {
        // Create graphics transform stack
        mTransforms = new Stack<AffineTransform>();

        // Create window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the window
                setupWindow(500,500);
            }
        });
    }

    // Create Game Function
    public static void createGame(GameEngine game, int framerate) {
        // Initialise Game
        game.init();

        // Start the Game
        game.gameLoop(framerate);
    }

    public static void createGame(GameEngine game) {
        // Call CreateGame
        createGame(game, 30);
    }

    // Game Timer
    protected class GameTimer extends Timer {
        private static final long serialVersionUID = 1L;
        private int framerate;

        protected GameTimer(int framerate, ActionListener listener) {
            super(1000/framerate, listener);
            this.framerate = framerate;
        }

        protected void setFramerate(int framerate) {
            if (framerate < 1) framerate = 1;
            this.framerate = framerate;

            int delay = 1000 / framerate;
            setInitialDelay(0);
            setDelay(delay);
        }

        protected int getFramerate() {
            return framerate;
        }
    }

    // Main Loop of the game. Runs continuously
    // and calls all the updates of the game and
    // tells the game to display a new frame.
    GameTimer timer = new GameTimer(30, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Determine the time step
            double passedTime = measureTime();
            double dt = passedTime / 1000.;

            // Update the Game
            update(dt);

            // Tell the Game to draw
            mPanel.repaint();
        }
    });

    // The GameEngine main Panel
    protected class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        // This gets called any time the Operating System
        // tells the program to paint itself
        public void paintComponent(Graphics graphics) {
            // Get the graphics object
            mGraphics = (Graphics2D)graphics;

            // Reset all transforms
            mTransforms.clear();
            mTransforms.push(mGraphics.getTransform());

            // Rendering settings
            mGraphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

            // Paint the game
            if (initialised) {
                GameEngine.this.paintComponent();
            }
        }
    }

    // Initialises and starts the game loop with the given framerate.
    public void gameLoop(int framerate) {
        initialised = true; // assume init has been called or won't be called

        timer.setFramerate(framerate);
        timer.setRepeats(true);

        // Main loop runs until program is closed
        timer.start();
    }

    //-------------------------------------------------------
    // Initialise function
    //-------------------------------------------------------
    public void init() {}

    //-------------------------------------------------------
    // Update function
    //-------------------------------------------------------
    public abstract void update(double dt);

    //-------------------------------------------------------
    // Paint function
    //-------------------------------------------------------
    public abstract void paintComponent();

    //-------------------------------------------------------
    // Keyboard functions
    //-------------------------------------------------------

    // Called whenever a key is pressed
    public void keyPressed(KeyEvent event) {}

    // Called whenever a key is released
    public void keyReleased(KeyEvent event) {}

    // Called whenever a key is pressed and immediately released
    public void keyTyped(KeyEvent event) {}

    //-------------------------------------------------------
    // Mouse functions
    //-------------------------------------------------------

    // Called whenever a mouse button is clicked
    // (pressed and released in the same position)
    public void mouseClicked(MouseEvent event) {}

    // Called whenever a mouse button is pressed
    public void mousePressed(MouseEvent event) {}

    // Called whenever a mouse button is released
    public void mouseReleased(MouseEvent event) {}

    // Called whenever the mouse cursor enters the game panel
    public void mouseEntered(MouseEvent event) {}

    // Called whenever the mouse cursor leaves the game panel
    public void mouseExited(MouseEvent event) {}

    // Called whenever the mouse is moved
    public void mouseMoved(MouseEvent event) {}

    // Called whenever the mouse is moved with the mouse button held down
    public void mouseDragged(MouseEvent event) {}

    //-------------------------------------------------------
    // Graphics Functions
    //-------------------------------------------------------

    // My Definition of some colors
    Color black = Color.BLACK;
    Color orange = Color.ORANGE;
    Color pink = Color.PINK;
    Color red = Color.RED;
    Color purple = new Color(128, 0, 128);
    Color blue = Color.BLUE;
    Color green = Color.GREEN;
    Color yellow = Color.YELLOW;
    Color white = Color.WHITE;

    // Changes the background Color to the color c
    public void changeBackgroundColor(Color c) {
        // Set background colour
        mGraphics.setBackground(c);
    }

    // Changes the background Color to the color (red,green,blue)
    public void changeBackgroundColor(int red, int green, int blue) {
        // Clamp values
        if(red < 0)   {red = 0;}
        if(red > 255) {red = 255;}

        if(green < 0)   {green = 0;}
        if(green > 255) {green = 255;}

        if(blue < 0)   {blue = 0;}
        if(blue > 255) {blue = 255;}

        // Set background colour
        mGraphics.setBackground(new Color(red,green,blue));
    }

    // Clears the background, makes the whole window whatever the background color is
    public void clearBackground(int width, int height) {
        // Clear background
        mGraphics.clearRect(0, 0, width, height);
    }

    // Changes the drawing Color to the color c
    public void changeColor(Color c) {
        // Set colour
        mGraphics.setColor(c);
    }

    // Changes the drawing Color to the color (red,green,blue)
    public void changeColor(int red, int green, int blue) {
        // Clamp values
        if(red < 0)   {red = 0;}
        if(red > 255) {red = 255;}

        if(green < 0)   {green = 0;}
        if(green > 255) {green = 255;}

        if(blue < 0)   {blue = 0;}
        if(blue > 255) {blue = 255;}

        // Set colour
        mGraphics.setColor(new Color(red,green,blue));
    }

    // Draws a line from (x1,y2) to (x2,y2)
    void drawLine(double x1, double y1, double x2, double y2) {
        // Draw a Line
        mGraphics.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    // Draws a line from (x1,y2) to (x2,y2) with width l
    void drawLine(double x1, double y1, double x2, double y2, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Line
        mGraphics.draw(new Line2D.Double(x1, y1, x2, y2));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function draws a rectangle at (x,y) with width and height (w,h)
    void drawRectangle(double x, double y, double w, double h) {
        // Draw a Rectangle
        mGraphics.draw(new Rectangle2D.Double(x, y, w, h));
    }

    // This function draws a rectangle at (x,y) with width and height (w,h)
    // with a line of width l
    void drawRectangle(double x, double y, double w, double h, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Rectangle
        mGraphics.draw(new Rectangle2D.Double(x, y, w, h));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function fills in a rectangle at (x,y) with width and height (w,h)
    void drawSolidRectangle(double x, double y, double w, double h) {
        // Fill a Rectangle
        mGraphics.fill(new Rectangle2D.Double(x, y, w, h));
    }

    // This function draws a circle at (x,y) with radius
    void drawCircle(double x, double y, double radius) {
        // Draw a Circle
        mGraphics.draw(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
    }

    // This function draws a circle at (x,y) with radius
    // with a line of width l
    void drawCircle(double x, double y, double radius, double l) {
        // Set the stroke
        mGraphics.setStroke(new BasicStroke((float)l));

        // Draw a Circle
        mGraphics.draw(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));

        // Reset the stroke
        mGraphics.setStroke(new BasicStroke(1.0f));
    }

    // This function draws a circle at (x,y) with radius
    void drawSolidCircle(double x, double y, double radius) {
        // Fill a Circle
        mGraphics.fill(new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2));
    }

    // This function draws text on the screen at (x,y)
    public void drawText(double x, double y, String s) {
        // Draw text on the screen
        mGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // This function draws bold text on the screen at (x,y)
    public void drawBoldText(double x, double y, String s) {
        // Draw text on the screen
        mGraphics.setFont(new Font("Arial", Font.BOLD, 40));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // This function draws text on the screen at (x,y)
    // with Font (font,size)
    public void drawText(double x, double y, String s, String font, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font(font, Font.PLAIN, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    // This function draws bold text on the screen at (x,y)
    // with Font (font,size)
    public void drawBoldText(double x, double y, String s, String font, int size) {
        // Draw text on the screen
        mGraphics.setFont(new Font(font, Font.BOLD, size));
        mGraphics.drawString(s, (int)x, (int)y);
    }

    //-------------------------------------------------------
    // Image Functions
    //-------------------------------------------------------

    // Loads an image from file
    public static Image loadImage(String filename) {
        try {
            // Load Image
            Image image = ImageIO.read(new File(filename));

            // Return Image
            return image;
        } catch (IOException e) {
            // Show Error Message
            System.out.println("Error: could not load image " + filename);
            System.exit(1);
        }

        // Return null
        return null;
    }

    // Loads a sub-image out of an image
    public Image subImage(Image source, int x, int y, int w, int h) {
        // Check if image is null
        if(source == null) {
            // Print Error message
            System.out.println("Error: cannot extract a subImage from a null image.\n");

            // Return null
            return null;
        }

        // Convert to a buffered image
        BufferedImage buffered = (BufferedImage)source;

        // Extract sub image
        Image image = buffered.getSubimage(x, y, w, h);

        // Return image
        return image;
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot draw null image.\n");
            return;
        }

        // Draw image on screen at (x,y)
        mGraphics.drawImage(image, (int)x, (int)y, null);
    }

    // Draws an image on the screen at position (x,y)
    public void drawImage(Image image, double x, double y, double w, double h) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot draw null image.\n");
            return;
        }
        // Draw image on screen at (x,y) with size (w,h)
        mGraphics.drawImage(image, (int)x, (int)y, (int)w, (int)h, null);
    }

    // Mirrors an image horizontally
    public Image mirrorImage(Image image) {
        // Check if image is null
        if(image == null) {
            // Print Error message
            System.out.println("Error: cannot mirror null image.\n");
            return null;
        }

        // Convert to a buffered image
        BufferedImage buffered = (BufferedImage)image;

        // Create a new buffered image
        BufferedImage mirror = new BufferedImage(buffered.getWidth(), buffered.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Create a graphics context
        Graphics2D g = mirror.createGraphics();

        // Draw the image mirrored
        g.drawImage(buffered, buffered.getWidth(), 0, 0, buffered.getHeight(), 0, 0, buffered.getWidth(), buffered.getHeight(), null);

        // Return the mirrored image
        return mirror;
    }

    //-------------------------------------------------------
    // Transform Functions
    //-------------------------------------------------------

    // Stack of transforms
    Stack<AffineTransform> mTransforms;

    // Save the current transform
    public void saveCurrentTransform() {
        // Push transform onto the stack
        mTransforms.push(mGraphics.getTransform());
    }

    // Restores the last transform
    public void restoreLastTransform() {
        // Set current transform to the top of the stack.
        mGraphics.setTransform(mTransforms.peek());

        // If there is more than one transform on the stack
        if(mTransforms.size() > 1) {
            // Pop a transform off the stack
            mTransforms.pop();
        }
    }

    // This function translates the drawing context by (x,y)
    void translate(double x, double y) {
        // Translate the drawing context
        mGraphics.translate(x,y);
    }

    // This function rotates the drawing context by a degrees
    void rotate(double a) {
        // Rotate the drawing context
        mGraphics.rotate(Math.toRadians(a));
    }

    // This function scales the drawing context by (x,y)
    void scale(double x, double y) {
        // Scale the drawing context
        mGraphics.scale(x, y);
    }

    // This function shears the drawing context by (x,y)
    void shear(double x, double y) {
        // Shear the drawing context
        mGraphics.shear(x, y);
    }

    //-------------------------------------------------------
    // Sound Functions
    //-------------------------------------------------------

    // Class used to store an audio clip
    public class AudioClip {
        // Format
        AudioFormat mFormat;

        // Audio Data
        byte[] mData;

        // Buffer Length
        long mLength;

        // Loop Clip
        Clip mLoopClip;

        public Clip getLoopClip() {
            // return mLoopClip
            return mLoopClip;
        }

        public void setLoopClip(Clip clip) {
            // Set mLoopClip to clip
            mLoopClip = clip;
        }

        public AudioFormat getAudioFormat() {
            // Return mFormat
            return mFormat;
        }

        public byte[] getData() {
            // Return mData
            return mData;
        }

        public long getBufferSize() {
            // Return mLength
            return mLength;
        }

        public AudioClip(AudioInputStream stream) {
            // Get Format
            mFormat = stream.getFormat();

            // Get length (in Frames)
            mLength = stream.getFrameLength() * mFormat.getFrameSize();

            // Allocate Buffer Data
            mData = new byte[(int)mLength];

            try {
                // Read data
                stream.read(mData);
            } catch(Exception exception) {
                // Print Error
                System.out.println("Error reading Audio File\n");

                // Exit
                System.exit(1);
            }

            // Set LoopClip to null
            mLoopClip = null;
        }
    }

    // Loads the AudioClip stored in the file specified by filename
    public AudioClip loadAudio(String filename) {
        try {
            // Open File
            File file = new File(filename);

            // Open Audio Input Stream
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);

            // Create Audio Clip
            AudioClip clip = new AudioClip(audio);

            // Return Audio Clip
            return clip;
        } catch(Exception e) {
            // Catch Exception
            System.out.println("Error: cannot open Audio File " + filename + "\n");
        }

        // Return Null
        return null;
    }

    // Plays an AudioClip
    public void playAudio(AudioClip audioClip) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        try {
            // Create a Clip
            Clip clip = AudioSystem.getClip();

            // Load data
            clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

            // Play Clip
            clip.start();
        } catch(Exception exception) {
            // Display Error Message
            System.out.println("Error playing Audio Clip\n");
        }
    }

    // Plays an AudioClip with a volume in decibels
    public void playAudio(AudioClip audioClip, float volume) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        try {
            // Create a Clip
            Clip clip = AudioSystem.getClip();

            // Load data
            clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

            // Create Controls
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Set Volume
            control.setValue(volume);

            // Play Clip
            clip.start();
        } catch(Exception exception) {
            // Display Error Message
            System.out.println("Error: could not play Audio Clip\n");
        }
    }

    // Starts playing an AudioClip on loop
    public void startAudioLoop(AudioClip audioClip) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Create Loop Clip if necessary
        if(clip == null) {
            try {
                // Create a Clip
                clip = AudioSystem.getClip();

                // Load data
                clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

                // Set Clip to Loop
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                // Set Loop Clip
                audioClip.setLoopClip(clip);
            } catch(Exception exception) {
                // Display Error Message
                System.out.println("Error: could not play Audio Clip\n");
            }
        }

        // Set Frame Position to 0
        clip.setFramePosition(0);

        // Start Audio Clip playing
        clip.start();
    }

    // Starts playing an AudioClip on loop with a volume in decibels
    public void startAudioLoop(AudioClip audioClip, float volume) {
        // Check audioClip for null
        if(audioClip == null) {
            // Print error message
            System.out.println("Error: audioClip is null\n");

            // Return
            return;
        }

        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Create Loop Clip if necessary
        if(clip == null) {
            try {
                // Create a Clip
                clip = AudioSystem.getClip();

                // Load data
                clip.open(audioClip.getAudioFormat(), audioClip.getData(), 0, (int)audioClip.getBufferSize());

                // Create Controls
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Set Volume
                control.setValue(volume);

                // Set Clip to Loop
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                // Set Loop Clip
                audioClip.setLoopClip(clip);
            } catch(Exception exception) {
                // Display Error Message
                System.out.println("Error: could not play Audio Clip\n");
            }
        }

        // Set Frame Position to 0
        clip.setFramePosition(0);

        // Start Audio Clip playing
        clip.start();
    }

    // Stops an AudioClip playing
    public void stopAudioLoop(AudioClip audioClip) {
        // Get Loop Clip
        Clip clip = audioClip.getLoopClip();

        // Check clip is not null
        if(clip != null){
            // Stop Clip playing
            clip.stop();
        }
    }

    //-------------------------------------------------------
    // Maths Functions
    //-------------------------------------------------------
    Random mRandom = null;

    // Function that returns a random integer between 0 and max
    public int rand(int max) {
        // Check if mRandom Exists
        if(mRandom == null) {
            // Create a new Random Object
            mRandom = new Random();
        }

        // Generate a random number
        double d = mRandom.nextDouble();

        // Convert to an integer in range [0, max) and return
        return (int)(d*max);
    }

    // Function that gives you a random number between 0 and max
    public float rand(float max) {
        // Check if mRandom Exists
        if(mRandom == null) {
            // Create a new Random Object
            mRandom = new Random();
        }

        // Generate a random number
        float d = mRandom.nextFloat();

        // Convert to range [0, max) and return
        return d*max;
    }

    // Function that gives you a random number between 0 and max
    public double rand(double max) {
        // Check if mRandom Exists
        if(mRandom == null) {
            // Create a new Random Object
            mRandom = new Random();
        }

        // Generate a random number
        double value = mRandom.nextDouble();

        // Convert to range [0, max) and return
        return value*max;
    }

    // Returns the largest integer that is less than or equal
    // to the argument value.
    public int floor(double value) {
        // Calculate and return floor
        return (int)Math.floor(value);
    }

    // Returns the smallest integer that is greater than or equal
    // to the argument value.
    public int ceil(double value){
        // Calculate and return ceil
        return (int)Math.ceil(value);
    }

    // Rounds the argument value to the closest integer.
    public int round(double value) {
        // Calculate and return round
        return (int)Math.round(value);
    }

    // Returns the square root of the parameter
    public double sqrt(double value) {
        // Calculate and return the sqrt
        return Math.sqrt(value);
    }

    // Returns the length of a vector
    public double length(double x, double y) {
        // Calculate and return the sqrt
        return Math.sqrt(x*x + y*y);
    }

    // Returns the distance between two points (x1,y1) and (x2,y2)
    public static double distance(double x1, double y1, double x2, double y2) {
        // Calculate and return the distance
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    // Converts an angle in radians to degrees
    public static double toDegrees(double radians) {
        // Calculate and return the degrees
        return Math.toDegrees(radians);
    }

    // Converts an angle in degrees to radians
    public static double toRadians(double degrees) {
        // Calculate and return the radians
        return Math.toRadians(degrees);
    }

    // Returns the absolute value of the parameter
    public static int abs(int value) {
        // Calculate and return abs
        return Math.abs(value);
    }

    // Returns the absolute value of the parameter
    public static float abs(float value) {
        // Calculate and return abs
        return Math.abs(value);
    }

    // Returns the absolute value of the parameter
    public static double abs(double value) {
        // Calculate and return abs
        return Math.abs(value);
    }

    // Returns the cos of value
    public static double cos(double value) {
        // Calculate and return cos
        return Math.cos(Math.toRadians(value));
    }

    // Returns the acos of value
    public static double acos(double value) {
        // Calculate and return acos
        return Math.toDegrees(Math.acos(value));
    }

    // Returns the sin of value
    public static double sin(double value) {
        // Calculate and return sin
        return Math.sin(Math.toRadians(value));
    }

    // Returns the asin of value
    public static double asin(double value) {
        // Calculate and return asin
        return Math.toDegrees(Math.asin(value));
    }

    // Returns the tan of value
    public static double tan(double value) {
        // Calculate and return tan
        return Math.tan(Math.toRadians(value));
    }
    // Returns the atan of value
    public static double atan(double value) {
        // Calculate and return atan
        return Math.toDegrees(Math.atan(value));
    }
    // Returns the atan2 of value
    public static double atan2(double x, double y) {
        // Calculate and return atan2
        return Math.toDegrees(Math.atan2(x,y));
    }
}