/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
public class Basis {
    Haus[][] aaHaus = new Haus[50][50];
    int xCenter, yCenter;
    
    float timeToNextSpawn = 10;
    int iTimeBetweenSpawns = 15;
    boolean bSpawnTick = true;
    int iRohstoff = 15, iWinCondition = 100;
    float fTimeToInc = 0;
    
    public int iLurch = 0, iWhirlWind = 0, iCog = 0, iGlider = 0, iEiscle = 0, iCaster = 0, iSuply = 10, iResourceSpeed = 2, iUsedSupp = 0, iRemainingNormal = 7;

    public Basis(LudumDare34 xMain, int x, int y, int iWincondition) {
        xCenter = x;
        yCenter = y;
        setTile(25, 25, new HausNormal(x, y, x, y), xMain);
        setTile(25, 24, new HausNormal(aaHaus[25][24].x, aaHaus[25][24].y, x, y), xMain);
        setTile(25, 26, new HausNormal(aaHaus[25][26].x, aaHaus[25][26].y, x, y), xMain);
        setTile(24, 24, new HausNormal(aaHaus[24][24].x, aaHaus[24][24].y, x, y), xMain);
        setTile(24, 25, new HausNormal(aaHaus[24][25].x, aaHaus[24][25].y, x, y), xMain);
        setTile(26, 24, new HausNormal(aaHaus[26][24].x, aaHaus[26][24].y, x, y), xMain);
        setTile(26, 25, new HausNormal(aaHaus[26][25].x, aaHaus[26][25].y, x, y), xMain);
        this.iWinCondition =iWincondition;
    }
    
    public void replaceTile(Haus h, Haus xNeu, LudumDare34 xMain){
        for (int i = 0; i < aaHaus.length; i++) {
            for (int j = 0; j < aaHaus[i].length; j++) {
                if(aaHaus[i][j] == h){
                    setTile(i, j, xNeu, xMain);
                    return;
                }
            }
        }
    }
    
    public void setTile(int x, int y, Haus xHaus, LudumDare34 xMain){
        xMain.addHaus(xHaus);
        aaHaus[x][y] = xHaus;
        setAuswahlIfPossible(x, y+1, xMain);
        setAuswahlIfPossible(x, y-1, xMain);
        if(x%2 == 0){
            setAuswahlIfPossible(x+1, y, xMain);
            setAuswahlIfPossible(x+1, y+1, xMain);
            setAuswahlIfPossible(x-1, y, xMain);
            setAuswahlIfPossible(x-1, y+1, xMain);
        }
        else{
            setAuswahlIfPossible(x+1, y, xMain);
            setAuswahlIfPossible(x+1, y-1, xMain);
            setAuswahlIfPossible(x-1, y, xMain);
            setAuswahlIfPossible(x-1, y-1, xMain);
        }
    }
    
    private void setAuswahlIfPossible(int x, int y, LudumDare34 xMain){
        if(x > 0 && y > 0 && x < aaHaus.length && y < aaHaus[x].length && aaHaus[x][y] == null){
            aaHaus[x][y] = new Auswahl((x-25)*Auswahl.getWidth()*((50.0f-13f)/50f)+xCenter, yCenter+(y-25)*Auswahl.getHeight()+(x%2==1?0:.5f*Auswahl.getHeight()));
            try{
                xMain.addHaus(aaHaus[x][y]);
            }catch(IndexOutOfBoundsException e){}
        }
    }
    
    public void addResources(int iBy){
        iRohstoff += iBy;
    }
    
    public boolean iEnoughRes(){
        return iRohstoff >= 10;
    }
    
    public void update(LudumDare34 xLib, float ftime){
        fTimeToInc -= ftime;
        if(fTimeToInc < 0){
            addResources(1);
            fTimeToInc += getTimeToNextInc();
        }
        timeToNextSpawn -= ftime;
        if(timeToNextSpawn < 0){
            timeToNextSpawn += iTimeBetweenSpawns;
            LudumDare34.xSound.playSound(LudumDare34.iSpawn);
            if(!bSpawnTick){
                bSpawnTick = true;
                spawn(true, xLib);
            }
            else{
                bSpawnTick = false;
                spawn(false, xLib);
            }
        }
        for (int i = 0; i < aaHaus.length; i++) {
            for (int j = 0; j < aaHaus[i].length; j++) {
                if(aaHaus[i][j] == null)
                    continue;
                if(aaHaus[i][j].live < 0){
                    aaHaus[i][j].remove(this);
                    xLib.rmHaus(aaHaus[i][j]);
                    aaHaus[i][j] = new Auswahl(aaHaus[i][j].x, aaHaus[i][j].y);
                    continue;
                }
                aaHaus[i][j].live += ftime;
                if(aaHaus[i][j].live > 100){
                    aaHaus[i][j].live = 100;
                }
            }
        }
    }
    
