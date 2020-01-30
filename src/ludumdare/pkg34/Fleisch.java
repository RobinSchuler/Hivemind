/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public class Fleisch extends Entity{
    boolean bCounted = false;

    public Fleisch(MYMap xMap, boolean enemy, int x, int y) {
        
        this.x = x+(float)Math.random()/2f;
        this.y = y+(float)Math.random()/2f;
        xAiming = x;
        yAiming = y;
        bEnemy = enemy;
        fLive = getMaxLive();
        iRenderIndex = xMap.addEntity(x, y, enemy, this);
        xAnims = new Animation[5];
        xAnims[0] = new Animation("./res/textures/" + getAnimationName() , getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[1] = new Animation("./res/textures/" + getAnimationName() , getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[2] = new Animation("./res/textures/" + getAnimationName() , getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[3] = new Animation("./res/textures/" + getAnimationName() , getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[4] = new Animation("./res/textures/" + getAnimationName() , getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
    }
    
    


    @Override
    Schuss getProjectile(float y, float x, float xTo, float yTO) {
        return new ScionSchuss(x, y, xTo, yTO);
    }
    
    @Override
    String getAnimationName() {
        return "food";
    }

    @Override
    int getAnimWidht() {
        return 1;
    }

    @Override
    int getAnimHeight() {
        return 1;
    }

    @Override
    int getAnimLength() {
        return 1;
    }

    @Override
    float getRange() {
        return 0;
    }

    @Override
    float getAttackCooldown() {
        return 0;
    }

    @Override
    float getDamage() {
        return 0;
    }

    @Override
    float getHealRate() {
        return 0;
    }

    @Override
    float getCloseArmor() {
        return 0;
    }

    @Override
    float getRangedArmor() {
        return 0;
    }

    @Override
    float getMaxLive() {
        return 10;
    }

    @Override
    float getDamagedArea() {
        return 0;
    }

    @Override
    boolean hasSlow() {
        return false;
    }

    @Override
    float getSpeed() {
        return 0;
    }

    @Override
    boolean isFlying() {
        return false;
    }

    @Override
    boolean isJumping() {
        return false;
    }
    
}
