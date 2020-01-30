/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.SimpleGameLibrarry;
import java.awt.Color;
import java.util.LinkedList;

 
public abstract class Entity {
    public static final boolean DEBUG_PUSH = false;
    private final float fPushSpeed = 1f;
    protected enum Status{
        STANDING,
        UP,
        DOWN,
        LEFT,
        RIGHT,
        ATTACKING,
        ATTACKINGHAUS
    }
    protected Animation[] xAnims = new Animation[5];
    protected volatile float x = 0, y = 0, xAiming = 0, yAiming = 0, xRender = 0, yRender = 0;
    protected volatile Status xStatus = Status.UP, xFacing = Status.DOWN, xFacingRender = Status.DOWN;
    protected volatile boolean bEnemy = false, bSelected = false;
    int iRenderIndex = 0;
    volatile float xPush = 0, yPush = 0, fDangerTimer = 0;
    private Entity xAttacking = null;
    private Haus xhAttacking = null;
    protected float fLive, fAttackCooldown, fSlowtime;
    private int iAttack = 0;
    LinkedList<int[]> lWalkList = new LinkedList<>();

    public Entity() {
    }
    
    
    
    
    

    public Entity(MYMap xMap, boolean enemy, int x, int y) {
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
        xAnims[0] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-f", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[1] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-r", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[2] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-l", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[3] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-b", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
        xAnims[4] = new Animation("./res/textures/" + sEnemyPregfix + getAnimationName() + "-s", getAnimLength(), 0, getAnimSpeed(), true, null, LudumDare34.scaleDown());
    }
    
    public Color getMiniMapColor(){
        if(fAttackCooldown < .2f && fAttackCooldown > 0){
            return Color.ORANGE;
        }
        if(bEnemy)
            return Color.RED;
        return Color.GREEN;
    }
    
    private volatile boolean onMoveCOmmand = false;
    abstract  String getAnimationName();
    abstract int getAnimWidht();
    abstract int getAnimHeight();
    abstract int getAnimLength();
    abstract float getRange();
    float getAnimSpeed(){
        return 1f;
    }
    abstract float getAttackCooldown();
    abstract float getDamage();
    abstract float getHealRate();
    abstract float getCloseArmor();
    abstract float getRangedArmor();
    abstract float getMaxLive();
    abstract float getDamagedArea();
    abstract boolean hasSlow();
    boolean isRanged(){
        return false;
    }
    static float getRadius(){return 1;}
    abstract float getSpeed();
    abstract boolean isFlying();
    abstract boolean isJumping();
    abstract Schuss getProjectile(float y, float x, float xTo, float yTO);
    public boolean isAlive(){
        return fLive > 0;
    }
    public float getActualSpeed(){
        if(fSlowtime > 0){
            return getSpeed()/2;
        }
        return getSpeed();
    }
    
    public boolean canAttack(Entity e){
        return isRanged() || !e.isFlying();
    }
    
