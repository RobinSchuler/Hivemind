/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import SimpleGameLibrarry.Map;
import SimpleGameLibrarry.ScriptReader;
import SimpleGameLibrarry.SimpleGameLibrarry;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

 
public class MYMap {
    public static final float fAggroRange = 10;
    TileContainer[][] aaMap;
    private int xSpawn = 25, ySpawn = 25;
    public int iWinResses = 100;
    public float fLooseTimer = 3*60;
    Animation xMiniMap, xMiniMapOverlay;
    public Schuss[] schuesse = new Schuss[20];
    int iShusAddCounter = 0;
    BufferedImage xMapOverlay;
    public int iFleisch = 0;

    public MYMap(int iWidth, int iHeight, BufferedImage iMiniMap) {
        if(iWidth < 50){
            iWidth = 50;
        }
        if(iHeight < 50){
            iHeight = 50;
        }
        aaMap = new TileContainer[iWidth][iHeight];
        for (int i = 0; i < aaMap.length; i++) {
            for (int j = 0; j < aaMap[i].length; j++) {
                aaMap[i][j] = new TileContainer(null, new Tile[0], TileContainer.Passability.UNPASSABLE);
            }
        }
        xMiniMap = new Animation(new BufferedImage[] {iMiniMap}, "minimap", 1, true, null);
        xMapOverlay = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_ARGB);
        xMiniMapOverlay = new Animation(new BufferedImage[] {xMapOverlay}, "minimapOverlay", 1, true, null);
        
