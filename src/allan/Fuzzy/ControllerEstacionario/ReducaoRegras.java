/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.ControllerEstacionario;

import Fuzzy.Model.ConjuntoAtivo;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class ReducaoRegras {
    
   
    public List<Regra> regras;
    public List<Regra> novasRegras;
    
    public ReducaoRegras(List<Regra> regras) {
        //FIS fis = FIS.createFromString(jLogicFuzzy, true);
        this.regras = regras;
        this.novasRegras = new ArrayList<>();
        
        //JFuzzyChart.get().chart(fis.getFunctionBlock(null));
    }
    
    
    public List<Regra> reduzirRegras(){
        boolean naoExiste;
        for (Regra regra: this.regras){
            naoExiste = true;
            for (Regra r: this.novasRegras){
                int randomNum = 0 + (int)(Math.random() * 2);
                if (r.antecedentes.equals(regra.antecedentes)){
                    naoExiste = false;
                    if (randomNum == 0) r.consequente = regra.consequente;
                }
            }
            if (naoExiste && !regra.regraCancelada()) this.novasRegras.add(regra);
        }
        return this.novasRegras;
    }

}






    
        
        

        

   