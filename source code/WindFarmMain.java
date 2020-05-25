/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windfarm;

import java.util.ArrayList;

/**
 *
 * @author Marjon
 */
public class WindFarmMain {
    public static WindFarmPane window;
    public static void main(String [] args){
        
        window = new WindFarmPane("Wind Farm Simulator");
        window.setVisible(true);
    }
}
