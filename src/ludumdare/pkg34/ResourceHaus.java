/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public class ResourceHaus extends Haus{
    private Animation xAnim = new Animation("./res/textures/food_haus", 1, 0, 1, true, null, LudumDare34.scaleDown());

    public ResourceHaus(float x, float y) {
        super(x, y);
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
        xBasis.iResourceSpeed--;
    }
    
}