    public void spawn(boolean bLurchOnly, LudumDare34 xLib){
        for (int i = 0; i < aaHaus.length; i++) {
            for (int j = 0; j < aaHaus[i].length; j++) {
                if(aaHaus[i][j] != null)
                    aaHaus[i][j].spawn(bLurchOnly, xLib);
            }
        }
    }
    
    public Haus get(float x, float y){
        float fGridWidth = Auswahl.getWidth()*((50.0f-13f)/50f);
        float fGridHeight =  Auswahl.getHeight();
        float iPosx = x - (xCenter-25*fGridWidth);/*(int)( (x-xCenter)/(Auswahl.getWidth()*((50.0f-13f)/50f))+25  );*/
        float iPosy = y - (yCenter-25*fGridHeight);/*(int)( (y-yCenter)/(Auswahl.getHeight()+(iPosx%1==0?0:.5f*Auswahl.getHeight()))+25  );*/
        int column = 0;
        int row  = 0;
        column = (int)(iPosx / fGridWidth);
        //column is not on the borders:
        if(column % 2 == 0){//on an even numer
            row = Math.round((iPosy / fGridHeight) -.05f)-1;
        }
        else{
            row = (int)( (iPosy) / fGridHeight);
        }
        if(!(Math.abs(iPosx) % fGridWidth >= Auswahl.getWidth()*(13f/50f))){
            //else the point lies in between the rectangular shapes:
            int iHalfRow = (int)( Math.abs(iPosy*2f) / (fGridHeight))-1;
            if(column % 2 == 1){
                iHalfRow++;
            }
            float littley = ((iPosy*2) % (fGridHeight))/fGridHeight;
            float littlex = (iPosx % fGridWidth)/(fGridWidth*(13f/50f));
            //System.out.print(column + " " + iHalfRow + " " + littlex + " " + littley + "   ");
            if(iHalfRow % 2 == 0){
                //System.out.print("1");
                if(littlex > (1) - littley){
                    //System.out.print("a");
                    //column = (int)(iPosx / fGridWidth); 
                    //row = iHalfRow*2;
                }
                else{
                    //System.out.print("b");
                    column--; 
                    if(column %2 == 0){
                        row--;
                    }
                }
            }
            else{
                if(littlex > 1 - littley){
                    //System.out.print("a");
                    //column = Math.round((iPosy / fGridHeight) -.05f); 
                    //row = iHalfRow*2;
                }
                else{
                    //System.out.print("b");
                    column--;
                    if(column%2 == 1){
                        row++;
                    }
                    //column = Math.round((iPosy / fGridHeight) -.05f)-2; 
                    //srow = iHalfRow*2;
                }
            }
        }
        //System.out.println("  : " + column + " " + row);
        if(column > 0 && row > 0 && column < aaHaus.length && row < aaHaus[column].length){
            return aaHaus[column][row];
        }
        return null;
    }
    
//    public Haus get(float xIn, float yIn){
//        float xp = xIn - xCenter + 25 *Auswahl.getWidth();//((50.0f-13f)/50f);
//        float yp = yIn - yCenter + 25 * Auswahl.getHeight();
//        double x = 1.0 * ( xp ) / Auswahl.getWidth();
//        double y = 1.0 * ( yp ) / Auswahl.getHeight();
//        double z = -0.5 * x - y;
//               y = -0.5 * x + y;
//        int ix = (int)Math.floor(x+0.5);
//        int iy = (int)Math.floor(y+0.5);
//        int iz = (int)Math.floor(z+0.5);
//        int s = ix+iy+iz;
//        if( s != 0 )
//        {
//            double abs_dx = Math.abs(ix-x);
//            double abs_dy = Math.abs(iy-y);
//            double abs_dz = Math.abs(iz-z);
//            if( abs_dx >= abs_dy && abs_dx >= abs_dz )
//                ix -= s;
//            else if( abs_dy >= abs_dx && abs_dy >= abs_dz )
//                iy -= s;
//            else
//                iz -= s;
//        }
//        int row = ix;
//        int column = ( iy - iz + (ix%2) ) / 2;
//        column+=-1;
//        //row+=25;
//        //column+=25;
//        System.out.println(row + " " + column);
//        if(row >= 0 && column >= 0 && row < aaHaus.length && column < aaHaus[row].length){
//            return aaHaus[row][column];
//        }
//        return null;
//    }
    
    
    float getTimeToNextInc(){
        return 14f/(float)iResourceSpeed;
    }
    
    public void decSup(){
        iUsedSupp--;
    }
    public void incSup(){
        iUsedSupp++;
    }
    
}