    public Animation getAnimation(){
        if(xStatus == Status.STANDING){
            return xAnims[4];
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
    
    public void render(SimpleGameLibrarry xLib, Camera xCam){
        try{
            float x = xRender;
            float y = yRender;
            if(!isAlive())
                return;
            if(x + getRadius() > xCam.getXOnMap()/xLib.getResolutionFactor() && y + getRadius() > xCam.getYOnMap() && x < xCam.getWidthOnMap() + xCam.getXOnMap()/xLib.getResolutionFactor() && y < xCam.getHeightOnMap() + xCam.getYOnMap()){
                if(bSelected)
                    xLib.drawCircleOutline(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), Color.green);
                if(bEnemy)
                    xLib.drawCircleOutline(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), Color.red);
                if(fAttackCooldown <= .2f && fAttackCooldown > 0){
                    xLib.drawCircleOutline(xCam.mapToScreenX(x), xCam.mapToScreenY(y), getRadius() * xLib.getResolutionFactor() / xCam.getWidthOnMap(), getRadius() / xCam.getHeightOnMap(), Color.orange);
                }
                xLib.drawAnimation(
                        xCam.mapToScreenX( x - (getAnimWidht()-1)*getRadius()/2), 
                        xCam.mapToScreenY(y - (getAnimHeight()-1)*getRadius() ), 
                        getRadius() * xLib.getResolutionFactor() * getAnimWidht() / xCam.getWidthOnMap(), 
                        getRadius() * getAnimHeight() / xCam.getHeightOnMap(), 
                        getAnimation());
                if(fLive < getMaxLive()){
                    xLib.drawRect(xCam.mapToScreenX(x), xCam.mapToScreenY(y- (getAnimHeight()-1)*getRadius()) - (getRadius()*.1f/xCam.getHeightOnMap()), getRadius() * xLib.getResolutionFactor()*getMaxLive() / (xCam.getWidthOnMap()*getMaxLive()), getRadius()*.1f/xCam.getHeightOnMap(), Color.RED);
                    xLib.drawRect(xCam.mapToScreenX(x), xCam.mapToScreenY(y- (getAnimHeight()-1)*getRadius()) - (getRadius()*.1f/xCam.getHeightOnMap()), getRadius() * xLib.getResolutionFactor()*fLive / (xCam.getWidthOnMap()*getMaxLive()), getRadius()*.1f/xCam.getHeightOnMap(), new Color(0, 225, 0));
                }
            }
            if(DEBUG_PUSH){
                xLib.drawLine(xCam.mapToScreenX(getCenterX()), xCam.mapToScreenY(getCenterY()), xCam.mapToScreenX(getCenterX()+xPush*10), xCam.mapToScreenY(getCenterY()+yPush*10), Color.MAGENTA);
            }
        }catch(NullPointerException e){}
    }
    
