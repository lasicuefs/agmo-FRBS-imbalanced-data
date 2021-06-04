/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.List;

/**
 *
 * @author Allen Hichard
 */
public class Instancia {
    
    public List<Double> caracteristicas; 
    public int classe;
    
    public Instancia(List<Double> caracteristicas, int classe){
        this.caracteristicas = caracteristicas;
        this.classe = classe;
    }
}
