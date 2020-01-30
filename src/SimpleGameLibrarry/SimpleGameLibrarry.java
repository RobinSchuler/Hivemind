package SimpleGameLibrarry;




import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Simple Game Librarry.
 * Use an Object of the SimpleSoundManager for Sounds.
 * Extend this class and call the start() method to start the Graphic and Sound engines
 * @see SimpleGameLibrarry.SimpleSoundmanager
 */
public abstract class SimpleGameLibrarry {
    
    private volatile boolean running = true,windowed, recordMode = false;
    private final Object runningMarker = new Object(); 
    private volatile boolean shutdown = false;
    private static int resolutionX, resolutionY, scaleUp ;
    private int currfps, currtps, currlps, recordModeFrameCounter = 0;
    private float minPsf, minPst, passedRenderTime;
    private volatile static BufferedImage drawboard;
    private static JFrame frame;
    private DisplayMode oldDisp;
    private String recordPath;
    private LinkedBlockingDeque<BufferedImage> recordImageQue = new LinkedBlockingDeque<>();
    private Object xWaitOnResize = new Object(), XWaitForResize = new Object();
    private volatile boolean bWaitResize = false;
    private int frameWidth = 0, frameHeight = 0;
    private CyclicBarrier xRenderBarrier;
    private RenderThread[] axRenderThreads;
    private volatile int iTick = 0;
    
    private class RenderThread extends Thread{
        private int iId;
        
        private void render(){
            try {
                xRenderBarrier.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            }

            onRender(iId, axRenderThreads.length);

            try {
                xRenderBarrier.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            }

            onRenderGui(iId, axRenderThreads.length);


            try {
                xRenderBarrier.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public RenderThread(int iId) {
            this.iId = iId;
        }
        @Override
        public void run() {
            if(iId != 0){
                while (!shutdown) {                    
                    render();
                }//while
            }//if
            else{
                super.run(); //To change body of generated methods, choose Tools | Templates.
                long time = System.currentTimeMillis();

                while(!shutdown){
                    if(bWaitResize){
                        try {
                            synchronized(xWaitOnResize){
                                xWaitOnResize.wait();
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    iTick++;
                    synchronized(XWaitForResize){
                        long timenow = System.currentTimeMillis();
                        long passedTime = timenow - time;
                        time = timenow;
                        if(passedTime < minPsf){
                            try {
                                Thread.sleep((int)minPsf - passedTime);
                            } catch (InterruptedException ex) {}
                            currfps = (int)(1_000f/minPsf);
                        }
                        else{
                            currfps = (int)(1_000f/passedTime);
                        }
                        passedRenderTime = passedTime/1_000f;
                        if(drawboard == null)
                            drawboard = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                        //drawboard.getGraphics().fillRect(0, 0, frameWidth, frameHeight);
                        
                        setUpRender();
                        render();

                        frame.getGraphics().drawImage(drawboard, 0, 0, null);
                        if(recordMode){
                            recordImageQue.offer(drawboard);
                        }//if
                    }//synchronized
                }//while
            }//else
        }//function        
    }//class
    
    private final Thread ticker = new Thread(new Runnable() {

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            while(!shutdown){
                while (running) {      
                    long timenow = System.currentTimeMillis();
                    long passedTime = timenow - time;
                    time = timenow;
                    if(passedTime < minPst){
                        try {
                            Thread.sleep((int)minPst - passedTime);
                        } catch (InterruptedException ex) {}
                        currtps = (int)(1_000f/minPst);
                    }
                    else{
                        currtps = (int)(1_000f/passedTime);
                    }
                    onUpdate(passedTime/1_000f);
                }
                synchronized(runningMarker){
                    try {
                        runningMarker.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
    });

    /**
     * Does nothing.
     * Call the start() method to start the the Graphic and Sound engines.
     */
    public SimpleGameLibrarry() {
    }
    
    public void setIcon(String path){
        frame.setIconImage((new ImageIcon(path)).getImage());
    }
    
    /**
     * 
     * @param resolutionX the resolution
     * @param resolutionY the resolution
     * @param maxFps the maximum frames that should be rendered per second
     * @param maxTps the maximum ticks that should be made per second
     * @param title the title of the game
     * @param windowed toggles between fullscreen and Winwow mode
     * @param light do light ticks
     */
    public void start(int resolutionX, int resolutionY, int maxFps, int maxTps, String title, boolean windowed, int iThreads) {
        this.windowed = windowed;
        setMaxFps(maxFps);
        setMaxTps(maxTps);
        frame = new JFrame(title);
        int scale = resolutionX;
        if(resolutionY < scale)
            scale = resolutionY;
        setResolution(resolutionX, resolutionY, scale);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(blankCursor);

        
        frame.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                onKey(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                onKey(e.getKeyCode(), false);
            }
        });
        frame.addMouseMotionListener(new MouseMotionListener() {
            int mousex = 0, mousey = 0;
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseMove(e.getX()*SimpleGameLibrarry.resolutionX/frameWidth, e.getY()*SimpleGameLibrarry.resolutionY/frameHeight, 
                        (e.getX()- mousex) *SimpleGameLibrarry.resolutionX/frameWidth, (e.getY() - mousey) *SimpleGameLibrarry.resolutionY/frameHeight);
                mousex = e.getX();
                mousey = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                onMouseMove(e.getX()*SimpleGameLibrarry.resolutionX/frameWidth, e.getY()*SimpleGameLibrarry.resolutionY/frameHeight, 
                        (e.getX()- mousex) *SimpleGameLibrarry.resolutionX/frameWidth, (e.getY() - mousey) *SimpleGameLibrarry.resolutionY/frameHeight);
                mousex = e.getX();
                mousey = e.getY();
            }
        });
        frame.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                onMouse(e.getButton(), true, e.getX()*SimpleGameLibrarry.resolutionX/frameWidth, e.getY()*SimpleGameLibrarry.resolutionY/frameHeight);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouse(e.getButton(), false, e.getX()*SimpleGameLibrarry.resolutionX/frameWidth, e.getY()*SimpleGameLibrarry.resolutionY/frameHeight);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        frame.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                onMouseWheel(e.getWheelRotation());
            }
        });
        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                bWaitResize = true;        
                synchronized(XWaitForResize){
                    frameHeight = frame.getHeight();
                    frameWidth = frame.getWidth();
                    bWaitResize = false;
                    drawboard = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                    synchronized(xWaitOnResize){
                        xWaitOnResize.notifyAll();
                    }
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });
        frame.setVisible(true);
        ticker.start();
        xRenderBarrier = new CyclicBarrier(iThreads);
        axRenderThreads = new RenderThread[iThreads];
        for (int i = 0; i < iThreads; i++) {
            axRenderThreads[i] = new RenderThread(i);
            axRenderThreads[i].start();
        }//for
    }
    
