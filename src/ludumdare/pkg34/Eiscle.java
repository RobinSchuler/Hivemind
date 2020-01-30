/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Eiscle extends Entity{

    public Eiscle(MYMap xMap, boolean enemy, int x, int y) {
        super(xMap, enemy, x, y);
    }

    @Override
    boolean isRanged() { 
        return true;
    }

    @Override
    Schuss getProjectile(float x, float y, float xTo, float yTO) {
        return new EiscleSchuss(x, y, xTo, yTO);
    }
    
    @Override
    String getAnimationName() {
        return "ice";
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
    float getSpeed() {
        return 4f;
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
        return 9;
    }

    @Override
    float getAttackCooldown() {
        return 1f;
    }

    @Override
    float getDamage() {
        return 9;
    }

    @Override
    float getHealRate() {
        return 0.02f;
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
        return 210;
    }

    @Override
    float getDamagedArea() {
        return 3;
    }

    @Override
    boolean hasSlow() {
        return true;
    }
    
}
