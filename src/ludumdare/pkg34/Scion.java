/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;

 
public class Scion extends  Entity{
    protected Animation[] xAnims = new Animation[9];

    public Scion(MYMap xMap, boolean enemy, int x, int y) {
        //super(new Color(0, 0, 150), xMap, enemy, x, y);
        
        this.x = x+(float)Math.random()/2f;
        this.y = y+(float)Math.random()/2f;
        xAiming = x;
        yAiming = y;
        bEnemy = enemy;
        fLive = getMaxLive();
        iRenderIndex = xMap.addEntity(x, y, enemy, this);
        String sEnemyPregfix = "gegner_";
        if(!bEnemy)
            sEnemyPregfix = "";
        xAnims[0] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-f-L", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[1] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-r-L", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[2] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-l-L", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[3] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-b-L", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[4] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-s", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[5] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-f-A", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[6] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-r-A", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[7] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-l-A", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[8] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-b-A", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
    }

    @Override
    public Animation getAnimation() {
        if(xStatus == Status.STANDING){
            return xAnims[4];
        }
        else if(xStatus == Status.ATTACKING || xStatus == Status.ATTACKINGHAUS){
            switch(xFacing){
                case RIGHT:
                    return xAnims[5];
                case LEFT:
                    return xAnims[6];
                case UP:
                    return xAnims[7];
                default:
                    return xAnims[8];
            }
        }
        else{
            switch(xFacingRender){
                case RIGHT:
                    return xAnims[1];
                case LEFT:
                    return xAnims[2];
                case UP:
                    return xAnims[3];
                default:
                    return xAnims[0];
            }
        }
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
        return 7;
    }

    @Override
    float getAttackCooldown() {
        return 2.5f;
    }

    @Override
    float getDamage() {
        return 50;
    }

    @Override
    float getHealRate() {
        return .5f;
    }

    @Override
    float getCloseArmor() {
        return -1f;
    }

    @Override
    float getRangedArmor() {
        return 6;
    }

    @Override
    float getMaxLive() {
        return 225;
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
        return "scion";
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
        return new ScionSchuss(x, y, xTo, yTO);
    }
    
}
