/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.SimpleGameLibrarry;
import java.awt.Color;

 
public abstract class DebugEntity extends Entity{
    Color xMyColor;

    public DebugEntity(Color xMyColor, MYMap xMap, boolean enemy, int x, int y) {
        super(xMap, enemy, x, y);
        this.xMyColor = xMyColor;
    }

    @Override
    String getAnimationName() {
        return "claw";
    }    

    @Override
    public void render(SimpleGameLibrarry xLib, Camera xCam) {
        if(x + getRadius() > xCam.getXOnMap()/xLib.getResolutionFactor() && y + getRadius() > xCam.getYOnMap() && x < xCam.getWidthOnMap() + xCam.getXOnMap()/xLib.getResolutionFactor() && y < xCam.getHeightOnMap() + xCam.getYOnMap()){
            xLib.drawCircle(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), xMyColor);
            if(bSelected)
                xLib.drawCircleOutline(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), Color.green);
            if(bEnemy)
                xLib.drawCircleOutline(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), Color.red);
            if(fLive < getMaxLive()){
                xLib.drawRect(xCam.mapToScreenX(x), xCam.mapToScreenY(y) - (getRadius()*.1f/xCam.getHeightOnMap()), getRadius() * xLib.getResolutionFactor()*fLive / (xCam.getWidthOnMap()*getMaxLive()), getRadius()*.1f/xCam.getHeightOnMap(), new Color(0, 225, 0));
            }
        }
        if(DEBUG_PUSH){
            xLib.drawLine(xCam.mapToScreenX(getCenterX()), xCam.mapToScreenY(getCenterY()), xCam.mapToScreenX(getCenterX()+xPush), xCam.mapToScreenY(getCenterY()+yPush), Color.MAGENTA);
        }
    }
    
}
