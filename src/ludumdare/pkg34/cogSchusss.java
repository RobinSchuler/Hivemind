/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public class cogSchusss extends Schuss{
    Animation xAnim = new Animation("./res/textures/kog_geschoss", 1, 0,  1, true, null, LudumDare34.scaleDown());

    public cogSchusss(float x, float y, float xAim, float yAim) {
        super(x, y, xAim, yAim);
    }

    @Override
    Animation getAnim() {
        return xAnim;
    }
    
}
