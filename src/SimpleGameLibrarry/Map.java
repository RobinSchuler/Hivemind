/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleGameLibrarry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

 
public class Map implements Serializable{
    private final ScriptReader xMapSettings = new ScriptReader();
    private final Tile[][][] aaaxMapTiles;

    public Map(Tile[][][] aaaxMapTiles) {
        this.aaaxMapTiles = aaaxMapTiles;
    }

    public Tile getMapTile(int x, int y, int z) {
       return aaaxMapTiles[x][y][z];
    }
    
    public int getWidht(){
        return aaaxMapTiles.length;
    }
    
    public int getLenght(){
        return aaaxMapTiles[0].length;
    }
    
    public int getHeight(int x, int y){
        return aaaxMapTiles[x][y].length;
    }

    public ScriptReader getMapSettings() {
        return xMapSettings;
    }
    
    
    
    public static Map loadMap(String sFileName) throws IOException{
        Map m = null;
        try (FileInputStream fileIn = new FileInputStream(sFileName); 
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            m = (Map) in.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("class not found!");
        }
        return m;
    }
    
    public void save(String sFileName) throws IOException{
        try (FileOutputStream fileOut = new FileOutputStream(sFileName); 
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        }
    }
}