    public void setVisible(boolean b){
        frame.setVisible(b);
    }
    
    /**
     * changes the maxFps
     * @param maxFps the maximum frames that should be rendered per second
     */
    public final void setMaxFps(int maxFps) {
        this.minPsf = 1_000f/maxFps;
    }

    /**
     * changes the maxTps
     * @param maxTps the maximum ticks that should be made per second
     */
    public final void setMaxTps(int maxTps) {
        this.minPst = 1_000f/maxTps;
    }

    /**
     * changes the resolution
     * @param resolutionX the resolution
     * @param resolutionY the resolution
     */
    public final void setResolution(int resolutionX, int resolutionY, int scale) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.scaleUp = scale;
        if(windowed){
            frame.setBounds(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*1/20, java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*1/20, 
                    resolutionX, resolutionY);
            frame.setDefaultCloseOperation(3);
            frameWidth = resolutionX;
            frameHeight = resolutionY;
        }
        else{
            frame.setUndecorated(true);
            GraphicsDevice device = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); 
            try{
                device.setFullScreenWindow(frame);
                if (device.isDisplayChangeSupported()) {
                    oldDisp = device.getDisplayMode();
                    int changeto = -1;
                    int dist = -1;
                    for (int i = 0; i < device.getDisplayModes().length; i++) {
                        if(dist < 0 || (dist >= device.getDisplayModes()[i].getWidth()-resolutionX && device.getDisplayModes()[i].getBitDepth() >= 16)){
                            dist = device.getDisplayModes()[i].getWidth()-resolutionX;
                            changeto = i;
                        }
                    }
                    if(changeto != -1)
                        device.setDisplayMode(device.getDisplayModes()[changeto]);
                }
                frameWidth = frame.getWidth();
                frameHeight = frame.getHeight();
            }
            catch(Exception e){
                device.setDisplayMode(oldDisp);
                device.setFullScreenWindow(null);
            }
        }
    }
    
    /**
     * test if this resolution is possible. creates a window to do so... may freeze the screen for a while
     * @param resolutionX the resolution aimed for 
     * @param resolutionY the resolution aimed for 
     * @return a String telling what the closest possible resolution was. the String is formated like this: "resolutionX" x "resoltionY"
     */
    public static String getActualResolutionForInput(int resolutionX, int resolutionY){
        GraphicsDevice device = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); 
        JFrame f = new JFrame();
        f.setUndecorated(true);
        try{
            device.setFullScreenWindow(f);
            if (device.isDisplayChangeSupported()) {
                int changeto = -1;
                int dist = -1;
                for (int i = 0; i < device.getDisplayModes().length; i++) {
                    if(dist < 0 || (dist >= device.getDisplayModes()[i].getWidth()-resolutionX && device.getDisplayModes()[i].getBitDepth() >= 16)){
                        dist = device.getDisplayModes()[i].getWidth()-resolutionX;
                        changeto = i;
                    }
                }
                if(changeto != -1){
                    device.setFullScreenWindow(null);
                    f.dispose();
                    return device.getDisplayModes()[changeto].getWidth() + " / " + device.getDisplayModes()[changeto].getHeight();
                }
            }
        }
        catch(Exception e){
            device.setFullScreenWindow(null);
            f.dispose();
        }
        device.setFullScreenWindow(null);
        f.dispose();
        return "No fitting resolution found... ";
    }

    /**
     * pauses the game
     * rendering might still occour
     * does nothing when the game is already paused
     */
    public final void pause(){
        running = false;
    }
    
    /**
     * resumes the game
     * does nothing when the game is already running
     */
    public final void resume(){
        running = false;
        synchronized(runningMarker){
            runningMarker.notifyAll();
        }
    }
    
    /**
     * override this method!
     */
    public abstract void setUpRender();
    
    /**
     * override this method!
     * do all your necessary drawing inside of this
     */
    public abstract void onRender(int iThreadId, int iThreadsMax);
    
    /**
     * override this method!
     * do all your shutdown logic here
     */
    public abstract void onShutdown();
    
    /**
     * override this method!
     * do all your necessary drawing for the GUI inside of this
     */
    public abstract void onRenderGui(int iThreadId, int iThreadsMax);
    
    /**
     * override this method!
     * do all your necessary game logic inside of this method.
     * @param passedTime the time that passed since the last onUpdate call
     */
    public abstract void onUpdate(float passedTime);
    
    /**
     * override this method!
     * this will be called when on pc and a key gets pressed or released
     * @param keyId the id of the key.
     * @param pressed if the key was pressed or released
     */
    public abstract void onKey(int keyId, boolean pressed);
    
    /**
     * override this method!
     * this will be called when the screen gets touched or any mouse button hit.
     * @param Id the id of the button or 0 if on android.
     * @param pressed if pressed or released
     * @param x the x location of the event
     * @param y the y location of the event
     */
    public abstract void onMouse(int Id, boolean pressed, int x, int y);
    
    /**
     * override this method!
     * this will be called when the screen gets touched or any mouse button hit.
     * @param ticksRotated number of ticks the wheel rotated
     */
    public abstract void onMouseWheel(int ticksRotated);
    
    /**
     * override this method!
     * @param x the new location of the mouse
     * @param y the new location of the mouse
     * @param movex the distance moved
     * @param movey the distance moved
     */
    public abstract void onMouseMove(int x, int y, int movex, int movey);
    
    /**
     * use this method during the onRender.
     * use this for HUD only!
     * 
     * @param i the image to be drawn
     * @param x the xcoord of the image
     * @param y the ycoord of the image
     */
    public final void drawImage(Image i, int x, int y){
        drawboard.getGraphics().drawImage(i, (int)(x*frameWidth/resolutionX), (int)(y*frameHeight/resolutionY),(int)(i.getWidth(null)*frameWidth/resolutionX),(int)(i.getHeight(null)*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param i the image to be drawn
     * @param x the xcoord of the image
     * @param y the ycoord of the image
     * @param width the width of the image
     * @param height the height of the image
     */
    public final void drawImage(Image i, float x, float y, float width, float height){
        drawboard.getGraphics().drawImage(i, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(width*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param i the image to be drawn
     * @param x the xcoord of the image
     * @param y the ycoord of the image
     * @param width the width of the image
     * @param height the height of the image
     * @param rotation  the angle of rotation in radians
     */
    public final void drawImage(Image i, float x, float y, float width, float height, float rotation){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.rotate(rotation, (int)( (x+width/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.drawImage(i, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(width*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param s the string to be drawn
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param fontName - the font name. This can be a font face name or a font family name, and may represent either a logical font or a physical font found in this GraphicsEnvironment. The family names for logical fonts are: Dialog, DialogInput, Monospaced, Serif, or SansSerif. Pre-defined String constants exist for all of these names, for example, DIALOG. If name is null, the logical font name of the new Font as returned by getName() is set to the name "Default". 
     * @param style - the style constant for the Font The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN. 
     * @param size - the point size of the Font
     */
    public final void drawString(String s, float x, float y, String fontName, int style, int size){
        Graphics g = drawboard.getGraphics();
        g.setFont(new Font(fontName, style, size));
        g.drawString(s, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param s the string to be drawn
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param fontName - the font name. This can be a font face name or a font family name, and may represent either a logical font or a physical font found in this GraphicsEnvironment. The family names for logical fonts are: Dialog, DialogInput, Monospaced, Serif, or SansSerif. Pre-defined String constants exist for all of these names, for example, DIALOG. If name is null, the logical font name of the new Font as returned by getName() is set to the name "Default". 
     * @param style - the style constant for the Font The style argument is an integer bitmask that may be PLAIN, or a bitwise union of BOLD and/or ITALIC (for example, ITALIC or BOLD|ITALIC). If the style argument does not conform to one of the expected integer bitmasks then the style is set to PLAIN. 
     * @param size - the point size of the Font
     * @param c the color of the string
     */
    public final void drawString(String s, float x, float y, String fontName, int style, int size, Color c){
        Graphics g = drawboard.getGraphics();
        g.setColor(c);
        g.setFont(new Font(fontName, style, size));
        g.drawString(s, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param s the string to be drawn
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     */
    public final void drawString(String s, float x, float y){
        drawboard.getGraphics().drawString(s, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param s the string to be drawn
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param c the color of the String
     */
    public final void drawString(String s, float x, float y, Color c){
        Graphics g = drawboard.getGraphics();
        g.setColor(c);
        g.drawString(s, (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param color the color of the rect
     */
    public final void drawRect(float x, float y, float widht, float height, Color color){
        Graphics g = drawboard.getGraphics();
        g.setColor(color);
        g.fillRect((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param color the color of the rect
     * @param rotation  the angle of rotation in radians
     */
    public final void drawRect(float x, float y, float widht, float height, Color color, float rotation){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.setColor(color);
        g.rotate(rotation, (int)( (x+widht/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.fillRect((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param animation the animation to be drawn
     */
    public final void drawAnimation(float x, float y, float widht, float height, Animation animation){
        try{
        drawboard.getGraphics().drawImage(animation.draw(passedRenderTime, iTick), (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(widht*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
        }catch(IndexOutOfBoundsException e){/*e.printStackTrace();*/};//FIXE
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param animation the animation to be drawn
     * @param rotation the angle of rotation in radians
     */
    public final void drawAnimation(float x, float y, float widht, float height, Animation animation, float rotation){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.rotate(rotation, (int)( (x+widht/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.drawImage(animation.draw(passedRenderTime, iTick), (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(widht*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the string
     * @param y the ycoord of the string
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param animation the animation sequence to be drawn
     */
    public final void drawSmoothAnimationSequence(float x, float y, float widht, float height, SmoothAnimationSequence animation){
        drawboard.getGraphics().drawImage(animation.draw(passedRenderTime, iTick), (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(widht*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the animation
     * @param y the ycoord of the animation
     * @param widht the width of the animation
     * @param height the hight of the animation
     * @param animation the animation sequence to be drawn
     * @param rotation  the angle of rotation in radians
     */
    public final void drawSmoothAnimationSequence(float x, float y, float widht, float height, SmoothAnimationSequence animation, float rotation){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.rotate(rotation, (int)( (x+widht/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.drawImage(animation.draw(passedRenderTime, iTick), (int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY),(int)(widht*scaleUp*frameWidth/resolutionX),(int)(height*scaleUp*frameHeight/resolutionY), null);
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the rect
     * @param y the ycoord of the rect
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param color the color of the rect
     */
    public final void drawRectOutline(float x, float y, float widht, float height, Color color){
        Graphics g = drawboard.getGraphics();
        g.setColor(color);
        g.drawRect((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the rect
     * @param y the ycoord of the rect
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param color the color of the rect
     * @param rotation  the angle of rotation in radians
     * @param thickness  the thickness of the line
     */
    public final void drawRectOutline(float x, float y, float widht, float height, Color color, float rotation, float thickness){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.rotate(rotation, (int)( (x+widht/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness * scaleUp));
        g.drawRect((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the rect
     * @param y the ycoord of the rect
     * @param widht the width of the rect
     * @param height the hight of the rect
     * @param color the color of the rect
     * @param rotation  the angle of rotation in radians
     */
    public final void drawRectOutline(float x, float y, float widht, float height, Color color, float rotation){
        Graphics2D g = (Graphics2D)drawboard.getGraphics();
        g.rotate(rotation, (int)( (x+widht/2)*scaleUp*frameWidth/resolutionX), (int)( (y+height/2)*scaleUp*frameHeight/resolutionY));
        g.setColor(color);
        g.drawRect((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the circle
     * @param y the ycoord of the circle
     * @param widht the width of the circle
     * @param height the hight of the circle
     * @param color the color of the circle
     */
    public final void drawCircle(float x, float y, float widht, float height, Color color){
        Graphics g = drawboard.getGraphics();
        g.setColor(color);
        g.fillOval((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the xcoord of the circle
     * @param y the ycoord of the circle
     * @param widht the width of the circle
     * @param height the hight of the circle
     * @param color the color of the circle
     */
    public final void drawCircleOutline(float x, float y, float widht, float height, Color color){
        Graphics g = drawboard.getGraphics();
        g.setColor(color);
        g.drawOval((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(widht*scaleUp*frameWidth/resolutionX), (int)(height*scaleUp*frameHeight/resolutionY));
    }
    
    /**
     * use this method during the onRender.
     * @param x the x coord of the line
     * @param y the y coord of the line
     * @param x2 the 2nd x coord of the line
     * @param y2 the 2nd xý coord of the line
     * @param color the color of the line
     */
    public final void drawLine(float x, float y, float x2, float y2, Color color){
        Graphics g = drawboard.getGraphics();
        g.setColor(color);
        g.setFont(new Font("Times New Roman", 0, 50));
        g.drawLine((int)(x*scaleUp*frameWidth/resolutionX), (int)(y*scaleUp*frameHeight/resolutionY), (int)(x2*scaleUp*frameWidth/resolutionX), (int)(y2*scaleUp*frameHeight/resolutionY));
    }
    /**
     * get the curret Fps
     * @return current Frames per second
     */
    public final int getCurrFps() {
        return currfps;
    }
    
    /**
     * get the current Tps
     * @return current Ticks per second
     */
    public final int getCurrTps() {
        return currtps;
    }
    
    /**
     * get the current Lps
     * @return current Ticks per second
     */
    public final int getCurrLps() {
        return currlps;
    }
    
    /**
     * call this to close the game
     */
    public final void shutdown(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(false);
                onShutdown();
                running = false;
                shutdown = true;
                if(recordMode){
                    recordMode = false;
                    try {
                        recorder.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(!windowed){
                    try {
                        java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setDisplayMode(oldDisp);
                    } catch (Exception exception) {
                    }
                }
                try {
                    for (int i = 0; i < axRenderThreads.length; i++) {
                        axRenderThreads[i].join();
                    }//for
                    while (ticker.isAlive()) {                
                        synchronized(runningMarker){
                            runningMarker.notifyAll();
                        }
                    }
                } catch (InterruptedException ex) {
                }
                finally{
                    System.exit(0);
                }
            }
        }).start();
    }
    
    /**
     * returns not the actual resolution just the value you set it to
     * @return the resolution X
     */
    public int getResolutionX() {
        return resolutionX;
    }

    /**
     * returns not the actual resolution just the value you set it to
     * @return the resolution Y
     */
    public int getResolutionY() {
        return resolutionY;
    }
    
    /**
     * @return resolution X / resolution Y
     */
    public float getResolutionFactor() {
        return resolutionX/(float)resolutionY;
    }
    
    private final Thread recorder = new Thread(new Runnable() {

        @Override
        public void run() {
            while (recordMode) {                
                while (!recordImageQue.isEmpty()) {      
                    try {
                        String suffix = "";
                        int counter = recordModeFrameCounter++;
                        for (int i = 0; i < 6; i++) {
                            suffix = counter%10 + suffix;
                            counter/=10;
                        }
                        ImageIO.write(recordImageQue.pop(), "png", new File(recordPath + "." + suffix + ".png"));
                    } catch (IOException ex) {
                        Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    });
    
    /**
     * starts to save all rendered frames
     * @param path the folder to save the frames into
     */
    public void recordSection(String path){
        recordModeFrameCounter = 0;
        this.recordPath = path;
        recordMode = true;
        recorder.start();
    }
    
    /**
     * stops saving all rendered frames
     * does nothing when recordSection(String path) wasn't called before
     */
    public void stopRecording(){
        recordMode = false;
        try {
            recorder.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleGameLibrarry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * keep the value between 0.1 and 0.9 for best results...
     * @return the ambience light
     */
    public abstract float light();
    
}
