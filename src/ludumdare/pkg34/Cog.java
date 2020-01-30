/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Cog extends Entity{

    public Cog(MYMap xMap, boolean enemy, int x, int y) {
        super( xMap, enemy, x, y);
    }
    
    @Override
    float getSpeed() {
        return 4.5f;
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
        return 6;
    }

    @Override
    float getAttackCooldown() {
        return 1f;
    }

    @Override
    float getDamage() {
        return 7f;
    }

    @Override
    float getHealRate() {
        return .25f;
    }

    @Override
    float getCloseArmor() {
        return 0;
    }

    @Override
    float getRangedArmor() {
        return 6.5f;
    }

    @Override
    float getMaxLive() {
        return 150;
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
        return true;
    }
    
    @Override
    String getAnimationName() {
        return "kog";
    }

    @Override
    int getAnimWidht() {
        return 2;
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
    Schuss getProjectile(float x, float y, float xTo, float yTO) {
        return new cogSchusss(x, y, xTo, yTO);
    }
    
}
