/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.ScriptReader;
import SimpleGameLibrarry.SimpleGameLibrarry;
import SimpleGameLibrarry.SimpleSoundmanager;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static ludumdare.pkg34.Camera.getWidthOnMap;

 
public class LudumDare34 extends SimpleGameLibrarry{
    
    ScriptReader xGlobalSettings;
    Animation xMouse = new Animation("./res/textures/cursor", 1, 0, 1, true, null, LudumDare34.scaleDown());
    MYMap xMap;
    Camera xCam;
    Basis xMainBasis;
    public Entity[] aAllEntitys = new Entity[400];
    volatile float fXMouse = 0, fYMouse = 0;
    LinkedList<Entity> xSelectedEntetys = new LinkedList<>();
    volatile boolean bDragging = false;
    volatile float fXfromDragging, fYFromDragging;
    Hud xHud = new Hud();
    LinkedList<Spawner> lSpawner = new LinkedList<>();
    float tLooseTime = -500;
    Animation victory = new Animation("./res/textures/victory", 1, 0, 1, true, null, LudumDare34.scaleDown());
    Animation defeat = new Animation("./res/textures/defeat", 1, 0, 1, true, null, LudumDare34.scaleDown());
    static boolean bDefeat = false, bVictory = false;
    int iStartSup = 0;
    public static SimpleSoundmanager xSound = new SimpleSoundmanager(2);
    static int iSpawn = xSound.registerSound(1, "./res/sounds/spawn", false);
    static int iDanger = xSound.registerSound(1, "./res/sounds/danger", false);
    static int iHit = xSound.registerSound(1, "./res/sounds/hit", false);
    static int iClick = xSound.registerSound(1, "./res/sounds/click", false);
    static int iMusik = xSound.registerSound(0, "./res/sounds/Grow", false);
    static int[] iMonsterSounds = new int[] {
        xSound.registerSound(1, "./res/sounds/monster1", false),
        xSound.registerSound(1, "./res/sounds/monster2", false),
        xSound.registerSound(1, "./res/sounds/monster3", false),
        xSound.registerSound(1, "./res/sounds/monster4", false),
        xSound.registerSound(1, "./res/sounds/monster5", false),
        xSound.registerSound(1, "./res/sounds/monster6", false),
    };
    private float fSoundTimer = 10;
    public static boolean bSaeDown = false;

