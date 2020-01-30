/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Spawner {
    float fTimeToSpawn, fTimeBetweenSpawns;
    int iUnitAmount, iNumSpawns, Adder;
    boolean bFriendly;
    String sType, sMessage;
    boolean bTriggerOnVisibility, triggered = false;
    int x,y;

    public Spawner(float initialTimeToSpawn, float fTimeBetweenSpawns, int iUnitAmount, int iNumSpawns, boolean bFriendly, String sType, String sMessage, boolean bTriggerOnVisibility, int iAdder, int x, int y) {
        this.fTimeToSpawn = initialTimeToSpawn;
        this.fTimeBetweenSpawns = fTimeBetweenSpawns;
        this.iUnitAmount = iUnitAmount;
        this.iNumSpawns = iNumSpawns;
        this.bFriendly = bFriendly;
        this.sType = sType;
        this.sMessage = sMessage.replace('_', ' ');
        this.bTriggerOnVisibility = bTriggerOnVisibility;
        this.x = x;
        this.y = y;
        this.Adder = iAdder;
    }

    

    public void update(LudumDare34 xLib, float fTime){
        if(bTriggerOnVisibility && !triggered && xLib.xMap.aaMap[x][y].containsFriend()){
            triggered = true;
            for (int i = 0; i < iUnitAmount; i++) {
                Entity xNew = null;
                if(sType.equals("lurch"))
                    xNew = new Lurch(xLib.xMap, bFriendly, x, y);
                if(sType.equals("claw"))
                    xNew = new Claw(xLib.xMap, bFriendly, x, y);
                if(sType.equals("kog"))
                    xNew = new Cog(xLib.xMap, bFriendly, x, y);
                if(sType.equals("glider"))
                    xNew = new Glider(xLib.xMap, bFriendly, x, y);
                if(sType.equals("eiscle"))
                    xNew = new Eiscle(xLib.xMap, bFriendly, x, y);
                if(sType.equals("scion"))
                    xNew = new Scion(xLib.xMap, bFriendly, x, y);
                xLib.addEntity(xNew);
                xNew.setAttackingAnPathing(xLib.xMap.getxSpawn(), xLib.xMap.getySpawn(), xLib.xMap);
            }
            iUnitAmount += Adder;
            if(!sMessage.equals(""))
                xLib.xHud.dispText(sMessage);
        }
        else if(!bTriggerOnVisibility){
            fTimeToSpawn -= fTime;
            if(fTimeToSpawn < 0){
                if(iNumSpawns-- <= 0){
                    return;
                }
                fTimeToSpawn += fTimeBetweenSpawns;
                for (int i = 0; i < iUnitAmount; i++) {
                    Entity xNew = null;
                    if(sType.equals("lurch"))
                        xNew = new Lurch(xLib.xMap, bFriendly, x, y);
                    if(sType.equals("claw"))
                        xNew = new Claw(xLib.xMap, bFriendly, x, y);
                    if(sType.equals("kog"))
                        xNew = new Cog(xLib.xMap, bFriendly, x, y);
                    if(sType.equals("glider"))
                        xNew = new Glider(xLib.xMap, bFriendly, x, y);
                    if(sType.equals("eiscle"))
                        xNew = new Eiscle(xLib.xMap, bFriendly, x, y);
                    if(sType.equals("scion"))
                        xNew = new Scion(xLib.xMap, bFriendly, x, y);
                    xLib.addEntity(xNew);
                    xNew.setAttackingAnPathing(xLib.xMap.getxSpawn(), xLib.xMap.getySpawn(), xLib.xMap);
                }
                iUnitAmount += Adder;
                if(!sMessage.equals(""))
                    xLib.xHud.dispText(sMessage);
            }
        }
    }
}
