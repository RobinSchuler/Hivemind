/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleGameLibrarry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

 
public class ScriptReader implements Serializable{
    HashMap<String, Serializable> xSettingsMap;
    private static final boolean bSilent = true;

    public ScriptReader() {
        xSettingsMap = new HashMap<>();
    }//cunstructor
    
    synchronized public void loadScript(String filename){
        try {
            System.out.println("loading: " + filename);
            File xFile = new File(filename);
            Scanner xScan = new Scanner(xFile);
            
            while (xScan.hasNextLine()) {
                String sCurr = "";
                while(!sCurr.endsWith(";") && xScan.hasNextLine()){
                    sCurr += " " + xScan.nextLine();
                }//while
                if(sCurr.endsWith(";")){
                    sCurr = sCurr.substring(0, sCurr.length()-1);//remove the ; at the end
                    sCurr = sCurr.replaceAll("\t", " ");
                    sCurr = sCurr.trim().replaceAll(" +", " ");
                    String[] asCurr = sCurr.split(" ");

                    saveSetting(asCurr);
                }//if
            }//while
        } //function
        catch (FileNotFoundException ex) {
            Logger.getLogger(ScriptReader.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File Not Found");
        }
    }
    
    synchronized public void loadNonFileScript(String script){
        InputStream is = new ByteArrayInputStream(script.getBytes());
        Scanner xScan = new Scanner(is);
        while (xScan.hasNextLine()) {
            String sCurr = "";
            while(!sCurr.endsWith(";") && xScan.hasNextLine()){
                sCurr += " " + xScan.nextLine();
            }//while
            if(sCurr.endsWith(";")){
                sCurr = sCurr.substring(0, sCurr.length()-1);//remove the ; at the end
                sCurr = sCurr.replaceAll("\t", " ");
                sCurr = sCurr.trim().replaceAll(" +", " ");
                String[] asCurr = sCurr.split(" ");
                
                saveSetting(asCurr);
            }//if
        }
    }
    
    synchronized public void manualSet(String sName, String sValue){
        xSettingsMap.put(sName, sValue);
    }
    synchronized public void manualSet(String sName, String[] sValue){
        xSettingsMap.put(sName, sValue);
    }
    synchronized public void manualSet(String sName, int[] sValue){
        xSettingsMap.put(sName, sValue);
    }
    synchronized public void manualSet(String sName, Serializable sValue){
        xSettingsMap.put(sName, sValue);
    }
    
    synchronized private void saveSetting(String[] asCurr){
        System.out.println("saving setting for: " + asCurr[0]);
        if(asCurr[0].equals("+")){
            Serializable xSer = xSettingsMap.get(asCurr[1]);
            if(xSer instanceof int[]){
                int[] xSerCasted = (int[]) xSer;
                if(asCurr[2].equals("[") && asCurr[asCurr.length-1].equals("]")){
                    int[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+asCurr.length-4);
                    try{
                        for (int i = 0; i < asCurr.length-4; i++) {
                            aPutBack[xSerCasted.length+i] = Integer.decode(asCurr[i+3]);
                        }//for
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }
                else{
                    int[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+1);
                    try{
                        aPutBack[aPutBack.length-1] = Integer.decode(asCurr[2]);
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }//else
            }//if
            else if(xSer instanceof float[]){
                float[] xSerCasted = (float[]) xSer;
                if(asCurr[2].equals("[") && asCurr[asCurr.length-1].equals("]")){
                    float[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+asCurr.length-4);
                    try{
                        for (int i = 0; i < asCurr.length-4; i++) {
                            aPutBack[xSerCasted.length+i] = Float.valueOf(asCurr[i+3]);
                        }//for
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }
                else{
                    float[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+1);
                    try{
                        aPutBack[aPutBack.length-1] = Float.valueOf(asCurr[2]);
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }//else
            }//else if
            else if(xSer instanceof boolean[]){
                boolean[] xSerCasted = (boolean[]) xSer;
                if(asCurr[2].equals("[") && asCurr[asCurr.length-1].equals("]")){
                    boolean[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+asCurr.length-4);
                    try{
                        for (int i = 0; i < asCurr.length-4; i++) {
                            if(asCurr[i+3].equals("true")){
                                aPutBack[xSerCasted.length+i] =  true;
                            }//if
                            else if(asCurr[i+3].equals("false")){
                                aPutBack[xSerCasted.length+i] =  false;
                            }//else if
                            else{
                                throw new NumberFormatException();
                            }//else
                        }//for
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }
                else{
                    boolean[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+1);
                    try{
                        if(asCurr[2].equals("true")){
                            aPutBack[xSerCasted.length-1] =  true;
                        }//if
                        else if(asCurr[2].equals("false")){
                            aPutBack[xSerCasted.length-1] =  false;
                        }//else if
                        else{
                            throw new NumberFormatException();
                        }//else
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }//else
            }//else if
            else if(xSer instanceof String[]){
                String[] xSerCasted = (String[]) xSer;
                if(asCurr[2].equals("[") && asCurr[asCurr.length-1].equals("]")){
                    String[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+asCurr.length-4);
                    try{
                        for (int i = 0; i < asCurr.length-4; i++) {
                            aPutBack[xSerCasted.length+i] = asCurr[i+3];
                        }//for
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }
                else{
                    String[] aPutBack = Arrays.copyOf(xSerCasted, xSerCasted.length+1);
                    try{
                        aPutBack[aPutBack.length-1] = asCurr[2];
                        xSettingsMap.put(asCurr[1], aPutBack);
                    }
                    catch(NumberFormatException e){
                        System.out.println("syntax not recognized");
                    }
                }//else
            }//else if
            else
                System.out.println("syntax not recognized");
        }
        else{
            if(asCurr.length == 2){
                try{
                    xSettingsMap.put(asCurr[0], Integer.valueOf(asCurr[1]));
                    System.out.println("as integer");
                }//try
                catch(NumberFormatException e){
                    try{
                        xSettingsMap.put(asCurr[0], Float.valueOf(asCurr[1]));
                        System.out.println("as float");
                    }//try
                    catch(NumberFormatException e2){
                        try{
                            if(asCurr[1].equals("true")){
                                xSettingsMap.put(asCurr[0], true);
                            }//if
                            else if(asCurr[1].equals("false")){
                                xSettingsMap.put(asCurr[0], false);
                            }//else if
                            else{
                                throw new NumberFormatException();
                            }//else
                            System.out.println("as boolean");
                        }//try
                        catch(NumberFormatException e3){
                            xSettingsMap.put(asCurr[0], asCurr[1]);
                            System.out.println("as string");
                        }//catch
                    }//catch
                }//catch
            }//if
            else if(asCurr[1].equals("[") && asCurr[asCurr.length-1].equals("]")){
                try{
                    int[] aiArr = new int[asCurr.length-3];
                    for (int i = 2; i < asCurr.length-1; i++) {
                        aiArr[i-2] = Integer.valueOf(asCurr[i]);
                    }//for
                    xSettingsMap.put(asCurr[0], aiArr);
                    System.out.println("as integer array");
                }//try
                catch(NumberFormatException e){
                    try{
                        float[] afArr = new float[asCurr.length-3];
                        for (int i = 2; i < asCurr.length-1; i++) {
                            afArr[i-2] = Float.valueOf(asCurr[i]);
                        }//for
                        xSettingsMap.put(asCurr[0], afArr);
                        System.out.println("as float array");
                    }//try
                    catch(NumberFormatException e2){
                        try{
                            boolean[] abArr = new boolean[asCurr.length-3];
                            for (int i = 2; i < asCurr.length-1; i++) {
                                if(asCurr[i].equals("true")){
                                    abArr[i-2] = true;
                                }//if
                                else if(asCurr[i].equals("false")){
                                    abArr[i-2] = false;
                                }//else if
                                else{
                                    throw new NumberFormatException();
                                }//else
                            }//for
                            xSettingsMap.put(asCurr[0], abArr);
                            System.out.println("as boolean array");
                        }//try
                        catch(NumberFormatException e3){
                            String[] asArr = new String[asCurr.length-3];
                            for (int i = 2; i < asCurr.length-1; i++) {
                                asArr[i-2] = asCurr[i];
                            }//for
                            xSettingsMap.put(asCurr[0], asArr);
                            System.out.println("as string array");
                        }//catch
                    }//catch
                }//catch
            }//else if
            else{
                System.out.println("syntax not recognized");
            }
        }
    }//function
    
    public int loadSetting(String sName, int iDefault){
        try{
            return loadIntSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public Serializable loadSetting(String sName){
        return xSettingsMap.get(sName);
    }//function
    
    public int[] loadSetting(String sName, int[] iDefault){
        try{
            return loadIntArrSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public String loadSetting(String sName, String iDefault){
        try{
            return loadStringSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public String[] loadSetting(String sName, String[] iDefault){
        try{
            return loadStringArrSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public float loadSetting(String sName, float iDefault){
        try{
            return loadFloatSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public float[] loadSetting(String sName, float[] iDefault){
        try{
            return loadFloatArrSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public boolean loadSetting(String sName, boolean iDefault){
        try{
            return loadBoolSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    public boolean[] loadSetting(String sName, boolean[] iDefault){
        try{
            return loadBoolArrSetting(sName);
        }//try
        catch(IllegalArgumentException e){if(!bSilent)System.out.println("setting not found: " + sName);}//catch
        return iDefault;
    }//function
    
    synchronized public String loadStringSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof String) {
            return (String) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public String[] loadStringArrSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof String[]) {
            return (String[]) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public int loadIntSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof Integer) {
            return (Integer) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public int[] loadIntArrSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof int[]) {
            return (int[]) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public boolean loadBoolSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof Boolean) {
            return (Boolean) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public boolean[] loadBoolArrSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof boolean[]) {
            return (boolean[]) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public float loadFloatSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof Float) {
            return (Float) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    synchronized public float[] loadFloatArrSetting(String sName){
        Object o = xSettingsMap.get(sName);
        if(o instanceof float[]) {
            return (float[]) o;
        }//if
        throw new IllegalArgumentException("setting not availiable");
    }//function
    
    @Override
    public synchronized String toString(){
        String s = "";
        for (Entry<String, Serializable> xSettingsMap1 : xSettingsMap.entrySet()) {
            s += xSettingsMap1.getKey() + ": ";
            if(xSettingsMap1.getValue() instanceof int[]){
                s += "[ ";
                for (int xS : ((int[])xSettingsMap1.getValue()) ) {
                    s += xS + " ";
                }//for
                s += "]\n"; 
            }
            else if(xSettingsMap1.getValue() instanceof float[]){
                s += "[ ";
                for (Serializable xS : ((float[])xSettingsMap1.getValue()) ) {
                    s += xS + " ";
                }//for
                s += "]\n";
            }
            else if(xSettingsMap1.getValue() instanceof boolean[]){
                s += "[ ";
                for (Serializable xS : ((boolean[])xSettingsMap1.getValue()) ) {
                    s += xS + " ";
                }//for
                s += "]\n";
            }
            else if(xSettingsMap1.getValue() instanceof Serializable[]){
                s += "[ ";
                for (Serializable xS : ((Serializable[])xSettingsMap1.getValue()) ) {
                    s += xS + " ";
                }//for
                s += "]\n";
            }
            else{
                  s += xSettingsMap1.getValue() + "\n";
            }
        }//for
        return s;
    }
    
}//class
