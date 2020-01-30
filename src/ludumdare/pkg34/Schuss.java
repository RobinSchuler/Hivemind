/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public abstract class Schuss {
    float x;
    float y;
    float xAim;
    float yAim;
    float fLiveTime = 0;
    

    public Schuss(float x, float y, float xAim, float yAim) {
        this.x = x;
        this.y = y;
        this.xAim = xAim;
        this.yAim = yAim;
    }
    
    abstract Animation getAnim();
    
    public void update(float fPassedTime){
        fLiveTime += fPassedTime;
    }
    public void render(LudumDare34 xLib, Camera xCam){
        if(done())
            return;
        xLib.drawAnimation(xCam.mapToScreenX(x + (xAim - x)*fLiveTime*2), xCam.mapToScreenY(y + (yAim - y)*fLiveTime*2), xCam.getTileWidth()*2f/3, xCam.getTileHeight()*2f/3, getAnim());
    }
    public boolean done(){
        return fLiveTime > .5f;
    }
}
