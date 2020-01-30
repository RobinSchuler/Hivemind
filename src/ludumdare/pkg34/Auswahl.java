/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;

 
public class Auswahl extends Haus{
    private volatile static boolean dispAuswahl = false;
    private Animation xAnim= new Animation("./res/textures/auswahl", 1, 0, 1, true, null, LudumDare34.scaleDown());

    public Auswahl(float x, float y) {
        super(x, y);
    }

    @Override
    public void render(SimpleGameLibrarry xLib, Camera xCam) {
        if(dispAuswahl)
            super.render(xLib, xCam); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void setDispAuswahl(boolean  xSet){
        dispAuswahl = xSet;
    } 

    @Override
    public Animation getAnim() {
        return xAnim;
    }

    @Override
    public void spawn(boolean bSpawnLurchOnly, LudumDare34 xLib) {
    }

    @Override
    public void remove(Basis xBasis) {
    }
    
    
    
}
