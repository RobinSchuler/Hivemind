package SimpleGameLibrarry;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
public class Animation {
    private volatile int amountFrames, indexImgs;
    private volatile int currFrame = 0;
    private volatile float currTime = 0;
    private final float lengthPerFrame;
    private static final ArrayList<BufferedImage[]> images = new ArrayList<>();
    private static final ArrayList<String> strings = new ArrayList<>();
    private AnimationDoneListener listner;
    private boolean loop;
    private boolean running = true;
    private volatile int iTickLastRendered = 0;

    public Animation(String name, int amountFrames, int startCountingAt, float length, boolean loop, AnimationDoneListener listener, float fScaleDown) {
        this.listner = listener;
        this.amountFrames = amountFrames;
        this.loop = loop;
        this.lengthPerFrame = length / amountFrames;
        int j = 0;
        synchronized(images){
            for (; j < strings.size(); j++) {
                if(strings.get(j).equals(name)){
                    indexImgs = j;
                    return;
                }
            }
            strings.add(name);
            images.add(new BufferedImage[amountFrames]);
            for (int i = 0; i < amountFrames; i++) {
                try {
                    images.get(j)[i] = ImageIO.read(new File(name + "." + ( (i+startCountingAt)/1000)%10 + "" + ( (i+startCountingAt)/100)%10 + "" + ( (i+startCountingAt)/10)%10 + "" + (i+startCountingAt)%10 + ".png"));
                    if(fScaleDown < 1){
                        BufferedImage bImg = new BufferedImage((int)(images.get(j)[i].getWidth()*fScaleDown), (int)(images.get(j)[i].getHeight()*fScaleDown), BufferedImage.TYPE_INT_ARGB);
                        bImg.getGraphics().drawImage(images.get(j)[i], 0, 0, (int)(images.get(j)[i].getWidth()*fScaleDown), (int)(images.get(j)[i].getHeight()*fScaleDown), null);
                        images.get(j)[i] = bImg;
                    }
                } catch (IOException ex) {
                    System.out.println("flilename: " + name + "." + ( (i+startCountingAt)/1000)%10 + "" + ( (i+startCountingAt)/100)%10 + "" + ( (i+startCountingAt)/10)%10 + "" + (i+startCountingAt)%10 + ".png");
                    Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            indexImgs = j;
        }
    } 
    public Animation(BufferedImage[] xManual, String name, float length, boolean loop, AnimationDoneListener listener) {
        this.listner = listener;
        this.amountFrames = xManual.length;
        this.loop = loop;
        this.lengthPerFrame = length / amountFrames;
        int j = 0;
        synchronized(images){
            for (; j < strings.size(); j++) {
                if(strings.get(j).equals(name)){
                    indexImgs = j;
                    return;
                }
            }
            strings.add(name);
            images.add(xManual);
            indexImgs = j;
        }
    } 
    
    public void overlayWith(Animation xAnim2){
        synchronized(images){
            for (int j = 0; j < strings.size(); j++) {
                if(strings.get(j).equals(indexImgs+"#"+xAnim2.indexImgs)){
                    indexImgs = j;
                    return;
                }
            }
            BufferedImage[] aNewArr = new BufferedImage[amountFrames];
            for (int i = 0; i < aNewArr.length && i < images.get(indexImgs).length && i < images.get(xAnim2.indexImgs).length; i++) {
                aNewArr[i] = new BufferedImage(images.get(indexImgs)[i].getWidth(), images.get(indexImgs)[i].getHeight(), BufferedImage.TYPE_INT_ARGB);
                    aNewArr[i].getGraphics().drawImage(images.get(indexImgs)[i], 0, 0, aNewArr[i].getWidth(), aNewArr[i].getHeight(),  null);
                    aNewArr[i].getGraphics().drawImage(images.get(xAnim2.indexImgs)[i], 0, 0, aNewArr[i].getWidth(), aNewArr[i].getHeight(),  null);
            }
            strings.add(indexImgs+"#"+xAnim2.indexImgs);
            images.add(aNewArr);
            indexImgs = strings.size() - 1;
        }
    }
    
    public Animation setTime(float time){
        currTime = time;
        currFrame = (int)(currTime / lengthPerFrame);
        currTime %= lengthPerFrame;
        if(currFrame >= amountFrames){
            if(loop)
                currFrame %= amountFrames;
            else{
                running = false;
                currFrame = amountFrames -1;
            }
            if(listner != null)
                listner.animationDone(this);
        }
        return this;
    }
        
    protected BufferedImage draw(float time, int iTickLastRendered){
        if(running){
            if(this.iTickLastRendered != iTickLastRendered){
                currTime += time;
                currFrame += (int)(currTime / lengthPerFrame);
                currTime %= lengthPerFrame;
                this.iTickLastRendered = iTickLastRendered;
            }
            if(currFrame >= amountFrames){
                if(loop)
                    currFrame %= amountFrames;
                else{
                    running = false;
                    currFrame = amountFrames -1;
                }
                if(listner != null)
                    listner.animationDone(this);
            }
        }
        synchronized(images){
            return images.get(indexImgs)[currFrame];
        }
    }
    
    public void run(){
        running = true;
    }
    
    public void stop(){
        running = false;
    }
    
    public void reset(){
        setTime(0);
        run();
    }

    protected void setListner(AnimationDoneListener listner) {
        this.listner = listner;
    }

    protected void setLoop(boolean loop) {
        this.loop = loop;
    }
}
