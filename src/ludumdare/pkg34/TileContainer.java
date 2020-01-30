/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.SimpleGameLibrarry;
import java.awt.Color;
import java.util.Arrays;

 
public class TileContainer {
    public enum Passability{
        WALKABLE,
        JUMPABLE,
        FLYABLE,
        UNPASSABLE
    }
    
    private final Tile xBackground;
    private final Tile[] aAditionalTiles;
    private volatile int iFogOfWar = -1;
    int iEnemyCount = 0, iFriendlyCount = 0;
    public static final Color xFogofWar = new Color(0, 0, 0, 255);
    public static final Color xFogofWar2 = new Color(0, 0, 0, 170);
    volatile Entity[] aEntitys = new Entity[50];
    private volatile Haus[] aHaus = new Haus[5];
    private Passability xPassabale = Passability.WALKABLE;

    public TileContainer(Tile xBackground, Tile[] aAditionalTiles, Passability xPassable) {
        this.xBackground = xBackground;
        this.aAditionalTiles = aAditionalTiles;
        this.xPassabale = xPassable;
    }

    public Haus[] getaHaus() {
        return aHaus;
    }

    
    
    public void render(SimpleGameLibrarry xLib, float x, float y, float width, float hight, Camera xCam, MYMap xMap, int xMe, int yME){
        if(iFogOfWar == -1){
            xLib.drawRect(x, y, width, hight, xFogofWar);
            return;
        }
        if(xBackground != null)
            xBackground.render(xLib, x, y, width, hight);
        for (int i = 0; i < aAditionalTiles.length; i++) {
            if(aAditionalTiles[i] != null)
                aAditionalTiles[i].render(xLib, x, y, width, hight);
        }
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                if(xMe+i >= 0 && xMe+i < xMap.aaMap.length && yME+j >= 0 && yME+j < xMap.aaMap[xMe+i].length && xMap.aaMap[i+xMe][j+yME] != null){
                    xMap.aaMap[xMe+i][yME+j].renderHaus(xLib, xCam);
                }
            }
        }
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                if(xMe+i >= 0 && xMe+i < xMap.aaMap.length && yME+j >= 0 && yME+j < xMap.aaMap[xMe+i].length && xMap.aaMap[i+xMe][j+yME] != null){
                    xMap.aaMap[xMe+i][yME+j].renderEntitys(xLib, xCam);
                }
            }
        }
        if(iFogOfWar == 0){
            xLib.drawRect(x, y, width, hight, xFogofWar2);
            return;
        }
        /*if(xPassabale != Passability.WALKABLE){
            
            xLib.drawRect(x, y, width, hight, new Color(255, 0, 255, 150));
        }*/
    }
    
    public void renderEntitys(SimpleGameLibrarry xLib, Camera xCam){
        if(iFogOfWar != 0 && iFogOfWar != -1){
            for (int i = 0; i < aEntitys.length; i++) {
                Entity aEntity = aEntitys[i];
                if(aEntity != null)
                    aEntity.render(xLib, xCam);
            }
        }
    }
    public void renderHaus(SimpleGameLibrarry xLib, Camera xCam){
        for (int i = 0; i < aHaus.length; i++) {
            if(aHaus[i] != null){
                aHaus[i].render(xLib, xCam);
            }
        }
    }

    public int getFogOfWar() {
        return iFogOfWar;
    }

    public void setFogOfWar(int iFogOfWar) {
        this.iFogOfWar = iFogOfWar;
    }   
    
    public boolean containsEnemy(){
        return iEnemyCount > 0;
    }
    public boolean containsFriend(){
        return iFriendlyCount > 0;
    }
    public void incEnemy(){
        iEnemyCount++;
    }
    public void decEnemy(){
        iEnemyCount--;
    }
    public void incFreindly(){
        iFriendlyCount++;
        if(iFriendlyCount > 0)
            iFogOfWar = 1;
    }
    public void decFriendly(){
        iFriendlyCount--;
        if(iFriendlyCount <= 0){
            iFogOfWar = 0;
        }
    }
    
    public void entityMovedOut(int id){
        aEntitys[id] = null;
    }
    
    public int entityMovedin(Entity e){
        int id = 0;
        while (aEntitys[id] != null) {            
            id ++;
            if(id >= aEntitys.length){
                aEntitys = Arrays.copyOf(aEntitys, aEntitys.length+20);
            }
        }
        aEntitys[id] = e;
        return id;
    }
    
    public Entity[] getEntitys(){
        return aEntitys;
    }
    
    public static Passability addPassableString(String s, Passability xCurr){
        if(s.equals("jumpable") /*&& xCurr == Passability.WALKABLE*/){
            return Passability.JUMPABLE;
        }
        if(s.equals("flyable") /*&& xCurr != Passability.UNPASSABLE*/){
            return Passability.FLYABLE;
        }
        if(s.equals("unpassable")){
            return Passability.UNPASSABLE;
        }
        if(s.equals("walkable")){
            return Passability.WALKABLE;
        }
        return xCurr;
    }
    
    public boolean issPassable(boolean forFlyer, boolean forJumper){
        return xPassabale == Passability.WALKABLE || (forFlyer && xPassabale == Passability.FLYABLE) || ( (forJumper || forFlyer) && xPassabale == Passability.JUMPABLE );
    }
    
    public void addHaus(Haus h){
        for (int i = 0; i < aHaus.length; i++) {
            if(aHaus[i] == null){
                aHaus[i] = h;
                return;
            }
        }
        throw new IndexOutOfBoundsException("no space for houses left");
    }
    public void rmHaus(Haus h){
        for (int i = 0; i < aHaus.length; i++) {
            if(aHaus[i] == h){
                aHaus[i] = null;
                return;
            }
        }
        throw new IndexOutOfBoundsException("no space for houses left");
    }
}
