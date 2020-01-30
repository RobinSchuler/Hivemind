/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

import SimpleGameLibrarry.Animation;
import java.awt.Color;
import java.util.concurrent.LinkedBlockingQueue;

 
public class Hud {
    Animation hud = new Animation("./res/textures/hud", 1, 0, 1, true, null, LudumDare34.scaleDown());
    Animation build = new Animation("./res/textures/build", 1, 0, 1, true, null, LudumDare34.scaleDown());
    Animation bud_text = new Animation("./res/textures/hud_text", 1, 0, 1, true, null, LudumDare34.scaleDown());
    LinkedBlockingQueue<String> ltext = new LinkedBlockingQueue<>();
    float fFadeTime = 5f;
    
    public int iClickedOn(float x, float y, LudumDare34 xLib){
        float fXBildStart = .8f;
        float fYBildStart = .05f;
        float fBildWidth = xLib.getResolutionFactor()*.2f;
        float fBildHeight = xLib.getResolutionFactor()*.2f*(196/231.0f);
        float xAct = (x - fXBildStart)*231*xLib.getResolutionFactor()/(fBildWidth);
        float yAct = (y - fYBildStart)*196/(fBildHeight);
        if(!xLib.xMainBasis.iEnoughRes()){
            return 0;
        }
        if(xAct >= 172 && yAct >= 157 && xAct <= 172+39 && yAct <= 157+39){
            return 1;//wihrwind
        }
        if(xAct >= 55 && yAct >= 157 && xAct <= 55+39 && yAct <= 157+39){
            return 2;//lurch
        }
        if(xAct >= 94 && yAct >= 2 && xAct <= 94+39 && yAct <= 2+39){
            return 3;//supp
        }
        if(xAct >= 16 && yAct >= 2 && xAct <= 16+39 && yAct <= 2+39){
            return 4;//res
        }
        if(xAct >= 16 && yAct >= 79 && xAct <= 16+39 && yAct <= 79+39 && xLib.xMainBasis.iLurch > 0){
            return 5;//cog
        }
        if(xAct >= 94 && yAct >= 79 && xAct <= 94+39 && yAct <= 79+39 && xLib.xMainBasis.iLurch > 0){
            return 6;//glider
        }
        if(xAct >= 172 && yAct >= 79 && xAct <= 172+39 && yAct <= 79+39 && xLib.xMainBasis.iWhirlWind > 0){
            return 7;//eiscle
        }
        if(xAct >= 172 && yAct >= 2 && xAct <= 172+39 && yAct <= 2+39 && xLib.xMainBasis.iEiscle > 0){
            return 8;//caster
        }
        return 0;
    }
    
    public void render(LudumDare34 xLib, int iThread, int iThreadMax){
        try{
            if(iThread == 7%iThreadMax)
                xLib.drawAnimation(0,0,xLib.getResolutionFactor(),1, hud);
            if(iThread == 7%iThreadMax)
                xLib.drawAnimation(xLib.getResolutionFactor()*.8f, .05f, xLib.getResolutionFactor()*.2f, xLib.getResolutionFactor()*.2f*(196/231.0f), build);
            if(iThread == 7%iThreadMax)
                xLib.drawString("Slime: " + xLib.xMainBasis.iRohstoff, xLib.getResolutionFactor()*.030f, .025f, "Times New Roman", 0, 15, Color.black);
            if(iThread == 7%iThreadMax)
                xLib.drawString("Supply: " + xLib.xMainBasis.iUsedSupp + "/" + xLib.xMainBasis.iSuply, xLib.getResolutionFactor()*.217f, .025f, "Times New Roman", 0, 15, Color.black);
            if(iThread == 7%iThreadMax)
                xLib.drawString("Remaining Time: " + (int)(xLib.tLooseTime), xLib.getResolutionFactor()*.393f, .025f, "Times New Roman", 0, 15, Color.black);
            if(iThread == 7%iThreadMax)
                xLib.drawString("Next unit spawn: " + (int)(xLib.xMainBasis.timeToNextSpawn), xLib.getResolutionFactor()*.600f, .025f, "Times New Roman", 0, 15, Color.black);
            if(iThread == 7%iThreadMax)
                xLib.drawString("Remaining DNA: " + (int)(xLib.xMainBasis.iWinCondition), xLib.getResolutionFactor()*.825f, .025f, "Times New Roman", 0, 15, Color.black);
            float fXBildStart = xLib.getResolutionFactor()*.8f;
            float fYBildStart = .05f;
            float fBildWidth = xLib.getResolutionFactor()*.2f;
            float fBildHeight = xLib.getResolutionFactor()*.2f*(196/231.0f);
            if(xLib.xMainBasis.iEnoughRes()){
                /* whirlwinds: 172(231) /157(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 157f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                /* lurch: 55(231) /157(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 55f/231f, fYBildStart + fBildHeight * 157f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                /* supp: 94(231) /2(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 94f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                /* res: 16(231) /2(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 16f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                if(xLib.xMainBasis.iLurch > 0){
                    /* cog: 16(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 16f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                    /* glider: 94(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 94f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                }
                else{
                    /* cog: 16(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 16f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                    /* glider: 94(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 94f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                }
                if(xLib.xMainBasis.iWhirlWind > 0){
                    /* eiscle: 172(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                }
                else{
                    /* eiscle: 172(231) /79(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                }
                if(xLib.xMainBasis.iEiscle > 0){
                    /* caster: 172(231) /2(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.green);
                }
                else{
                    /* caster: 172(231) /2(196)   39/39 */
                    xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                }
            }
            else{
                /* res: 16(231) /2(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 16f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* whirlwinds: 172(231) /157(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 157f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* lurch: 55(231) /157(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 55f/231f, fYBildStart + fBildHeight * 157f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* cog: 16(231) /79(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 16f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* glider: 94(231) /79(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 94f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* eiscle: 172(231) /79(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 79f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* caster: 172(231) /2(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 172f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
                /* supp: 94(231) /2(196)   39/39 */
                xLib.drawCircleOutline(fXBildStart + fBildWidth * 94f/231f, fYBildStart + fBildHeight * 2f/196, fBildWidth * 39f/231f, fBildHeight * 39f/196, Color.red);
            }
            int i = 0;
            float xMap = (1-.26f)*xLib.getResolutionFactor();
            for (String ltext1 : ltext) {
                xLib.drawAnimation(0, .95f-.01f*ltext.size()+.01f*(i++) - 0.03f, xMap, 0.06f, bud_text);
                xLib.drawString(ltext1, .05f, .95f-.01f*ltext.size()+.01f*(i++), "Times New Roman", 0, 20, Color.BLACK);
            }
        }catch(IndexOutOfBoundsException e){
            System.out.println("FIXME indexoob in hud");
        }
    }
    
    public void update(float fTime){
        fFadeTime -= fTime;
        if(fFadeTime + fTime > 0 && fFadeTime <= 0){
            fFadeTime += 5;
            ltext.poll();
        }
    }
    
    public void dispText(String sText){
        fFadeTime = 5f;
        ltext.offer(sText);
    }
}
