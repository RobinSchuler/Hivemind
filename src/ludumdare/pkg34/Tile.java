/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;

 
public class Tile {
    private final Animation xAnimation;

    public Tile(Animation xAnimation) {
        this.xAnimation = xAnimation;
    }
    
    
    public void render(SimpleGameLibrarry xLib, float x, float y, float width, float hight){
        if(xAnimation != null){
            //System.out.println("blup? " + x + " " + y + " " + width + " " + hight);
            xLib.drawAnimation(x, y, width, hight, xAnimation);
        }
        else
            xLib.drawRect(x, y, width, hight, TileContainer.xFogofWar);
    }
    
}
