/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;
import java.awt.Color;

 
public abstract class Haus {
    float x = 0, y = 0, live = 100;
    
    
    public Haus(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract Animation getAnim();
    public void remove(Basis xBasis){};//FIXME eicle won't compile if this is abstract but it should be!
    
    public static float getWidth(){
        return 2;
    }
    
    public static float getHeight(){
        return 2;
    }
    
    public abstract void spawn(boolean bSpawnLurchOnly, LudumDare34 xLib);
    
    public void render(SimpleGameLibrarry xLib, Camera xCam){
        if(x + getWidth()> xCam.getXOnMap()/xLib.getResolutionFactor() && y + getHeight()> xCam.getYOnMap() && x < xCam.getWidthOnMap() + xCam.getXOnMap()/xLib.getResolutionFactor() && y < xCam.getHeightOnMap() + xCam.getYOnMap()){
            if(getAnim() != null)
                xLib.drawAnimation(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getWidth()* xLib.getResolutionFactor() / xCam.getWidthOnMap(), getHeight()/ xCam.getHeightOnMap(), getAnim());
            if(live < 100){
                xLib.drawRect(xCam.mapToScreenX(x), xCam.mapToScreenY(y- getHeight()) - (getWidth()*.1f/xCam.getHeightOnMap()), getWidth()* xLib.getResolutionFactor()*live / (xCam.getWidthOnMap()*100), getHeight()*.1f/xCam.getHeightOnMap(), new Color(0, 225, 0));
            }
        }
    }
    
    
    
    public boolean isAlive(){
        return live > 0;
    }
    
    public void damage(float dmg){
        live -= dmg;
    }
    
}
