/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleGameLibrarry;

import java.io.Serializable;

 
public class Tile implements Serializable{
    private final String sName, sData;
    private final int iLayer;

    public Tile(String sName, String sData, int iLayer) {
        this.sName = sName;
        this.sData = sData;
        this.iLayer = iLayer;
    }

    public int getLayer() {
        return iLayer;
    }

    public String getData() {
        return sData;
    }

    public String getName() {
        return sName;
    }
    
    
    
}
