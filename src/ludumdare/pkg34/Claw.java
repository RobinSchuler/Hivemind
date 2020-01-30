/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Claw extends Entity{

    public Claw(MYMap xMap, boolean enemy, int x, int y) {
        super(xMap, enemy, x, y);
    }
    
    @Override
    String getAnimationName() {
        return "claw";
    }

    @Override
    int getAnimWidht() {
        return 1;
    }

    @Override
    int getAnimHeight() {
        return 2;
    }

    @Override
    int getAnimLength() {
        return 2;
    }


    @Override
    Schuss getProjectile(float y, float x, float xTo, float yTO) {
        return new ScionSchuss(x, y, xTo, yTO);
    }
    
    @Override
    float getSpeed() {
        return 5.5f;
    }

    @Override
    boolean isFlying() {
        return false;
    }

    @Override
    boolean isJumping() {
        return false;
    }

    @Override
    float getRange() {
        return 2;
    }

    @Override
    float getAttackCooldown() {
        return 1.5f;
    }

    @Override
    float getDamage() {
        return 3f;
    }

    @Override
    float getHealRate() {
        return .1f;
    }

    @Override
    float getCloseArmor() {
        return 1;
    }

    @Override
    float getRangedArmor() {
        return 0;
    }

    @Override
    float getMaxLive() {
        return 90;
    }

    @Override
    float getDamagedArea() {
        return 2.5f;
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
