/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import java.awt.Color;

 
public class Lurch extends Entity{
    volatile float fLiveTime = 0;
    float fTimeTojump = .75f;

    public Lurch(MYMap xMap, boolean enemy, int x, int y) {
        super(xMap, enemy, x, y);
    }

    @Override
    String getAnimationName() {
        return "lurch";
    }

    @Override
    Schuss getProjectile(float y, float x, float xTo, float yTO) {
        return new ScionSchuss(x, y, xTo, yTO);
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
        return 2;
    }

    @Override
    public void update(float fTime, MYMap xMap, Basis xB, Camera xCam, LudumDare34 xLib) {
        super.update(fTime, xMap, xB, xCam, xLib); //To change body of generated methods, choose Tools | Templates.
        fLiveTime += fTime;
        if(fLiveTime >= fTimeTojump || xStatus == Status.STANDING){
            fTimeTojump = .75f + (float)(Math.random()*0.1-0.2);
            fLiveTime = fLiveTime %fTimeTojump;
        }
    }
    
    

    @Override
    float getSpeed() {
        if(fLiveTime <= .30){
            return 15f;
        }
        return 0;
    }

    @Override
    boolean isFlying() {
        return false;
    }

    @Override
    boolean isJumping() {
        return true;
    }

    @Override
    float getRange() {
        return 2;
    }

    @Override
    float getAttackCooldown() {
        return .5f;
    }

    @Override
    float getDamage() {
        return 1.5f;
    }

    @Override
    float getHealRate() {
        return .1f;
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
        return 30;
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
    boolean isRanged() {
        return false;
    }
    
}
