/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;
import static ludumdare.pkg34.Haus.getHeight;
import static ludumdare.pkg34.Haus.getWidth;

 
public class EiscleHaus extends Haus{
    private Animation xAnim = new Animation("./res/textures/hausice", 1, 0, 1, true, null, LudumDare34.scaleDown());
    private volatile static boolean dispAuswahl = false;
    private Animation xAnim2= new Animation("./res/textures/auswahl", 1, 0, 1, true, null, LudumDare34.scaleDown());

    public EiscleHaus(float x, float y) {
        super(x, y);
    }

    @Override
    public Animation getAnim() {
        return xAnim;
    }

    @Override
    public void render(SimpleGameLibrarry xLib, Camera xCam) {
        super.render(xLib, xCam); //To change body of generated methods, choose Tools | Templates.
        if(dispAuswahl)
            xLib.drawAnimation(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getWidth()* xLib.getResolutionFactor() / xCam.getWidthOnMap(), getHeight()/ xCam.getHeightOnMap(), xAnim2);
    }
    
    public static void setDispAuswahl(boolean  xSet){
        dispAuswahl = xSet;
    }

    @Override
    public void spawn(boolean bSpawnLurchOnly, LudumDare34 xLib) {
        if(!bSpawnLurchOnly){
            if(xLib.xMainBasis.iUsedSupp >= xLib.xMainBasis.iSuply)
                return;
            xLib.xMainBasis.incSup();
            xLib.addEntity(new Eiscle(xLib.xMap, false, (int)x, (int)y));
        }
    }
    @Override
    public void remove(Basis xBasis) {
        xBasis.iEiscle--;
    }

    
}
