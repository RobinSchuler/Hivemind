/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Glider extends Entity{

    public Glider(MYMap xMap, boolean enemy, int x, int y) {
        super(xMap, enemy, x, y);
    }
    
    @Override
    String getAnimationName() {
        return "glider";
    }

    @Override
    int getAnimWidht() {
        return 2;
    }

    @Override
    Schuss getProjectile(float y, float x, float xTo, float yTO) {
        return new ScionSchuss(x, y, xTo, yTO);
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
    float getSpeed() {
        return 5f;
    }

    @Override
    boolean isFlying() {
        return true;
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
        return 2f;
    }

    @Override
    float getDamage() {
        return 15;
    }

    @Override
    float getHealRate() {
        return 0.75f;
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
        return 3f;
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
