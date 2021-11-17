/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Allen Hichard
 */
public class Regra{
    
    public List<Integer> antecedentes;
    public int consequente;
    public double tnorma;
    
    
    public int tamanhoInstancia(){
        return this.antecedentes.size() + 1;
    }
    public Regra(){
        this.antecedentes = new ArrayList<>();
    }
    
    public void addAntecedente(int antecedente){
        this.antecedentes.add(antecedente);
    }
    
    public void addConsequente(int classe){
        this.consequente = classe;
    }
    
    public void setTnorma(double tnorma){
        this.tnorma = tnorma;
    }

    @Override
    public String toString() {
        return "Regra{" + "antecedentes=" + antecedentes + ", consequente=" + consequente + ", tnorma=" + tnorma + '}';
    }
    
    public boolean regraCancelada(){
        boolean regraDontCare = true;
        for (int valor : this.antecedentes){
            if(valor != -1){
                regraDontCare = false;
            }
        }
        return regraDontCare;
    }
    
    
    
    
    
}
