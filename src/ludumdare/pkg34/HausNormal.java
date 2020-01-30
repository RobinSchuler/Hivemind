/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;

 
public class HausNormal extends Haus{
    private Animation xAnim = new Animation("./res/textures/haus", 1, 0, 1, true, null, LudumDare34.scaleDown());
    private static Animation xQueen = new Animation("./res/textures/queen-s", 14, 0, 3f, true, null, LudumDare34.scaleDown());
    private float xSpawn, ySpawn;

    public HausNormal(float x, float y, float xSpawn, float ySpanw) {
        super(x, y);
        this.xSpawn = xSpawn;
        this.ySpawn = ySpanw;
    }

    @Override
    public Animation getAnim() {
        return xAnim;
    }

    @Override
    public void render(SimpleGameLibrarry xLib, Camera xCam) {
        super.render(xLib, xCam); //To change body of generated methods, choose Tools | Templates.
        xLib.drawAnimation(xCam.mapToScreenX(xSpawn - Entity.getRadius()*1f), xCam.mapToScreenY(ySpawn - Entity.getRadius()), Entity.getRadius() * xLib.getResolutionFactor() * 4 / xCam.getWidthOnMap(), Entity.getRadius() * 4 / xCam.getHeightOnMap(), xQueen);
    }
    
    

    @Override
    public void spawn(boolean bSpawnLurchOnly, LudumDare34 xLib) {
    }

    @Override
    public void remove(Basis xBasis) {
        xBasis.iRemainingNormal--;
        if(xBasis.iRemainingNormal <= 0){
            LudumDare34.loose();
        }
    }
    
}
