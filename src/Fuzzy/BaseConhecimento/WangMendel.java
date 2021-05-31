/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fuzzy.BaseConhecimento;

import Model.ConjuntoAtivo;
import Model.Instancia;
import Model.Regra;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import org.antlr.runtime.RecognitionException;

/**
 *
 * @author Allen Hichard
 */
public class WangMendel {
    
    public FIS fis;
    public List<Instancia> instancias;
    public List<String> variaveis;
    public List<Regra> regras;
    
    public WangMendel(String jLogicFuzzy, List<Instancia> instancias, List<String> variaveis) throws RecognitionException {
        //FIS fis = FIS.createFromString(jLogicFuzzy, true);
        this.fis = FIS.createFromString(jLogicFuzzy, true);
        this.instancias = instancias;
        this.variaveis = variaveis;
        this.regras = new ArrayList<>();
        //JFuzzyChart.get().chart(fis.getFunctionBlock(null));
    }
    
    public void criarRegras(){
        for (Instancia instancia: this.instancias){
            Regra regra = new Regra();
            Iterator var = this.variaveis.iterator();
            Iterator caracteristicas = instancia.caracteristicas.iterator();
            double tnorma = 1;
            while (var.hasNext()){
                String variavel = (String) var.next();
                double valor = (double) caracteristicas.next();
                ConjuntoAtivo ca = conjuntoAtivado(variavel, valor);
                regra.addAntecedente(ca.indexConjuntoAtivado);
                tnorma *= ca.pertinencia;
            }
            regra.setTnorma(tnorma);
            regra.addConsequente(instancia.classe);
            tratarInconsistencias(regra);
        }
        
        //for (Regra regra : this.regras){
          //  System.out.println(regra.toString());
        //}
        //return new ArrayList();
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
            for (Regra r: this.regras){
                if (r.antecedentes.equals(regra.antecedentes)){
                    if (r.tnorma < regra.tnorma){
                        r.consequente = regra.consequente;
                        r.tnorma = regra.tnorma;
                    } return;
                }
                index++;
            }
            this.regras.add(regra);
        }
       
    }
}
