/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windfarm;

/**
 *
 * @author Marjon
 */
public class Turbine {
    private int x;
    private int y;
    
    public Turbine(){
        x = 0;
        y = 0;
    }
    
    public Turbine(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Turbine(Turbine other){
        this.x = other.x;
        this.y = other.y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public String toString(){
        return "("+x+","+y+")";
    }
}