    public void damage(Entity attacker, MYMap xMap, Basis xB){
        if(!bEnemy && fDangerTimer < 0){
            fDangerTimer = 10f;
            LudumDare34.xSound.playSound(LudumDare34.iDanger);
        }
        LinkedList<Entity> xEntitys = xMap.getNeighbors(getCenterX(), getCenterY(), attacker.getDamagedArea());
        if(attacker.getDamagedArea() == 0){
            xEntitys.add(this);
        }
        for (Entity xEntity : xEntitys) {
            if(xEntity != null && xEntity.bEnemy == this.bEnemy && (!xEntity.isFlying() || this == xEntity) ){
                float fDamageDealt = attacker.getDamage();
                if(attacker.isRanged()){
                    fDamageDealt -= getRangedArmor();
                }
                else{
                    fDamageDealt -= getCloseArmor();
                }
                if(attacker.hasSlow()){
                    fSlowtime = 3;
                }
                if(!attacker.hasSlow() || this == xEntity){
                    xEntity.fLive -= fDamageDealt;
                    if(xEntity.fLive <= 0){
                        xMap.entityDied((int)xEntity.x, (int)xEntity.y, xEntity.bEnemy, xEntity.iRenderIndex, xEntity);
                        if(!xEntity.bEnemy){
                            xB.decSup();
                        }
                        if(xEntity instanceof Fleisch && !((Fleisch)xEntity).bCounted){
                            ((Fleisch)xEntity).bCounted = true;
                            xB.iWinCondition--;
                            if(xB.iWinCondition <= 0){
                                LudumDare34.win();
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void prepareRender(){
        xRender = x;
        yRender = y;
        xFacingRender = xFacing;
    }
    
    public void update(float fTime, MYMap xMap, Basis xB, Camera xCam, LudumDare34 xLib){
        if(!isAlive())
            return;
        fLive += getHealRate()*fTime;
        fDangerTimer -= fTime;
        if(fLive > getMaxLive()){
            fLive = getMaxLive();
        }
        fAttackCooldown -= fTime;
        if(xStatus == Status.ATTACKING && fAttackCooldown + fTime > .5f && fAttackCooldown <= .5f && isRanged()){
            xMap.addSchuss(getProjectile(getCenterX(), getCenterY(), xAttacking.getCenterX(), xAttacking.getCenterY()));
        }
        else if(xStatus == Status.ATTACKINGHAUS && fAttackCooldown + fTime > .5f && fAttackCooldown <= .5f && isRanged()){
            xMap.addSchuss(getProjectile(getCenterX(), getCenterY(), xhAttacking.x, xhAttacking.y));
        }
        fSlowtime -= fTime;
        float xOld = x;
        float yOld = y;
        if(xStatus == Status.ATTACKING){
            if(xAttacking == null || !xAttacking.isAlive() || distance(xAttacking) > getRange()){
                xAttacking = null;
                xStatus = Status.STANDING;
            }
            else{
                if(fAttackCooldown < 0){
                   // if((iAttack++)%15 == 0 && x + getRadius() > xCam.getXOnMap()/xLib.getResolutionFactor() && y + getRadius() > xCam.getYOnMap() && x < xCam.getWidthOnMap() + xCam.getXOnMap()/xLib.getResolutionFactor() && y < xCam.getHeightOnMap() + xCam.getYOnMap())
                    //    LudumDare34.xSound.playSound(LudumDare34.iHit);
                    fAttackCooldown = getAttackCooldown();
                    xAttacking.damage(this, xMap, xB);
                }
            }
        }
        if(xStatus == Status.ATTACKINGHAUS){
            if(xhAttacking == null || !xhAttacking.isAlive() || distance(xhAttacking.x, xhAttacking.y) > getRange()){
                xhAttacking = null;
                xStatus = Status.STANDING;
            }
            else{
                if(fAttackCooldown < 0){
                    fAttackCooldown = getAttackCooldown();
                    xhAttacking.damage(getDamage());
                }
            }
        }
        if( !onMoveCOmmand && xStatus != Status.ATTACKING && (( xMap.getTileContainer((int)x, (int)y).containsEnemy() && !bEnemy ) || ( xMap.getTileContainer((int)x, (int)y).containsFriend()&& bEnemy ))){
            for (int i = (int)(x-MYMap.fAggroRange); i <= x+MYMap.fAggroRange; i++) {
                for (int j = (int)(y-MYMap.fAggroRange); j <= y+MYMap.fAggroRange; j++) {
                    if(i >= 0 && j >= 0 && i < xMap.aaMap.length && j < xMap.aaMap[i].length && xMap.aaMap[i][j] != null){
                        Entity closest = null;
                        Haus hClosest = null;
                        float hDist = MYMap.fAggroRange+10;
                        float fDist = MYMap.fAggroRange+10;
                        for (Entity col : xMap.getTileContainer(i, j).getEntitys()) {
                            if(col != null && col.bEnemy != bEnemy && canAttack(col)){
                                float fNewDist = col.distance(this);
                                if(fNewDist < fDist){
                                    fDist = fNewDist;
                                    closest = col;
                                }
                            }
                        }
                        for (Haus aHau : xMap.getTileContainer(i, j).getaHaus()) {
                            if(aHau != null && bEnemy && !(aHau instanceof Auswahl)){
                                float fNewDist = distance(aHau.x, aHau.y);
                                if(fNewDist < hDist){
                                    hDist = fNewDist;
                                    hClosest = aHau;
                                }
                            }
                        }
                        if(fDist <= getRange()){
                            xStatus = Status.ATTACKING;
                            xAttacking = closest;
                        }
                        else if(fDist < MYMap.fAggroRange){
                            xAiming = closest.x;
                            yAiming = closest.y;
                            xStatus = Status.DOWN;
                        }
                        else if(hDist <= getRange()){
                            xStatus = Status.ATTACKINGHAUS;
                            xhAttacking = hClosest;
                        }
                        else if(hDist < MYMap.fAggroRange){
                            xAiming = hClosest.x;
                            yAiming = hClosest.y;
                            xStatus = Status.DOWN;
                        }
                    }
                }
            }
        }
        if(xStatus != Status.STANDING && xStatus != Status.ATTACKING && xStatus != Status.ATTACKINGHAUS){
            float fSpeedx = (xAiming + getRadius()/2 - x);
            float fSpeedy = (yAiming + getRadius()/2 - y);
            float fToNormalize = (float) Math.sqrt(fSpeedx*fSpeedx+ fSpeedy*fSpeedy);
            xPush = (fSpeedx / fToNormalize)* getActualSpeed();
            yPush = (fSpeedy / fToNormalize)* getActualSpeed();
            if(fToNormalize > 1){
                x += (fSpeedx / fToNormalize) * fTime * getActualSpeed();
                y += (fSpeedy / fToNormalize) * fTime * getActualSpeed();
            }
            else{
                x += (fSpeedx ) * fTime * getActualSpeed();
                y += (fSpeedy ) * fTime * getActualSpeed();
            }
        }
        LinkedList<Entity> list = xMap.getNeighbors(getCenterX(), getCenterY(), getRadius());
        if(list.size()>1){
            Entity closest = null;
            float fDist = 100;
            for (Entity list1 : list) {
                if(list1 != this && list1.distance(this) < fDist && list1.isFlying() == isFlying()){
                    closest = list1;
                    fDist = closest.distance(this);
                }
            }
            if(closest != null){
                float fSpeedx = getCenterX()-closest.getCenterX();
                float fSpeedy = getCenterY()-closest.getCenterY();
                float fToNormalize = (float) Math.sqrt(fSpeedx*fSpeedx+ fSpeedy*fSpeedy);
                xPush += (fSpeedx / fToNormalize) * fPushSpeed;
                yPush += (fSpeedy / fToNormalize) * fPushSpeed;
                if(fToNormalize != 0){
                    x += (fSpeedx / fToNormalize) * fTime * fPushSpeed;
                    y += (fSpeedy / fToNormalize) * fTime * fPushSpeed;
                }
            }
        }
        if(xStatus != Status.ATTACKING && xStatus != Status.ATTACKINGHAUS && Math.abs(x - xOld) - .1f*fTime > Math.abs(y - yOld)){
            if(x - xOld - .1f*fTime > 0){
                xStatus = Status.RIGHT;
                xFacing = Status.RIGHT;
            }
            else if( xOld - x - .1f*fTime > 0){
                xStatus = Status.LEFT;
                xFacing = Status.LEFT;
            }
        }
        else if(xStatus != Status.ATTACKING  && xStatus != Status.ATTACKINGHAUS && Math.abs(y - yOld) - .1f*fTime > Math.abs(x - xOld)){
            if(y - yOld -.1f*fTime > 0){
                xStatus = Status.DOWN;
                xFacing = Status.DOWN;
            }
            else if( yOld - y - 1.f*fTime > 0){
                xStatus = Status.UP;
                xFacing = Status.UP;
            }
        }
        if(!lWalkList.isEmpty() && Math.abs(x - xAiming) < getRadius()*1.2f && Math.abs(y - yAiming) <  getRadius()*1.2f){
            xAiming = lWalkList.peek()[0];
            yAiming = lWalkList.poll()[1];
            if(xStatus != Status.ATTACKING && xStatus != Status.ATTACKINGHAUS)
                xFacing = Status.DOWN;
        }
        if(xStatus != Status.ATTACKING && xStatus != Status.ATTACKINGHAUS && Math.abs(x - xAiming) < getRadius() && Math.abs(y - yAiming) <  getRadius()){
            xStatus = Status.STANDING;
            if(lWalkList.isEmpty())
                onMoveCOmmand = false;
        }
        int iPushDir = xMap.iPushDir(isFlying(), isJumping(), x, y, xOld, yOld);
        if(iPushDir == 1){
            x = xOld;
        }
        if(iPushDir == 2){
            y = yOld;
        }
        if(iPushDir == 3){
            x = xOld;
            y = yOld;
        }
        if( ((int)xOld) != ((int)x) || ((int)yOld) != ((int)y) ){
            iRenderIndex = xMap.entityMoved((int)xOld, (int)yOld, (int)x - (int)xOld, (int)y - (int)yOld, bEnemy, iRenderIndex, this);
        }
    }
    
    float getCenterX(){
        return x + getRadius()/2;
    }
    float getCenterY(){
        return y+ getRadius()/2;
    }
    
    public void updatePart2(float fTime, MYMap xMap){
        
    }
    
    public float distance(Entity e){
        float xDist = e.getCenterX() - getCenterX();
        float yDist = e.getCenterY() - getCenterY();
        return (float)Math.sqrt(xDist*xDist + yDist*yDist);
    }
    
    public float distance(float x, float y){
        float xDist = x - getCenterX();
        float yDist = y - getCenterY();
        return (float)Math.sqrt(xDist*xDist + yDist*yDist);
    }
    
    public boolean isIn(float x, float y, float xTo, float yTo){
        return this.x + getRadius()  > x && this.y + getRadius() > y && this.x < xTo && this.y < yTo;
    }

    public void setSelected(boolean bSelected) {
        this.bSelected = bSelected;
    }
    
    public void pathfind(float x, float y, MYMap xMap){
        int[][] iDis = new int[xMap.aaMap.length][xMap.aaMap[0].length];
        LinkedList<int[]> lToWorkNext = new LinkedList<>();
        lToWorkNext.add(new int[] {(int)x,(int)y,(int)Integer.MIN_VALUE});
        while (!lToWorkNext.isEmpty()) {            
            int[] iCurr = lToWorkNext.pop();
            if(iCurr[0] >= 0 && iCurr[1] >= 0 && iCurr[0] < xMap.aaMap.length && iCurr[1] < xMap.aaMap[iCurr[0]].length &&
                    ( iDis[iCurr[0]][iCurr[1]] > iCurr[2]) && xMap.aaMap[ iCurr[0]][ iCurr[1]].issPassable(isFlying(), isJumping()) && 
                    (bEnemy || xMap.aaMap[iCurr[0]][iCurr[1]].getFogOfWar() != -1)){
                iDis[iCurr[0]][iCurr[1]] = iCurr[2];
                lToWorkNext.add(new int[] {iCurr[0]+1,iCurr[1],iCurr[2]+1});
                lToWorkNext.add(new int[] {iCurr[0]-1,iCurr[1],iCurr[2]+1});
                lToWorkNext.add(new int[] {iCurr[0],iCurr[1]+1,iCurr[2]+1});
                lToWorkNext.add(new int[] {iCurr[0],iCurr[1]-1,iCurr[2]+1});
            }
        }
        int xNow = (int)getCenterX();
        int yNow = (int)getCenterY();
        while (xNow != (int)x || yNow != (int)y) { 
            iDis[xNow][yNow] = Integer.MAX_VALUE;
            int iMinDis = Integer.MAX_VALUE;
            int iDir = 0;
            
            if(xNow-1 >= 0 && yNow >= 0 && xNow-1 < xMap.aaMap.length && yNow < xMap.aaMap[xNow-1].length && iDis[xNow-1][yNow] < iMinDis){
                iMinDis = iDis[xNow-1][yNow];
                iDir = 1;
            }
            if(xNow+1 >= 0 && yNow >= 0 && xNow+1 < xMap.aaMap.length && yNow < xMap.aaMap[xNow+1].length && iDis[xNow+1][yNow] < iMinDis){
                iMinDis = iDis[xNow+1][yNow];
                iDir = 2;
            }
            if(xNow >= 0 && yNow-1 >= 0 && xNow < xMap.aaMap.length && yNow-1 < xMap.aaMap[xNow].length && iDis[xNow][yNow-1] < iMinDis){
                iMinDis = iDis[xNow][yNow-1];
                iDir = 3;
            }
            if(xNow >= 0 && yNow+1 >= 0 && xNow < xMap.aaMap.length && yNow+1 < xMap.aaMap[xNow].length && iDis[xNow][yNow+1] < iMinDis){
                iMinDis = iDis[xNow][yNow+1];
                iDir = 4;
            }
            if(iDir == 1){
                lToWorkNext.add(new int[] {xNow-1,yNow});
                xNow--;
            }
            if(iDir == 2){
                lToWorkNext.add(new int[] {xNow+1,yNow});
                xNow++;
            }
            if(iDir == 3){
                lToWorkNext.add(new int[] {xNow,yNow-1});
                yNow--;
            }
            if(iDir == 4){
                lToWorkNext.add(new int[] {xNow,yNow+1});
                yNow++;
            }
            if(iDir == 0){
                break;
            }
        }
        
        lWalkList = lToWorkNext;
        if(!lToWorkNext.isEmpty()){
            xAiming = lToWorkNext.peek()[0];
            yAiming = lToWorkNext.poll()[1];
        }
    }
    
    public void setMoving(float x, float y, MYMap xMap){
        xAiming = x;
        yAiming = y;
        lWalkList.clear();
        //pathfind(x,y,xMap);
        xStatus = Status.DOWN;
        onMoveCOmmand = true;
    }
    
    public void setAttacking(float x, float y, MYMap xMap){
        xAiming = x;
        yAiming = y;
        lWalkList.clear();
        //pathfind(x,y,xMap);
        xStatus = Status.DOWN;
        onMoveCOmmand = false;
    }
    
    public void setAttackingAnPathing(float x, float y, MYMap xMap){
        xAiming = x;
        yAiming = y;
        pathfind(x,y,xMap);
        xStatus = Status.DOWN;
        onMoveCOmmand = false;
    }
    
}
