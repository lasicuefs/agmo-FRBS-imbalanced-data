/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */ 
package Fuzzy.Backup;

import Fuzzy.Model.ConjuntoAtivo;
import Fuzzy.Model.Instancia;
import Fuzzy.Model.Regra;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jFuzzyLogic.FIS;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class ReducaoRegras1 {
    
    public FIS fis;
    public List<Instancia> instancias;
    public List<String> variaveis;
    public List<Regra> regras;
    public List<Regra> novasRegras;
    
    public ReducaoRegras1(FIS fis, List<Instancia> instancias, List<String> variaveis, List<Regra> regras) {
        //FIS fis = FIS.createFromString(jLogicFuzzy, true);
        this.fis = fis;
        this.instancias = instancias;
        this.variaveis = variaveis;
        this.regras = regras;
        this.novasRegras = new ArrayList<>();
        
        //JFuzzyChart.get().chart(fis.getFunctionBlock(null));
    }
    
    
    public List<Regra> reduzirRegras(){
        int dontcare = -1;
        for (Instancia instancia: this.instancias){    
            for (Regra r: this.regras){
                Regra regra = new Regra();
                regra.antecedentes = r.antecedentes;
                regra.consequente = r.consequente;
                boolean calculou = false;
                Iterator var = this.variaveis.iterator();
                Iterator caracteristicas = instancia.caracteristicas.iterator();
                double tnorma = 1;
                for (int valor: regra.antecedentes){
                    if (valor != dontcare){
                        calculou = true;
                        String variavel = (String) var.next();
                        double v = (double) caracteristicas.next();         
                        ConjuntoAtivo ca = conjuntoAtivado(variavel, v);
                        tnorma *= ca.pertinencia;
                    }
                }
                if (calculou){
                    regra.setTnorma(tnorma);
                    tratarInconsistencias(regra);
                }
            }
        }
        /*System.out.println(" *********** Regras Sem filtro ***************");
        for (int i = 0; i < this.regras.size(); i++){
            System.out.print(this.regras.get(i) + " ");
        }
        System.out.println("");
        System.out.println("Novas Regras");
        
        for (int i = 0; i < this.novasRegras.size(); i++){
            System.out.print(this.novasRegras.get(i) + " ");
        }
        System.out.println("");*/
        
        return this.novasRegras;
    }
        
    
    private ConjuntoAtivo conjuntoAtivado(String variavel, double valor){
      
        String terms[] = {"BAIXA", "MEDIA", "ALTA"};
        int term = -1;
        double pertinencia = 0;
        for (int i = 0; i < terms.length; i++) {
            fis.setVariable(variavel, valor);
            double p = fis.getVariable(variavel).getMembership(terms[i]);
            if (p > pertinencia){
                pertinencia = p;
                term = i;
            }
        }
        return new ConjuntoAtivo(term, pertinencia);
    }
    
    private void tratarInconsistencias(Regra regra){
        if (regra.tnorma > 0){
            int index = 0;
            for (Regra r: this.novasRegras){
                if (r.antecedentes.equals(regra.antecedentes)){
                    if (r.tnorma < regra.tnorma){
                        r.consequente = regra.consequente;
                        r.tnorma = regra.tnorma;
                    } 
                    return;
                }
                index++;
            }
            this.novasRegras.add(regra);
        }
       
    }
}






    
        
        

        

   