        Graphics g = xMapOverlay.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, iWidth, iHeight);
    }
    
    /*private int getDir(float xYou, float yYou, float xO, float yO){
        if(Math.abs(xYou - xO) > Math.abs(yYou - yO)){
            if(xYou > xO)
                return 2;
            else
                return 4;
        }
        else{
            if(yYou > yO)
                return 3;
            else
                return 1; 
        }
    }*/
    
    /**
     * 1: resetx ; 2:resety
     */
    public int iPushDir(boolean flying,boolean jumping, float x, float y, float x2, float y2){
        if(x < 0 || y < 0 || x >= aaMap.length || y >= aaMap[(int)x].length || !aaMap[(int)x][(int)y].issPassable(flying, jumping))
            if(x2 >= 0 && y >= 0 && x2 < aaMap.length && y > aaMap[(int)x2].length && aaMap[(int)x2][(int)y].issPassable(flying, jumping))
                return 1;
            else if(x >= 0 && y2 >= 0 && x < aaMap.length && y2 < aaMap[(int)x].length && aaMap[(int)x][(int)y2].issPassable(flying, jumping))
                return 2;
            else
                return 3;
        return 0;
        /*for (int i = (int)(+x); i <=  x2; i++) {
            for (int j = (int)(+y); j <=y2; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[(int)i].length && aaMap[(int)i][(int)j] != null){
                    if(!aaMap[(int)i][(int)j].issPassable(flying, jumping))
                        return getDir(x, y, i + 1 / Camera.getWidthOnMap(), j + 1 / Camera.getHeightOnMap());
                }
                else
                        return getDir(x, y, i + 1 / Camera.getWidthOnMap(), j + 1 / Camera.getHeightOnMap());
            }
        }*/
    }
    
    public static MYMap loadMap(String sFile, LudumDare34 xLib) throws IOException{
        Map xLibMap = Map.loadMap(sFile);
        BufferedImage xIm = new BufferedImage(xLibMap.getWidht(), xLibMap.getLenght(), BufferedImage.TYPE_INT_ARGB);
        MYMap xRet = new MYMap(xLibMap.getWidht(), xLibMap.getLenght(), xIm);
        xRet.iWinResses = xLibMap.getMapSettings().loadSetting("winresources", 100);
        xRet.fLooseTimer = xLibMap.getMapSettings().loadSetting("losetimer", 3*60);
        xLib.tLooseTime = xLibMap.getMapSettings().loadSetting("timer", 60*5);
        for (int i = 0; i < xLibMap.getWidht(); i++) {
            for (int j = 0; j < xLibMap.getLenght(); j++) {
                int k = 0;
                Animation xAnim = null;
                TileContainer.Passability xPassable = TileContainer.Passability.WALKABLE;
                boolean bSthSaved = false;
                while (k < xLibMap.getHeight(i, j) && xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Background", false)) {
                    int[] aMinimapColor = xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Rgba", new int[] {0,0,0,0});
                    Color xMinimap = new Color(aMinimapColor[0], aMinimapColor[1], aMinimapColor[2] , aMinimapColor[3]);
                    Graphics g = xIm.getGraphics();
                    g.setColor(xMinimap);
                    g.fillRect(i, j, 1, 1);
                    xPassable = TileContainer.addPassableString(xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_passability", "walkable"), xPassable);
                    if(xAnim == null && xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName(), new String[] {"circle"})[0].equals("textured")){
                        xAnim = new Animation(
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Texture", "null"), 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_NumFrames", 1), 0, 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_AnimLength", 1.0f), true, null, LudumDare34.scaleDown());
                        bSthSaved = true;
                    }
                    else if(xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName(), new String[] {"circle"})[0].equals("textured")){
                        Animation xAnim2 = new Animation(
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Texture", "null"), 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_NumFrames", 1), 0, 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_AnimLength", 1.0f), true, null, LudumDare34.scaleDown()); 
                        bSthSaved = true;
                        xAnim.overlayWith(xAnim2);
                    }
                    
                    k++;
                }
                
                Tile[] aRemainingTiles = new Tile[xLibMap.getHeight(i, j) - k];
                int iIndex = k;
                
                while (k < xLibMap.getHeight(i, j)) { 
                    if(xLibMap.getMapTile(i, j, k).getName().equals("spawn")){
                        xRet.xSpawn = i;
                        xRet.ySpawn = j;
                        k++;
                        continue;
                    }
                    ScriptReader xScript = new ScriptReader();
                    xScript.loadNonFileScript(xLibMap.getMapTile(i, j, k).getData());
                    if(xLibMap.getMapTile(i, j, k).getName().equals("lurch")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Lurch(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("claw")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Claw(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("kog")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Cog(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("glider")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Glider(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("eiscle")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Eiscle(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("scion")){
                        if(!xScript.loadSetting("Enemy", true))
                            xLib.iStartSup++;
                        xLib.addEntity(new Scion(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        k++;
                        continue;
                    }
                    if(xLibMap.getMapTile(i, j, k).getName().equals("food")){
                        xLib.addEntity(new Fleisch(xRet, xScript.loadSetting("Enemy", true), (int)i, (int)j));
                        xRet.iFleisch++;
                        k++;
                        continue;
                    }
                    if(xScript.loadSetting("spawner", false)){
                        xLib.addSpawner(xScript,i,j);
                        k++;
                        continue;
                    }
                    int[] aMinimapColor = xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Rgba", new int[] {0,0,0,0});
                    Color xMinimap = new Color(aMinimapColor[0], aMinimapColor[1], aMinimapColor[2] , aMinimapColor[3]);
                    Graphics g = xIm.getGraphics();
                    g.setColor(xMinimap);
                    g.fillRect(i, j, 1, 1);
                    xPassable = TileContainer.addPassableString(xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_passability", "walkable"), xPassable);
                    if( xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName(), new String[] {"circle"})[0].equals("textured")){
                        Animation xAnimLocal = new Animation(
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_Texture", "null"), 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_NumFrames", 1), 0, 
                                xLibMap.getMapSettings().loadSetting(xLibMap.getMapTile(i, j, k).getName() + "_AnimLength", 1.0f), true, null, LudumDare34.scaleDown());              
                        aRemainingTiles[k - iIndex] = new Tile(xAnimLocal);
                        bSthSaved = true;
                    }
                    k++;
                }
                
                if(!bSthSaved)
                    xPassable = TileContainer.Passability.UNPASSABLE;
                int iEnemy = xRet.aaMap[i][j].iEnemyCount;
                int iFriendly = xRet.aaMap[i][j].iFriendlyCount;
                Entity[] aOldEntitys = xRet.aaMap[i][j].aEntitys;
                
                xRet.aaMap[i][j] = new TileContainer(new Tile(xAnim), aRemainingTiles, xPassable);
                for (int l = 0; l < iEnemy; l++) {
                    xRet.aaMap[i][j].incEnemy();
                }
                for (int l = 0; l < iFriendly; l++) {
                    xRet.aaMap[i][j].incFreindly();
                }
                xRet.aaMap[i][j].aEntitys = aOldEntitys;
            }
        }
            System.out.println(xRet.iFleisch + " " + (int)(xRet.iFleisch  * xLibMap.getMapSettings().loadSetting("dnapercentage", .75f)));
            xRet.iFleisch = (int)(xRet.iFleisch  * xLibMap.getMapSettings().loadSetting("dnapercentage", .75f));
        
        return xRet;
    }
    
    public void render(SimpleGameLibrarry xLib, Camera xCam, int iThread, int iThreadsMax){
        for (int i = (int)(xCam.getXOnMap()/xLib.getResolutionFactor()) - 1; i < xCam.getXOnMap()/xLib.getResolutionFactor() + xCam.getWidthOnMap() + 2; i++) {
            for (int j = (int)(xCam.getYOnMap()) - 1; j < xCam.getYOnMap()+ xCam.getHeightOnMap() + 2; j++) {
                if( (i * aaMap.length + j)%iThreadsMax == iThread && i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length ){
                        xLib.drawRect(xCam.mapToScreenX(i), xCam.mapToScreenY(j), xCam.getTileWidth(),  xCam.getTileHeight(), Color.black);
                    if(aaMap[i][j] != null)
                        aaMap[i][j].render(xLib, xCam.mapToScreenX(i), xCam.mapToScreenY(j), xCam.getTileWidth(),  xCam.getTileHeight(), xCam, this, i, j);
                    else
                        xLib.drawRect(xCam.mapToScreenX(i), xCam.mapToScreenY(j), xCam.getTileWidth(),  xCam.getTileHeight(), Color.black);
                }
            }
        }
    }
    
    public void renderMap(LudumDare34 xLib, Camera xCam, int iThread, int iThreadsMax){
        
        
        
        float xMap = (1-.26f)*xLib.getResolutionFactor(), yMap = 1-.3061f, widthMap = .26f*xLib.getResolutionFactor(), heightMap = .3061f;
        
        
        
        
        if(iThread == 7%iThreadsMax){
            xLib.drawAnimation(xMap, yMap, widthMap, heightMap, xMiniMap);
            xLib.drawAnimation(xMap, yMap, widthMap, heightMap, xMiniMapOverlay);
            /*for (int i = 0; i < xLib.xMap.aaMap.length; i++) {
                for (int j = 0; j < xLib.xMap.aaMap[i].length; j++) {
                    if(xLib.xMap.aaMap[i][j].getFogOfWar() == -1){
                        xLib.drawRect(xMap + i *widthMap/xLib.xMap.aaMap.length , yMap + j *heightMap/xLib.xMap.aaMap[i].length, widthMap/xLib.xMap.aaMap.length, heightMap/xLib.xMap.aaMap[i].length, Color.black);
                    }
                    else if(xLib.xMap.aaMap[i][j].getFogOfWar() == 0){
                        xLib.drawRect(xMap + i *widthMap/xLib.xMap.aaMap.length, yMap + j *heightMap/xLib.xMap.aaMap[i].length, widthMap/xLib.xMap.aaMap.length, heightMap/xLib.xMap.aaMap[i].length, TileContainer.xFogofWar);
                    }
                }
            }*/
            for (int i = 0; i < xLib.aAllEntitys.length; i++) {
                try{
                    if(xLib.aAllEntitys[i] != null && xLib.xMap.aaMap[(int)xLib.aAllEntitys[i].getCenterX()][(int)xLib.aAllEntitys[i].getCenterY()].containsFriend()){
                        xLib.drawCircle(xMap + xLib.aAllEntitys[i].x*widthMap/aaMap.length, 
                                yMap+ xLib.aAllEntitys[i].y*heightMap/aaMap[0].length, aaMap.length/widthMap, aaMap[0].length/heightMap, 
                                xLib.aAllEntitys[i].getMiniMapColor());
                    }
                }catch(IndexOutOfBoundsException e){}
            }
            xLib.drawRectOutline(xMap + xCam.getXOnMap()*widthMap/(aaMap.length*xLib.getResolutionFactor()), 
                            yMap+ xCam.getYOnMap()*heightMap/aaMap[0].length, 
                            (xCam.getWidthOnMap()/aaMap.length)*widthMap, 
                            (xCam.getHeightOnMap()/aaMap[0].length)*heightMap,
                            Color.WHITE);
            for (Schuss schuesse1 : schuesse) {
                if(schuesse1 != null)
                    schuesse1.render(xLib, xCam);
            }
        }
    }
    
    public int entityMoved(int xF, int yF, int xBy, int yBy, boolean bEnemy, int iIndex, Entity e){            
        //System.out.println(xF + "+" + xBy + " " + yF + "+" + yBy);
        int iRet = aaMap[xF + xBy][yF + yBy].entityMovedin(e);
        aaMap[xF][yF].entityMovedOut(iIndex);
        for (int i = (int)(-fAggroRange) + xF + xBy; i <= fAggroRange+xF+xBy; i++) {
            for (int j = (int)(-fAggroRange) + yF + yBy; j <= fAggroRange+yF+yBy; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length && aaMap[i][j] != null){
                    if(Math.sqrt(Math.abs(-i + xF + xBy)*Math.abs(-i+ xF + xBy) + Math.abs(-j+yF+yBy)*Math.abs(-j+yF+yBy)) <= fAggroRange){ 
                        if(bEnemy)
                            aaMap[i][j].incEnemy();
                        else{
                            aaMap[i][j].incFreindly();
                            xMapOverlay.setRGB(i,j, new Color(0, 0, 0, 0).getRGB());
                        }
                    }
                }
            }
        }
        for (int i = (int)(-fAggroRange) + xF; i <= fAggroRange+xF; i++) {
            for (int j = (int)(-fAggroRange) + yF; j <= fAggroRange+yF; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length && aaMap[i][j] != null){
                    if(Math.sqrt(Math.abs(-i + xF)*Math.abs(-i+ xF) + Math.abs(-j+yF)*Math.abs(-j+yF)) <= fAggroRange){ 
                        if(bEnemy)
                            aaMap[i][j].decEnemy();
                        else{
                            aaMap[i][j].decFriendly();
                            if(aaMap[i][j].getFogOfWar() == 0)
                                xMapOverlay.setRGB(i,j, TileContainer.xFogofWar2.getRGB());
                        }
                    }
                }
            }
        }
        /*for (int i = (int)(-fAggroRange); i < fAggroRange+1; i++) {
            for (int j = (int)(-fAggroRange); j < fAggroRange+1; j++) {
                if(i+xF >= 0 && j+yF >= 0 && i+xF < aaMap.length && j+yF < aaMap[i+xF].length && aaMap[i+xF][j+yF] != null){
                    if(Math.sqrt(Math.abs(i)*Math.abs(i)* + Math.abs(j)*Math.abs(j)) <= fAggroRange){ 
                        if(Math.sqrt(Math.abs(i+xBy)*Math.abs(i+xBy)* + Math.abs(j+yBy)*Math.abs(j+yBy)) > fAggroRange){
                            if(bEnemy)
                                aaMap[xF+i][yF+j].decEnemy();
                            else
                                aaMap[xF+i][yF+j].decFriendly();
                        }
                    }
                    else if(Math.sqrt(Math.abs(i+xBy)*Math.abs(i+xBy)* + Math.abs(j+yBy)*Math.abs(j+yBy)) < fAggroRange){
                        if(bEnemy)
                            aaMap[xF+i+xBy][yF+j+yBy].incEnemy();
                        else
                            aaMap[xF+i+xBy][yF+j+yBy].incFreindly();
                    }
                }
            }
        }*/
        return iRet;
    }
    public void entityDied(int xF, int yF, boolean bEnemy, int iIndex, Entity e){ 
        aaMap[xF][yF].entityMovedOut(iIndex);
        for (int i = (int)(-fAggroRange) + xF; i <= fAggroRange+xF; i++) {
            for (int j = (int)(-fAggroRange) + yF; j <= fAggroRange+yF; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length && aaMap[i][j] != null){
                    if(Math.sqrt(Math.abs(-i + xF)*Math.abs(-i+ xF) + Math.abs(-j+yF)*Math.abs(-j+yF)) <= fAggroRange){ 
                        if(bEnemy)
                            aaMap[i][j].decEnemy();
                        else{
                            aaMap[i][j].decFriendly();
                            if(aaMap[i][j].getFogOfWar() == 0)
                                xMapOverlay.setRGB(i,j, TileContainer.xFogofWar2.getRGB());
                        }
                    }
                }
            }
        }
    }
    
    public int addEntity(int xF, int yF, boolean bEnemy, Entity e){
        int iRet = aaMap[xF][ yF].entityMovedin(e);
        for (int i = (int)(-fAggroRange); i <= fAggroRange; i++) {
            for (int j = (int)(-fAggroRange); j <= fAggroRange; j++) {
                if(i+xF >= 0 && j+yF >= 0 && i+xF < aaMap.length && j+yF < aaMap[i+xF].length && Math.sqrt(Math.abs(i)*Math.abs(i) + Math.abs(j)*Math.abs(j)) <= fAggroRange){
                    if(bEnemy)
                        aaMap[xF+i][yF+j].incEnemy();
                    else{
                        aaMap[xF+i][yF+j].incFreindly();
                        xMapOverlay.setRGB(xF+i,yF+j, new Color(0, 0, 0, 0).getRGB());
                    }
                }
            }
        }
        return iRet;
    }
    
    TileContainer getTileContainer(int x, int y){
        return aaMap[x][y];
    }
    
    public void addSchuss(Schuss s){
        schuesse[(iShusAddCounter++)%schuesse.length] = s;
    }
    
    LinkedList<Entity> getNeighbors(float x, float y, float fRadius){
        LinkedList<Entity> lRet = new LinkedList<>();
        
        for (int i = (int)(x - fRadius)-1; i <= x+ fRadius+1; i++) {
            for (int j = (int)(y -fRadius)-1; j <= y+fRadius+1; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length && aaMap[i][j] != null){
                    Entity[] arr = aaMap[i][j].getEntitys();
                    for (Entity arr1 : arr) {
                        if(arr1 != null){
                            if(arr1.distance(x, y) < fRadius){
                                lRet.add(arr1);
                            }
                        }
                    }
                }
            }
        }
        
        return lRet;
    }
    
    LinkedList<Entity> getNeighbors(float x, float y, float xTo, float yTo){
        //System.out.println(x + " " + y + "   "+ xTo + " " + yTo);
        LinkedList<Entity> lRet = new LinkedList<>();
        
        for (int i = (int)(x)-1; i <= xTo+1; i++) {
            for (int j = (int)(y)-1; j <= yTo+1; j++) {
                if(i >= 0 && j >= 0 && i < aaMap.length && j < aaMap[i].length && aaMap[i][j] != null){
                    Entity[] arr = aaMap[i][j].getEntitys();
                    for (Entity arr1 : arr) {
                        if(arr1 != null){
                            //System.out.println("1");
                            if(arr1.isIn(x, y, xTo, yTo)){
                               // System.out.println("2");
                                lRet.add(arr1);
                            }
                        }
                    }
                }
            }
        }
        
        return lRet;
    }

    public int getxSpawn() {
        return xSpawn;
    }

    public int getySpawn() {
        return ySpawn;
    }
    
    
    
}