    public LudumDare34(String sLevel,int resolutionX, int resolutionY, int maxFps, int maxTps, float music, float sound, float master, boolean musicB, boolean soundB, boolean masterB, boolean bScaleDown) throws IOException {
        xGlobalSettings = new ScriptReader();
        xSound.setMasterVolumeOn(masterB);
        xSound.setChannelVolumeOn(0, musicB);
        xSound.setChannelVolumeOn(1, soundB);
        xSound.changeMasterVolume(master);
        xSound.changeChanelVolume(0, music);
        xSound.changeChanelVolume(1, sound);
        LudumDare34.bSaeDown = bScaleDown;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!bVictory && !bDefeat) {  
                    xSound.playSound(iMusik);
                    try {
                        Thread.sleep(39160);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LudumDare34.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        xMap = MYMap.loadMap(sLevel, this);
        xMainBasis = new Basis(this, xMap.getxSpawn(), xMap.getySpawn(), xMap.iWinResses);
        for (int i = 0; i < iStartSup; i++) {
            xMainBasis.incSup();
        }
        xMainBasis.iWinCondition = xMap.iFleisch;
        tLooseTime = xMap.fLooseTimer;
        xCam  = new Camera(this,xMap.getxSpawn(),xMap.getySpawn());
        xCam.x2 = xMap.getxSpawn() / xCam.getWidthOnMap() + 1;//- 1/2;
        xCam.y2 = xMap.getySpawn() /xCam.getHeightOnMap() ;//- 1/2;
        start( resolutionX,
            resolutionY,
            xGlobalSettings.loadSetting("fpsCap", maxFps), 
            xGlobalSettings.loadSetting("tpsCap", maxTps),
            "Groth of the Hive", 
            false, 
            Runtime.getRuntime().availableProcessors()-1
        );//function call
    }
    public static float scaleDown(){
        if(bSaeDown)
            return .3f;
        return 1;
    }
    
    public void addEntity(Entity e){
        for (int i = 0; i < aAllEntitys.length; i++) {
            if(aAllEntitys[i] == null){
                aAllEntitys[i] = e;
                return;
            }
        }
    }
    
    public void addHaus(Haus h){
        xMap.getTileContainer( (int)(h.x ), (int)(h.y ) ).addHaus(h);
        if(h instanceof Auswahl)
            return;
        int iRadius = 3;
        for (int i = -iRadius; i <= iRadius; i++) {
            for (int j = -iRadius; j <= iRadius; j++) {
                if(Math.sqrt(i*i+j*j) <= iRadius){
                    try{
                        xMap.getTileContainer( (int)(h.x + h.getWidth()/2 + i), (int)(h.y + h.getHeight()/2 + j)).incFreindly();
                    }catch(IndexOutOfBoundsException e){}
                }
            }
        }
    }
    public void rmHaus(Haus h){
        xMap.getTileContainer( (int)h.x, (int)h.y).rmHaus(h);
        if(h instanceof Auswahl)
            return;
        int iRadius = 3;
        for (int i = -iRadius; i <= iRadius; i++) {
            for (int j = -iRadius; j <= iRadius; j++) {
                if(Math.sqrt(i*i+j*j) <= iRadius){
                    try{
                        xMap.getTileContainer( (int)(h.x + h.getWidth()/2 + i), (int)(h.y + h.getHeight()/2 + j)).decFriendly();
                    }catch(IndexOutOfBoundsException e){}
                }
            }
        }
    }

    @Override
    public void setUpRender() {
        for (Entity aAllEntity : aAllEntitys) {
            if(aAllEntity != null)
                aAllEntity.prepareRender();
        }
        xCam.applyCamMovements();
        drawRect(0, 0, getResolutionFactor(), 1, Color.BLACK);
    }

    @Override
    public void onRender(int iThreadId, int iThreadsMax) {
        xMap.render(this, xCam, iThreadId, iThreadsMax);
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onRenderGui(int iThreadId, int iThreadsMax) {
        if(bDragging && iThreadId == 2%iThreadsMax){
            float a = fXfromDragging*getResolutionFactor();
            float b = fYFromDragging;
            float c = fXMouse*getResolutionFactor();
            float d = fYMouse;
            drawRectOutline(a < c ? a : c, b<d?b:d, a<c?c-a:a-c,  b<d?d-b:b-d, Color.green);
        }
        xMap.renderMap(this, xCam, iThreadId, iThreadsMax);
        xHud.render(this, iThreadId, iThreadsMax);
        drawAnimation(fXMouse*getResolutionFactor(), fYMouse, .02f*getResolutionFactor(), .02f, xMouse);
        if(bVictory){
            drawAnimation(0, 0, getResolutionFactor(), 1, victory);
        }
        if(bDefeat){
            drawAnimation(0, 0, getResolutionFactor(), 1, defeat);
        }
    }
    
    public static void loose(){
        System.out.println("LOOSE");
        if(!bVictory){
            bDefeat = true;
        }
    }
    
    public static void win(){
        System.out.println("WIN");
        if(!bDefeat){
            bVictory = true;
        }
    }

    @Override
    public void onUpdate(float passedTime) {
        fSoundTimer -= passedTime;
        if(fSoundTimer < 0){
            fSoundTimer *= (float)Math.random()*30+10;
            xSound.playSound(iMonsterSounds[(int)(Math.random()*6)]);
        }
        if(tLooseTime > -400){
            tLooseTime -= passedTime;
            if(tLooseTime < 0){
                loose();
            }
        }
        for (Schuss s : xMap.schuesse) {
            if(s != null)
                s.update(passedTime);
        }
        xHud.update(passedTime);
        xMainBasis.update(this, passedTime);
        for (int i = 0; i < aAllEntitys.length; i++) {
            Entity aAllEntity = aAllEntitys[i];
            if(aAllEntity != null)
                aAllEntity.update(passedTime, xMap, xMainBasis, xCam, this);
        }
        for (Spawner lSpawner1 : lSpawner) {
            lSpawner1.update(this, passedTime);
        }
        float fCamMoveX = 0;
        float fCamMoveY = 0;
        if(fXMouse < .02f){
            fCamMoveX -= 1;
        }
        else if(fXMouse > .98f){
            fCamMoveX += 1;
        }
        if(fYMouse < .02f){
            fCamMoveY -= 1;
        }
        else if(fYMouse > .98f){
            fCamMoveY += 1;
        }
        xCam.moveCamX(fCamMoveX*passedTime, this);
        xCam.moveCamY(fCamMoveY*passedTime);
        
    }
    
    boolean bAttackClick = false;

    @Override
    public void onKey(int keyId, boolean pressed) {
       if(keyId == KeyEvent.VK_ESCAPE && !pressed)
           shutdown();
       if(bVictory || bDefeat){
           return;
       }
       if(keyId == KeyEvent.VK_SPACE && !pressed){
            xCam.x2 = xMap.getxSpawn() / xCam.getWidthOnMap() + 1;//- 1/2;
            xCam.y2 = xMap.getySpawn() /xCam.getHeightOnMap() ;//- 1/2;
       }
       if(keyId == KeyEvent.VK_L && !pressed){
            clickedOnBuildButton(2);
       }
       if(keyId == KeyEvent.VK_C && !pressed){
            clickedOnBuildButton(1);
       }
       if(keyId == KeyEvent.VK_K && !pressed){
            clickedOnBuildButton(5);
       }
       if(keyId == KeyEvent.VK_G && !pressed){
            clickedOnBuildButton(6);
       }
       if(keyId == KeyEvent.VK_S && !pressed){
            clickedOnBuildButton(8);
       }
       if(keyId == KeyEvent.VK_E && !pressed){
            clickedOnBuildButton(7);
       }
       if(keyId == KeyEvent.VK_F && !pressed){
            clickedOnBuildButton(3);
       }
       if(keyId == KeyEvent.VK_R && !pressed){
            clickedOnBuildButton(4);
       }
       /*if(keyId == KeyEvent.VK_1 && !pressed){
            for (int i = 0; i < 2; i++) {
                addEntity(new Lurch(xMap, true, 10, 10));
            }
       }
       if(keyId == KeyEvent.VK_2 && !pressed){
            for (int i = 0; i < 1; i++) {
                addEntity(new Claw(xMap, true, 10, 10));
            }
       }
       if(keyId == KeyEvent.VK_3 && !pressed){
            for (int i = 0; i < 1; i++) {
                addEntity(new Cog(xMap, true, 10, 10));
            }
       }
       if(keyId == KeyEvent.VK_4 && !pressed){
            for (int i = 0; i < 1; i++) {
                addEntity(new Glider(xMap, true, 10, 10));
            }
       }
       if(keyId == KeyEvent.VK_5 && !pressed){
            for (int i = 0; i < 1; i++) {
                addEntity(new Eiscle(xMap, true, 10, 10));
            }
       }
       if(keyId == KeyEvent.VK_6 && !pressed){
            for (int i = 0; i < 1; i++) {
                addEntity(new Scion(xMap, true, 10, 10));
            }
       }*/
       if(keyId == KeyEvent.VK_A){
           bAttackClick = true;
       }
    }
    
    public int iBuildMarker = 0;
    public void clickedOnBuildButton(int iD){
        if(xMainBasis.iEnoughRes()){
            xMainBasis.addResources(-10);
            iBuildMarker = iD;
            if(iD == 5 || iD == 6){
                LurchHaus.setDispAuswahl(true);
            }
            else if(iD == 7){
                WihrlwindHaus.setDispAuswahl(true);
            }
            else if(iD == 8){
                EiscleHaus.setDispAuswahl(true);
            }
            else
                Auswahl.setDispAuswahl(true);
        }
    }
    
    public void build(int iID, float mouseX, float mouseY){
        Haus clicked = xMainBasis.get(xCam.screenToMapX(mouseX), xCam.screenToMapY(mouseY));
        Auswahl.setDispAuswahl(false);
        LurchHaus.setDispAuswahl(false);
        WihrlwindHaus.setDispAuswahl(false);
        EiscleHaus.setDispAuswahl(false);
        if(clicked == null){
            xMainBasis.addResources(10);
            return;
        }
        else{
            if(iID == 1 && (clicked instanceof Auswahl)){
                xMainBasis.replaceTile(clicked, new WihrlwindHaus(clicked.x, clicked.y), this);
                xMainBasis.iWhirlWind++;
                return;
            }
            if(iID == 2 && (clicked instanceof Auswahl)){
                xMainBasis.replaceTile(clicked, new LurchHaus(clicked.x, clicked.y), this);
                xMainBasis.iLurch++;
                return;
            }
            if(iID == 3 && (clicked instanceof Auswahl)){
                xMainBasis.replaceTile(clicked, new SuplyHaus(clicked.x, clicked.y), this);
                xMainBasis.iSuply+=5;
                return;
            }
            if(iID == 4 && (clicked instanceof Auswahl)){
                xMainBasis.replaceTile(clicked, new ResourceHaus(clicked.x, clicked.y), this);
                xMainBasis.iResourceSpeed++;
                return;
            }
            if(iID == 5 && (clicked instanceof LurchHaus)){
                xMainBasis.replaceTile(clicked, new CogHaus(clicked.x, clicked.y), this);
                xMainBasis.iLurch--;
                xMainBasis.iCog++;
                return;
            }
            if(iID == 6 && (clicked instanceof LurchHaus)){
                xMainBasis.replaceTile(clicked, new GliderHaus(clicked.x, clicked.y), this);
                xMainBasis.iLurch--;
                xMainBasis.iGlider++;
                return;
            }
            if(iID == 7 && (clicked instanceof WihrlwindHaus)){
                xMainBasis.replaceTile(clicked, new EiscleHaus(clicked.x, clicked.y), this);
                xMainBasis.iWhirlWind--;
                xMainBasis.iEiscle++;
                return;
            }
            if(iID == 8 && (clicked instanceof EiscleHaus)){
                xMainBasis.replaceTile(clicked, new CasterHaus(clicked.x, clicked.y), this);
                xMainBasis.iEiscle--;
                xMainBasis.iCaster++;
                return;
            }
            xMainBasis.addResources(10);
            
        }
    }

    @Override
    public void onMouse(int Id, boolean pressed, int x, int y) {
       if(bVictory || bDefeat){
           return;
       }
        fXMouse = x/(float)getResolutionX();
        fYMouse = y/(float)getResolutionY();
        if(Id == MouseEvent.BUTTON1){
            if(!bAttackClick)
                bDragging = pressed;
            if(pressed){
                if(!bAttackClick){
                    for (Entity xSelectedEntety : xSelectedEntetys) {
                        xSelectedEntety.setSelected(false);
                    }
                    xSelectedEntetys.clear();
                    fXfromDragging = fXMouse;
                    fYFromDragging = fYMouse;
                }
            }
            else{
                if(bAttackClick){
                    for (Entity xSelectedEntety : xSelectedEntetys) {
                        xSelectedEntety.setAttacking(xCam.screenToMapX(fXMouse), xCam.screenToMapY(fYMouse), xMap);
                    }
                }
                else if(iBuildMarker != 0){
                    build(iBuildMarker, fXMouse, fYMouse);
                    iBuildMarker = 0;
                }
                else{
                    int iHud = xHud.iClickedOn(fXMouse, fYMouse, this);
                    if(iHud == 0){
                        LinkedList<Entity> selection = xMap.getNeighbors(
                                xCam.screenToMapX(fXMouse<fXfromDragging?fXMouse:fXfromDragging), 
                                xCam.screenToMapY(fYMouse<fYFromDragging?fYMouse:fYFromDragging), 
                                xCam.screenToMapX(fXMouse>=fXfromDragging?fXMouse:fXfromDragging), 
                                xCam.screenToMapY(fYMouse>=fYFromDragging?fYMouse:fYFromDragging));
                        //System.out.println(selection.size());
                        for (Iterator<Entity> iterator = selection.iterator(); iterator.hasNext();) {
                            Entity next = iterator.next();
                            if(next.bEnemy){
                                iterator.remove();
                            }
                            else
                                next.setSelected(true);
                        }
                        xSelectedEntetys = selection;
                    }
                    else{
                        clickedOnBuildButton(iHud);
                    }
                }
            }
        }
        else if(Id == MouseEvent.BUTTON3 && !bAttackClick){
            for (Entity xSelectedEntety : xSelectedEntetys) {
                xSelectedEntety.setMoving(xCam.screenToMapX(fXMouse), xCam.screenToMapY(fYMouse), xMap);
            }
        }
        if(!pressed)
            bAttackClick = false;
    }

    @Override
    public void onMouseWheel(int ticksRotated) {
    }

    @Override
    public void onMouseMove(int x, int y, int movex, int movey) {
        fXMouse = x/(float)getResolutionX();
        fYMouse = y/(float)getResolutionY();
    }

    @Override
    public float light() {
        return 0.2f;
    }
    
    
    public void addSpawner(ScriptReader xScript, int x, int y){
        lSpawner.add(new Spawner(xScript.loadSetting("initialTimeToSpawn", 1f)+1f, xScript.loadSetting("timeBetweenSpawns", 10f), 
                xScript.loadSetting("unitAmount", 0), xScript.loadSetting("numSpawns", 1), xScript.loadSetting("friendly", false), xScript.loadSetting("type", "lurch"), 
                xScript.loadSetting("message", ""), xScript.loadSetting("triggerOnVisibility", false),
                xScript.loadSetting("amountadder", 0),
                x,y));
    }
    
    
}
