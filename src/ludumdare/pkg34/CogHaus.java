/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public class CogHaus extends Haus{
    private Animation xAnim = new Animation("./res/textures/cog_haus", 1, 0, 1, true, null, LudumDare34.scaleDown());

    public CogHaus(float x, float y) {
        super(x, y);
    }

    @Override
    public Animation getAnim() {
        return xAnim;
    }

    @Override
    public void spawn(boolean bSpawnLurchOnly, LudumDare34 xLib) {
        if(!bSpawnLurchOnly){
            if(xLib.xMainBasis.iUsedSupp >= xLib.xMainBasis.iSuply)
                return;
            xLib.xMainBasis.incSup();
            xLib.addEntity(new Cog(xLib.xMap, false, (int)x, (int)y));
        }
    }

    @Override
    public void remove(Basis xBasis) {
        xBasis.iCog--;
    }
    
}
