/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludumdare.pkg34;

 
class Camera {
    LudumDare34 xLib;
    volatile float x = 0, y = 0, x2 = 0, y2= 0;

    public Camera(LudumDare34 xLib, float x, float y) {
        this.xLib = xLib;
        this.x2 = x / getWidthOnMap();//- 1/2;
        this.y2 = y /getHeightOnMap();//- 1/2;
    }
    
    float getXOnMap(){
        return x * getWidthOnMap();
    }
    float getYOnMap(){
        return y * getHeightOnMap();
    }
    public static float getWidthOnMap(){
        return 30;
    }
    public static float getHeightOnMap(){
        return 25;
    }
    float screenToMapX(float x){
        return x * getWidthOnMap() + getXOnMap() / xLib.getResolutionFactor();
    }
    float screenToMapY(float x){
        return x * getHeightOnMap() + getYOnMap() ;
    }
    float mapToScreenX(int x){
        return x * xLib.getResolutionFactor() / getWidthOnMap() - getXOnMap()/ getWidthOnMap();
    }
    float mapToScreenY(int y){
        return y / getHeightOnMap() - getYOnMap()/ getHeightOnMap();
    }
    float mapToScreenX(float x){
        return x  * xLib.getResolutionFactor() / getWidthOnMap() - getXOnMap() / getWidthOnMap();
    }
    float mapToScreenY(float y){
        return y / getHeightOnMap() - getYOnMap()/ getHeightOnMap();
    }
    float getTileWidth(){
        return 1 * xLib.getResolutionFactor() /getWidthOnMap() + .0001f;
    }
    float getTileHeight(){
        return 1/getHeightOnMap() + .0001f;
    }
    
    void moveCamX(float x2, LudumDare34 xLib){
        this.x2 += x2;
        if(this.x2 < 0){
            this.x2 = 0;
        }
        else if(this.x2* getWidthOnMap() + getWidthOnMap() > xLib.xMap.aaMap.length * xLib.getResolutionFactor()){
            this.x2 = (xLib.xMap.aaMap.length* xLib.getResolutionFactor()) /getWidthOnMap() - 1;
        }
    }
    
    void moveCamY(float y2){
        this.y2 += y2;
        if(this.y2 < 0){
            this.y2 = 0;
        }
        else if(this.y2 * getHeightOnMap() + getHeightOnMap() > xLib.xMap.aaMap[0].length){
            this.y2 = xLib.xMap.aaMap[0].length / getHeightOnMap() - 1;
        }
    }
    
    void applyCamMovements(){
        x = x2;
        y = y2;
    }
}
